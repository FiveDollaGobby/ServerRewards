package me.bebo.serverRewards;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private final ServerRewards plugin;
    private final Map<UUID, Double> playerBalances = new HashMap<>();

    public EconomyManager(ServerRewards plugin) {
        this.plugin = plugin;
    }

    public double getBalance(Player player) {
        return playerBalances.getOrDefault(player.getUniqueId(), 0.0);
    }

    public boolean deposit(Player player, double amount) {
        if (amount <= 0) return false;
        UUID uuid = player.getUniqueId();
        double newBalance = getBalance(player) + amount;
        playerBalances.put(uuid, newBalance);
        return true;
    }

    public boolean withdraw(Player player, double amount) {
        if (amount <= 0 || getBalance(player) < amount) return false;
        UUID uuid = player.getUniqueId();
        double newBalance = getBalance(player) - amount;
        playerBalances.put(uuid, newBalance);
        return true;
    }

    public Map<UUID, Double> getBalances() {
        return new HashMap<>(playerBalances);
    }

    public void setBalance(UUID uuid, double balance) {
        playerBalances.put(uuid, balance);
    }
}