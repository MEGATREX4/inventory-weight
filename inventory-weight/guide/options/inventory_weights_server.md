---
title: "Inventory Weights Server Configuration"
description: "Detailed guide to configuring server-side item weights and settings in the MT Inventory Weight mod."
---

# **Inventory Weights Server Configuration**

The `ItemWeightsConfigServer` class manages the server-side configuration for the **MT Inventory Weight** mod. It handles custom item weights, maximum carrying weight, and pocket weight adjustments that impact player movement and interaction based on the weight of items in their inventory.

### **Configuration Overview**

The server configuration is stored in the `inventory_weights_server.json` file within the `config/inventoryweight` directory. This file allows server owners to define global weights for different item categories and to adjust the maximum allowable weight players can carry before experiencing negative effects, such as reduced movement speed.

### **Key Configuration Variables**

- **`maxWeight`**: The maximum weight a player can carry before they are penalized with movement restrictions.
- **`pocketWeight`**: The weight associated with items stored in a player's armor pockets.

### **Key Methods**

- **`loadConfig()`**:
    - Reads the configuration from `inventory_weights_server.json`.
    - Loads item weights and key variables like `maxWeight` and `pocketWeight` from the config.
    - If the config file does not exist, a default one is created with pre-defined values for item categories (buckets, bottles, blocks, etc.).
    - Updates the `InventoryWeightUtil` class with the loaded pocket weight to ensure consistency in the game mechanics.

- **`saveConfig()`**:
    - Writes the current item weights and server settings (such as `maxWeight` and `pocketWeight`) back to the `inventory_weights_server.json` file.
    - Ensures the custom values are preserved and can be reused upon server restart.

- **`getMaxWeightFromConfig()`**:
    - Retrieves the maximum carrying weight from the configuration file.

- **`setMaxWeight(float value)`**:
    - Updates the maximum carrying weight in memory and saves it to the configuration file.

### **File Structure**

The `inventory_weights_server.json` file has the following structure:

```json
{
    "buckets": 3.5,
    "bottles": 0.5,
    "blocks": 10.0,
    "ingots": 2.5,
    "nuggets": 0.1,
    "items": 1.0,
    "creative": 0.0,
    "maxWeight": 50.0,
    "pocketWeight": 5.0
}
```

*   **`buckets`, `bottles`, `blocks`, `ingots`, `nuggets`, `items`, and `creative`**: These fields define the weight values for different categories of items.
*   **`maxWeight`**: Defines the maximum weight a player can carry before being penalized.
*   **`pocketWeight`**: Sets the weight of items carried in a player's armor pockets.

### **Default Values**

If no configuration exists, the system creates a default config file with preset values for each item category:

*   **Buckets**: `810.0`
*   **Bottles**: `270.0`
*   **Blocks**: `810.0`
*   **Ingots**: `90.0`
*   **Nuggets**: `10.0`
*   **Items**: `50.0`
*   **Creative mode**: `30000` (Values for some items associated with Creative mode)
*   **Max Weight**: `80000`
*   **Pocket Weight**: `10000`

hese values can be customized by editing the JSON file directly or by using mod-specific configuration tools.

### **Customizing Weights**

To customize the weight of specific item categories, simply adjust the values in the `inventory_weights_server.json` file. For example:

```json
{
    "blocks": 12.0,
    "ingots": 3.0
}
```

This change would set weight of blocks to `12.0` and ingots to `3.0`, impacting how much of these items a player can carry before they are slowed down or penalized.

### **Error Handling**

If an error occurs while loading or saving the configuration, it is caught and printed to the server console. This ensures that the server remains operational, even if the config file has issues.

### **How the Configuration Affects Gameplay**

*   **Max Weight**: The `maxWeight` value directly affects how much a player can carry before penalties (such as reduced movement speed) are applied.
*   **Pocket Weight**: The `pocketWeight` value allows for a special inventory system where items carried in pockets (e.g., within armor) have their own weight category, impacting total weight differently.

These server-side settings allow for fine-tuning of the gameplay experience to suit different servers' needs, adding a layer of realism and strategy to inventory management in Minecraft.

This flexibility allows the server admin to tailor the weight system to fit any custom modpack or playstyle.