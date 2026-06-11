---
title: "Inventory Weights Items Configuration"
description: "Learn how to customize item weights for MT Inventory Weight using datapacks, category config values, NBT rules, and add-ons."
---

# Inventory Weights Items Configuration

::: warning
MT Inventory Weight no longer uses the old `ItemWeightConfigItems` class, the old `inventory_weights_items.json` file, or any item-specific `inventory_weights_items.toml` config file.

Item-specific weights are now handled by a **data-driven datapack system**.

If you had custom item weights in an old JSON/TOML config, you must migrate them into datapack JSON files.
:::

Specific item weight overrides are now handled with **datapacks**.

General item category weights are handled by the **server config** through fzzy_config.

## Ways to Customize Item Weights

There are four main ways to customize weights:

1. Server config category weights
2. Datapack item overrides
3. Datapack NBT-specific rules
4. Add-ons using the public API

## Server Category Weights

Broad item categories are configured in:

```text
config/inventoryweight/server-config.toml
```

These values control default/fallback weights for categories such as:

- buckets
- bottles and potions
- blocks
- ingots, gems, alloys, and shards
- nuggets
- generic items
- creative/technical items

For full details, see the [Server Configuration](./inventory_weights_server.md) page.

## Datapack Item Weights

Specific item weights should be configured through datapacks.

Item weight files go here:

```text
data/<namespace>/inventory_weight/items/*.json
```

Example file:

```text
data/my_pack/inventory_weight/items/vanilla_overrides.json
```

Example content:

```json
{
  "minecraft:stone": 260.0,
  "minecraft:diamond": {
    "weight": 120.0
  }
}
```

In this example:

- `minecraft:stone` has a weight of `260.0`
- `minecraft:diamond` has a weight of `120.0`

## Array Format

Datapacks can also use an array format:

```json
[
  {
    "item": "minecraft:stone",
    "weight": 260.0
  },
  {
    "item": "minecraft:diamond",
    "weight": 120.0
  }
]
```

## NBT-Specific Item Weights

NBT-specific rules allow different weights depending on item NBT.

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

This means a bundle with matching NBT data can receive a special weight.

Depending on the NBT structure, lists and string values can be checked.

## Loading and Reloading

Datapack item weights are loaded:

- when the server starts
- when `/reload` is used

The server syncs resolved item weight data to clients so tooltips show the same values as the server.

## Conflict Handling

If the same item is defined in multiple datapack files, MT Inventory Weight logs a warning.

The first loaded definition is kept.

::: warning
Avoid defining the same item weight in multiple datapacks unless you know which datapack load order will win.
:::

## Containers and Backpacks

Supported shulkers and backpacks use special weight handling.

Current rule:

```text
final container weight = empty container weight + weight inside / 2
```

Tooltips show:

```text
Weight inside: %s
Weight: %s
```

`Weight inside` is the full uncompressed contents weight.

`Weight` is the effective weight used by the player weight system.

## Default Automatic Weight Calculation

If an item does not have a datapack override, MT Inventory Weight calculates a fallback weight automatically.

The default calculation can consider:

- item category
- max stack size
- food value and saturation
- fireproof status
- durability
- armor protection
- tool durability
- item rarity
- block hardness
- block blast resistance
- block transparency
- slabs and stairs
- block entities

This allows most vanilla and modded items to work without manual configuration.

## Add-on API

Mods can provide custom item weight logic through the public add-on API:

```text
com.megatrex4.api.v1
```

Add-ons can register an `ItemWeightProvider` to calculate weights dynamically from item data, NBT, capabilities, or another mod's API.

See the [Add-on API](./compatibilities/addon-api.md) page for examples.

## Example Datapack Structure

```text
my_weight_pack/
├─ pack.mcmeta
└─ data/
   └─ my_weight_pack/
      └─ inventory_weight/
         └─ items/
            └─ item_weights.json
```

Example `pack.mcmeta`:

```json
{
  "pack": {
    "pack_format": 15,
    "description": "Custom MT Inventory Weight item weights"
  }
}
```

Example `item_weights.json`:

```json
{
  "minecraft:iron_ingot": 100.0,
  "minecraft:diamond": 200.0,
  "minecraft:obsidian": 1200.0
}
```

## Old Config Migration

Old versions used item-specific config files such as:

```text
config/inventoryweight/inventory_weights_items.json
```

Some experimental or transitional builds may also have used item-specific TOML-style config files.

::: warning
These old item-specific config files are no longer read by MT Inventory Weight.

The item weight system is now data-driven. Specific item weights must be defined through datapacks in:

```text
data/<namespace>/inventory_weight/items/*.json
```
:::

To migrate old custom item weights, move them into a datapack item weight file.
