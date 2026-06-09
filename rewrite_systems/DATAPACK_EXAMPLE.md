# Inventory Weight Datapack Structure

This document explains how to create datapacks for the Inventory Weight mod.

## Directory Structure

```
datapacks/
└── my_weights/
    └── data/
        └── inventory_weight/
            ├── items/
            │   ├── tools/
            │   │   ├── diamond.json
            │   │   └── diamond_axe.json
            │   └── armor/
            │       └── custom_armor.json
            └── pockets/
                ├── armor.json
                └── custom_pockets.json
```

## File Formats

### Item Weights Format

You can use either object format with single weight, or array format for multiple items.

#### Option 1: Simple Format (Object with weight)
```json
{
  "minecraft:diamond_sword": 5.5,
  "minecraft:diamond_pickaxe": 6.0,
  "minecraft:diamond_axe": 5.8
}
```

#### Option 2: Array Format
```json
{
  "items": [
    {
      "item": "minecraft:diamond_sword",
      "weight": 5.5
    },
    {
      "item": "minecraft:iron_sword",
      "weight": 3.2
    }
  ]
}
```

#### Option 3: NBT-based Weight (for items with NBT data)
```json
{
  "modded_item:ammo": {
    "weightWhenNbt": [
      {
        "nbtPath": "AmmoType",
        "value": "9mm",
        "weight": 0.1
      },
      {
        "nbtPath": "AmmoType",
        "value": "20mm",
        "weight": 0.25
      }
    ]
  }
}
```

### Pocket Weights Format

Define the number of pockets for armor items.

#### Simple Format
```json
{
  "minecraft:diamond_helmet": 3,
  "minecraft:diamond_chestplate": 4,
  "minecraft:iron_helmet": 2
}
```

#### Array Format
```json
{
  "pockets": [
    {
      "item": "minecraft:diamond_helmet",
      "pockets": 3
    },
    {
      "item": "minecraft:diamond_chestplate",
      "pockets": 4
    }
  ]
}
```

## Conflict Resolution

If the same item is defined in multiple files, the mod will:
1. Log a warning showing which files conflict
2. Keep the first definition encountered
3. Use default calculation for subsequent conflicting definitions

### Example Warning:
```
[WARN] Conflict detected for item 'minecraft:diamond_axe': 
previously defined in data/inventory_weight/items/tools/diamond.json, 
now in data/inventory_weight/items/tools/diamond_axe.json. 
Keeping first definition.
```

## Best Practices

1. **Organize by category**: Use subfolders like `tools/`, `armor/`, `food/`, `blocks/` for better organization
2. **Use descriptive names**: Name files after the items they contain
3. **Avoid conflicts**: Don't define the same item in multiple files unless intentional
4. **Use consistent formatting**: Choose one format and stick with it
5. **Document custom entries**: Add comments (in JSON objects) or use a README

## Example Complete Datapack

### `datapacks/my_weights/data/inventory_weight/items/tools.json`
```json
{
  "minecraft:diamond_sword": 5.5,
  "minecraft:diamond_pickaxe": 6.0,
  "minecraft:diamond_axe": 5.8,
  "minecraft:iron_sword": 3.2,
  "minecraft:iron_pickaxe": 3.5,
  "modname:custom_tool": 7.2
}
```

### `datapacks/my_weights/data/inventory_weight/pockets/armor.json`
```json
{
  "minecraft:diamond_helmet": 3,
  "minecraft:diamond_chestplate": 4,
  "minecraft:diamond_leggings": 4,
  "minecraft:diamond_boots": 2,
  "minecraft:iron_helmet": 2,
  "minecraft:iron_chestplate": 3,
  "minecraft:iron_leggings": 3,
  "minecraft:iron_boots": 1
}
```

## Loading Order

Datapacks are loaded in the following order:
1. When server starts (`SERVER_STARTING` event)
2. When datapacks are reloaded (`START_DATA_PACK_RELOAD` event)

All files are scanned in the `inventory_weight/items/` and `inventory_weight/pockets/` directories.
