package me.bebo.serverRewards;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RewardManager {
    private final ServerRewards plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<String, ItemStack> itemTemplates = new ConcurrentHashMap<>();

    public RewardManager(ServerRewards plugin) {
        this.plugin = plugin;
        loadTemplates();
    }

    private void loadTemplates() {
        if (!plugin.getConfig().isConfigurationSection("rewards_shop.gui.items")) return;

        for (String itemKey : plugin.getConfig().getConfigurationSection("rewards_shop.gui.items").getKeys(false)) {
            String path = "rewards_shop.gui.items." + itemKey;

            try {
                Material material = Material.valueOf(plugin.getConfig().getString(path + ".material"));
                int amount = plugin.getConfig().getInt(path + ".amount", 1);
                String name = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString(path + ".name", itemKey));

                List<String> lore = new ArrayList<>();
                for (String line : plugin.getConfig().getStringList(path + ".lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                ItemStack item = new ItemStack(material, amount);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(lore);
                item.setItemMeta(meta);

                itemTemplates.put(itemKey, item);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material for reward item: " + itemKey);
            }
        }
    }

    public Inventory createRewardsGUI(Player player) {
        int size = plugin.getConfig().getInt("rewards_shop.gui.size", 54);
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("rewards_shop.gui.title", "Rewards Shop"));

        Inventory inv = Bukkit.createInventory(null, size, title);

        if (itemTemplates.isEmpty()) {
            plugin.getLogger().warning("No reward items loaded!");
            return inv;
        }

        int playerPoints = plugin.getPlayerDataManager().getPlayerData(player).getTotalPoints();

        for (String itemKey : itemTemplates.keySet()) {
            String path = "rewards_shop.gui.items." + itemKey;
            int slot = plugin.getConfig().getInt(path + ".slot");
            int price = plugin.getConfig().getInt(path + ".price", 0);

            if (slot < 0 || slot >= size) {
                plugin.getLogger().warning("Invalid slot " + slot + " for " + itemKey);
                continue;
            }

            ItemStack item = itemTemplates.get(itemKey).clone();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

            lore.add("");
            lore.add(ChatColor.GOLD + "Price: " + ChatColor.YELLOW + price + " points");
            lore.add(playerPoints >= price ?
                    ChatColor.GREEN + "Click to purchase!" :
                    ChatColor.RED + "You need " + (price - playerPoints) + " more points!");

            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
        }

        return inv;
    }

    public boolean processPurchase(Player player, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return false;
        }

        String clickedName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        for (String itemKey : itemTemplates.keySet()) {
            String path = "rewards_shop.gui.items." + itemKey;
            String configName = ChatColor.stripColor(itemTemplates.get(itemKey).getItemMeta().getDisplayName());

            if (clickedName.equals(configName)) {
                int price = plugin.getConfig().getInt(path + ".price", 0);

                if (!canPurchase(player, data, price)) {
                    return false;
                }

                deductPoints(data, price);
                executeCommands(player, path);
                notifyPlayer(player, clickedName, price);

                return true;
            }
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.shop.item_not_found",
                        "&cThis reward is no longer available")));
        return false;
    }

    private boolean canPurchase(Player player, PlayerData data, int price) {
        if (data.getTotalPoints() < price) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.shop.insufficient_points",
                                    "&cYou need {needed} more points!")
                            .replace("{needed}", String.valueOf(price - data.getTotalPoints()))));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            return false;
        }

        if (isOnCooldown(player)) {
            long remaining = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            String time = formatCooldown(remaining);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.shop.cooldown",
                                    "&eYou can purchase again in: &a{time}")
                            .replace("{time}", time)));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            return false;
        }

        return true;
    }

    private void deductPoints(PlayerData data, int price) {
        int total = data.getTotalPoints();
        if (total <= 0) return;

        int killPoints = (int) Math.round(price * (data.getKillPoints() / (double) total));
        int minePoints = (int) Math.round(price * (data.getMinePoints() / (double) total));
        int buildPoints = price - killPoints - minePoints;

        data.setKillPoints(Math.max(0, data.getKillPoints() - killPoints));
        data.setMinePoints(Math.max(0, data.getMinePoints() - minePoints));
        data.setBuildPoints(Math.max(0, data.getBuildPoints() - buildPoints));
    }

    private void executeCommands(Player player, String path) {
        String safePlayerName = player.getName().replace("\"", "\\\"");

        for (String command : plugin.getConfig().getStringList(path + ".commands")) {
            try {
                String sanitizedCommand = sanitizeCommand(command, safePlayerName);
                if (isCommandAllowed(sanitizedCommand)) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sanitizedCommand);
                } else {
                    plugin.getLogger().warning("Blocked potentially dangerous command: " + command);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to execute reward command: " + command + " - " + e.getMessage());
            }
        }
    }

    private String sanitizeCommand(String command, String playerName) {
        String sanitized = command.replace("{player}", playerName);
        
        String[] dangerousCommands = {
            "op", "deop", "ban", "kick", "stop", "reload", "restart", 
            "give command_block", "give barrier", "give structure_block",
            "give jigsaw_block", "give debug_stick", "give knowledge_book",
            "execute", "function", "schedule", "forceload", "setblock",
            "fill", "clone", "summon", "tp", "teleport", "spawnpoint",
            "setworldspawn", "weather", "time", "difficulty", "gamerule"
        };
        
        for (String dangerous : dangerousCommands) {
            if (sanitized.toLowerCase().startsWith(dangerous.toLowerCase())) {
                throw new SecurityException("Dangerous command blocked: " + dangerous);
            }
        }
        
        return sanitized;
    }

    private boolean isCommandAllowed(String command) {
        String[] allowedCommands = {
            "give", "effect", "enchant", "title", "tellraw", "playsound",
            "particle", "weather", "time", "gamemode", "fly", "speed",
            "heal", "feed", "repair", "clear", "xp", "experience"
        };
        
        for (String allowed : allowedCommands) {
            if (command.toLowerCase().startsWith(allowed.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }

    private void notifyPlayer(Player player, String itemName, int price) {
        setCooldown(player);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.shop.purchase_success",
                                "&aPurchased {item} for {price} points!")
                        .replace("{item}", itemName)
                        .replace("{price}", String.valueOf(price))));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.closeInventory();
    }

    private boolean isOnCooldown(Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    private void setCooldown(Player player) {
        long cooldownMillis = TimeUnit.HOURS.toMillis(
                plugin.getConfig().getLong("rewards_shop.cooldown_hours", 24));
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldownMillis);
    }

    private String formatCooldown(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        return String.format("%dh %02dm", hours, minutes);
    }
}