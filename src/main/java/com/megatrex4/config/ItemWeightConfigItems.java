package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.ItemWeights;

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
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    float weight = jsonObject.get(key).getAsFloat();
                    ItemWeights.setItemWeight(key, weight);
                }
            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("minecraft:diamond", 5.0f);
                jsonObject.addProperty("minecraft:gold_ingot", 2.0f);
                try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
                    GSON.toJson(jsonObject, fileWriter);
                }
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
            try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(jsonObject, fileWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
