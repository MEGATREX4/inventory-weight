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

    // Get player's specific max weight (base weight)
    public static float getPlayerMaxWeight(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                (nbt) -> InventoryWeightState.fromNbt(nbt),
                () -> new InventoryWeightState(),
                "inventoryweight_data"
        );
        return state.getMaxWeight();
    }

    public static final String MULTIPLIER_KEY = "inventoryweight:multiplier";

    // Set player's specific weight multiplier
    public static void setPlayerMultiplier(ServerPlayerEntity player, float multiplier) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                InventoryWeightState::fromNbt,
                InventoryWeightState::new,
                "inventoryweight_data"
        );
        state.setPlayerMultiplier(player.getUuidAsString(), multiplier);
        world.getPersistentStateManager().set("inventoryweight_data", state);
    }

    // Get player's specific weight multiplier
    public static float getPlayerMultiplier(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                InventoryWeightState::fromNbt,
                InventoryWeightState::new,
                "inventoryweight_data"
        );
        return state.getPlayerMultiplier(player.getUuidAsString());
    }

    //get player`s maxweight with multiplier
    public static float getPlayerMaxWeightWithMultiplier(ServerPlayerEntity player) {
        return getPlayerMaxWeight(player) + getPlayerMultiplier(player);
    }

    public static String getItemCategory(ItemStack stack) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();

        if (isOperatorUtilitiesItem(itemId)) {
            return "creative";
        }
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

    private static boolean isOperatorUtilitiesItem(String itemId) {
        return itemId.equals("minecraft:barrier") ||
                itemId.equals("minecraft:light") ||
                itemId.equals("minecraft:structure_block") ||
                itemId.equals("minecraft:jigsaw") ||
                itemId.contains("command_block") ||
                itemId.equals("minecraft:structure_void") ||
                itemId.contains("portal") ||
                itemId.equals("minecraft:debug_stick") ||
                itemId.equals("minecraft:spawner") ||
                itemId.contains("spawn_egg") ||
                itemId.equals("minecraft:bedrock");
    }

    public static boolean isBlock(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }
}
