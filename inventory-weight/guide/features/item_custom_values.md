---
title: "Custom Item Weights"
description: "How to set and manage custom item weights in MT Inventory Weight using the new data-driven datapack system."
---

# Custom Item Weights

Custom item weights let modpack makers and server owners override the weight of specific items.

MT Inventory Weight now uses a **data-driven datapack system** for item-specific weights.

::: warning
The old custom item weight config file is no longer used.

Old versions used:

```text
config/inventoryweight/inventory_weights_items.json
```

Some transitional builds may also have used item-specific TOML config files.

These files are no longer read by MT Inventory Weight. Move custom item weights into datapack JSON files instead.
:::

## Where Custom Item Weights Go

Custom item weight datapack files go here:

```text
data/<namespace>/inventory_weight/items/*.json
```

Example:

```text
data/my_weight_pack/inventory_weight/items/custom_weights.json
```

## Simple Object Format

```json
{
  "minecraft:diamond_sword": 350.0,
  "minecraft:gold_ingot": 100.0
}
```

In this example:

- `minecraft:diamond_sword` weighs `350.0`
- `minecraft:gold_ingot` weighs `100.0`

## Object-with-Weight Format

You can also write:

```json
{
  "minecraft:diamond_sword": {
    "weight": 350.0
  },
  "minecraft:gold_ingot": {
    "weight": 100.0
  }
}
```

## Array Format

```json
[
  {
    "item": "minecraft:diamond_sword",
    "weight": 350.0
  },
  {
    "item": "minecraft:gold_ingot",
    "weight": 100.0
  }
]
```

## NBT-Specific Custom Weights

NBT-specific rules allow one item type to have different weights depending on its NBT data.

Example:

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

This can be used for:

- filled containers
- upgraded items
- items with custom modes
- modded items with NBT data
- special variants of the same item

## Example Datapack

Folder structure:

```text
my_weight_pack/
├─ pack.mcmeta
└─ data/
   └─ my_weight_pack/
      └─ inventory_weight/
         └─ items/
            └─ custom_weights.json
```

`pack.mcmeta` for Minecraft 1.20.1:

```json
{
  "pack": {
    "pack_format": 15,
    "description": "Custom MT Inventory Weight item weights"
  }
}
```

`custom_weights.json`:

```json
{
  "minecraft:iron_ingot": 100.0,
  "minecraft:diamond": 200.0,
  "minecraft:obsidian": 1200.0
}
```

## Loading Custom Weights

Datapack item weights are loaded:

- when the server starts
- when `/reload` is used

The server syncs resolved item weight data to clients so tooltips match the server.

## Conflict Handling

If the same item is defined in multiple datapack files, MT Inventory Weight logs a warning.

The first loaded definition is kept.

::: warning
Avoid defining the same item in multiple datapacks unless you intentionally rely on datapack load order.
:::

## Custom Weights vs Default Weights

Custom datapack weights override the automatic default calculation.

If no custom datapack weight exists, the mod falls back to automatic calculation based on:

- item category
- stack size
- durability
- rarity
- food value
- block hardness
- blast resistance
- and other built-in rules

## Custom Weights vs Add-ons

Add-ons can also provide item weights through the API.

Depending on provider priority, an add-on may override or run before datapack weights.

For developer integrations, see:

- [Add-on API](././compatibilities/addon-api.md)

## Migrating from Old Configs

Old config example:

```json
{
  "minecraft:diamond_sword": 100.0,
  "minecraft:gold_ingot": 50.0
}
```

New datapack equivalent:

```json
{
  "minecraft:diamond_sword": 100.0,
  "minecraft:gold_ingot": 50.0
}
```

The JSON object can look similar, but the file must now be inside a datapack path:

```text
data/<namespace>/inventory_weight/items/*.json
```

not inside:

```text
config/inventoryweight/
```

## Related Pages

- [Inventory Weights Items Configuration](../options/inventory_weights_items.md)
- [Datapack Customization](../сompatibilities/datapacks.md)
- [Default Item Values](./item_default_values.md)
