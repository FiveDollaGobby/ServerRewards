package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.ServerRewards;
import me.bebo.serverRewards.PlayerData;  // Add this import
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointsCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public PointsCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        player.sendMessage(ChatColor.GOLD + "=== Your Points ===");
        player.sendMessage(ChatColor.GREEN + "Kill Points: " + data.getKillPoints());
        player.sendMessage(ChatColor.GREEN + "Mine Points: " + data.getMinePoints());
        player.sendMessage(ChatColor.GREEN + "Build Points: " + data.getBuildPoints());
        player.sendMessage(ChatColor.GREEN + "Total Points: " + data.getTotalPoints());
        player.sendMessage(ChatColor.GOLD + "=================");

        return true;
    }
}