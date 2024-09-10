package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.ItemWeights;
import com.megatrex4.PlayerDataHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ItemWeightConfigItems {
    private static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_items.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (FileReader fileReader = new FileReader(CONFIG_PATH.toFile())) {
                    JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
                    ItemWeights.loadWeightsFromConfig(jsonObject);
                }
            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject defaultJsonObject = createDefaultConfig();
                saveConfig(defaultJsonObject); // Save default values to the file
                ItemWeights.loadWeightsFromConfig(defaultJsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            JsonObject jsonObject = new JsonObject();

            // Only add custom item weights, not static weights
            for (Map.Entry<String, Float> entry : ItemWeights.getCustomItemWeights().entrySet()) {
                String itemName = entry.getKey();
                if (isDynamicItem(itemName)) {
                    jsonObject.addProperty(itemName, entry.getValue());
                }
            }
            saveConfig(jsonObject); // Save filtered item weights
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to check if the item is dynamic
    private static boolean isDynamicItem(String itemName) {
        return !ItemWeights.isStaticItem(itemName);
    }

    private static JsonObject createDefaultConfig() {
        JsonObject defaultJsonObject = new JsonObject();

        // Iterate over all registered items
        for (Item item : Registries.ITEM) {
            String itemId = Registries.ITEM.getId(item).toString().toLowerCase();
            ItemStack itemStack = new ItemStack(item);
            String category = PlayerDataHandler.getItemCategory(itemStack);
            float weight = ItemWeights.getItemWeight(category.toLowerCase());

            // Add item and its default weight to the JSON object
            defaultJsonObject.addProperty(itemId, weight);
        }

        return defaultJsonObject;
    }

    private static void saveConfig(JsonObject jsonObject) throws IOException {
        try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(jsonObject, fileWriter);
        }
    }
}
