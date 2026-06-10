---
title: "Commands"
description: "A guide to MT Inventory Weight admin commands for setting base weight, capacity bonuses, reading player weight data, and debugging item or armor weights."
---

# Commands

MT Inventory Weight provides server commands for administrators and operators.

Commands can be used to:

- change the global base max weight
- set player capacity bonuses
- inspect current player weight
- inspect final max weight
- debug item weights
- debug armor pocket values

::: warning
All commands listed here require permission level `4` by default.
:::

## `/inventoryweight`

Main command for managing inventory weight values.

## Set Commands

### `/inventoryweight set base <value>`

Sets the global base maximum weight from the server config.

Example:

```text
/inventoryweight set base 120000
```

This changes:

```toml
maxWeight = 120000.0
```

The change is applied live and synced to clients.

::: info
This is a global server value, not a per-player base value.
:::

### `/inventoryweight set bonus <value>`

Sets your own additive capacity bonus.

Example:

```text
/inventoryweight set bonus 10000
```

This adds `10000` to your final max weight.

### `/inventoryweight set bonus <player> <value>`

Sets another player's additive capacity bonus.

Example:

```text
/inventoryweight set bonus Steve 10000
```

This adds `10000` to Steve's final max weight.

::: warning
Older versions used the word `multiplier`, but the current command is `bonus`.

This value is additive. It does not multiply max weight.
:::

## Get Commands

### `/inventoryweight get base`

Gets the global base maximum weight.

Example:

```text
/inventoryweight get base
```

### `/inventoryweight get bonus`

Gets your own additive capacity bonus.

Example:

```text
/inventoryweight get bonus
```

### `/inventoryweight get bonus <player>`

Gets another player's additive capacity bonus.

Example:

```text
/inventoryweight get bonus Steve
```

### `/inventoryweight get combined`

Gets your final maximum weight.

Example:

```text
/inventoryweight get combined
```

This value includes:

- global base max weight
- player capacity bonus
- armor pocket capacity
- add-on capacity providers
- max-weight event modifiers

### `/inventoryweight get combined <player>`

Gets another player's final maximum weight.

Example:

```text
/inventoryweight get combined Steve
```

### `/inventoryweight get value`

Gets your current carried inventory weight.

Example:

```text
/inventoryweight get value
```

### `/inventoryweight get value <player>`

Gets another player's current carried inventory weight.

Example:

```text
/inventoryweight get value Steve
```

## Debug Commands

## `/debugweight`

Displays the calculated weight of the item in your main hand.

Example:

```text
/debugweight
```

This is useful for checking:

- datapack item weights
- automatic fallback weights
- NBT-specific rules
- shulker/backpack effective weight
- modded item weights
- add-on provider behavior

Example output:

```text
minecraft:diamond_sword weighs 350
```

## `/debugarmor`

Displays armor pocket information for your equipped armor.

Example:

```text
/debugarmor
```

This can show each armor item's pocket count and the total armor capacity bonus.

Example output:

```text
minecraft:diamond_chestplate: 2 pockets
Total armor capacity bonus: 18000
```

If no armor with pockets is found, the command tells the player.

## Common Usage Scenarios

### Increase Carry Capacity for Everyone

```text
/inventoryweight set base 150000
```

This raises the global base max weight.

### Give One Player Extra Capacity

```text
/inventoryweight set bonus Steve 25000
```

This gives Steve an extra `25000` max weight.

### Check a Player's Final Max Weight

```text
/inventoryweight get combined Steve
```

### Check How Heavy an Item Is

Hold the item in your main hand and run:

```text
/debugweight
```

### Check Armor Pockets

Equip armor and run:

```text
/debugarmor
```

## Current Maximum Weight Formula

The final maximum weight is generally:

```text
server config maxWeight + max-weight attribute value + player capacity bonus + armor pocket capacity + add-on modifiers
```

See [Maximum Weight](./max_weight.md) for more details.

## Server Config and Live Updates

The base max weight is also stored in the server config:

```text
config/inventoryweight/server-config.toml
```

Changing it through fzzy_config or commands applies live while the server is running.

## Permissions

By default, commands require permission level `4`, which usually means server operator/admin.

This prevents normal players from changing weight balance or inspecting other players' data.

## Related Pages

- [Maximum Weight](./max_weight.md)
- [Server Configuration](../options/inventory_weights_server.md)
- [Default Item Values](./item_default_values.md)
- [Pockets](./pockets.md)
