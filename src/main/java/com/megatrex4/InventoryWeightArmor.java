package com.megatrex4;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.util.InventoryWeightUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InventoryWeightArmor {
    private static float POCKET_WEIGHT = InventoryWeightUtil.POCKET_WEIGHT;
    private static final Map<String, Integer> itemPocketsMap = new HashMap<>();
    private static final Gson GSON = new Gson();

    public static boolean hasPockets(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            int pockets = InventoryWeightArmor.getPocketsBasedOnProtection(armorItem);
            return pockets > 0;
        }
        return false;
    }

    public static int getPockets(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return InventoryWeightArmor.getPocketsBasedOnProtection(armorItem);
        }
        return 0;
    }


    public static float getPocketWeight() {
        return ItemWeightsConfigServer.pocketWeight;
    }

    public static void setPocketWeight(String pocketWeight, Float newValue) {
        ItemWeightsConfigServer.pocketWeight = newValue;
        InventoryWeightArmor.POCKET_WEIGHT = newValue;
        ItemWeightsConfigServer.saveConfig();
    }

    // Load datapack data using Minecraft's resource system
    public static void loadDatapackData(ResourceManager resourceManager) {
        // Clear existing data to ensure no old values are kept
        itemPocketsMap.clear();

        try {
            for (Map.Entry<Identifier, Resource> entry : resourceManager.findResources("inventoryweight", path -> path.getPath().endsWith(".json")).entrySet()) {
                Identifier identifier = entry.getKey();
                Resource resource = entry.getValue();
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonArray itemsArray = jsonObject.getAsJsonArray("items");

                    for (JsonElement element : itemsArray) {
                        JsonObject itemObject = element.getAsJsonObject();
                        String itemId = itemObject.get("item").getAsString();
                        int pockets = itemObject.get("pockets").getAsInt();
                        itemPocketsMap.put(itemId, pockets);
                        System.out.println("Loaded item: " + itemId + " with pockets: " + pockets); // Debugging line
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the error
        }
    }


    // Get pockets based on datapack data or default
    public static int getPocketsBasedOnProtection(ArmorItem armorItem) {
        String itemId = Registries.ITEM.getId(armorItem).toString();
        Integer pocketsFromDatapack = itemPocketsMap.get(itemId);
        if (pocketsFromDatapack != null) {
            return pocketsFromDatapack;
        }
        // Default calculation if no datapack value
        int protectionValue = armorItem.getProtection();
        return Math.max(1, 7 - (int)(protectionValue / 1.2));
    }

    // Calculate the total armor weight based on pockets
    public static float calculateArmorWeight(ServerPlayerEntity player) {
        float totalArmorWeight = 0;

        for (ItemStack armorPiece : player.getInventory().armor) {
            if (armorPiece.getItem() instanceof ArmorItem armorItem) {
                int pockets = getPocketsBasedOnProtection(armorItem);
                totalArmorWeight += pockets * POCKET_WEIGHT;
            }
        }

        return totalArmorWeight;
    }


}
