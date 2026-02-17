package com.megatrex4.datapack;

import com.google.gson.*;
import com.megatrex4.InventoryWeight;
import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.util.NbtPocketHandler;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class DatapackPocketWeightLoader {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, String> pocketSourceMap = new HashMap<>();
    private static final Set<String> conflictedItems = new HashSet<>();
    private static final Map<String, Integer> pocketWeightMap = new HashMap<>();

    public static void loadDatapackPocketWeights(ResourceManager resourceManager) {
        pocketSourceMap.clear();
        conflictedItems.clear();
        pocketWeightMap.clear();

        try {
            // Find all JSON files in the inventory_weight/pockets directory
            for (Map.Entry<Identifier, Resource> entry : resourceManager.findResources(
                    "inventory_weight/pockets", path -> path.getPath().endsWith(".json")).entrySet()) {
                
                Identifier identifier = entry.getKey();
                Resource resource = entry.getValue();
                
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        loadPocketsFromJsonObject(jsonObject, identifier.toString());
                    } else if (jsonElement.isJsonArray()) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        loadPocketsFromJsonArray(jsonArray, identifier.toString());
                    }
                } catch (Exception e) {
                    InventoryWeight.LOGGER.error("Failed to load pocket weights from {}: {}", identifier, e.getMessage());
                }
            }
            
            // Log any conflicts
            if (!conflictedItems.isEmpty()) {
                for (String itemId : conflictedItems) {
                    InventoryWeight.LOGGER.warn(
                        "Pocket definition for item '{}' is defined in multiple JSON files. Using default value.",
                        itemId
                    );
                }
            }
        } catch (IOException e) {
            InventoryWeight.LOGGER.error("Error scanning inventory_weight/pockets from datapack: {}", e.getMessage());
        }
    }

    private static void loadPocketsFromJsonObject(JsonObject jsonObject, String filePath) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String itemId = entry.getKey();
            JsonElement value = entry.getValue();
            
            // Check for conflicts
            if (pocketSourceMap.containsKey(itemId) && !pocketSourceMap.get(itemId).equals(filePath)) {
                conflictedItems.add(itemId);
                InventoryWeight.LOGGER.warn(
                    "Conflict detected for pocket definition '{}': previously defined in {}, now in {}. Keeping first definition.",
                    itemId, pocketSourceMap.get(itemId), filePath
                );
                continue; // Skip this entry, keep the first one
            }
            
            if (!conflictedItems.contains(itemId)) {
                if (value.isJsonObject()) {
                    // Handle object format with pockets field
                    JsonObject valueObj = value.getAsJsonObject();
                    if (valueObj.has("pocketsWhenNbt")) {
                        // Handle NBT-specific pockets
                        NbtPocketHandler handler = NbtPocketHandler.parse(itemId, valueObj);
                        NbtPocketHandler.registerHandler(handler);
                        pocketSourceMap.put(itemId, filePath);
                    } else if (valueObj.has("pockets")) {
                        int pockets = valueObj.get("pockets").getAsInt();
                        pocketWeightMap.put(itemId, pockets);
                        pocketSourceMap.put(itemId, filePath);
                    }
                } else if (value.isJsonPrimitive()) {
                    // Handle simple numeric value (pockets count)
                    try {
                        int pockets = value.getAsInt();
                        pocketWeightMap.put(itemId, pockets);
                        pocketSourceMap.put(itemId, filePath);
                    } catch (NumberFormatException e) {
                        InventoryWeight.LOGGER.error("Invalid pocket count for item '{}' in {}: {}", itemId, filePath, value);
                    }
                }
            }
        }
    }

    private static void loadPocketsFromJsonArray(JsonArray jsonArray, String filePath) {
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject itemObj = element.getAsJsonObject();
                
                if (!itemObj.has("item")) {
                    InventoryWeight.LOGGER.error("Pocket object in {} is missing 'item' field", filePath);
                    continue;
                }
                
                String itemId = itemObj.get("item").getAsString();
                
                // Check for conflicts
                if (pocketSourceMap.containsKey(itemId) && !pocketSourceMap.get(itemId).equals(filePath)) {
                    conflictedItems.add(itemId);
                    InventoryWeight.LOGGER.warn(
                        "Conflict detected for pocket definition '{}': previously defined in {}, now in {}. Keeping first definition.",
                        itemId, pocketSourceMap.get(itemId), filePath
                    );
                    continue;
                }
                
                if (!conflictedItems.contains(itemId)) {
                    if (itemObj.has("pocketsWhenNbt")) {
                        // Handle NBT-specific pockets in array format
                        NbtPocketHandler handler = NbtPocketHandler.parse(itemId, itemObj);
                        NbtPocketHandler.registerHandler(handler);
                        pocketSourceMap.put(itemId, filePath);
                    } else if (itemObj.has("pockets")) {
                        int pockets = itemObj.get("pockets").getAsInt();
                        pocketWeightMap.put(itemId, pockets);
                        pocketSourceMap.put(itemId, filePath);
                    } else {
                        InventoryWeight.LOGGER.error("Pocket definition for item '{}' in {} is missing 'pockets' or 'pocketsWhenNbt' field", itemId, filePath);
                    }
                }
            }
        }
    }

    /**
     * Gets the number of pockets for an item based on datapack data.
     * Returns null if no pocket definition is found.
     */
    public static Integer getPocketsForItem(String itemId) {
        return pocketWeightMap.get(itemId);
    }

    public static Map<String, String> getPocketSourceMap() {
        return Collections.unmodifiableMap(pocketSourceMap);
    }

    public static Set<String> getConflictedItems() {
        return Collections.unmodifiableSet(conflictedItems);
    }

    public static Map<String, Integer> getPocketWeightMap() {
        return Collections.unmodifiableMap(pocketWeightMap);
    }
}
