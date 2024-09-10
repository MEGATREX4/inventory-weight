package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.InventoryWeightState;
import com.megatrex4.ItemWeights;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ItemWeightsConfigServer {
    private static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_server.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
                ItemWeights.loadWeightsFromConfig(jsonObject);

                // Load maxWeight from the config
                if (jsonObject.has("maxWeight")) {
                    InventoryWeightState state = InventoryWeightState.fromNbt(null);
                    state.setMaxWeight(jsonObject.get("maxWeight").getAsFloat());
                }

            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("buckets", 810.0f);
                jsonObject.addProperty("bottles", 270.0f);
                jsonObject.addProperty("blocks", 810.0f);
                jsonObject.addProperty("ingots", 90.0f);
                jsonObject.addProperty("nuggets", 10.0f);
                jsonObject.addProperty("items", 5.0f);
                jsonObject.addProperty("maxWeight", 10000.0f);
                Files.write(CONFIG_PATH, GSON.toJson(jsonObject).getBytes());
                ItemWeights.loadWeightsFromConfig(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveConfig() {
        try {
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, Float> entry : ItemWeights.getCustomItemWeights().entrySet()) {
                jsonObject.addProperty(entry.getKey(), entry.getValue());
            }

            // Save maxWeight to the config
            InventoryWeightState state = InventoryWeightState.fromNbt(null); // Assuming the state is loaded somewhere else
            jsonObject.addProperty("maxWeight", state.getMaxWeight());

            try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(jsonObject, fileWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get maxWeight from the configuration file
    public static float getMaxWeightFromConfig() {
        float maxWeight = 10000.0f; // Default value
        try {
            if (Files.exists(CONFIG_PATH)) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
                if (jsonObject.has("maxWeight")) {
                    maxWeight = jsonObject.get("maxWeight").getAsFloat();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maxWeight;
    }
}
