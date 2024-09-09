package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemWeightsConfigClient {
    private static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_client.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static float someSetting = 1.0f; // Default value for example

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
                someSetting = jsonObject.get("someSetting").getAsFloat();
            } else {
                // Create default config file if it doesn't exist
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("someSetting", 1.0f);
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
            jsonObject.addProperty("someSetting", someSetting);
            try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(jsonObject, fileWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
