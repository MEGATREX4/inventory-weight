package com.megatrex4;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class PlayerDataHandler {
    public static final String MAX_WEIGHT_KEY = "inventoryweight:max";

    public static void setPlayerMaxWeight(ServerPlayerEntity player, float value) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                (nbt) -> InventoryWeightState.fromNbt(nbt),
                () -> new InventoryWeightState(),
                "inventoryweight_data"
        );
        state.setMaxWeight(value);
        world.getPersistentStateManager().set("inventoryweight_data", state);
    }

    public static float getPlayerMaxWeight(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                (nbt) -> InventoryWeightState.fromNbt(nbt),
                () -> new InventoryWeightState(),
                "inventoryweight_data"
        );
        return state.getMaxWeight();
    }

    public static float getPlayerCurrentWeight(ServerPlayerEntity player) {
        float totalWeight = 0;

        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();
                float itemWeight = ItemWeights.getItemWeight(itemId);

                if (itemWeight == 0) {
                    String itemCategory = getItemCategory(stack);
                    itemWeight = ItemWeights.getItemWeight(itemCategory.toLowerCase());
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
            return "buckets";
        } else if (itemId.contains("bottle")) {
            return "bottles";
        } else if (itemId.contains("ingot")) {
            return "ingots";
        } else if (itemId.contains("nugget")) {
            return "nuggets";
        } else if (isBlock(stack)) {
            return "blocks";
        } else {
            return "items";
        }
    }

    public static boolean isBlock(ItemStack stack) {
        // Check if the ItemStack's item is an instance of BlockItem
        return stack.getItem() instanceof BlockItem;
    }
}
