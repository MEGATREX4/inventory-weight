package com.megatrex4.data;

import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.InventoryWeightState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerDataHandler {
    public static final String MAX_WEIGHT_KEY = "inventoryweight:max";
    public static final String ARMOR_MAX_KEY = "inventoryweight:armor_max";

    public static void setPlayerMaxWeight(ServerPlayerEntity player, float value) {
        MinecraftServer server = player.getServer();
        InventoryWeightState state = server.getOverworld().getPersistentStateManager().getOrCreate(
                InventoryWeightState::fromNbt,
                InventoryWeightState::new,
                "inventoryweight_data"
        );
        state.setMaxWeight(server, value);
        server.getOverworld().getPersistentStateManager().set("inventoryweight_data", state);
    }


    // Set player's specific weight multiplier
    public static void setPlayerMultiplier(ServerPlayerEntity player, float multiplier) {
        MinecraftServer server = player.getServer();
        InventoryWeightState state = server.getOverworld().getPersistentStateManager().getOrCreate(
                InventoryWeightState::fromNbt,
                InventoryWeightState::new,
                "inventoryweight_data"
        );
        state.setPlayerMultiplier(server, player.getUuidAsString(), multiplier);
        server.getOverworld().getPersistentStateManager().set("inventoryweight_data", state);
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

    // Get the player's maximum armor weight
    public static float getPlayerMaxArmorWeight(ServerPlayerEntity player) {
        return InventoryWeightArmor.calculateArmorWeight(player); // Convert weight to multiplier factor, adjust as necessary
    }

    // Get player’s max weight with multiplier and armor weight
    public static float getPlayerMaxWeightWithMultiplier(ServerPlayerEntity player) {
        return getPlayerMaxWeight(player) + getPlayerMultiplier(player) + getPlayerMaxArmorWeight(player);
    }

    public static ItemCategoryInfo getItemCategoryInfo(ItemStack stack) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();
        String category;

        if (isCreative(itemId)) {
            category = "creative";
        } else if (itemId.contains("bucket")) {
            category = "buckets";
        } else if (itemId.contains("bottle") || itemId.contains("potion")) {
            category = "bottles";
        } else if (itemId.contains("ingot") || itemId.contains("alloy") || itemId.contains("gem") || itemId.contains("shard")) {
            category = "ingots";
        } else if (itemId.contains("nugget")) {
            category = "nuggets";
        } else if (isBlock(stack)) {
            category = "blocks";
        } else {
            category = "items";
        }

        return new ItemCategoryInfo(stack, category);
    }

    private static boolean isCreative(String itemId) {
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
                itemId.equals("minecraft:bedrock") ||
                itemId.contains("creative");
    }

    public static boolean isBlock(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    public static class ItemCategoryInfo {
        private final ItemStack stack;
        private final String category;

        public ItemCategoryInfo(ItemStack stack, String category) {
            this.stack = stack;
            this.category = category;
        }

        public ItemStack getStack() {
            return stack;
        }

        public String getCategory() {
            return category;
        }
    }

}
