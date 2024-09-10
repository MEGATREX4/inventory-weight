package com.megatrex4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ItemWeights {
    // Static weights for different categories
    public static float BUCKETS = InventoryWeightUtil.BUCKETS;
    public static float BOTTLES = InventoryWeightUtil.BOTTLES;
    public static float BLOCKS = InventoryWeightUtil.BLOCKS;
    public static float INGOTS = InventoryWeightUtil.INGOTS;
    public static float NUGGETS = InventoryWeightUtil.NUGGETS;
    public static float ITEMS = InventoryWeightUtil.ITEMS;

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

    public static void loadWeightsFromConfig(JsonObject config) {
        if (config.has("maxWeight")) {
            // This is part of the server configuration
            InventoryWeightUtil.MAXWEIGHT = config.get("maxWeight").getAsFloat();
        }

        if (config.has("buckets")) {
            // Load item-specific weights (assuming this is server config)
            BUCKETS = config.get("buckets").getAsFloat();
            BOTTLES = config.get("bottles").getAsFloat();
            BLOCKS = config.get("blocks").getAsFloat();
            INGOTS = config.get("ingots").getAsFloat();
            NUGGETS = config.get("nuggets").getAsFloat();
            ITEMS = config.get("items").getAsFloat();
        }

        for (Map.Entry<String, JsonElement> entry : config.entrySet()) {
            // Assuming the remaining entries are dynamic item weights from items config
            if (!entry.getKey().equals("maxWeight") && !entry.getKey().equals("buckets") && !entry.getKey().equals("bottles") &&
                    !entry.getKey().equals("blocks") && !entry.getKey().equals("ingots") && !entry.getKey().equals("nuggets") &&
                    !entry.getKey().equals("items")) {

                customItemWeights.put(entry.getKey(), entry.getValue().getAsFloat());
            }
        }
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
