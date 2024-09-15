---
title: "Custom Item Weights"
description: "How to set and manage custom item weights in the MT Inventory Weight mod."
---

# **Custom Item Weights**

In **MT Inventory Weight**, you can customize the weights of specific items beyond the default settings. This guide explains how to set custom item weights and manage your configurations.

## **What Are Custom Item Weights?**

Custom item weights allow you to define specific weights for individual items, overriding default values or adding new weights for items not covered by defaults. This feature is useful for tailoring the inventory weight system to better fit your gameplay or modpack requirements.

## **Configuring Custom Item Weights**

### **Configuration File**

Custom item weights are stored in a JSON configuration file. The default path for this file is:

`config/inventoryweight/inventory_weights_items.json`


Here’s how you can configure custom item weights:

1. **Locate the Configuration File**

   If the file doesn’t already exist, the mod will create it with default values when it first runs. You can manually create or edit this file to add custom weights.

2. **Editing the JSON File**

   Open the `inventory_weights_items.json` file and define your custom weights in JSON format. For example:

```json
{
     "minecraft:diamond_sword": 100.0,
     "minecraft:gold_ingot": 50.0
}
```

* `"item"`: The full item ID (e.g., minecraft:diamond_sword).
* `"weight"`: The custom weight you want to assign.

3. Saving the Configuration

After editing the file, save your changes. The mod will automatically load the new weights when the world or server is restarted.

## Additional Resources

For more information on how item weights are categorized and managed, refer to the [Inventory Weights Items](../options/inventory_weights_items.md) guide.