package com.megatrex4.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.ItemWeights;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ItemWeightsConfigServer {
    private static final Path CONFIG_PATH = Paths.get("config/inventoryweight", "inventory_weights_server.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
                ItemWeights.loadWeightsFromConfig(jsonObject);
            } else {
                Files.createDirectories(CONFIG_PATH.getParent());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("buckets", 81.0f);
                jsonObject.addProperty("bottles", 27.0f);
                jsonObject.addProperty("blocks", 81.0f);
                jsonObject.addProperty("ingots", 9.0f);
                jsonObject.addProperty("nuggets", 1.0f);
                jsonObject.addProperty("items", 0.5f);
                Files.write(CONFIG_PATH, GSON.toJson(jsonObject).getBytes());
                ItemWeights.loadWeightsFromConfig(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
