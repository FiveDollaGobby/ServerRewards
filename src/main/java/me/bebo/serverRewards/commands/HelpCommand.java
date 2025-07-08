package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.ServerRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public HelpCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "ServerRewards Help" + ChatColor.GOLD + " ===");
        sender.sendMessage(ChatColor.YELLOW + "/points" + ChatColor.WHITE + " - Check your current points balance");
        sender.sendMessage(ChatColor.YELLOW + "/rewards" + ChatColor.WHITE + " - Open the rewards shop to spend your points");
        sender.sendMessage(ChatColor.YELLOW + "/convert <kill | mine | build> <amount>" + ChatColor.WHITE + " - Convert points to in-game money");
        sender.sendMessage(ChatColor.YELLOW + "/stats [player]" + ChatColor.WHITE + " - View your or another player's stats");
        sender.sendMessage(ChatColor.YELLOW + "/top <kill | mine | build | total>" + ChatColor.WHITE + " - View the top players");

        if (sender.hasPermission("serverrewards.admin")) {
            sender.sendMessage(ChatColor.RED + "Admin Commands:");
            sender.sendMessage(ChatColor.YELLOW + "/setpoints <player> <kill | mine | build> <amount>" + ChatColor.WHITE + " - Set a player's points");
            sender.sendMessage(ChatColor.YELLOW + "/resetpoints <player> <kill | mine | build | all>" + ChatColor.WHITE + " - Reset a player's points");
            sender.sendMessage(ChatColor.YELLOW + "/reloadrewards" + ChatColor.WHITE + " - Reload the plugin configuration");
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "Earn points by:");
        sender.sendMessage(ChatColor.GRAY + "- Killing mobs: " +
                ChatColor.WHITE + plugin.getConfig().getInt("points.kill.zombie", 1) + " per zombie, " +
                plugin.getConfig().getInt("points.kill.player", 10) + " per player");
        sender.sendMessage(ChatColor.GRAY + "- Mining ores: " +
                ChatColor.WHITE + plugin.getConfig().getInt("points.mine.ores.DIAMOND_ORE", 3) + " per diamond, " +
                plugin.getConfig().getInt("points.mine.ores.IRON_ORE", 2) + " per iron");
        sender.sendMessage(ChatColor.GRAY + "- Building: " +
                ChatColor.WHITE + plugin.getConfig().getInt("points.build.building_blocks", 1) + " per block placed");

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Need " +
                plugin.getConfig().getInt("rewards_shop.required_points", 100) +
                " points to access the rewards shop!");
        sender.sendMessage(ChatColor.GOLD + "Cooldown: " +
                plugin.getConfig().getInt("rewards_shop.cooldown_hours", 24) + " hours between rewards");

        return true;
    }
}