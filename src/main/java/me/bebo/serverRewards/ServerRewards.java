package me.bebo.serverRewards;

import me.bebo.serverRewards.commands.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public final class ServerRewards extends JavaPlugin {
    private EconomyManager economyManager;
    private PlayerDataManager playerDataManager;
    private RewardManager rewardManager;
    private ChallengeManager challengeManager;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        this.dataManager = new DataManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.economyManager = new EconomyManager(this);
        this.rewardManager = new RewardManager(this);
        this.challengeManager = new ChallengeManager(this);

        try {
            dataManager.loadAllData();
            getLogger().info("Successfully loaded player data");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load player data", e);
        }

        registerCommands();
        registerEvents();

        startAutoSaveTask();
        getLogger().info("ServerRewards v" + getDescription().getVersion() + " enabled!");
    }

    private void registerCommands() {
        registerCommand("points", new PointsCommand(this));
        registerCommand("reward", new RewardCommand(this));
        registerCommand("rewards", new RewardsCommand(this));
        registerCommand("convert", new ConvertCommand(this));
        registerCommand("stats", new StatsCommand(this));
        registerCommand("top", new TopCommand(this));
        registerCommand("setpoints", new SetPointsCommand(this));
        registerCommand("resetpoints", new ResetPointsCommand(this));
        registerCommand("reloadrewards", new ReloadCommand(this));
        registerCommand("serverrewards", new InfoCommand(this));
        registerCommand("rewardshelp", new HelpCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void startAutoSaveTask() {
        int saveInterval = getConfig().getInt("settings.auto_save.interval", 300) * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                dataManager.saveAllData();
                getLogger().info("Auto-saved all player data");
            }
        }.runTaskTimer(this, saveInterval, saveInterval);
    }

    @Override
    public void onDisable() {
        try {
            dataManager.saveAllData();
            getLogger().info("Successfully saved player data on shutdown");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to save player data on shutdown", e);
        }
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            getLogger().warning("Command '" + name + "' not registered in plugin.yml!");
        }
    }

    public EconomyManager getEconomyManager() { return economyManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public RewardManager getRewardManager() { return rewardManager; }
    public ChallengeManager getChallengeManager() { return challengeManager; }
    public DataManager getDataManager() { return dataManager; }
}