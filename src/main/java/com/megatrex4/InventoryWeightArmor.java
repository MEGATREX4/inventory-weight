package com.megatrex4;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class InventoryWeightArmor {
    private static final float POCKET_WEIGHT = 500;
    private static final Map<String, Integer> itemPocketsMap = new HashMap<>();

    static {
        // Example data; this should be loaded from JSON or datapacks
        itemPocketsMap.put("minecraft:leather_helmet", 4);
        itemPocketsMap.put("minecraft:gold_helmet", 4);
        // Add more entries as needed
    }

    // Public method to access itemPocketsMap
    public static Integer getItemPockets(String itemId) {
        return itemPocketsMap.get(itemId);
    }

    // Calculate the total armor weight based on pockets
    public static float calculateArmorWeight(ServerPlayerEntity player) {
        float totalArmorWeight = 0;

        for (ItemStack armorPiece : player.getInventory().armor) {
            String itemId = Registries.ITEM.getId(armorPiece.getItem()).toString();
            Integer pockets = getItemPockets(itemId); // Use the public method to get pockets
            if (pockets != null) {
                totalArmorWeight += pockets * POCKET_WEIGHT;
            }
        }

        return totalArmorWeight; // Return total weight directly
    }

}

