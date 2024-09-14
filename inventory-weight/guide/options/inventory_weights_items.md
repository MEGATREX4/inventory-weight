---
title: "Inventory Weights Items Configuration"
description: "Learn how to configure custom item weights for the MT Inventory Weight mod."
---

# **Inventory Weights Items Configuration**

The `ItemWeightConfigItems` class is designed to manage and store custom item weight configurations for the **MT Inventory Weight** mod. It works by reading from and writing to the `inventory_weights_items.json` file, which contains item-specific weight data.

### **Configuration Options**

The `inventory_weights_items.json` file holds the custom item weights specified by players or mod authors. This allows for precise control over the weights assigned to specific items, providing flexibility in how the weight system affects gameplay.

### **Key Methods**

- **`loadConfig()`**:
    - This method checks for the existence of the `inventory_weights_items.json` file.
    - If the file exists, it reads the file and loads custom item weights using `ItemWeights.loadCustomWeightsFromConfig()`.
    - If the file does not exist, it creates a default configuration and saves it, then loads the default values into the mod.

- **`saveConfig()`**:
    - This method writes the custom item weights to the `inventory_weights_items.json` file.
    - It filters out static or default item weights, saving only dynamically configured weights from the player's modifications.
    - The `ItemWeights.getCustomItemWeights()` method is used to retrieve the custom weights before saving.

### **File Structure**

The `inventory_weights_items.json` file contains custom weights in the following format:

```json
{
    "minecraft:stone": 810.0
}
```

In this example, minecraft:stone is assigned a weight of 810.0f. You can add or modify items and their respective weights in this file.

### **Custom Weight Management**

*   **`createDefaultConfig()`**:
    
    *   This method creates a default configuration with preset weights for known items. It currently assigns a weight of `810.0f` to `minecraft:stone`.
    *   You can modify or expand this default list to include more items as needed.
*   **`isDynamicItem(String itemName)`**:
    
    *   This method checks whether a given item is dynamic (i.e., customizable by the player) and returns `true` if it can be modified.
    *   Static items that are predefined cannot be modified and are filtered out when saving the configuration.

### **Error Handling**

In case of an error while reading or writing to the `inventory_weights_items.json` file, exceptions are caught and printed to the console for debugging purposes. This ensures that the mod does not crash if something goes wrong during file handling.

### **Usage Example**

To add a custom weight to an item, simply open the `inventory_weights_items.json` file in the `config/inventoryweight` directory and add the item ID along with its desired weight. For example:

```json
{     
    "minecraft:iron_ingot": 100.0,
    "minecraft:diamond": 200.0 
}
```

After saving the file, the mod will load these weights the next time it runs.

### **Default Item Weights**

The `createDefaultConfig()` method can be modified to include default weights for additional items. By default, only `minecraft:stone` is assigned a weight, but you can extend this list based on the items in your modpack or world.

### **Extending the Configuration**

The configuration system is flexible and can be easily extended by adding more items and their corresponding weights to the `inventory_weights_items.json` file. Custom weights allow players to tailor the game’s weight system to their preferences, giving them control over how certain items impact movement and gameplay.
