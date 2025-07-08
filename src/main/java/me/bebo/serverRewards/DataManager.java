package me.bebo.serverRewards;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {
    private final ServerRewards plugin;
    private File dataFile;
    private YamlConfiguration dataConfig;

    public DataManager(ServerRewards plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create data.yml!", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveAllData() {
        dataConfig.set("players", null);

        plugin.getPlayerDataManager().getAllPlayers().forEach(uuid -> {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);
            String path = "players." + uuid.toString();
            dataConfig.set(path + ".killPoints", data.getKillPoints());
            dataConfig.set(path + ".minePoints", data.getMinePoints());
            dataConfig.set(path + ".buildPoints", data.getBuildPoints());
            dataConfig.set(path + ".title", data.getTitle());
            dataConfig.set(path + ".lastNotificationAt", data.getLastNotificationAt());
        });

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save data.yml!", e);
        }
    }

    public void loadAllData() {
        if (!dataConfig.contains("players")) return;

        for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                PlayerData data = new PlayerData();

                data.setKillPoints(dataConfig.getInt("players." + uuidStr + ".killPoints"));
                data.setMinePoints(dataConfig.getInt("players." + uuidStr + ".minePoints"));
                data.setBuildPoints(dataConfig.getInt("players." + uuidStr + ".buildPoints"));
                data.setTitle(dataConfig.getString("players." + uuidStr + ".title", "Newbie"));
                data.setLastNotificationAt(dataConfig.getInt("players." + uuidStr + ".lastNotificationAt", 0));

                plugin.getPlayerDataManager().setPlayerData(uuid, data);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID format in data.yml: " + uuidStr);
            }
        }
    }
}