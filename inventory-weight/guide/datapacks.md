---
title: "MT Inventory Weight Datapacks"
description: "How to customize MT Inventory Weight item weights and pocket values with datapacks."
---

# Datapack Customization

MT Inventory Weight supports datapacks for item weights and armor pocket values.

## Item Weights

Item weight files go here:

```text
data/<namespace>/inventory_weight/items/*.json
```

Example:

```json
{
  "minecraft:stone": 260.0,
  "minecraft:diamond": {
    "weight": 120.0
  }
}
```

## NBT Item Weights

NBT-specific weight example:

```json
{
  "minecraft:bundle": {
    "weightWhenNbt": {
      "Items": {
        "value": "minecraft:glass",
        "weight": 100.0
      }
    }
  }
}
```

## Pockets

Pocket files go here:

```text
data/<namespace>/inventory_weight/pockets/*.json
```

Example:

```json
{
  "minecraft:leather_chestplate": 6,
  "minecraft:diamond_chestplate": {
    "pockets": 2
  }
}
```

## NBT Pockets

NBT-specific pocket example:

```json
{
  "minecraft:diamond_chestplate": {
    "pocketsWhenNbt": {
      "SomeKey": {
        "value": "some_value",
        "pockets": 5
      }
    }
  }
}
```

## Reloading

Datapack data is loaded:

- when the server starts
- when `/reload` is used

The server syncs resolved datapack data to clients so tooltips show server-correct values.
