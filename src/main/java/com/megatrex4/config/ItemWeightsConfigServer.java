package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.InventoryWeight;
import com.megatrex4.util.InventoryWeightUtil;
import com.megatrex4.util.ItemWeights;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemWeightsConfigServer {
    public static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_server.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static float maxWeight = InventoryWeightUtil.MAXWEIGHT; // Default value
    public static float pocketWeight = InventoryWeightUtil.POCKET_WEIGHT; // Default value

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();

                // Load item weights from the config
                ItemWeights.loadWeightsFromConfig(jsonObject);

                // Load maxWeight from the config
                if (jsonObject.has("maxWeight")) {
                    maxWeight = jsonObject.get("maxWeight").getAsFloat();
                }

                // Load pocketWeight from the config if present
                if (jsonObject.has("pocketWeight")) {
                    pocketWeight = jsonObject.get("pocketWeight").getAsFloat(); // Update server-side value
                    InventoryWeightUtil.POCKET_WEIGHT = pocketWeight; // Also update the util class
                }

                if (jsonObject.has("realistic_mode")) {
                    InventoryWeight.REALISTIC_MODE = jsonObject.get("realistic_mode").getAsBoolean();
                } else {
                    jsonObject.addProperty("realistic_mode", InventoryWeight.REALISTIC_MODE);
                }

            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("buckets", ItemWeights.BUCKETS);
                jsonObject.addProperty("bottles", ItemWeights.BOTTLES);
                jsonObject.addProperty("blocks", ItemWeights.BLOCKS);
                jsonObject.addProperty("ingots", ItemWeights.INGOTS);
                jsonObject.addProperty("nuggets", ItemWeights.NUGGETS);
                jsonObject.addProperty("items", ItemWeights.ITEMS);
                jsonObject.addProperty("creative", ItemWeights.CREATIVE);
                jsonObject.addProperty("maxWeight", maxWeight);
                jsonObject.addProperty("pocketWeight", pocketWeight);
                jsonObject.addProperty("realistic_mode", InventoryWeight.REALISTIC_MODE);
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
            jsonObject.addProperty("buckets", ItemWeights.getItemWeight("buckets"));
            jsonObject.addProperty("bottles", ItemWeights.getItemWeight("bottles"));
            jsonObject.addProperty("blocks", ItemWeights.getItemWeight("blocks"));
            jsonObject.addProperty("ingots", ItemWeights.getItemWeight("ingots"));
            jsonObject.addProperty("nuggets", ItemWeights.getItemWeight("nuggets"));
            jsonObject.addProperty("items", ItemWeights.getItemWeight("items"));
            jsonObject.addProperty("creative", ItemWeights.getItemWeight("creative"));
            jsonObject.addProperty("maxWeight", maxWeight);
            jsonObject.addProperty("pocketWeight", pocketWeight);
            jsonObject.addProperty("realistic_mode", InventoryWeight.REALISTIC_MODE);

            try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(jsonObject, fileWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static float getMaxWeightFromConfig() {
        return maxWeight;
    }

    public static void setMaxWeight(float value) {
        maxWeight = value;
        saveConfig();
    }
}
