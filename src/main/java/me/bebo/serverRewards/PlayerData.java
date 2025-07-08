package me.bebo.serverRewards;

public class PlayerData {
    private int killPoints;
    private int minePoints;
    private int buildPoints;
    private String title = "Newbie";
    private int lastNotificationAt = 0;
    private boolean rewardAvailable = false;

    // Points methods
    public int getKillPoints() { return Math.max(0, killPoints); }
    public void setKillPoints(int killPoints) { this.killPoints = Math.max(0, killPoints); }
    public void addKillPoints(int amount) { this.killPoints = Math.max(0, this.killPoints + amount); }

    public int getMinePoints() { return Math.max(0, minePoints); }
    public void setMinePoints(int minePoints) { this.minePoints = Math.max(0, minePoints); }
    public void addMinePoints(int amount) { this.minePoints = Math.max(0, this.minePoints + amount); }

    public int getBuildPoints() { return Math.max(0, buildPoints); }
    public void setBuildPoints(int buildPoints) { this.buildPoints = Math.max(0, buildPoints); }
    public void addBuildPoints(int amount) { this.buildPoints = Math.max(0, this.buildPoints + amount); }

    public int getTotalPoints() { return getKillPoints() + getMinePoints() + getBuildPoints(); }

    // Title methods
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title : "Newbie"; }

    // Notification tracking
    public int getLastNotificationAt() { return lastNotificationAt; }
    public void setLastNotificationAt(int points) { this.lastNotificationAt = Math.max(0, points); }

    // Reward availability
    public boolean isRewardAvailable() { return rewardAvailable; }
    public void setRewardAvailable(boolean rewardAvailable) { this.rewardAvailable = rewardAvailable; }

    // Reset points
    public void resetPoints(String type) {
        switch (type.toLowerCase()) {
            case "kill": killPoints = 0; break;
            case "mine": minePoints = 0; break;
            case "build": buildPoints = 0; break;
            case "all":
                killPoints = 0;
                minePoints = 0;
                buildPoints = 0;
                break;
        }
    }
}