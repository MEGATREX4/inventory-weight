package com.megatrex4.impl.weight;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public final class ItemCategoryClassifier {
    private ItemCategoryClassifier() {}

    public static ItemCategory classify(ItemStack stack) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase();

        if (isCreative(itemId)) {
            return ItemCategory.CREATIVE;
        }
        if (itemId.contains("bucket")) {
            return ItemCategory.BUCKETS;
        }
        if (itemId.contains("bottle") || itemId.contains("potion")) {
            return ItemCategory.BOTTLES;
        }
        if (itemId.contains("ingot") || itemId.contains("alloy") || itemId.contains("gem") || itemId.contains("shard")) {
            return ItemCategory.INGOTS;
        }
        if (itemId.contains("nugget")) {
            return ItemCategory.NUGGETS;
        }
        if (stack.getItem() instanceof BlockItem) {
            return ItemCategory.BLOCKS;
        }
        return ItemCategory.ITEMS;
    }

    public static boolean isCreative(String itemId) {
        return itemId.equals("minecraft:barrier")
                || itemId.equals("minecraft:light")
                || itemId.equals("minecraft:structure_block")
                || itemId.equals("minecraft:jigsaw")
                || itemId.contains("command_block")
                || itemId.equals("minecraft:structure_void")
                || itemId.contains("portal")
                || itemId.equals("minecraft:debug_stick")
                || itemId.equals("minecraft:spawner")
                || itemId.contains("spawn_egg")
                || itemId.equals("minecraft:bedrock")
                || itemId.contains("creative");
    }
}
