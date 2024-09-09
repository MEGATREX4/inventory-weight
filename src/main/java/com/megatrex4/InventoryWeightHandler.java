package com.megatrex4;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class InventoryWeightHandler {
    public static float calculateInventoryWeight(ServerPlayerEntity player) {
        float totalWeight = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().toString(); // Simplify item name extraction as needed
                totalWeight += ItemWeights.getItemWeight(itemName) * stack.getCount();
            }
        }
        return totalWeight;
    }

    public static void checkWeight(ServerPlayerEntity player) {
        float maxWeight = PlayerDataHandler.getPlayerMaxWeight(player);
        float inventoryWeight = calculateInventoryWeight(player);
        if (inventoryWeight > maxWeight) {
            // Implement actions if weight exceeds the limit
        }
    }

    public static void updatePlayerWeight(ServerWorld world, ServerPlayerEntity player) {
        float currentWeight = calculateInventoryWeight(player);
    }

    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            updatePlayerWeight(world, player);
        }
    }
}
