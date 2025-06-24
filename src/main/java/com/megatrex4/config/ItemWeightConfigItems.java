package com.megatrex4.config;

import com.google.gson.*;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.util.NbtWeightHandler;
<<<<<<< HEAD
=======
import com.megatrex4.util.ItemCategory;
>>>>>>> testrepo/main

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
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        if (entry.getValue().isJsonObject()) {
                            NbtWeightHandler.parse(entry.getKey(), entry.getValue().getAsJsonObject());
                        }
                    }
                    ItemWeights.loadCustomWeightsFromConfig(jsonObject);
                }
            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject defaultJsonObject = createDefaultConfig();
                saveConfig(defaultJsonObject); // Save default values to the file
                ItemWeights.loadCustomWeightsFromConfig(defaultJsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            JsonObject jsonObject = new JsonObject();

            // Add only custom item weights to the JSON object
            for (Map.Entry<String, Float> entry : ItemWeights.getCustomItemWeights().entrySet()) {
                String itemName = entry.getKey();
                if (isDynamicItem(itemName)) {
                    jsonObject.addProperty(itemName, entry.getValue());
                }
            }
            saveConfig(jsonObject); // Save filtered item weights to file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Change visibility of this method from private to protected
    public static boolean isDynamicItem(String itemName) {
<<<<<<< HEAD
        return !ItemWeights.isStaticItem(itemName);
=======
        return !ItemWeights.isStaticItem(ItemCategory.fromName(itemName));
>>>>>>> testrepo/main
    }

    private static JsonObject createDefaultConfig() {
        JsonObject defaultJsonObject = new JsonObject();
        // Add default weights for known items
        defaultJsonObject.addProperty("minecraft:stone", 810.0f);
        return defaultJsonObject;
    }

    private static void saveConfig(JsonObject jsonObject) throws IOException {
        try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(jsonObject, fileWriter);
        }
    }
}
