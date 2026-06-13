---
title: "LuckPerms"
description: "LuckPerms InventoryWeight connects LuckPerms rank meta values with MT Inventory Weight, allowing server admins to grant flat, percent, or multiplier-based carry capacity bonuses through LuckPerms meta keys."
---


LuckPerms InventoryWeight is a server-side add-on that connects **LuckPerms** meta values with **MT Inventory Weight** max carry capacity.

It allows server owners to give extra carry weight to players based on rank, user-specific meta, temporary permissions, inherited groups, or any other LuckPerms setup that results in meta values being available on the player.

## Required Mods

Install these mods on the server:

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [LuckPerms](https://modrinth.com/mod/luckperms)
- [Fabric API](https://modrinth.com/mod/fabric-api)

## Environment

This add-on is intended for server

It does not need to run on the client because it only changes server-side capacity calculation.

## How It Works

MT Inventory Weight exposes a max-weight modification event:

```text
InventoryWeightEvents.MODIFY_MAX_WEIGHT
```

LuckPerms InventoryWeight listens to that event. When MT Inventory Weight asks for a player's max carry capacity, the add-on:

1. Gets the player's loaded LuckPerms `User`.
2. Reads configured LuckPerms meta values from the user's cached meta data.
3. Parses those values as floats.
4. Applies flat, percent, and multiplier bonuses to the current max weight.
5. Returns the modified value back to MT Inventory Weight.

No config file is required because all balancing is controlled through LuckPerms meta.

## Supported Meta Keys

The add-on reads exactly these LuckPerms meta keys:

```text
inventoryweight.maxweight.add
inventoryweight.maxweight.percent
inventoryweight.maxweight.multiplier
```

### `inventoryweight.maxweight.add`

Adds a flat amount to the player's max carry weight.

Example:

```text
/lp group vip meta set inventoryweight.maxweight.add 50
```

Effect:

```text
+50 max weight
```

### `inventoryweight.maxweight.percent`

Adds a percentage multiplier to the player's max carry weight.

Example:

```text
/lp group vip meta set inventoryweight.maxweight.percent 25
```

Effect:

```text
+25% max weight
```

### `inventoryweight.maxweight.multiplier`

Applies a direct multiplier.

Example:

```text
/lp group vip meta set inventoryweight.maxweight.multiplier 1.5
```

Effect:

```text
1.5x max weight
```

## Formula

The add-on uses this formula:

```text
final max weight = (current max weight + add) * (1 + percent / 100) * multiplier
```

Where:

- `current max weight` is the value already calculated by MT Inventory Weight.
- `add` comes from `inventoryweight.maxweight.add`.
- `percent` comes from `inventoryweight.maxweight.percent`.
- `multiplier` comes from `inventoryweight.maxweight.multiplier`.

If a meta key is missing, the default values are:

```text
add = 0
percent = 0
multiplier = 1
```

## Example Calculation

Given:

```text
Current max weight: 100
inventoryweight.maxweight.add = 50
inventoryweight.maxweight.percent = 25
inventoryweight.maxweight.multiplier = 1.5
```

Calculation:

```text
final = (100 + 50) * (1 + 25 / 100) * 1.5
final = 150 * 1.25 * 1.5
final = 281.25
```

So the player's max weight becomes:

```text
281.25
```

## Rank Examples

### Default Rank

```text
/lp group default meta set inventoryweight.maxweight.add 0
```

```text
/lp group vip meta set inventoryweight.maxweight.add 50
/lp group vip meta set inventoryweight.maxweight.percent 10
```

## User-specific Bonuses

You can assign bonuses directly to a player:

```text
/lp user PlayerName meta set inventoryweight.maxweight.add 100
```

Temporary bonuses also work with LuckPerms' normal temporary meta support, depending on the commands and LuckPerms setup you use.

## Permissions vs Meta

This add-on uses **meta values**, not permission nodes.

Use:

```text
/lp group vip meta set inventoryweight.maxweight.add 50
```

Do not use:

```text
/lp group vip permission set inventoryweight.maxweight.add.50 true
```

Meta is preferred because it stores numeric values cleanly.

## Troubleshooting

### LuckPerms does not appear in the mod list

If the log does not contain `luckperms`, then LuckPerms is not installed or not being loaded.

Make sure the LuckPerms Fabric jar is in the server mods folder.

### The add-on still logs `Hello Fabric world!`

That means the template initializer is still being used.

Replace the initializer with the real LuckPerms InventoryWeight implementation.

### Meta values do nothing

Check:

- LuckPerms is installed and loaded.
- MT Inventory Weight is installed and loaded.
- LuckPerms InventoryWeight is installed on the server.
- The meta key is spelled exactly correctly.
- The meta value is a valid number.

Correct examples:

```text
inventoryweight.maxweight.add
inventoryweight.maxweight.percent
inventoryweight.maxweight.multiplier
```

### Invalid numbers are ignored

The add-on parses meta values as floats.

Valid examples:

```text
50
25
1.5
2.0
```

Invalid examples:

```text
fifty
+25%
one point five
```

### Client does not have the add-on

That is expected. This add-on is server-side only.

Clients still need whatever MT Inventory Weight requires for client-side HUD/tooltips, depending on your server/modpack setup.