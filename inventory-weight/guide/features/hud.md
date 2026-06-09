---
title: "Custom HUD Display"
description: "Detailed information about the MT Inventory Weight HUD, including sprite/bar display styles, text options, positioning, and client configuration."
---

# Custom HUD Display

MT Inventory Weight adds a client-side HUD that shows the player's current carried weight compared to their maximum carry capacity.

The HUD helps players quickly understand when they are light, heavy, near capacity, or overloaded.

## HUD Data Source

The HUD reads synced player weight data from the server.

The server calculates:

- current inventory weight
- maximum weight
- overload state

The client displays that information through the HUD.

## HUD Styles

The HUD supports two display styles:

```text
SPRITE
BAR
```

These are configured in the client config:

```text
config/inventoryweight/client-config.toml
```

If [Mod Menu](https://modrinth.com/mod/modmenu) is installed, these settings can usually be changed from the in-game config screen.

## Sprite HUD

The `SPRITE` style uses the classic MT Inventory Weight PNG icon display.

```toml
hudStyle = "SPRITE"
```

### Sprite Icons

The sprite HUD uses several texture types:

- empty inventory icon
- filled inventory icons
- overload icon
- Strength/Haste overlay icon

Texture paths:

```text
assets/inventoryweight/textures/gui/inventory_empty.png
assets/inventoryweight/textures/gui/inventory_overload.png
assets/inventoryweight/textures/gui/inventory_strength.png
assets/inventoryweight/textures/gui/inventory_filled/inventory_filled_1.png
...
assets/inventoryweight/textures/gui/inventory_filled/inventory_filled_12.png
```

### Filled Icon Stages

The sprite display has 12 fill stages.

The filled icon is chosen based on:

```text
current weight / max weight
```

As the player's carried weight increases, the displayed icon changes from low-fill to high-fill.

### Overload Icon

If the player reaches or exceeds max weight, the overload icon is shown.

```text
current weight >= max weight
```

This tells the player they are overloaded and may receive penalties.

### Strength/Haste Overlay

If the player has Strength or Haste, the HUD can display an additional overlay icon.

These effects can reduce the effective overload level.

## Bar HUD

The `BAR` style uses a simple rectangular progress bar.

```toml
hudStyle = "BAR"
```

The bar fill shows the current weight percentage.

Color behavior:

| Weight Percentage | Color Meaning |
| --- | --- |
| below 50% | light load |
| 50% to 79% | moderate load |
| 80% to 99% | near capacity |
| 100% or more | overloaded |

When overloaded, the bar uses a red border.

## HUD Position

The HUD position is controlled by:

```toml
hudPosition = "BOTTOM_RIGHT"
```

Available positions:

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

## Custom Position

When using:

```toml
hudPosition = "CUSTOM"
```

these values are used:

```toml
xOffset = 0.5
yOffset = 0.5
```

The offsets are screen percentages:

- `0.0` means left/top
- `0.5` means center
- `1.0` means right/bottom

Example center position:

```toml
hudPosition = "CUSTOM"
xOffset = 0.5
yOffset = 0.5
```

## Sprite Size

Sprite HUD size is controlled by:

```toml
spriteSize = 16
```

The source textures are 16x16, but the HUD can scale them.

Example larger icon:

```toml
spriteSize = 24
```

## Bar Size

Bar HUD size is controlled by:

```toml
barWidth = 82
barHeight = 10
```

These only affect the HUD when:

```toml
hudStyle = "BAR"
```

## HUD Text

The HUD can also show text near the icon or bar.

Text mode is controlled by:

```toml
hudTextMode = "CURRENT_MAX"
```

Available modes:

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

| Mode | Example |
| --- | --- |
| `NONE` | no text |
| `CURRENT` | `45.0k` |
| `MAX` | `90.0k` |
| `CURRENT_MAX` | `45.0k/90.0k` |
| `PERCENT` | `50%` |
| `REMAINING` | `45.0k` |
| `CURRENT_MAX_PERCENT` | `45.0k/90.0k (50%)` |

## HUD Text Position

Text position is controlled by:

```toml
hudTextPosition = "BELOW"
```

Available positions:

```text
BELOW
ABOVE
LEFT
RIGHT
INSIDE
CUSTOM
```

## HUD Text Offset

Text can be fine-tuned with pixel offsets:

```toml
hudTextXOffset = 0
hudTextYOffset = 0
```

These offsets apply to every text position.

When `hudTextPosition` is `CUSTOM`, these values act as the custom text offset relative to the HUD element.

## Keeping Text On Screen

The HUD can prevent text from going off-screen:

```toml
keepHudTextOnScreen = true
```

When enabled:

- text below the HUD flips above if it would leave the bottom of the screen
- text above the HUD flips below if it would leave the top
- text left of the HUD flips right if it would leave the left side
- text right of the HUD flips left if it would leave the right side
- final text position is clamped inside the screen

This is especially useful with the sprite HUD near screen corners.

## Text Color and Shadow

Text color:

```toml
hudTextColor = 16777215
```

`16777215` is white (`0xFFFFFF`).

Text shadow:

```toml
hudTextShadow = true
```

## Example: Classic Sprite HUD

```toml
hudStyle = "SPRITE"
hudPosition = "BOTTOM_RIGHT"
spriteSize = 16
hudTextMode = "CURRENT_MAX"
hudTextPosition = "ABOVE"
keepHudTextOnScreen = true
```

## Example: Simple Bar HUD

```toml
hudStyle = "BAR"
hudPosition = "HOTBAR_RIGHT"
barWidth = 82
barHeight = 10
hudTextMode = "PERCENT"
hudTextPosition = "ABOVE"
```

## Disabling HUD Text

To show only the icon/bar:

```toml
hudTextMode = "NONE"
```

## Related Pages

- [Client Configuration](../options/inventory_weights_client.md)
- [Maximum Weight](./max_weight.md)
- [Overload Effect](./overload_effect.md)
