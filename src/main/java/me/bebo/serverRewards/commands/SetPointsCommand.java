package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.PlayerData;
import me.bebo.serverRewards.ServerRewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPointsCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public SetPointsCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /setpoints <player> <kill|mine|build> <amount>");
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
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
            
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "Amount cannot be negative!");
                return true;
            }
            if (amount > Integer.MAX_VALUE / 2) {
                sender.sendMessage(ChatColor.RED + "Amount too large! Maximum: " + (Integer.MAX_VALUE / 2));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount! Please enter a valid number.");
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);

        try {
            switch (type) {
                case "kill":
                    data.setKillPoints(amount);
                    break;
                case "mine":
                    data.setMinePoints(amount);
                    break;
                case "build":
                    data.setBuildPoints(amount);
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Invalid type. Use kill, mine or build");
                    return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s " + type + " points to " + amount);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An unexpected error occurred: " + e.getMessage());
            plugin.getLogger().warning("Error in SetPointsCommand: " + e.getMessage());
        }
        return true;
    }
}