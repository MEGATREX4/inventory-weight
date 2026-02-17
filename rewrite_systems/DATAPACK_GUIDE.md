# Datapack Structure - Inventory Weight Mod

This document explains the datapack structure for the Inventory Weight mod, which now uses a datapack-based system for defining item and pocket weights instead of config files.

## Overview

The mod looks for JSON configuration files in the `data/inventory_weight/` directory within your datapack. This directory should contain two main subdirectories:

```
data/
  inventory_weight/
    items/           # Item weight definitions
    pockets/         # Armor pocket definitions
```

## Item Weights

Item weights are defined in JSON files located in `data/inventory_weight/items/`.

### Directory Structure

You can organize item weight files into any folder structure you prefer. For example:

```
data/inventory_weight/items/
  tools/
    diamond.json
    iron.json
  weapons/
    swords.json
    axes.json
  custom_items/
    ammunition.json
```

### File Formats

#### Object Format

```json
{
  "minecraft:diamond_pickaxe": 45.5,
  "minecraft:diamond_axe": 42.0
}
```

#### Array Format

```json
{
  "items": [
    {
      "item": "minecraft:diamond_pickaxe",
      "weight": 45.5
    },
    {
      "item": "minecraft:diamond_axe",
      "weight": 42.0
    }
  ]
}
```

Or without wrapper key:

```json
[
  {
    "item": "minecraft:diamond_pickaxe",
    "weight": 45.5
  },
  {
    "item": "minecraft:diamond_axe",
    "weight": 42.0
  }
]
```

### NBT-Based Weights

For items with NBT data (like custom ammunition where weight depends on NBT values), use the `weightWhenNbt` format:

```json
[
  {
    "item": "examplemod:ammo_bag",
    "weightWhenNbt": {
      "nbtKey": "ammoType",
      "values": {
        "9mm": 5.5,
        "20mm": 12.0,
        "default": 2.5
      }
    }
  }
]
```

The structure works as follows:
- `item`: The item ID to apply weights to
- `nbtKey`: The NBT tag key to check for
- `values`: A map of NBT values to their corresponding weights
- `default`: The weight to use if the NBT key doesn't match any of the specified values

### Conflict Detection

If the same item is defined in multiple JSON files, the mod will:
1. Use the weight from the first file that defined it
2. Log a warning message to the server console identifying the conflict
3. Example: `Conflict detected for item 'minecraft:diamond_pickaxe': previously defined in inventory_weight/items/tools/diamond.json, now in inventory_weight/items/weapons/common.json. Keeping first definition.`

## Pocket Weights

Armor pocket weights are defined in JSON files located in `data/inventory_weight/pockets/`.

### Directory Structure

```
data/inventory_weight/pockets/
  armor/
    diamond.json
    iron.json
  custom_armor/
    modded_armor.json
```

### File Formats

#### Object Format

```json
{
  "minecraft:diamond_helmet": 3,
  "minecraft:diamond_chestplate": 8,
  "minecraft:diamond_leggings": 5,
  "minecraft:diamond_boots": 2
}
```

#### Array Format

```json
{
  "items": [
    {
      "item": "minecraft:diamond_helmet",
      "pockets": 3
    },
    {
      "item": "minecraft:diamond_chestplate",
      "pockets": 8
    }
  ]
}
```

Or without wrapper key:

```json
[
  {
    "item": "minecraft:diamond_helmet",
    "pockets": 3
  },
  {
    "item": "minecraft:diamond_chestplate",
    "pockets": 8
  }
]
```

### Default Pocket Calculation

If an armor piece is not defined in any datapack JSON file, the mod will automatically calculate pockets based on:
- Protection value
- Toughness value
- Formula: `max(1, 7 - floor(protection / 1.2) - toughness)`

## Conflict Detection for Pockets

Similar to items, if the same armor piece is defined in multiple JSON files:
1. The definition from the first file is used
2. A warning is logged to the server console
3. Example: `Conflict detected for pocket definition 'minecraft:diamond_helmet': previously defined in inventory_weight/pockets/armor/diamond.json, now in inventory_weight/pockets/custom/myarmor.json. Keeping first definition.`

## Complete Example Datapack

Here's a complete example of a datapack structure:

```
mypack/
  pack.mcmeta
  data/
    inventory_weight/
      items/
        tools/
          diamond.json
          iron.json
        weapons/
          swords.json
      pockets/
        armor/
          diamond.json
          iron.json
```

### Example pack.mcmeta

```json
{
  "pack": {
    "pack_format": 26,
    "description": "Custom weights for Inventory Weight Mod"
  }
}
```

## Server Configuration

Global settings like maximum weight, pocket weight multiplier, and realistic mode are still configured in the server config file:
- Location: `config/inventoryweight/inventory_weights_server.json`

This file controls:
- `maxWeight`: Maximum inventory weight allowed
- `pocketWeight`: Weight multiplier for armor pockets
- `realisticMode`: Whether realistic weight penalties apply
- `overloadPenaltyStrength`: Strength of overload effects

## Reloading Datapacks

You can reload datapacks in-game using the command:
```
/reload
```

The mod will automatically reload item and pocket weights from the datapack when using this command.

## Troubleshooting

### Items don't have the expected weight
1. Check the server console for conflict warnings
2. Verify the JSON file is in the correct directory
3. Make sure you used the correct item ID (e.g., `minecraft:diamond_pickaxe`)
4. Check JSON syntax is valid (no trailing commas, proper quotes)

### Datapack not loading
1. Verify the datapack is enabled in server settings
2. Check the directory structure matches exactly: `data/inventory_weight/items/` or `data/inventory_weight/pockets/`
3. Ensure JSON files are valid (use JSONLint if unsure)
4. Check server logs for error messages

### Default weights are being used
1. The item might be in a conflict state - check the console for warnings
2. The datapack might not be loaded - try using `/reload`
3. The file might not be found - verify the path and filename are correct
