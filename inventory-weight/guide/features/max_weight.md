---
title: "Maximum Weight"
description: "A guide to how maximum carry weight is calculated in MT Inventory Weight, including base server config, armor pockets, capacity bonuses, commands, and add-ons."
---

# Maximum Weight

In MT Inventory Weight, a player's **maximum weight** is the amount of weight they can carry before becoming overloaded.

When the player's current carried weight reaches or exceeds their maximum weight, the Overload system can apply penalties such as movement speed reduction, attack speed reduction, attack damage reduction, jump reduction, and the Overload status effect.

## Final Maximum Weight

The current calculation is:

```text
server config maxWeight
+ inventoryweight:generic.max_weight attribute value
+ CCA capacity bonus
+ armor pocket capacity
+ add-on capacity providers
= final max weight
```

Add-ons may also modify this value through the public API.

## Server Config Base Weight

The base carry capacity comes from the server config:

```text
config/inventoryweight/server-config.toml
```

Default:

```toml
maxWeight = 90000.0
```

## Max Weight Attribute

The Minecraft attribute adds extra max weight on top of the server config value:

```text
inventoryweight:generic.max_weight
```

Default attribute value:

```text
0
```

Other mods, such as PlayerEx or RPG/level systems, can add standard attribute modifiers to this attribute.

Normal config updates do not overwrite player attribute values.

## Capacity Bonus

Older versions sometimes called this value a "multiplier", but in the current system it is an **additive capacity bonus**.

That means it does not multiply the player's max weight. It adds a flat value.

Example:

```text
server config maxWeight = 90000
inventoryweight:generic.max_weight = 5000
capacity bonus = 10000
final max weight = 105000
```

Admins can set this value with commands.

## Armor Pocket Capacity

Armor pockets add extra max weight.

Formula:

```text
armor pocket capacity = total pockets worn * pocketWeight
```

The `pocketWeight` value is configured in the server config.

Default:

```toml
pocketWeight = 9000.0
```

Example:

```text
4 pockets * 9000 = 36000 extra capacity
```

If the server config max weight is `90000` and the player has `0` attribute bonus, the final max weight becomes:

```text
90000 + 0 + 36000 = 126000
```

See the [Pockets](./pockets.md) page for details about pocket calculation and datapack overrides.

## Full Example

Assume:

```text
server config maxWeight = 90000
inventoryweight:generic.max_weight = 5000
admin/player capacity bonus = 10000
armor pockets = 4
pocketWeight = 9000
```

Calculate armor pocket capacity:

```text
4 * 9000 = 36000
```

Final max weight:

```text
90000 + 5000 + 10000 + 36000 = 141000
```

The player can carry up to `141000` weight before being overloaded.

## Commands

Operators can inspect and change weight values with commands.

### Set Configured Default Maximum Weight

```text
/inventoryweight set base <value>
```

Example:

```text
/inventoryweight set base 120000
```

This changes the configured default/fallback max weight. It does not forcibly overwrite existing player max-weight attribute values.

### Get Configured Default Maximum Weight

```text
/inventoryweight get base
```

### Set Player Capacity Bonus

```text
/inventoryweight set bonus <player> <value>
```

or for yourself:

```text
/inventoryweight set bonus <value>
```

Example:

```text
/inventoryweight set bonus Steve 10000
```

### Get Player Capacity Bonus

```text
/inventoryweight get bonus <player>
```

or for yourself:

```text
/inventoryweight get bonus
```

### Get Combined Maximum Weight

```text
/inventoryweight get combined <player>
```

or for yourself:

```text
/inventoryweight get combined
```

This returns the final max weight after the max-weight attribute, player capacity bonus, armor pockets, and other providers.

### Get Current Weight

```text
/inventoryweight get value <player>
```

or for yourself:

```text
/inventoryweight get value
```

## Add-on API

Add-ons can modify max weight through the public API.

Relevant API types:

```text
com.megatrex4.api.v1.CapacityProvider
com.megatrex4.api.v1.CapacityModifier
InventoryWeightEvents.MODIFY_MAX_WEIGHT
```

Add-ons can provide:

- additive capacity bonuses
- multiplicative capacity modifiers
- class/race/progression bonuses
- trinket-based capacity bonuses
- equipment-based capacity bonuses

Example:

```java
registrar.registerCapacityProvider(
        new Identifier("example", "warrior_bonus"),
        1000,
        player -> CapacityModifier.additive(10000.0f)
);
```

## Multiplayer Behavior

The server is authoritative for maximum weight.

Clients receive synced player state for HUD display and synced server config/datapack data for tooltips.

If a server config value is changed through fzzy_config, MT Inventory Weight applies the change live and updates online players.

## Related Pages

- [Server Configuration](../options/inventory_weights_server.md)
- [Pockets](./pockets.md)
- [Overload Effect](./overload_effect.md)
- [Add-on API](././сompatibilities/addon-api.md)
