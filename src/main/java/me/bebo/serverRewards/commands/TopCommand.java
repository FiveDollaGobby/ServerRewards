package me.bebo.serverRewards.commands;

import me.bebo.serverRewards.PlayerData;
import me.bebo.serverRewards.ServerRewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TopCommand implements CommandExecutor {
    private final ServerRewards plugin;

    public TopCommand(ServerRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /top <kill|mine|build|total>");
            return true;
        }

        String type = args[0].toLowerCase();
        Map<UUID, Integer> pointsMap = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            switch (type) {
                case "kill":
                    pointsMap.put(player.getUniqueId(), data.getKillPoints());
                    break;
                case "mine":
                    pointsMap.put(player.getUniqueId(), data.getMinePoints());
                    break;
                case "build":
                    pointsMap.put(player.getUniqueId(), data.getBuildPoints());
                    break;
                case "total":
                    pointsMap.put(player.getUniqueId(), data.getTotalPoints());
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Invalid type. Use kill, mine, build or total");
                    return true;
            }
        }

        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(pointsMap.entrySet());
        sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        sender.sendMessage(ChatColor.GOLD + "=== Top 10 Players (" + type + ") ===");
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            Map.Entry<UUID, Integer> entry = sorted.get(i);
            String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            sender.sendMessage(ChatColor.GREEN + String.valueOf(i+1) + ". " + name + ": " + entry.getValue());
        }

        return true;
    }
}