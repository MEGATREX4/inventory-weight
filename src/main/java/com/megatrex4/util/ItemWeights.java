package com.megatrex4.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.megatrex4.InventoryWeight;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.HashMap;
import java.util.Map;

import static com.megatrex4.data.PlayerDataHandler.isBlock;

public class ItemWeights {
    // Static weights for different categories
    public static float BUCKETS = InventoryWeightUtil.BUCKETS;
    public static float BOTTLES = InventoryWeightUtil.BOTTLES;
    public static float BLOCKS = InventoryWeightUtil.BLOCKS;
    public static float INGOTS = InventoryWeightUtil.INGOTS;
    public static float NUGGETS = InventoryWeightUtil.NUGGETS;
    public static float ITEMS = InventoryWeightUtil.ITEMS;

    public static float CREATIVE = InventoryWeightUtil.CREATIVE;

    private static final Map<String, Float> customItemWeights = new HashMap<>();

    public static void loadCustomWeightsFromConfig(JsonObject jsonObject) {
        customItemWeights.clear(); // Clear previous data

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            // Only load dynamic items
            if (ItemWeightConfigItems.isDynamicItem(key)) {
                customItemWeights.put(key, entry.getValue().getAsFloat());
            }
        }
    }

    public static String getItemId(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }

    public static float getItemWeight(ItemStack stack) {
        PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(stack);
        String category = categoryInfo.getCategory();
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();

        // First check custom weights
        if (customItemWeights.containsKey(itemId)) {
            return customItemWeights.get(itemId);
        }

        if (isStaticItem(category)) {
            switch (category) {
                case "buckets", "bottles", "ingots", "nuggets", "items":
                    return ItemWeightCalculator.calculateItemWeight(categoryInfo.getStack(), category);
                case "blocks":
                    return BlockWeightCalculator.calculateBlockWeight(categoryInfo.getStack(), category);
                case "creative":
                    if (isBlock(stack)) {
                        return BlockWeightCalculator.calculateBlockWeight(stack, "creative");
                    } else {
                        return ItemWeightCalculator.calculateItemWeight(stack, "creative");
                    }
            }
        }
        return ITEMS;
    }




    public static float getItemWeight(String item) {
        // First check custom weights
        if (customItemWeights.containsKey(item)) {
            return customItemWeights.get(item);
        }

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
                case "creative":
                    return CREATIVE;
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
        // Assuming the issue lies here
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            // Verify if it's a number and process accordingly
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    float value = primitive.getAsFloat();
                    setItemWeight(key, value);
                } else {
					InventoryWeight.LOGGER.error("Unsupported primitive type for key {}", key);
                }
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
		return switch (item) {
			case "buckets", "bottles", "blocks", "ingots", "nuggets", "items", "creative" -> true;
			default -> false;
		};
    }
}
