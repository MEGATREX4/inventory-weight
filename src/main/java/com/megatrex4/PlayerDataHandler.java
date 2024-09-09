package com.megatrex4;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

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

        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();

                long itemWeight = ItemWeights.getItemWeight(itemId);

                if (itemWeight == 0) {
                    String itemCategory = getItemCategory(stack);
                    itemWeight = ItemWeights.getItemWeight(itemCategory);
                }

                // Calculate total weight of this item
                totalWeight += itemWeight * stack.getCount();
            }
        }

        return totalWeight;
    }

    public static String getItemCategory(ItemStack stack) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();

        if (itemId.contains("bucket")) {
            return "BUCKETS";
        } else if (itemId.contains("bottle")) {
            return "BOTTLES";
        } else if (itemId.contains("ingot")) {
            return "INGOTS";
        } else if (itemId.contains("nugget")) {
            return "NUGGETS";
        } else if (isBlock(stack)) {
            return "BLOCKS";
        } else {
            return "ITEMS";
        }
    }

    public static boolean isBlock(ItemStack stack) {
        // This method checks if the ItemStack corresponds to a block
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        return itemId != null && Registries.BLOCK.get(itemId) != null;
    }
}
