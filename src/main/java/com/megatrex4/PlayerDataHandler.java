package com.megatrex4;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerDataHandler {
    public static final String MAX_WEIGHT_KEY = "inventoryweight:max";

    public static void setPlayerMaxWeight(ServerPlayerEntity player, long value) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                (nbt) -> InventoryWeightState.fromNbt(nbt),
                () -> new InventoryWeightState(),
                "inventoryweight_data"
        );
        state.setMaxWeight(value);
        world.getPersistentStateManager().set("inventoryweight_data", state);
    }

    public static long getPlayerMaxWeight(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                (nbt) -> InventoryWeightState.fromNbt(nbt),
                () -> new InventoryWeightState(),
                "inventoryweight_data"
        );
        return state.getMaxWeight();
    }

    public static long getPlayerCurrentWeight(ServerPlayerEntity player) {
        long totalWeight = 0;

        // Access player's main inventory using the inventory method
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().getName().getString().toLowerCase(); // Get the item's name
                long itemWeight = ItemWeights.getItemWeight(itemName);
                totalWeight += itemWeight * stack.getCount(); // Calculate total weight of this item
            }
        }

        return totalWeight;
    }



}
