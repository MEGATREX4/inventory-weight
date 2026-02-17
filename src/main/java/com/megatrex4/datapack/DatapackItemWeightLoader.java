package com.megatrex4.datapack;

import com.google.gson.*;
import com.megatrex4.InventoryWeight;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.util.NbtWeightHandler;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class DatapackItemWeightLoader {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, String> itemSourceMap = new HashMap<>();
    private static final Set<String> conflictedItems = new HashSet<>();

    public static void loadDatapackItemWeights(ResourceManager resourceManager) {
        itemSourceMap.clear();
        conflictedItems.clear();

        try {
            // Find all JSON files in the inventory_weight/items directory
            for (Map.Entry<Identifier, Resource> entry : resourceManager.findResources(
                    "inventory_weight/items", path -> path.getPath().endsWith(".json")).entrySet()) {
                
                Identifier identifier = entry.getKey();
                Resource resource = entry.getValue();
                
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        loadItemsFromJsonObject(jsonObject, identifier.toString());
                    } else if (jsonElement.isJsonArray()) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        loadItemsFromJsonArray(jsonArray, identifier.toString());
                    }
                } catch (Exception e) {
                    InventoryWeight.LOGGER.error("Failed to load item weights from {}: {}", identifier, e.getMessage());
                }
            }
            
            // Log any conflicts
            if (!conflictedItems.isEmpty()) {
                for (String itemId : conflictedItems) {
                    InventoryWeight.LOGGER.warn(
                        "Item '{}' is defined in multiple JSON files. Using default weight value.",
                        itemId
                    );
                }
            }
        } catch (IOException e) {
            InventoryWeight.LOGGER.error("Error scanning inventory_weight/items from datapack: {}", e.getMessage());
        }
    }

    private static void loadItemsFromJsonObject(JsonObject jsonObject, String filePath) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String itemId = entry.getKey();
            JsonElement value = entry.getValue();
            
            // Check for conflicts
            if (itemSourceMap.containsKey(itemId) && !itemSourceMap.get(itemId).equals(filePath)) {
                conflictedItems.add(itemId);
                InventoryWeight.LOGGER.warn(
                    "Conflict detected for item '{}': previously defined in {}, now in {}. Keeping first definition.",
                    itemId, itemSourceMap.get(itemId), filePath
                );
                continue; // Skip this entry, keep the first one
            }
            
            if (!conflictedItems.contains(itemId)) {
                if (value.isJsonObject()) {
                    // Handle NBT-specific weight
                    JsonObject valueObj = value.getAsJsonObject();
                    if (valueObj.has("weightWhenNbt")) {
                        NbtWeightHandler handler = NbtWeightHandler.parse(itemId, valueObj);
                        NbtWeightHandler.registerHandler(handler);
                        itemSourceMap.put(itemId, filePath);
                    } else if (valueObj.has("weight")) {
                        // Handle regular weight with object format
                        float weight = valueObj.get("weight").getAsFloat();
                        ItemWeights.setItemWeight(itemId, weight);
                        itemSourceMap.put(itemId, filePath);
                    }
                } else if (value.isJsonPrimitive()) {
                    // Handle simple numeric weight
                    try {
                        float weight = value.getAsFloat();
                        ItemWeights.setItemWeight(itemId, weight);
                        itemSourceMap.put(itemId, filePath);
                    } catch (NumberFormatException e) {
                        InventoryWeight.LOGGER.error("Invalid weight value for item '{}' in {}: {}", itemId, filePath, value);
                    }
                }
            }
        }
    }

    private static void loadItemsFromJsonArray(JsonArray jsonArray, String filePath) {
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();
                
                if (!itemObj.has("item")) {
                    InventoryWeight.LOGGER.error("Item object in {} is missing 'item' field", filePath);
                    continue;
                }
                
                String itemId = itemObj.get("item").getAsString();
                
                // Check for conflicts
                if (itemSourceMap.containsKey(itemId) && !itemSourceMap.get(itemId).equals(filePath)) {
                    conflictedItems.add(itemId);
                    InventoryWeight.LOGGER.warn(
                        "Conflict detected for item '{}': previously defined in {}, now in {}. Keeping first definition.",
                        itemId, itemSourceMap.get(itemId), filePath
                    );
                    continue;
                }
                
                if (!conflictedItems.contains(itemId)) {
                    if (itemObj.has("weight")) {
                        float weight = itemObj.get("weight").getAsFloat();
                        ItemWeights.setItemWeight(itemId, weight);
                        itemSourceMap.put(itemId, filePath);
                    } else if (itemObj.has("weightWhenNbt")) {
                        // Handle NBT-specific weight in array format
                        NbtWeightHandler handler = NbtWeightHandler.parse(itemId, itemObj);
                        NbtWeightHandler.registerHandler(handler);
                        itemSourceMap.put(itemId, filePath);
                    } else {
                        InventoryWeight.LOGGER.error("Item '{}' in {} is missing 'weight' or 'weightWhenNbt' field", itemId, filePath);
                    }
                }
            }
        }
    }

    public static Map<String, String> getItemSourceMap() {
        return Collections.unmodifiableMap(itemSourceMap);
    }

    public static Set<String> getConflictedItems() {
        return Collections.unmodifiableSet(conflictedItems);
    }
}
