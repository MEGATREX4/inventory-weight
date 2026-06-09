---
title: "Inventory Weights Server Configuration"
description: "Detailed guide to server-side configuration for MT Inventory Weight using fzzy_config."
---

# Inventory Weights Server Configuration

MT Inventory Weight uses **fzzy_config** for server-side configuration.

The old `ItemWeightsConfigServer` class and `inventory_weights_server.json` file are no longer used.

Server settings are now stored in a fzzy_config TOML file, usually located at:

```text
config/inventoryweight/server-config.toml
```

These settings control gameplay behavior and are synced to clients when needed.

## What the Server Config Controls

The server config controls:

- maximum player carry weight
- armor pocket capacity value
- realistic/gradual penalty mode
- overload penalty strength
- default category weights
- fallback weights for many modded items

The server is authoritative for these values in multiplayer.

## Live Updates

Server settings can update while the game/server is running.

When changed through fzzy_config, MT Inventory Weight applies the update live:

- runtime settings are refreshed
- all online players are recalculated
- overload state is updated
- synced player weight data is updated
- clients receive updated server settings for HUD/tooltips

You should see a log similar to:

```text
Applying Inventory Weight server settings live
```

## Configuration Options

### `maxWeight`

Maximum weight a player can carry before they become overloaded.

Default:

```toml
maxWeight = 90000.0
```

If the player's carried weight reaches or exceeds this value, overload penalties can apply.

### `pocketWeight`

Capacity added by one armor pocket.

Default:

```toml
pocketWeight = 9000.0
```

Example:

```text
4 pockets * 9000 = 36000 extra capacity
```

### `realisticMode`

Enables gradual penalties before the player is fully overloaded.

Default:

```toml
realisticMode = false
```

When enabled, players can receive movement/combat penalties as they approach their maximum weight.

### `overloadPenaltyStrength`

Multiplier for overload penalties.

Default:

```toml
overloadPenaltyStrength = 1.0
```

Setting this to `0.0` disables attribute and jump penalties.

### `bucketWeight`

Base weight for bucket-like items.

Default:

```toml
bucketWeight = 120.0
```

### `bottleWeight`

Base weight for bottle and potion-like items.

Default:

```toml
bottleWeight = 60.0
```

### `blockWeight`

Base weight for block items.

Default:

```toml
blockWeight = 240.0
```

Block weight can also be affected by:

- hardness
- blast resistance
- transparency
- block entity status
- slab/stair shape
- rarity

### `ingotWeight`

Base weight for ingots, gems, alloys, and shards.

Default:

```toml
ingotWeight = 90.0
```

### `nuggetWeight`

Base weight for nuggets.

Default:

```toml
nuggetWeight = 10.0
```

### `itemWeight`

Base weight for generic items.

Default:

```toml
itemWeight = 50.0
```

This is also used as the empty base weight for supported containers such as shulkers and backpacks.

### `creativeWeight`

Weight for creative/technical items.

Default:

```toml
creativeWeight = 30000.0
```

This can apply to items such as:

- barrier
- structure block
- command block variants
- bedrock
- spawner
- spawn eggs
- other technical/creative items

## Example Server Config

```toml
maxWeight = 90000.0
pocketWeight = 9000.0
realisticMode = false
overloadPenaltyStrength = 1.0
bucketWeight = 120.0
bottleWeight = 60.0
blockWeight = 240.0
ingotWeight = 90.0
nuggetWeight = 10.0
itemWeight = 50.0
creativeWeight = 30000.0
```

## Gameplay Effects

### Below max weight

Players can move normally.

If `realisticMode` is enabled, gradual penalties may begin before max weight is reached.

### At or above max weight

Players become overloaded.

The overload system can apply:

- movement speed reduction
- attack speed reduction
- attack damage reduction
- jump reduction
- overload status effect

### Strength and Haste

Strength and Haste can reduce the effective overload level.

This gives those effects a useful interaction with the weight system.

## Armor Pockets

Armor can increase carrying capacity through pockets.

Pocket values can come from:

1. NBT-specific pocket rules
2. datapack pocket definitions
3. default armor protection/toughness calculation

The final capacity bonus is:

```text
pocket count * pocketWeight
```

Example:

```text
5 pockets * 9000 = 45000 extra max weight
```

## Custom Item Weights

The server config controls category weights only.

For specific item overrides, use datapacks:

```text
data/<namespace>/inventory_weight/items/*.json
```

See the [Items Configuration](./inventory_weights_items.md) page.

## Custom Pocket Values

Pocket definitions are configured with datapacks:

```text
data/<namespace>/inventory_weight/pockets/*.json
```

Example:

```json
{
  "minecraft:diamond_chestplate": {
    "pockets": 2
  }
}
```

## Commands

Server operators can also change or inspect weight values with commands.

Examples:

```text
/inventoryweight set base <value>
/inventoryweight get base
/inventoryweight get value
/inventoryweight get combined
/inventoryweight set bonus <player> <value>
/inventoryweight get bonus <player>
```

Command changes are applied live and synced to clients.

## Old JSON Config Migration

Old versions used:

```text
config/inventoryweight/inventory_weights_server.json
```

This file is no longer used.

Move old category values into:

```text
config/inventoryweight/server-config.toml
```

Move specific item overrides into datapacks.

## Troubleshooting

### Server config changes do not apply

Check the log for:

```text
Applying Inventory Weight server settings live
```

If it does not appear:

- make sure you changed the server config, not only the client config
- make sure fzzy_config is installed
- try closing the config screen so fzzy_config saves the update
- try `/reload`
- restart the server if necessary

### Clients see wrong tooltip weights

The server syncs datapack/config values to clients.

If tooltips are wrong:

- reconnect to the server
- use `/reload`
- check for datapack conflicts
- check the server log for sync messages

### Values reset after editing manually

Stop the game/server before manually editing config files, or edit through the fzzy_config screen.

Some config systems save on close and can overwrite manual changes made while the game is running.
