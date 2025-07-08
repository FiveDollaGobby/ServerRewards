package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.ServerRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardsCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public RewardsCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.player_only", "&cThis command is for players only!")));
            return true;
        }

        Player player = (Player) sender;
        int totalPoints = plugin.getPlayerDataManager().getPlayerData(player).getTotalPoints();
        int requiredPoints = plugin.getConfig().getInt("rewards_shop.required_points", 100);

        if (totalPoints < requiredPoints) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("messages.shop.insufficient_points",
                                    "&cYou need at least {required} points to access the rewards shop!")
                            .replace("{required}", String.valueOf(requiredPoints))));
            return true;
        }

        player.openInventory(plugin.getRewardManager().createRewardsGUI(player));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.shop.welcome",
                        "&6Welcome to the Rewards Shop!")));
        return true;
    }
}