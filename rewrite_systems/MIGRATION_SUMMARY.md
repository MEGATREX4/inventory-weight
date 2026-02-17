# Migration Summary - Cloth-Config to Fzzy-Config & Datapack System

## Changes Made

### 1. Dependency Migration (Files Modified: `build.gradle`, `gradle.properties`)
- **Removed**: Cloth-Config dependency (`me.shedaniel.cloth:cloth-config-fabric:11.1.118`)
- **Added**: Fzzy-Config dependency (`me.fzzyhmstrs.fzzy_config:fzzy-config-fabric:0.7.6+1.20.1`)
- **Repository Added**: FzzyMaven repository for fzzy-config

### 2. Item Weight System Overhaul

#### New Classes Created:
- **`DatapackItemWeightLoader.java`**: Loads item weights from datapack JSON files in `inventory_weight/items/`
  - Supports both object and array JSON formats
  - Handles NBT-based weights for complex items
  - Implements conflict detection - warns when same item is defined in multiple files
  - Keeps first definition when conflicts are detected
  
- **`DatapackPocketWeightLoader.java`**: Loads armor pocket definitions from `inventory_weight/pockets/`
  - Similar structure to item loader
  - Supports object and array formats
  - Conflict detection for duplicate armor pieces
  - Falls back to default calculation if not found in datapack

#### Files Modified:
- **`ItemWeightConfigItems.java`**: 
  - Now a stub that delegates to datapack system
  - No longer loads/saves from config files
  - `loadConfig()` and `saveConfig()` return with informational messages
  
- **`ItemWeightsConfigServer.java`**:
  - Removed item weight loading from config
  - Still handles server-level settings (maxWeight, pocketWeight, realisticMode)
  - Maintains backward compatibility for global settings
  
- **`InventoryWeightArmor.java`**:
  - Removed `itemPocketsMap` field
  - Updated `loadDatapackData()` to use new `DatapackPocketWeightLoader`
  - Updated `getPocketsBasedOnProtection()` to query datapack loader first
  
- **`InventoryWeight.java`**:
  - Added import for `DatapackItemWeightLoader`
  - Updated `loadDatapack()` to call new item weight loader
  - Datapacks are now reloaded on both server start and `/reload` command

### 3. Configuration System Evolution

**Old System**:
- Item weights stored in `config/inventoryweight/inventory_weights_items.json`
- Server config in `config/inventoryweight/inventory_weights_server.json`
- Only server admin could modify weights

**New System**:
- Item weights in datapack: `data/inventory_weight/items/**/*.json`
- Pocket definitions in datapack: `data/inventory_weight/pockets/**/*.json`
- Server config still in `config/inventoryweight/inventory_weights_server.json` for global settings
- Multiple datapacks can be used and will merge (with conflict detection)
- Resource packs can include these datapacks easily

### 4. Example Datapack Files

Created example datapack structure:
```
src/main/resources/data/inventory_weight/
  items/
    tools/
      diamond.json (diamond tools weights)
    ammunition/
      custom_ammo.json (NBT-based ammo ammunition weights)
  pockets/
    armor/
      diamond.json (diamond armor pockets)
```

## Datapack Format

### Item Weights
```json
[
  {
    "item": "minecraft:diamond_pickaxe",
    "weight": 45.5
  }
]
```

Or with NBT:
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

### Pocket Weights
```json
[
  {
    "item": "minecraft:diamond_helmet",
    "pockets": 3
  }
]
```

## Error Handling

### Conflict Detection
When the same item is defined in multiple JSON files:
```
[WARN]: Conflict detected for item 'minecraft:diamond_axe': previously defined in inventory_weight/items/tools/diamond.json, now in inventory_weight/items/tools/custom.json. Keeping first definition.
```

### Invalid JSON
- Invalid weights: Error logged, item skipped
- Missing required fields: Error logged, item skipped
- Invalid JSON syntax: File skipped with error message

## Backward Compatibility

- Old config files are left in place but not used
- Server max weight and global settings still work from server config
- Item category defaults (buckets, blocks, etc.) maintained
- NBT weight handling preserved and improved

## Migration Guide for Users

1. **No action required** - The mod works with both old and new systems during transition
2. **To add new item weights**: Create JSON files in `inventory_weight/items/` folder within a datapack
3. **To override default weights**: Different datapack formats can coexist
4. **Reload datapacks**: Use `/reload` command in-game

## File Structure Overview

```
build.gradle                                    ✓ Updated (fzzy-config)
gradle.properties                               ✓ Updated (fzzy_config_version)
src/main/java/com/megatrex4/
  ├── InventoryWeight.java                    ✓ Updated (load datapacks)
  ├── InventoryWeightArmor.java              ✓ Updated (use pocket loader)
  ├── config/
  │   ├── ItemWeightConfigItems.java         ✓ Updated (stub)
  │   └── ItemWeightsConfigServer.java       ✓ Updated (removed item loading)
  ├── datapack/                               ✓ NEW (new package)
  │   ├── DatapackItemWeightLoader.java      ✓ NEW
  │   └── DatapackPocketWeightLoader.java    ✓ NEW
  └── util/
      └── ItemWeights.java                    ✓ Compatible (no changes needed)
src/main/resources/data/inventory_weight/
  ├── items/                                  ✓ NEW (example files)
  │   └── tools/diamond.json
  │   └── ammunition/custom_ammo.json
  └── pockets/                                ✓ NEW (example files)
      └── armor/diamond.json
DATAPACK_GUIDE.md                             ✓ NEW (documentation)
```

## Next Steps

1. Build the project: `./gradlew build`
2. Test the mod with example datapacks included
3. Create custom datapacks following the DATAPACK_GUIDE.md
4. Use `/reload` command to test datapack reloading
