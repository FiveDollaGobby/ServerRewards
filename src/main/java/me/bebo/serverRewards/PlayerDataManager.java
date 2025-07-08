package me.bebo.serverRewards;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {
    private final ServerRewards plugin;
    private final HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerDataManager(ServerRewards plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerData());
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> new PlayerData());
    }

    public void setPlayerData(UUID uuid, PlayerData data) {
        playerDataMap.put(uuid, data);
    }

    public Set<UUID> getAllPlayers() {
        return playerDataMap.keySet();
    }

    public void resetPlayerData(Player player, String type) {
        PlayerData data = getPlayerData(player);
        switch (type.toLowerCase()) {
            case "kill": data.setKillPoints(0); break;
            case "mine": data.setMinePoints(0); break;
            case "build": data.setBuildPoints(0); break;
            case "all":
                data.setKillPoints(0);
                data.setMinePoints(0);
                data.setBuildPoints(0);
                break;
        }
    }
}