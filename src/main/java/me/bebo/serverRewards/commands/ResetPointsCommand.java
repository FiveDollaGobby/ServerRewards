package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.ServerRewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetPointsCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public ResetPointsCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /resetpoints <player> <kill|mine|build|all>");
            return true;
        }

        if (!sender.hasPermission("serverrewards.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        String type = args[1].toLowerCase();
        plugin.getPlayerDataManager().resetPlayerData(target, type);

        sender.sendMessage(ChatColor.GREEN + "Reset " + target.getName() + "'s " + type + " points");
        return true;
    }
}