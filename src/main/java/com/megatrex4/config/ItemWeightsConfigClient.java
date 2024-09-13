package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.hud.InventoryWeightHUD;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemWeightsConfigClient {
    private static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_client.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String hudPosition = "BOTTOM_RIGHT"; // Default position as string
    public static float xOffset = 0.5f; // Default x offset for CUSTOM position
    public static float yOffset = 0.5f; // Default y offset for CUSTOM position

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
                if (jsonObject.has("hudPosition")) {
                    hudPosition = jsonObject.get("hudPosition").getAsString();
                }
                xOffset = jsonObject.has("xOffset") ? jsonObject.get("xOffset").getAsFloat() : 0.5f;
                yOffset = jsonObject.has("yOffset") ? jsonObject.get("yOffset").getAsFloat() : 0.5f;
            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("hudPosition", hudPosition);
                jsonObject.addProperty("xOffset", xOffset);
                jsonObject.addProperty("yOffset", yOffset);
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
            jsonObject.addProperty("hudPosition", hudPosition);
            jsonObject.addProperty("xOffset", xOffset);
            jsonObject.addProperty("yOffset", yOffset);
            try (FileWriter fileWriter = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(jsonObject, fileWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
