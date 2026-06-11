---
title: "Default Item Values"
description: "How default item weights are calculated in MT Inventory Weight and how server category values, datapacks, and automatic fallback logic interact."
---

# Default Item Values

MT Inventory Weight uses default item values when an item does not have a specific datapack or add-on override.

The current system is **data-driven** and configuration-driven:

- broad category values are configured in the server config
- specific item overrides are configured with datapacks
- automatic fallback logic handles most vanilla and modded items
- add-ons can provide custom logic through the API

::: warning
The old `InventoryWeightUtil` static-value workflow and old item JSON config system are no longer the main configuration method.

Specific item weights should be configured through datapacks, not through `inventory_weights_items.json` or item-specific TOML config files.
:::

## Server Category Defaults

Default category values are configured in:

```text
config/inventoryweight/server-config.toml
```

Current default values:

| Setting | Default | Description |
| --- | ---: | --- |
| `bucketWeight` | `120.0` | Bucket-like items |
| `bottleWeight` | `60.0` | Bottles and potions |
| `blockWeight` | `240.0` | Base block item weight |
| `ingotWeight` | `90.0` | Ingots, gems, alloys, and shards |
| `nuggetWeight` | `10.0` | Nuggets |
| `itemWeight` | `50.0` | Generic fallback item weight |
| `creativeWeight` | `30000.0` | Creative/technical items |
| `pocketWeight` | `9000.0` | Capacity added by one armor pocket |
| `maxWeight` | `90000.0` | Base player max weight |

These values can be edited with fzzy_config and can update live while the server is running.

## Item Category Detection

The mod classifies items into categories based on the item ID and item type.

Examples:

- IDs containing `bucket` use `bucketWeight`
- IDs containing `bottle` or `potion` use `bottleWeight`
- IDs containing `ingot`, `alloy`, `gem`, or `shard` use `ingotWeight`
- IDs containing `nugget` use `nuggetWeight`
- block items use `blockWeight`
- creative/technical items use `creativeWeight`
- everything else uses `itemWeight`

## Default Item Weight Calculation

For regular items, the mod can consider:

- category base weight
- max stack size
- food value
- saturation
- snack status
- fireproof status
- durability
- armor protection
- tool durability
- rarity

The result is floored and clamped to at least `1.0`.

## Default Block Weight Calculation

For blocks, the mod can consider:

- base block weight
- block hardness
- blast resistance
- opacity/transparency
- block entity status
- slab shape
- stair shape
- rarity

This gives blocks more reasonable automatic weights without requiring every block to be listed manually.

## Creative/Technical Items

Some items are treated as creative/technical items and use `creativeWeight`.

Examples can include:

- barrier
- light
- structure block
- jigsaw
- command blocks
- structure void
- portal-like items
- debug stick
- spawner
- spawn eggs
- bedrock

## Containers

Supported shulkers and backpacks use a special rule:

```text
final container weight = empty container weight + weight inside / 2
```

The empty container weight normally uses `itemWeight`.

Tooltip example:

```text
Weight inside: 10.0k
Weight: 5.1k
```

## Override Order

The mod checks weight providers by priority.

Typical order:

1. NBT-specific datapack rules
2. datapack item overrides
3. backpack/container providers
4. shulker box provider
5. vanilla block calculation
6. vanilla item calculation
7. generic fallback

Add-ons can register custom providers with their own priorities.

## Customizing Defaults

Use the server config to adjust broad category weights:

```toml
bucketWeight = 120.0
bottleWeight = 60.0
blockWeight = 240.0
ingotWeight = 90.0
nuggetWeight = 10.0
itemWeight = 50.0
creativeWeight = 30000.0
```

Use datapacks for exact item weights:

```text
data/<namespace>/inventory_weight/items/*.json
```

Example:

```json
{
  "minecraft:stone": 260.0,
  "minecraft:diamond": 120.0
}
```

## Related Pages

- [Server Configuration](../options/inventory_weights_server.md)
- [Items Configuration](../options/inventory_weights_items.md)
- [Custom Item Weights](./item_custom_values.md)
- [Datapack Customization](../сompatibilities/datapacks.md)
