package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.ServerRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public ReloadCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("serverrewards.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "ServerRewards config reloaded!");
        return true;
    }
}