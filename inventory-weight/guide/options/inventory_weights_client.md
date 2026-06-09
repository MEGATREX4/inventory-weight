---
title: "Inventory Weights Client Configuration"
description: "Learn how to configure and customize the client-side HUD and tooltip settings for MT Inventory Weight."
---

# Inventory Weights Client Configuration

MT Inventory Weight uses **fzzy_config** for client-side configuration.

The old `ItemWeightsConfigClient` class and `inventory_weights_client.json` file are no longer used.

Client settings are now stored in a fzzy_config TOML file, usually located at:

```text
config/inventoryweight/client-config.toml
```

If [Mod Menu](https://modrinth.com/mod/modmenu) is installed, players can usually open the config screen directly from the in-game Mods menu.

## What the Client Config Controls

The client config only affects local display and UI features, such as:

- HUD style
- HUD position
- HUD text
- HUD text position
- tooltip visibility
- sprite/bar display settings

Gameplay values such as max weight, item category weights, pocket weight, and overload penalties are controlled by the server config.

## Configuration Options

### `hudStyle`

Controls the visual style of the HUD.

Available values:

```text
SPRITE
BAR
```

- `SPRITE` uses the old bundle-style PNG icon display.
- `BAR` uses a simple rectangular progress bar.

Default:

```toml
hudStyle = "SPRITE"
```

### `hudPosition`

Controls where the HUD appears on the screen.

Available values:

```text
TOP_LEFT
TOP_RIGHT
CENTER_LEFT
CENTER_RIGHT
BOTTOM_LEFT
BOTTOM_RIGHT
HOTBAR_LEFT
HOTBAR_RIGHT
CENTER_HOTBAR
CUSTOM
```

Default:

```toml
hudPosition = "BOTTOM_RIGHT"
```

### `xOffset` and `yOffset`

Used when:

```toml
hudPosition = "CUSTOM"
```

These values are percentages of the screen size.

Examples:

```toml
xOffset = 0.5
yOffset = 0.5
```

This places the HUD near the center of the screen.

```toml
xOffset = 0.0
yOffset = 0.0
```

This places the HUD near the top-left corner.

### `spriteSize`

Controls the rendered size of the sprite HUD.

The source textures are 16x16, but this value can scale them.

Default:

```toml
spriteSize = 16
```

### `barWidth` and `barHeight`

Controls the size of the bar HUD.

Default:

```toml
barWidth = 82
barHeight = 10
```

These options only affect the HUD when:

```toml
hudStyle = "BAR"
```

### `hudTextMode`

Controls what text is shown near the HUD.

Available values:

```text
NONE
CURRENT
MAX
CURRENT_MAX
PERCENT
REMAINING
CURRENT_MAX_PERCENT
```

Examples:

| Value | Example Display |
| --- | --- |
| `NONE` | no text |
| `CURRENT` | `45.0k` |
| `MAX` | `90.0k` |
| `CURRENT_MAX` | `45.0k/90.0k` |
| `PERCENT` | `50%` |
| `REMAINING` | `45.0k` |
| `CURRENT_MAX_PERCENT` | `45.0k/90.0k (50%)` |

Default:

```toml
hudTextMode = "CURRENT_MAX"
```

### `hudTextPosition`

Controls where HUD text appears relative to the HUD element.

Available values:

```text
BELOW
ABOVE
LEFT
RIGHT
INSIDE
CUSTOM
```

Default:

```toml
hudTextPosition = "BELOW"
```

### `hudTextXOffset` and `hudTextYOffset`

Extra pixel offsets for HUD text.

These values work with all text positions.

Example:

```toml
hudTextXOffset = 4
hudTextYOffset = -2
```

When `hudTextPosition` is set to `CUSTOM`, these values act as the custom text position relative to the HUD element.

### `keepHudTextOnScreen`

Prevents HUD text from rendering outside the screen.

Default:

```toml
keepHudTextOnScreen = true
```

When enabled:

- text below the HUD flips above if it would go off the bottom of the screen
- text above the HUD flips below if it would go off the top
- text on the left flips right if it would go off-screen
- text on the right flips left if it would go off-screen
- final text position is clamped inside the screen

This is especially useful for the sprite HUD because the icon can be close to screen edges.

### `hudTextColor`

Text color as an RGB integer.

Default white:

```toml
hudTextColor = 16777215
```

`16777215` is decimal for `0xFFFFFF`.

### `hudTextShadow`

Controls whether HUD text has a shadow.

Default:

```toml
hudTextShadow = true
```

### `showTooltips`

Controls whether item weight tooltips are shown.

Default:

```toml
showTooltips = true
```

### Legacy Options

These options are kept for migration compatibility:

```toml
showNumbers = true
showPercentage = true
```

New configurations should use `hudTextMode` instead.

## Example Client Config

```toml
hudStyle = "SPRITE"
hudPosition = "BOTTOM_RIGHT"
xOffset = 0.5
yOffset = 0.5
spriteSize = 16
barWidth = 82
barHeight = 10
hudTextMode = "CURRENT_MAX"
hudTextPosition = "BELOW"
hudTextXOffset = 0
hudTextYOffset = 0
keepHudTextOnScreen = true
hudTextColor = 16777215
hudTextShadow = true
showTooltips = true
```

## Example: Simple Bar with Percentage Text

```toml
hudStyle = "BAR"
hudPosition = "HOTBAR_RIGHT"
hudTextMode = "PERCENT"
hudTextPosition = "ABOVE"
barWidth = 82
barHeight = 10
```

## Example: Sprite HUD with Text Above

```toml
hudStyle = "SPRITE"
hudPosition = "BOTTOM_RIGHT"
hudTextMode = "CURRENT_MAX"
hudTextPosition = "ABOVE"
spriteSize = 16
keepHudTextOnScreen = true
```

## Config Management

fzzy_config manages loading, saving, validation, and GUI generation automatically.

You no longer need manual methods such as:

```text
loadConfig()
saveConfig()
```

Changes made through the config screen are saved by fzzy_config.

## Troubleshooting

### The HUD text goes off-screen

Enable:

```toml
keepHudTextOnScreen = true
```

### I do not see a config screen

Install:

- [Mod Menu](https://modrinth.com/mod/modmenu)
- [fzzy_config](https://modrinth.com/mod/fzzy-config)

### I want to disable tooltips

Set:

```toml
showTooltips = false
```
