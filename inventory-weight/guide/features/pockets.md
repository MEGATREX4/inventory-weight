---
title: "Custom Tooltips"
description: "Overview of the custom tooltips added by MT Inventory Weight, including item weight, container weight, exact values, and armor pocket information."
---

# Custom Tooltips

MT Inventory Weight adds client-side item tooltips that help players understand how each item affects their carried weight.

Tooltips are calculated using the same weight system as the HUD and overload logic. On multiplayer servers, the client receives synced server/datapack data so tooltip values match the server.

## Enabling or Disabling Tooltips

Tooltips can be enabled or disabled in the client config:

```toml
showTooltips = true
```

The client config is usually located at:

```text
config/inventoryweight/client-config.toml
```

If [Mod Menu](https://modrinth.com/mod/modmenu) is installed, this can usually be changed from the in-game config screen.

## Regular Item Tooltips

For most items, the tooltip shows:

```text
Weight: %s
```

Example:

```text
Weight: 120
```

If the item stack has more than one item, the tooltip can also show the stack's total weight:

```text
Total Weight: %s
```

Example:

```text
Weight: 120
Total Weight: 7.7k
```

## Modified/Base Weight Tooltips

Some items have a calculated weight that differs from their base weight because of modifiers such as:

- rarity
- durability
- food value
- block hardness
- blast resistance
- fireproof status
- container compression
- other provider/add-on logic

When a weight has modifiers, the tooltip can show:

```text
Base Weight: %s
```

Example:

```text
Weight: 450
Base Weight: 240
```

## Shulker and Backpack Tooltips

Supported shulkers and backpacks have special container tooltips.

They show both the full weight of the contents and the effective compressed weight used by the mod.

Example:

```text
Weight inside: 10.0k
Weight: 5.1k
```

The current container rule is:

```text
final container weight = empty container weight + weight inside / 2
```

Where:

- **Weight inside** is the full uncompressed weight of stored items.
- **Weight** is the effective weight used by the inventory weight system.

For stacked containers, the tooltip can also show:

```text
Total weight inside: %s
Total Weight: %s
```

::: info
Containers are not weightless. They reduce the effective weight of stored items, but the stored items still count.
:::

## Armor Pocket Tooltips

Armor and items with pocket data can show pocket information:

```text
Pockets: %s
```

Example:

```text
Pockets: 3
```

Pockets add extra maximum carry capacity when the armor is worn.

The capacity added is:

```text
pockets * pocketWeight
```

The `pocketWeight` value is configured on the server.

See the [Pockets](./pockets.md) page for more information.

## Compact and Exact Values

By default, large values are displayed in compact form.

Examples:

```text
1.5k
2.0M
3.4B
```

Hold **Shift** to show exact numeric values.

Example without Shift:

```text
Weight: 1.5k
```

Example with Shift:

```text
Weight: 1,500
```

When exact values are available, the tooltip can show a hint:

```text
Hold Shift for exact values
```

## Where Tooltip Values Come From

Tooltip values can come from several sources:

1. Datapack item weight definitions
2. NBT-specific datapack rules
3. Add-on API providers
4. Built-in shulker/backpack providers
5. Built-in vanilla block/item fallback calculation
6. Server category weights from fzzy_config

## Datapack Sync

On multiplayer servers, item weights and pocket definitions are controlled by the server.

The server syncs resolved datapack/config data to clients so tooltip values are accurate.

If tooltips look wrong, try:

- reconnecting to the server
- using `/reload`
- checking for datapack conflicts
- checking server logs for sync messages

## Customizing Tooltip Behavior

Tooltip visibility is controlled by client config:

```toml
showTooltips = true
```

Specific item weights should be customized with datapacks:

```text
data/<namespace>/inventory_weight/items/*.json
```

Pocket values should be customized with datapacks:

```text
data/<namespace>/inventory_weight/pockets/*.json
```

For more details, see:

- [Inventory Weights Items Configuration](../options/inventory_weights_items.md)
- [Pockets](./pockets.md)
- [Datapack Customization](../compatibilities/datapacks.md)
