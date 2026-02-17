package com.megatrex4.config;

import com.google.gson.*;
import com.megatrex4.InventoryWeight;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.util.NbtWeightHandler;

import com.megatrex4.util.ItemCategory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ItemWeightConfigItems {
    public static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_items.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        // Item weights are now loaded from datapacks instead of config files
        // This method is kept for backward compatibility but doesn't load anything
        InventoryWeight.LOGGER.info("Item weights are loaded from datapacks in inventory_weight/items/");
    }

    public static void saveConfig() {
        // Saving to file is no longer supported - use datapacks instead
        InventoryWeight.LOGGER.info("Item weights are now managed through datapacks. No file saving available.");
    }

    // Change visibility of this method from private to protected
    public static boolean isDynamicItem(String itemName) {
        return !ItemWeights.isStaticItem(ItemCategory.fromName(itemName));
    }
}

