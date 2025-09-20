package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.PlayerData;
import me.bebo.serverRewards.ServerRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConvertCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public ConvertCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /convert <kill|mine|build> <amount>");
            return true;
        }

        String type = args[0].toLowerCase();
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "Amount must be positive!");
                return true;
            }
            if (amount > Integer.MAX_VALUE / 2) {
                player.sendMessage(ChatColor.RED + "Amount too large! Maximum: " + (Integer.MAX_VALUE / 2));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount! Please enter a valid number.");
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        int currentPoints = switch (type) {
            case "kill" -> data.getKillPoints();
            case "mine" -> data.getMinePoints();
            case "build" -> data.getBuildPoints();
            default -> {
                player.sendMessage(ChatColor.RED + "Invalid type! Use kill, mine, or build");
                yield -1;
            }
        };

        if (currentPoints == -1) return true;
        if (currentPoints < amount) {
            player.sendMessage(ChatColor.RED + "Not enough points! You have: " + currentPoints);
            return true;
        }

        double rate = plugin.getConfig().getDouble("economy.conversion." + type, 1.0);
        double convertedAmount = amount * rate;

        try {
            switch (type) {
                case "kill" -> data.setKillPoints(data.getKillPoints() - amount);
                case "mine" -> data.setMinePoints(data.getMinePoints() - amount);
                case "build" -> data.setBuildPoints(data.getBuildPoints() - amount);
            }

            if (!plugin.getEconomyManager().deposit(player, convertedAmount)) {
                player.sendMessage(ChatColor.RED + "Failed to deposit money to your account!");
                return true;
            }

            player.sendMessage(String.format(
                    "%sConverted %s%d %s points %sto %s$%.2f",
                    ChatColor.GREEN,
                    ChatColor.YELLOW,
                    amount,
                    type,
                    ChatColor.GREEN,
                    ChatColor.YELLOW,
                    convertedAmount
            ));
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "An unexpected error occurred: " + e.getMessage());
            plugin.getLogger().warning("Error in ConvertCommand: " + e.getMessage());
        }

        return true;
    }
}