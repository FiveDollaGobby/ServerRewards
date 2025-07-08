package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.ServerRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InfoCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public InfoCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "=== ServerRewards ===");
        sender.sendMessage(ChatColor.GREEN + "Version: " + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "Author: Bebo");
        sender.sendMessage(ChatColor.GREEN + "Type /rewardshelp for commands");
        return true;
    }
}