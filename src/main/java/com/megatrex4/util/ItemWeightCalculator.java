package com.megatrex4.util;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;

import static com.megatrex4.util.Rarity.getRarityWeight;

public class ItemWeightCalculator {

    public static float calculateItemWeight(ItemStack stack, String category) {
        Item item = stack.getItem();
        int maxStackSize = item.getMaxCount();
        int maxDurability = stack.getMaxDamage();

        // Base weight from category
        float weight = getCategoryBaseWeight(category);

        // Modify weight based on stack size
        if (maxStackSize > 1) {
            float stackMultiplier = 1 + (10f / maxStackSize); // Example multiplier
            weight *= stackMultiplier;
        }

        else if (maxStackSize == 1 && maxDurability > 0) {
            if (isHasArmor(item)) {
                weight += (float) (getArmorValue(item) * 10);
                weight += (InventoryWeightUtil.ITEMS + (((float) maxDurability / 300) * 300));
            }
            if (isHasDamage(item)) {
                weight += (float) (InventoryWeightUtil.ITEMS + ((maxDurability / 1500.0) * 300));
            }
        }

        weight *= (getRarityWeight(stack) * 1.3f);

        return (int) Math.floor(Math.max(weight, 1.0f));
    }

    private static float getCategoryBaseWeight(String category) {
        return switch (category) {
            case "ingots" -> InventoryWeightUtil.INGOTS;
            case "nuggets" -> InventoryWeightUtil.NUGGETS;
            case "buckets" -> InventoryWeightUtil.BUCKETS;
            case "bottles" -> InventoryWeightUtil.BOTTLES;
            case "blocks" -> InventoryWeightUtil.BLOCKS;
            case "creative" -> InventoryWeightUtil.CREATIVE;
            default -> InventoryWeightUtil.ITEMS;
        };
    }

    private static boolean isHasDamage(Item item) {
        // Add logic to determine if the item has damage (durability)
        return item instanceof ToolItem; // Example
    }

    private static boolean isHasArmor(Item item) {
        // Add logic to determine if the item has armor value
        return item instanceof ArmorItem; // Example
    }

    private static int getArmorValue(Item item) {
        // Add logic to retrieve armor value if the item is an armor item
        if (item instanceof ArmorItem) {
            return ((ArmorItem) item).getProtection(); // Example
        }
        return 0;
    }


}
