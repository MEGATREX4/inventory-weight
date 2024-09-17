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

### **Examples and Calculations**

#### **Block of Netherite**

- **Rarity**: Common
- **Blast Resistance**: 1,200
- **Hardness**: 50
- **Transparent**: No
- **Base Weight**: 810.0f (BLOCKS category)
- **Blast Resistance Weight**: 1,200 * 100 = 120,000 (capped to 3,000)
- **Hardness Weight**: 50 * 10 = 500
- **Transparency Adjustment**: 0 (not transparent)
- **Rarity Weight**: 1.0 (Common)
- **Total Weight**: (810.0 + 3,000 + 500) * 1.0 ≈ 4,310.0

#### **Glass**

- **Rarity**: Common
- **Blast Resistance**: 0.3
- **Hardness**: 0.3
- **Transparent**: Yes
- **Base Weight**: 810.0f (BLOCKS category)
- **Blast Resistance Weight**: 0.3 * 100 = 30
- **Hardness Weight**: 0.3 * 10 = 3
- **Transparency Adjustment**: -1,000
- **Rarity Weight**: 1.0 (Common)
- **Total Weight**: (810.0 + 30 + 3 - 1,000) * 1.0 ≈ -157.0 (capped to 50.0f)

#### **Short Grass**

- **Rarity**: Common
- **Blast Resistance**: 0
- **Hardness**: 0
- **Transparent**: Yes
- **Base Weight**: 810.0f (BLOCKS category)
- **Blast Resistance Weight**: 0
- **Hardness Weight**: 0
- **Transparency Adjustment**: -1,000
- **Rarity Weight**: 1.0 (Common)
- **Total Weight**: (810.0 + 0 + 0 - 1,000) * 1.0 ≈ -190.0 (capped to 50.0f)

## **Item Weights**

For regular items, the weight is influenced by:

- **Category Base Weight**: Base weight is derived from the category of the item (e.g., ingots, nuggets).
- **Stack Size**: Weight is adjusted based on the stack size, with a multiplier effect.
- **Durability**: Weight is modified based on the item’s durability, especially for single-stack items.
- **Rarity**: Additional weight is applied based on the item's rarity, increasing weight according to rarity tiers.

### **Examples and Calculations**

#### **Netherite Scrap**

- **Rarity**: Common
- **Stackable**: Yes (64)
- **Base Weight**: 50.0f (ITEMS category)
- **Stack Multiplier**: 1 + (10 / 64) ≈ 1.156
- **Rarity Weight**: 1.0 (Common)
- **Total Weight**: 50.0 * 1.156 * 1.0 ≈ 57.8

#### **Iron Diamond Helmet**

- **Rarity**: Common
- **Stackable**: No (Durability only affects single items)
- **Durability**: 363
- **Armor Value**: 3
- **Base Weight**: 50.0f (ITEMS category)
- **Durability Weight**: 50.0 + ((363 / 300) * 300) ≈ 171.0
- **Armor Weight**: 3 * 10 = 30.0
- **Rarity Weight**: 1.0 (Common)
- **Total Weight**: (50.0 + 171.0 + 30.0) * 1.0 ≈ 251.0

#### **Nether Star**

- **Rarity**: Uncommon
- **Stackable**: 64
- **Base Weight**: 50.0f (ITEMS category)
- **Stack Multiplier**: 1 + (10 / 64) ≈ 1.156
- **Rarity Weight**: 1.2 (Uncommon)
- **Total Weight**: 50.0 * 1.156 * 1.2 ≈ 69.8

#### **Ender Pearl**

- **Rarity**: Common
- **Stackable**: 16
- **Base Weight**: 50.0f (ITEMS category)
- **Stack Multiplier**: 1 + (10 / 16) ≈ 1.625
- **Rarity Weight**: 1.0 (Common)
- **Total Weight**: 50.0 * 1.625 * 1.0 ≈ 81.3
