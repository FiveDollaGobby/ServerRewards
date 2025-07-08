# 🏆 ServerRewards v1.0
ServerRewards is a comprehensive rewards system for Minecraft servers that tracks player activities and provides in-game rewards. Perfect for survival and economy servers looking to incentivize gameplay with a balanced points system.

## ✨ Key Features
### 📊 Activity Tracking
- **⚔️ Combat Rewards:** Earn points for killing mobs and players
- **⛏️ Mining Rewards:** Get points for mining valuable ores
- **🏗️ Building Rewards:** Gain points for placing blocks
- **📈 Milestone Tracking:** Automatic notifications when reaching point thresholds

### 🛍️ Rewards Shop
- **🛒 75+ Reward Items:** From tools to special items
- **⏳ Cooldown System:** 30-minute cooldown between rewards
- **📊 Point Deduction:** Automatically deducts points on purchase
- **🎯 Category System:** Organized by starter kits, tools, combat, etc.

## ⚙️ Configuration
- **📝 Customizable YAML Config:** Easy-to-edit configuration file
- **⚡ Live Reloading:** `/reloadrewards` updates settings instantly
- **📊 Adjustable Points:** Configure points for every mob, block, and activity

## 📥 Installation
1. Download ServerRewards.jar
2. Place in `plugins/` folder
3. Restart server
4. Edit `plugins/ServerRewards/config.yml` if needed

## ⚙️ Sample Config Highlights
```yaml
points:
  kill:
    ZOMBIE: 1
    PLAYER: 10
  mine:
    DIAMOND_ORE: 3
    IRON_ORE: 2
  build:
    DIAMOND_BLOCK: 3

rewards_shop:
  required_points: 100
  cooldown_hours: 0.5  # 30-minute cooldown
  
  gui:
    title: "&6&lREWARDS SHOP"
    size: 54
```

## 🔑 Permissions
- `serverrewards.*` - All commands (default: op)
- `serverrewards.rewards` - Access rewards shop (default: true)
- `serverrewards.convert` - Convert points to money (default: true)

## 🕹️ Usage
- **Players:** `/rewards` - Open rewards shop
- **Players:** `/points` - Check your points balance
- **Admins:** `/setpoints <player> <type> <amount>` - Manage player points

## 📌 Technical Specs
- **Minecraft:** 1.21+
- **Java:** 21+
- **Dependencies:** PaperAPI
- **Storage:** YAML-based data storage

## ❓ FAQ
**Q: Can I add custom rewards?**  
A: Yes! Simply edit the `rewards_shop.gui.items` section in config.yml

**Q: How often do points save?**  
A: Auto-saves every 5 minutes by default (configurable)

**Q: Can players trade points?**  
A: Not directly, but they can convert points to economy money

## 🌟 Pro Tips
- Great for economy servers when combined with Vault
- Use the 30-minute cooldown to balance reward frequency
- The conversion system works great with server shops
- Perfect for minigame servers to reward participation

(The ultimate rewards system to keep players engaged and progressing!)

