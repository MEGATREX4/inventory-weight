---
title: "Default Item Values"
description: "How to use and configure default item weights in the MT Inventory Weight mod."
---

# **Default Item Values**

In **MT Inventory Weight**, you can configure default item weights to suit your gameplay needs. This guide explains how default item weights work, how to modify them, and how they interact with custom item weights.

## **Understanding Default Item Values**

**MT Inventory Weight** uses a set of default values to determine the weight of various items. These values are applied when no specific custom weight is defined for an item. The mod categorizes items into several groups, each with its own default weight:

- **Buckets**: Represents the weight of items like water buckets.
- **Bottles**: Represents the weight of items like glass bottles.
- **Blocks**: Represents the weight of block-type items.
- **Ingots**: Represents the weight of ingots, such as iron or gold ingots.
- **Nuggets**: Represents the weight of small items like gold nuggets.
- **Items**: Represents a general weight for miscellaneous items.
- **Creative**: Represents a special weight for items used in creative mode.

### **Default Weights**

The default weights are set as follows:

- **BUCKETS** = 810.0f
- **BOTTLES** = 270.0f
- **BLOCKS** = 810.0f
- **INGOTS** = 90.0f
- **NUGGETS** = 10.0f
- **ITEMS** = 50.0f
- **CREATIVE** = 30000.0f
- **POCKET_WEIGHT** = 10000.0f
- **MAXWEIGHT** = 80000.0f

You can define default item weights in your configuration files. For more information, see the [Configuration Guide](../options/inventory_weights_server.md).

These values will override the defaults provided by the mod when the world or server loads. To apply these configurations:

1. Save your configurations in a `.json` file.
2. Place the file in the appropriate folder for your mod's configuration (`config/inventory_weight`).
3. Restart your Minecraft world or server to apply changes.

## **Block Weights**

For block-type items, the weight is influenced by several factors:

- **Base Weight**: Default weight for blocks is set by `InventoryWeightUtil.BLOCKS`.
- **Hardness**: Weight increases with block hardness.
- **Blast Resistance**: Weight increases based on blast resistance, with a cap to prevent excessive values.
- **Transparency**: Weight decreases if the block is transparent.
- **Rarity**: Additional weight is applied based on the item's rarity tier, with a multiplier effect.

In creative mode, blocks have a special weight defined by `InventoryWeightUtil.CREATIVE`.

```java
            weight += (hardness * 10);
            weight += Math.min((blastResistance * 50), 10000);

            // Subtract a value if the block is transparent
            if (isTransparent) {
                weight -= 1000;
            }

            weight *= (getRarityWeight(stack) * 1.3f);

            return (int) Math.floor(Math.max(weight, InventoryWeightUtil.ITEMS));
```

## **Item Weights**

For regular items, the weight is influenced by:

- **Category Base Weight**: Base weight is derived from the category of the item (e.g., ingots, nuggets).
- **Stack Size**: Weight is adjusted based on the stack size, with a multiplier effect.
- **Durability**: Weight is modified based on the item’s durability, especially for single-stack items.
- **Rarity**: Additional weight is applied based on the item's rarity, increasing weight according to rarity tiers.

```java
        // Modify weight based on stack size
        if (maxStackSize > 1) {
            float stackMultiplier = 1 + (10f / maxStackSize); // Example multiplier
            weight *= stackMultiplier;
        }

        else if (maxStackSize == 1 && maxDurability > 0) {
            if (isHasArmor(item)) {
                weight += (float) (getArmorValue(item) * 10);
                weight += (InventoryWeightUtil.ITEMS + (((float) maxDurability / 300) * 300));
            }
            if (isHasDamage(item)) {
                weight += (float) (InventoryWeightUtil.ITEMS + ((maxDurability / 1500.0) * 300));
            }
        }

        weight *= (getRarityWeight(stack) * 1.3f);

        return (int) Math.floor(Math.max(weight, 1.0f));
```
