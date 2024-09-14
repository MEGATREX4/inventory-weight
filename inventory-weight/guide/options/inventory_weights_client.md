---
title: "Inventory Weights Client Configuration"
description: "Learn how to configure and customize the client-side settings for the MT Inventory Weight mod."
---

# **Inventory Weights Client Configuration**

The `ItemWeightsConfigClient` class is responsible for managing the client-side configuration for the **MT Inventory Weight** mod. It handles the display settings for the weight HUD, such as its position on the screen and custom offsets.

### **Configuration Options**

The following settings are managed by the `inventory_weights_client.json` file, which is automatically created and maintained by the mod in the `config/inventoryweight` directory:

- **hudPosition**: Determines the HUD's position on the screen. The default position is `"BOTTOM_RIGHT"`.
- **xOffset**: Specifies the horizontal offset for custom HUD positioning. Default value is `0.5f`.
- **yOffset**: Specifies the vertical offset for custom HUD positioning. Default value is `0.5f`.

### **Config Management**

The `ItemWeightsConfigClient` class provides two main methods to handle loading and saving the configuration:

- **`loadConfig()`**:
    - This method is responsible for loading the configuration from the `inventory_weights_client.json` file. 
    - If the file exists, it parses the file and sets the `hudPosition`, `xOffset`, and `yOffset` values based on the data found.
    - If the file does not exist, it creates a new directory and file with default values.

- **`saveConfig()`**:
    - This method writes the current values of `hudPosition`, `xOffset`, and `yOffset` into the `inventory_weights_client.json` file.
    - It ensures that any changes made to the configuration in-game or programmatically are saved to disk.

### **File Structure**

When the config file is created, it will look something like this:

```json
{
    "hudPosition": "BOTTOM_RIGHT",
    "xOffset": 0.5,
    "yOffset": 0.5
}
```

### **HUD Positioning**

The `hudPosition` option can be customized to several predefined positions, or you can set it to `"CUSTOM"` for manual adjustment. The `xOffset` and `yOffset` values are only used when the HUD is set to the `"CUSTOM"` position, allowing players to place the weight HUD anywhere on their screen.

### **Error Handling**

The mod also includes basic error handling for loading and saving the config file:

*   If the configuration file cannot be read (e.g., due to corruption), default values are applied, and the mod attempts to create or overwrite the file.
*   If any `IOException` occurs while reading or writing, the exception is printed to the console for debugging purposes.

### **Usage Example**

If you want to manually adjust the HUD position to the top left of the screen, you would modify the config file as follows:


```json
{     
    "hudPosition": "CUSTOM",
    "xOffset": 0.0,
    "yOffset": 0.0 
}
```

This flexibility allows players to tailor the HUD to fit their specific screen layout preferences.