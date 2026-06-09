# NBT-Based Pocket Configuration Guide

## Overview

You can now define different pocket counts for armor items based on their NBT data (enchantments, custom NBT tags, etc.). This allows enchanted armor to have different inventory capacities than standard armor.

## Use Cases

- **Enchanted armor has more/fewer pockets** - Deeply enchanted armor could have enhanced pocket space
- **Special enchantments provide benefits** - Certain enchantments could increase pocket capacity
- **Cursed items have penalties** - Cursed armor could reduce pocket count
- **Custom NBT-tagged items** - Mods that add custom NBT data can have custom pocket definitions

## Format

### NBT-Based Pocket Definition (Object Format)

```json
{
  "minecraft:diamond_chestplate": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:unbreaking",
          "pockets": 5
        },
        {
          "value": "minecraft:protection",
          "pockets": 6
        }
      ]
    }
  }
}
```

### Mixed Format (Standard + NBT)

```json
{
  "minecraft:diamond_helmet": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:protection",
          "pockets": 5
        }
      ]
    }
  },
  "minecraft:iron_helmet": 2
}
```

## How It Works

### Priority Order

When checking pocket count for armor, the system checks in this order:

1. **NBT-specific definition** - If armor has matching enchantment, use NBT pocket count
2. **Standard datapack definition** - If no NBT match but item defined in datapack, use that
3. **Default calculation** - Falls back to calculated value based on protection level

### Example: Unbreaking Enchantment

```json
{
  "minecraft:diamond_chestplate": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:unbreaking",
          "pockets": 5
        }
      ]
    }
  }
}
```

A diamond chestplate with Unbreaking will have **5 pockets**.
A diamond chestplate without any enchantment will use the **default calculation**.

## NBT Key Reference

### Minecraft Enchantments

Use `"Enchantments"` as the NBT key to check for specific enchantments applied to armor:

```json
{
  "Enchantments": [
    {
      "value": "minecraft:protection",
      "pockets": 6
    },
    {
      "value": "minecraft:unbreaking",
      "pockets": 5
    },
    {
      "value": "minecraft:thorns",
      "pockets": 4
    }
  ]
}
```

### Common Enchantments

| Enchantment | ID |
|---|---|
| Protection | `minecraft:protection` |
| Fire Protection | `minecraft:fire_protection` |
| Feather Falling | `minecraft:feather_falling` |
| Blast Protection | `minecraft:blast_protection` |
| Projectile Protection | `minecraft:projectile_protection` |
| Respiration | `minecraft:respiration` |
| Aqua Affinity | `minecraft:aqua_affinity` |
| Thorns | `minecraft:thorns` |
| Unbreaking | `minecraft:unbreaking` |
| Mending | `minecraft:mending` |

## Complete Example

File: `data/inventory_weight/pockets/armor_enchanted.json`

```json
{
  "minecraft:diamond_helmet": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:unbreaking",
          "pockets": 4
        },
        {
          "value": "minecraft:protection",
          "pockets": 5
        },
        {
          "value": "minecraft:respiration",
          "pockets": 3
        }
      ]
    }
  },
  "minecraft:diamond_chestplate": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:unbreaking",
          "pockets": 5
        },
        {
          "value": "minecraft:protection",
          "pockets": 6
        },
        {
          "value": "minecraft:thorns",
          "pockets": 4
        }
      ]
    }
  },
  "minecraft:diamond_leggings": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:unbreaking",
          "pockets": 5
        },
        {
          "value": "minecraft:protection",
          "pockets": 6
        }
      ]
    }
  },
  "minecraft:diamond_boots": {
    "pocketsWhenNbt": {
      "Enchantments": [
        {
          "value": "minecraft:feather_falling",
          "pockets": 3
        },
        {
          "value": "minecraft:unbreaking",
          "pockets": 3
        }
      ]
    }
  }
}
```

## Edge Cases

### Multiple Enchantments

If armor has multiple enchantments and only one matches the datapack definition, the first matching enchantment is used:

```json
{
  "Enchantments": [
    {
      "value": "minecraft:protection",    // Checked first
      "pockets": 6
    },
    {
      "value": "minecraft:unbreaking",    // Checked if protection not found
      "pockets": 5
    }
  ]
}
```

### No Matching NBT

If armor has enchantments but none match your datapack definition, the system falls back to:
1. Standard pocket definition (if defined)
2. Default calculation

### Conflict Handling

If the same item is defined with NBT in multiple files, a warning is logged and the first definition is kept (same as item weights).

## API Usage

If you need to check pockets programmatically:

```java
import com.megatrex4.InventoryWeightArmor;

ItemStack armor = ...;
int pockets = InventoryWeightArmor.getPocketsWithNbtCheck(armor);
```

This will check:
1. NBT-specific pockets
2. Standard datapack definition
3. Default calculation

## Performance Notes

- NBT checks only happen for armor items
- NBT lookups are cached in memory after loading
- No performance impact on non-armor items
