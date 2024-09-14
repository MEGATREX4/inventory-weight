---
title: "Datapack Options for MT Inventory Weight"
description: "How to use datapacks to configure custom item weights in the MT Inventory Weight mod."
---

# **Datapack Options for MT Inventory Weight**

Datapacks are a built-in Minecraft feature that allows you to customize game mechanics by adding or modifying content without altering the core game or mods. In the context of **MT Inventory Weight**, datapacks can be used to configure the number of pockets for armor items.

## **Why Use Datapacks with MT Inventory Weight?**

Using datapacks with **MT Inventory Weight** allows you to:
- **Define pockets for armor**: Assign the number of pockets to different armor items based on your gameplay preferences.
- **Extend the mod**: Include custom or modded armor items and specify their pockets.
- **Customize gameplay**: Adjust how pockets affect item carrying capacity to better fit your world.

## **How Datapacks Work for Armor Pockets**

The mod looks for `.json` files within the `inventoryweight` folder of a datapack. These files define the number of pockets for armor items. When the world or server loads, the mod reads these `.json` files and applies the specified configurations to the armor items in the game.

## **Steps to Create a Datapack for Armor Pockets**

Here’s how you can create a datapack to define armor pockets for **MT Inventory Weight**.

### **Create the Datapack Folder Structure**

1. Navigate to the `datapacks` folder inside your Minecraft world or server directory.
   - Example path: `world/datapacks/`
2. Inside the `datapacks` folder, create a new folder for your datapack, e.g., `[datapack_namespace]`.
3. Inside this folder, create another folder called `data`.
4. Inside `data`, create a folder called `inventoryweight` (this name is important as the mod looks for this folder).
5. Inside `inventoryweight`, create a `.json` file to hold your armor pockets definitions. For example, name it `armor_pockets.json`.

The folder structure should look like this:

```md
datapacks/
└── [datapack_namespace]/
    └── data/
        └── inventoryweight/
            └── items.json
```


Make sure to follow this structure so that the **MT Inventory Weight** mod can properly detect and load your custom configurations.

### **Configuring `items.json`**

Inside `items.json`, you will define custom pockets vlaues using the following format:

```json
{
  "items": [
    {
      "item": "minecraft:leather_helmet",
      "pockets": 5
    },
    {
      "item": "techreborn:quantum_leggins",
      "pockets": 2
    }
  ]
}
```

*    `"item"`: The full item ID (including the namespace, e.g., minecraft: or a mod's namespace).
*    `"pockets"`: The number of pockets to apply to the armor item.

Activating the Datapack

Once the folder structure and `items.json` file are properly set up, the datapack will be automatically loaded when your Minecraft world or server starts. If you make changes to the datapack, use the `/reload` command in Minecraft to apply them.