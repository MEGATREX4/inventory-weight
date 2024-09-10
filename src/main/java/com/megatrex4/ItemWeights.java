package com.megatrex4;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ItemWeights {
    // Static weights for different categories
    public static float BUCKETS = 810.0f;
    public static float BOTTLES = 270.0f;
    public static float BLOCKS = 810.0f;
    public static float INGOTS = 90.0f;
    public static float NUGGETS = 10.0f;
    public static float ITEMS = 40.0f;

    private static final Map<String, Float> customItemWeights = new HashMap<>();

    // This method is used to load default weights
    public static void loadDefaultWeights(JsonObject jsonObject) {
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
        if (isStaticItem(item)) {
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
            }
        }

        // If the item is not recognized or does not match any category, return default for items
        return ITEMS;
    }

    public static void setItemWeight(String item, float weight) {
        customItemWeights.put(item, weight);
    }

    public static void loadWeightsFromConfig(JsonObject jsonObject) {
        // Load custom weights from the JSON object
        customItemWeights.clear();  // Clear existing custom weights
        jsonObject.entrySet().stream()
                .forEach(entry -> customItemWeights.put(entry.getKey(), entry.getValue().getAsFloat()));
    }

    public static Map<String, Float> getCustomItemWeights() {
        return customItemWeights;
    }

    public static Float getCustomItemWeight(String itemId) {
        return customItemWeights.get(itemId);
    }

    // Helper method to check if an item is static
    public static boolean isStaticItem(String item) {
        switch (item) {
            case "buckets":
            case "bottles":
            case "blocks":
            case "ingots":
            case "nuggets":
            case "items":
                return true;
            default:
                return false;
        }
    }
}
