---
title: "Compatibilities"
description: "Compatibility information for MT Inventory Weight, including fzzy_config, Mod Menu, Trinkets, datapacks, backpack/container support, multiplayer, and add-on API support."
order: 4
---

# Compatibility of MT Inventory Weight

MT Inventory Weight is designed to work with vanilla Minecraft, modded items, modded armor, multiplayer servers, datapacks, and optional add-ons.

The mod now uses **fzzy_config** for configuration and no longer requires Cloth Config.

## Configuration Support

### fzzy_config

MT Inventory Weight uses [fzzy_config](https://modrinth.com/mod/fzzy-config) for both server and client configuration.

fzzy_config provides:

- in-game config screens
- synced server settings
- client-only HUD options
- live config updates while the game/server is running
- validation for numeric values
- enum/drop-down style options for HUD settings

Server settings such as max weight, pocket weight, base item weights, realistic mode, and overload penalty strength can be changed in-game. When server settings are changed through the config screen, the mod updates player weight data and syncs the new values to clients automatically.

Client settings such as HUD style, HUD position, text display mode, tooltip visibility, and sprite/bar display options are also configurable in-game.

::: info
Cloth Config is no longer used by MT Inventory Weight. You only need Cloth Config if another mod in your modpack requires it.
:::

### Mod Menu

MT Inventory Weight is compatible with [Mod Menu](https://modrinth.com/mod/modmenu).

When Mod Menu is installed, players can access the MT Inventory Weight configuration screen from the Mods menu, depending on the installed fzzy_config/Mod Menu integration support.

Mod Menu also provides convenient links to:

- Modrinth page
- CurseForge page
- wiki/documentation
- project website
- source code

::: tip
Mod Menu is optional, but it is recommended for players who want easier access to the config screen.
:::

## Item and Block Compatibility

MT Inventory Weight can calculate weight for many vanilla and modded items automatically.

The default calculation supports:

- generic items
- blocks
- buckets
- bottles and potions
- ingots, gems, alloys, and shards
- nuggets
- tools
- armor
- food
- fireproof items
- rarity-based modifiers
- durability-based modifiers
- block hardness and blast resistance
- slabs and stairs
- block entities

Most modded items and blocks should receive a reasonable fallback weight even if they do not have a custom datapack definition.

## Armor and Pocket Compatibility

Armor can add extra carrying capacity through the pocket system.

Pocket values can come from:

1. NBT-specific pocket definitions
2. datapack pocket definitions
3. default armor protection/toughness calculation

This means modded armor can work automatically, while modpack authors can still override pocket values through datapacks.

## Trinkets Compatibility

MT Inventory Weight has optional compatibility with [Trinkets](https://modrinth.com/mod/trinkets).

When Trinkets is installed, equipped trinket items are included in the player's total carried weight.

If Trinkets is not installed, MT Inventory Weight skips this compatibility layer automatically.

::: info
Trinkets is optional. The mod works normally without it.
:::

## Backpack and Container Compatibility

MT Inventory Weight supports many backpack and container-style items by reading their stored item NBT.

Supported or partially supported storage types include:

- vanilla shulker boxes
- Traveler's Backpack
- Scout-style pouches and satchels
- Create toolboxes
- Extended Drawers-style storage
- Inmis-style backpacks
- Pack It Up-style backpacks
- Sophisticated Storage-style NBT storage, when item contents are available in the stack NBT
- other backpack/container items with similar NBT structures

::: warning
The list above includes known/proven compatibility. Other backpack or container mods may also work if their items store contents in a readable NBT structure.
:::

### Shulker and Backpack Weight Reduction

Stored items inside supported shulkers and backpacks are compressed for weight purposes.

The current rule is:

```text
final container weight = empty container weight + weight inside / 2
```

Tooltips show both values:

```text
Weight inside: 10.0k
Weight: 5.1k
```

Where:

- **Weight inside** is the full uncompressed weight of stored items.
- **Weight** is the effective weight used by the inventory weight system.

This makes containers useful without making them weightless.

## Datapack Compatibility

MT Inventory Weight supports datapacks for customizing item weights and armor pocket values.

Datapack item weights are loaded from:

```text
data/<namespace>/inventory_weight/items/*.json
```

Datapack pocket definitions are loaded from:

```text
data/<namespace>/inventory_weight/pockets/*.json
```

These datapacks are loaded on server start and during `/reload`. The resolved data is synced to clients so tooltips can show server-correct values.

### Item Weight Example

```json
{
  "minecraft:stone": 260.0,
  "minecraft:diamond": {
    "weight": 120.0
  }
}
```

### Pocket Example

```json
{
  "minecraft:leather_chestplate": 6,
  "minecraft:diamond_chestplate": {
    "pockets": 2
  }
}
```

## Add-on API

MT Inventory Weight now includes a public add-on API.

Add-ons can hook into the weight system without depending on internal implementation classes.

The public API is located in:

```text
com.megatrex4.api.v1
```

Add-ons can register:

- custom item weight providers
- extra player weight sources
- max-weight/capacity providers
- pocket providers
- weight modification events
- overload state listeners

### Add-on Entrypoint

Add-ons should declare the `inventoryweight` entrypoint in their `fabric.mod.json`:

```json
{
  "entrypoints": {
    "inventoryweight": [
      "com.example.ExampleInventoryWeightAddon"
    ]
  },
  "depends": {
    "inventoryweight": ">=2.0.0"
  }
}
```

Example add-on class:

```java
package com.example;

import com.megatrex4.api.v1.InventoryWeightEntrypoint;
import com.megatrex4.api.v1.InventoryWeightRegistrar;
import com.megatrex4.api.v1.WeightResult;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ExampleInventoryWeightAddon implements InventoryWeightEntrypoint {
    @Override
    public void registerInventoryWeight(InventoryWeightRegistrar registrar) {
        registrar.registerItemWeightProvider(
                new Identifier("example", "custom_item_weight"),
                9000,
                (stack, context, lookup) -> {
                    Identifier id = Registries.ITEM.getId(stack.getItem());

                    if (!id.equals(new Identifier("example", "heavy_item"))) {
                        return Optional.empty();
                    }

                    return Optional.of(WeightResult.of(500.0f));
                }
        );
    }
}
```

::: tip
Use the public API package for add-ons. Classes under `com.megatrex4.impl` are internal and may change without warning.
:::

## Multiplayer Support

MT Inventory Weight supports multiplayer.

The server is authoritative for:

- max weight
- item weights
- datapack definitions
- pocket definitions
- overload state
- player weight state

Player weight state is synced to clients for HUD rendering, and datapack/config data is synced so client tooltips match the server.

The mod works in:

- singleplayer
- LAN worlds
- dedicated servers
- modded multiplayer servers

## Optional Compatibility Summary

| Mod / System | Required? | Support |
| --- | --- | --- |
| fzzy_config | Required | Main config system |
| Mod Menu | Optional | Easier config access and links |
| Trinkets | Optional | Equipped trinkets count toward weight |
| Cloth Config | Not required | No longer used by MT Inventory Weight |
| Datapacks | Optional | Custom item weights and pockets |
| Add-ons | Optional | Public API for integrations |
