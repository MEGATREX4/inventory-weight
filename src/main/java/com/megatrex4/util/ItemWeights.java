package com.megatrex4.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.megatrex4.InventoryWeight;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.util.ItemCategory;
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
            JsonElement value = entry.getValue();

            if (value.isJsonObject() && value.getAsJsonObject().has("weightWhenNbt")) {
                NbtWeightHandler handler = NbtWeightHandler.parse(key, value.getAsJsonObject());
                NbtWeightHandler.registerHandler(handler);
            } else if (value.isJsonPrimitive()) {
                customItemWeights.put(key, value.getAsFloat());
            }
        }
    }

    public static String getItemId(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }

    public static float getItemWeight(ItemStack stack) {
        PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(stack);
        ItemCategory category = categoryInfo.getCategory();
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();

        // Check for NBT-specific weight
        Float nbtWeight = NbtWeightHandler.getWeightFromNbt(stack);
        if (nbtWeight != null) {
            return nbtWeight; // Return the NBT-specific weight if found
        }

        // Existing custom weight check
        if (customItemWeights.containsKey(itemId)) {
            return customItemWeights.get(itemId);
        }

        if (isStaticItem(category)) {
            switch (category) {
                case BUCKETS, BOTTLES, INGOTS, NUGGETS, ITEMS ->
                        {
                            return ItemWeightCalculator.calculateItemWeight(categoryInfo.getStack(), category);
                        }
                case BLOCKS ->
                        {
                            return BlockWeightCalculator.calculateBlockWeight(categoryInfo.getStack(), category);
                        }
                case CREATIVE -> {
                    if (isBlock(stack)) {
                        return BlockWeightCalculator.calculateBlockWeight(stack, ItemCategory.CREATIVE);
                    } else {
                        return ItemWeightCalculator.calculateItemWeight(stack, ItemCategory.CREATIVE);
                    }
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
        ItemCategory category = ItemCategory.fromName(item);
        if (isStaticItem(category)) {
            return category.getBaseWeight();
        }

        // If the item is not recognized or does not match any category, return default for items
        return ITEMS;
    }


    public static float getItemWeight(ItemCategory category) {
        return getItemWeight(category.getName());
    }

    public static void setItemWeight(String item, float weight) {
        customItemWeights.put(item, weight);
    }

    public static void setItemWeight(ItemCategory category, float weight) {
        setItemWeight(category.getName(), weight);
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
    public static boolean isStaticItem(ItemCategory category) {
        return switch (category) {
            case BUCKETS, BOTTLES, BLOCKS, INGOTS, NUGGETS, ITEMS, CREATIVE -> true;
            default -> false;
        };
    }
}
