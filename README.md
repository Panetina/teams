# Teams Mod

**Made by Panyel**
**Team Name:** Panetina
**Minecraft Version:** 1.21.1
**Platform:** Fabric

A complete team management system for Fabric Minecraft 1.21.1 featuring colored chat, team-specific borders, spawn points, rewards, private team chat, and more.

---

## 📁 Installation

1. Place the mod JAR in your server's `mods/` folder.
2. Install **Fabric API** and **Fabric Loader 0.19.3+**.
3. Start the server once to generate the default configuration:

```text
config/teams/teams.json
```

---

## 🎮 Commands

### Player Commands

| Command        | Description                      | Example          |
| -------------- | -------------------------------- | ---------------- |
| `/t <message>` | Send a private team chat message | `/t Hello team!` |

### Admin Commands (OP Level 4)

| Command                             | Description                                                | Example                     |
| ----------------------------------- | ---------------------------------------------------------- | --------------------------- |
| `/teamadd <player> <teamId>`        | Add a player to a team                                     | `/teamadd Player1 team1`    |
| `/teamremove <player>`              | Remove a player from their team                            | `/teamremove Player1`       |
| `/teamgive <teamId> <item> [count]` | Give items to all team members (including offline players) | `/teamgive team1 diamond 5` |
| `/teamteleport <player>`            | Teleport to a team member                                  | `/teamteleport Player1`     |
| `/teamborder set <radius>`          | Set team border radius                                     | `/teamborder set 100`       |
| `/teammerge`                        | Toggle merged border mode                                  | `/teammerge`                |

---

## ⚙️ Configuration

**Location:**

```text
config/teams/teams.json
```

### Team Structure

```json
{
  "id": "team1",
  "name": "Kingdom of Oak",
  "prefix": "[OAK]",
  "color": "#55AA55",
  "spawn": {
    "x": 0,
    "y": 64,
    "z": 0
  },
  "borderRadius": 64,
  "merged": false,
  "members": [],
  "commands": []
}
```

### Configuration Fields

| Field          | Type    | Description                                    |
| -------------- | ------- | ---------------------------------------------- |
| `id`           | String  | Unique team identifier used in commands        |
| `name`         | String  | Display name shown to players                  |
| `prefix`       | String  | Team tag shown in chat                         |
| `color`        | String  | Hex color (`#55AA55`) or named color (`green`) |
| `spawn`        | Object  | Team spawn coordinates                         |
| `borderRadius` | Integer | Team border radius in blocks                   |
| `merged`       | Boolean | `true` = global border, `false` = team border  |
| `members`      | Array   | Team member UUIDs (auto-managed)               |
| `commands`     | Array   | Commands executed when a player joins          |

### Join Commands Example

```json
"commands": [
  "team join blue",
  "effect give @s minecraft:water_breathing 60 1",
  "give @s minecraft:blue_wool 1",
  "tellraw @s {\"text\":\"Welcome to the Blue Dominion!\",\"color\":\"blue\"}"
]
```

---

## 🎨 Color Support

### Chat & Tab List Colors

Supports:

* Hex colors (`#55AA55`, `#FF4444`, `#4444FF`)
* Named colors (`red`, `blue`, `green`, `yellow`)

### Overhead Nameplate Colors

Due to Minecraft limitations, **only named colors** work for overhead nameplates.

Automatically supported vanilla team colors:

```text
black
dark_blue
dark_green
dark_aqua
dark_red
dark_purple
gold
gray
dark_gray
blue
green
aqua
red
light_purple
yellow
white
```

To enable colored overhead nameplates, add a command such as:

```json
"team join blue"
```

to your team's `commands` array.

---

## 🏠 Team Spawn & Borders

### Spawn Teleport

Players are automatically teleported to the team spawn when joining.

### Respawn Priority

1. Bed Spawn
2. Team Spawn
3. World Spawn

### Team Borders

* Each team has its own border centered on its spawn.
* Border size is calculated as:

```text
radius × 2
```

### Merged Mode

When:

```json
"merged": true
```

the team uses the server's global world border instead of its own border.

---

## 🎁 Rewards System

### Online Players

Items are delivered immediately.

### Offline Players

Items are stored and automatically delivered on next login.

### Features

* Persistent pending rewards
* Survive server restarts
* Automatic delivery system

Example:

```mcfunction
/teamgive team1 minecraft:diamond 5
```

---

## 💬 Chat Features

### Team Chat (`/t`)

* Available to all players who belong to a team
* Visible only to team members
* Displays colored team prefix
* Displays colored player names
* Logged to console

Console format:

```text
[TEAM CHAT][PREFIX][Player]: message
```

### Global Chat

* Team members display colored names
* Non-team players remain white
* Fully logged to server console

---

## 🚫 Disabled Vanilla Features

The vanilla command:

```mcfunction
/teammsg
```

is disabled to avoid confusion with:

```mcfunction
/t
```

---

## 📋 Default Teams

Generated automatically on first server start.

| Team ID | Name           | Color | Border    |
| ------- | -------------- | ----- | --------- |
| `team1` | Kingdom of Oak | Green | 64 blocks |
| `team2` | Iron Empire    | Gray  | 64 blocks |
| `team3` | Blue Dominion  | Blue  | 64 blocks |

---

## 🔧 Troubleshooting

### Overhead Nameplate Not Colored?

* Use a named color, not a hex color.
* Verify the vanilla team exists.
* Check with:

```mcfunction
/team list <color>
```

### Player Can't Use `/t`?

Players must belong to a team first:

```mcfunction
/teamadd <player> <teamId>
```

### Border Not Working?

* Verify `borderRadius` in configuration.
* Ensure:

```json
"merged": false
```

* Update border size with:

```mcfunction
/teamborder set <radius>
```

### Commands Not Executing on Join?

* Verify the `commands` array exists.
* Ensure commands are valid.
* Commands execute with OP permissions.

---

## 📝 Logging

All team chat is logged with:

```text
[TEAM CHAT]
```

for easy moderation and monitoring.

Regular chat remains logged using standard Minecraft formatting.

---

## 🛠️ Development

| Property          | Value       |
| ----------------- | ----------- |
| Minecraft Version | 1.21.1      |
| Mappings          | Fabric Yarn |
| Java Version      | 21          |
| Dependencies      | Fabric API  |

---

## 📄 License

This mod is provided **as-is**.

You are free to modify it for personal use.

---

## Credits

**Made by Panyel**
**Team Name:** Panetina

---

> 💡 **Pro Tip:** Always keep a backup of `config/teams/teams.json` before major server updates.
