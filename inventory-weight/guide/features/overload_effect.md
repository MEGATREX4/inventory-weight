---
title: "Overload Effect"
description: "Details on the Overload effect, penalties, realistic mode, and interactions with Strength and Haste in MT Inventory Weight."
---

# Overload Effect

The **Overload** effect is applied when a player's carried weight reaches or exceeds their maximum carry capacity.

It represents the player carrying too much weight and can reduce movement, combat speed, attack damage, and jump strength.

## When Overload Applies

A player becomes overloaded when:

```text
current inventory weight >= max weight
```

Creative and spectator players are ignored.

When the player drops below max weight, the overload effect and related attribute penalties are removed.

## Max Weight

A player's max weight can come from:

- server config `maxWeight`
- armor pockets
- player capacity bonuses
- add-on capacity providers
- max-weight modification events

Example:

```text
max weight = base max weight + armor pocket capacity + other bonuses
```

## Overload Level Calculation

The mod calculates how full the player's inventory is:

```text
percentage full = current weight / max weight * 100
```

For every 10% above 100%, the overload level increases by 1.

Simplified:

```text
overload level = (percentage full - 100) / 10
```

The internal overload level is capped before final adjustment.

The final visible/effective overload level is at least 1 and capped at 10.

## Strength and Haste Interaction

Strength and Haste reduce the overload level.

The mod checks the effect amplifiers:

```text
adjusted overload level = overload level - (strength amplifier + haste amplifier)
```

The final level cannot go below 1.

::: info
Minecraft effect amplifiers are zero-based internally. Strength I has amplifier `0`, Strength II has amplifier `1`, and so on.
:::

## Penalties While Overloaded

When overloaded, the mod applies attribute penalties.

Base values:

```text
movement speed reduction: 50%
attack speed reduction: 50%
attack damage reduction: 25%
```

Each overload level can increase penalties by 5% per level.

Penalties are capped at 90%.

The server config option:

```toml
overloadPenaltyStrength = 1.0
```

controls the strength of the overload penalties.

Setting it to `0.0` disables attribute and jump penalties.

## Jump Penalty

Overload also reduces jump strength.

The heavier the overload level, the harder it is to jump.

Jump reduction is capped so the player is not reduced below a small minimum jump strength.

## Realistic Mode

The server config has:

```toml
realisticMode = false
```

When realistic mode is disabled, penalties mainly apply when the player is overloaded.

When realistic mode is enabled, gradual penalties can begin before full overload. These penalties start after the player passes roughly 10% of max capacity and increase as the player approaches max weight.

Realistic mode can affect:

- movement speed
- attack speed
- attack damage
- jump height

## Status Effect Behavior

The Overload status effect is used as the visible marker for overload.

The actual attribute modifiers are managed by the weight penalty system, not directly by the status effect class.

This keeps penalty logic centralized and prevents duplicate modifiers.

## Example

Assume:

```text
max weight = 90000
current weight = 117000
```

Calculate fullness:

```text
117000 / 90000 * 100 = 130%
```

This is 30% over the limit.

Overload level before Strength/Haste adjustment:

```text
(130 - 100) / 10 = 3
```

If the player has Haste II, its internal amplifier is `1`, so:

```text
3 - 1 = 2
```

The player receives an adjusted overload level of 2.

## Removing Overload

Overload is removed when:

- current weight drops below max weight
- the player enters creative mode
- the player enters spectator mode
- penalty strength is disabled and penalties are cleared

## Configuration

Relevant server config options:

```toml
maxWeight = 90000.0
pocketWeight = 9000.0
realisticMode = false
overloadPenaltyStrength = 1.0
```

See the [Server Configuration](../options/inventory_weights_server.md) page for more information.

## Related Pages

- [Inventory Weights Items Configuration](../options/inventory_weights_items.md)
- [Pockets](./pockets.md)
- [Custom Tooltips](./tooltips.md)
