package com.megatrex4;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class InventoryWeightHandler {
    public static long calculateInventoryWeight(ServerPlayerEntity player) {
        long totalWeight = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().toString(); // Simplify item name extraction as needed
                totalWeight += ItemWeights.getItemWeight(itemName) * stack.getCount();
            }
        }
        return totalWeight;
    }

    public static void checkWeight(ServerPlayerEntity player) {
        long maxWeight = PlayerDataHandler.getPlayerMaxWeight(player);
        long inventoryWeight = calculateInventoryWeight(player);
        if (inventoryWeight > maxWeight) {
            // Implement actions if weight exceeds the limit
        }
    }
}
