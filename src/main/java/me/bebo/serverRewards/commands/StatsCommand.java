package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.PlayerData;
import me.bebo.serverRewards.ServerRewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public StatsCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;

        if (args.length > 0) {
            if (!sender.hasPermission("serverrewards.stats.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to view others' stats!");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
            target = (Player) sender;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);

        sender.sendMessage(ChatColor.GOLD + "=== " + target.getName() + "'s Stats ===");
        sender.sendMessage(ChatColor.GREEN + "Rank: " + data.getTitle());
        sender.sendMessage(ChatColor.GREEN + "Kill Points: " + data.getKillPoints());
        sender.sendMessage(ChatColor.GREEN + "Mine Points: " + data.getMinePoints());
        sender.sendMessage(ChatColor.GREEN + "Build Points: " + data.getBuildPoints());
        sender.sendMessage(ChatColor.GREEN + "Total Points: " + data.getTotalPoints());

        return true;
    }
}