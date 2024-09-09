package com.megatrex4;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ItemWeights {
    public static float BUCKETS = 81.0f;
    public static float BOTTLES = 27.0f;
    public static float BLOCKS = 81.0f;
    public static float INGOTS = 9.0f;
    public static float NUGGETS = 1.0f;
    public static float ITEMS = 0.5f;

    private static final Map<String, Float> customItemWeights = new HashMap<>();

    // This method loads default weights from the server config
    public static void loadDefaultWeightsFromServerConfig(JsonObject jsonObject) {
        BUCKETS = jsonObject.get("buckets").getAsFloat();
        BOTTLES = jsonObject.get("bottles").getAsFloat();
        BLOCKS = jsonObject.get("blocks").getAsFloat();
        INGOTS = jsonObject.get("ingots").getAsFloat();
        NUGGETS = jsonObject.get("nuggets").getAsFloat();
        ITEMS = jsonObject.get("items").getAsFloat();
    }

    public static float getItemWeight(String item) {
        // First check custom weights
        if (customItemWeights.containsKey(item)) {
            return customItemWeights.get(item);
        }

        // Check if item falls into default categories
        switch (item) {
            case "buckets":
                return BUCKETS;
            case "bottles":
                return BOTTLES;
            case "blocks":
                return BLOCKS;
            case "ingots":
                return INGOTS;
            case "nuggets":
                return NUGGETS;
            case "items":
                return ITEMS;
            default:
                // If the item is not recognized or does not match any category, return default for items
                return ITEMS;
        }
    }

    public static void setItemWeight(String item, float weight) {
        customItemWeights.put(item, weight);
    }

    public static void loadWeightsFromConfig(JsonObject jsonObject) {
        loadDefaultWeightsFromServerConfig(jsonObject);  // Update this to load default values

        customItemWeights.clear();  // Clear existing custom weights
        jsonObject.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("buckets") &&
                        !entry.getKey().equals("bottles") &&
                        !entry.getKey().equals("blocks") &&
                        !entry.getKey().equals("ingots") &&
                        !entry.getKey().equals("nuggets") &&
                        !entry.getKey().equals("items"))
                .forEach(entry -> customItemWeights.put(entry.getKey(), entry.getValue().getAsFloat()));
    }

    public static Map<String, Float> getCustomItemWeights() {
        return customItemWeights;
    }

    public static Float getCustomItemWeight(String itemId) {
        return customItemWeights.get(itemId);
    }
}
