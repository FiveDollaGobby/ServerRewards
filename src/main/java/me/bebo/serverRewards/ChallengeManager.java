package me.bebo.serverRewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class ChallengeManager {
    private final ServerRewards plugin;
    private final Map<UUID, Map<String, Integer>> playerProgress = new ConcurrentHashMap<>();

    public ChallengeManager(ServerRewards plugin) {
        this.plugin = plugin;
    }

    public void trackProgress(Player player, String challengeType, int amount) {
        UUID playerId = player.getUniqueId();
        Map<String, Integer> progress = playerProgress.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        progress.put(challengeType, progress.getOrDefault(challengeType, 0) + amount);

        checkChallengeCompletion(player, challengeType);
    }

    private void checkChallengeCompletion(Player player, String challengeType) {
        int currentProgress = getProgress(player, challengeType);
        int required = plugin.getConfig().getInt("challenges." + challengeType + ".goal", 100);

        if (currentProgress >= required) {
            completeChallenge(player, challengeType);
        }
    }

    private void completeChallenge(Player player, String challengeType) {
        setProgress(player, challengeType, 0);

        for (String command : plugin.getConfig().getStringList("challenges." + challengeType + ".reward_commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
        }

        int rewardPoints = plugin.getConfig().getInt("challenges." + challengeType + ".reward_points", 0);
        if (rewardPoints > 0) {
            plugin.getPlayerDataManager().getPlayerData(player).addKillPoints(rewardPoints);
        }

        player.sendMessage(ChatColor.GREEN + "Completed challenge: " + challengeType);
    }

    public int getProgress(Player player, String challengeType) {
        return playerProgress.getOrDefault(player.getUniqueId(), new ConcurrentHashMap<>())
                .getOrDefault(challengeType, 0);
    }

    public void setProgress(Player player, String challengeType, int amount) {
        playerProgress.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(challengeType, amount);
    }
}