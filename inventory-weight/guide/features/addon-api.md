---
title: "MT Inventory Weight Add-on API"
description: "How to create add-ons for MT Inventory Weight using the public API."
---

# MT Inventory Weight Add-on API

MT Inventory Weight exposes a public API for other mods and add-ons.

Use this package:

```text
com.megatrex4.api.v1
```

Do not depend on classes under:

```text
com.megatrex4.impl
```

Those classes are internal implementation details.

## Entrypoint

Add this to your `fabric.mod.json`:

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

Then implement:

```java
package com.example;

import com.megatrex4.api.v1.InventoryWeightEntrypoint;
import com.megatrex4.api.v1.InventoryWeightRegistrar;

public final class ExampleInventoryWeightAddon implements InventoryWeightEntrypoint {
    @Override
    public void registerInventoryWeight(InventoryWeightRegistrar registrar) {
        // Register providers here.
    }
}
```

## Provider Types

The registrar supports:

```java
registerItemWeightProvider(...)
registerPlayerWeightSource(...)
registerCapacityProvider(...)
registerPocketProvider(...)
```

## Item Weight Provider

Use an item weight provider when your mod has items that need custom weight logic.

```java
registrar.registerItemWeightProvider(
        new Identifier("example", "mana_tablet_weight"),
        9000,
        (stack, context, lookup) -> {
            Identifier id = Registries.ITEM.getId(stack.getItem());

            if (!id.equals(new Identifier("example", "mana_tablet"))) {
                return Optional.empty();
            }

            float baseWeight = 250.0f;
            float manaWeight = 0.0f;

            if (stack.hasNbt() && stack.getNbt() != null) {
                manaWeight = stack.getNbt().getInt("Mana") / 100.0f;
            }

            return Optional.of(WeightResult.of(baseWeight + manaWeight, baseWeight));
        }
);
```

## Player Weight Source

Use a player weight source when your mod adds extra equipment or inventory slots.

```java
registrar.registerPlayerWeightSource(
        new Identifier("example", "belt_slots"),
        1000,
        (player, context, lookup) -> {
            WeightResult total = WeightResult.ZERO;

            for (ItemStack stack : getExampleBeltStacks(player)) {
                total = total.add(lookup.getWeight(stack, context).multiply(stack.getCount()));
            }

            return total;
        }
);
```

## Capacity Provider

Use a capacity provider when your mod should increase or multiply max carry weight.

```java
registrar.registerCapacityProvider(
        new Identifier("example", "class_bonus"),
        1000,
        player -> {
            if (isWarrior(player)) {
                return CapacityModifier.additive(10_000.0f);
            }

            return CapacityModifier.none();
        }
);
```

## Pocket Provider

Use a pocket provider when your mod adds armor or equipment that should provide pocket capacity.

```java
registrar.registerPocketProvider(
        new Identifier("example", "custom_armor_pockets"),
        9000,
        (stack, wearer) -> {
            Identifier id = Registries.ITEM.getId(stack.getItem());

            if (id.equals(new Identifier("example", "utility_chestplate"))) {
                return OptionalInt.of(8);
            }

            return OptionalInt.empty();
        }
);
```

## Priority

Higher priority providers run first.

Suggested priority ranges:

```text
10000+ hard overrides
9000   datapack/NBT-like overrides
8000   containers/backpacks
1000   normal compatibility
0      fallback
```

## Events

`InventoryWeightEvents` allows advanced modification:

```java
InventoryWeightEvents.MODIFY_ITEM_WEIGHT.register((stack, context, current) -> current);
InventoryWeightEvents.MODIFY_PLAYER_INVENTORY_WEIGHT.register((player, current) -> current);
InventoryWeightEvents.MODIFY_MAX_WEIGHT.register((player, currentMax) -> currentMax);
InventoryWeightEvents.OVERLOAD_CHANGED.register((player, overloaded) -> {});
```
