---
title: "Default Item Values for MT Inventory Weight"
description: "How to use and configure default item weights in the MT Inventory Weight mod."
---

# **Default Item Values for MT Inventory Weight**

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

```java
public static float BUCKETS = 810.0f;
public static float BOTTLES = 270.0f;
public static float BLOCKS = 810.0f;
public static float INGOTS = 90.0f;
public static float NUGGETS = 10.0f;
public static float ITEMS = 50.0f;

public static float CREATIVE = 30000.0f;
public static float POCKET_WEIGHT = 10000.0f;
public static float MAXWEIGHT = 80000.0f;
```

You can define default item weights in your configuration files. For more information, see the [Configuration Guide](./options/inventory_weights_server.md).

These values will override the defaults provided by the mod when the world or server loads. To apply these configurations:

1. Save your configurations in a `.json` file.
2. Place the file in the appropriate folder for your mod's configuration (e.g., `config/inventory_weight`).
3. Restart your Minecraft world or server to apply changes.

