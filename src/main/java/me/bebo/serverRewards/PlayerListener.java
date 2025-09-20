package me.bebo.serverRewards;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    private final ServerRewards plugin;

    public PlayerListener(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().getPlayerData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        String entityType = event.getEntityType().name();
        String configPath = "points.kill." + entityType;

        int points = plugin.getConfig().contains(configPath)
                ? plugin.getConfig().getInt(configPath)
                : plugin.getConfig().getInt("points.kill.default", 1);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(killer);
        data.addKillPoints(points);
        checkMilestone(killer, data);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        String blockType = block.getType().name();
        String configPath = "points.mine." + blockType;

        if (!plugin.getConfig().contains(configPath)) return;

        int points = plugin.getConfig().getInt(configPath,
                plugin.getConfig().getInt("points.mine.default", 0));

        if (points > 0) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(event.getPlayer());
            data.addMinePoints(points);
            checkMilestone(event.getPlayer(), data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        String blockType = block.getType().name();
        String configPath = "points.build." + blockType;

        int points = plugin.getConfig().contains(configPath)
                ? plugin.getConfig().getInt(configPath)
                : plugin.getConfig().getInt("points.build.default", 1);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(event.getPlayer());
        data.addBuildPoints(points);
        checkMilestone(event.getPlayer(), data);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) ||
                event.getClickedInventory() == null ||
                event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        InventoryView view = event.getView();
        String title = view.getTitle();
        String configTitle = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("rewards_shop.gui.title", "Rewards Shop"));

        if (!title.equals(configTitle)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        plugin.getRewardManager().processPurchase(player, clicked);
    }

    private void checkMilestone(Player player, PlayerData data) {
        int required = plugin.getConfig().getInt("rewards_shop.required_points", 100);
        int totalPoints = data.getTotalPoints();
        int currentMilestone = (totalPoints / required) * required;

        if (currentMilestone > 0 && currentMilestone > data.getLastNotificationAt()) {
            data.setLastNotificationAt(currentMilestone);
            data.setRewardAvailable(true);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.reward_available",
                                    "&aYou've reached {points} points! Type &e/rewards &ato spend them!")
                            .replace("{points}", String.valueOf(currentMilestone))));
        }
    }
}