package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.util.ItemWeights;

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

        // Add static item weights for some items namespace:item_name with defaultJsonObject.addProperty
        // Add custom item weights with defaultJsonObject.addProperty
        defaultJsonObject.addProperty("minecraft:stone", 810.0f);


        return defaultJsonObject;
    }

    private static void saveConfig(JsonObject jsonObject) throws IOException {
        try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(jsonObject, fileWriter);
        }
    }
}
