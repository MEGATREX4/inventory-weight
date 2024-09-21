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

        float weight = getCategoryBaseWeight(category);

        if (maxStackSize > 1) {
            float stackMultiplier = 1 + (10f / maxStackSize);
            weight *= stackMultiplier;
        }

        else if (maxStackSize == 1 && maxDurability > 0) {
            if (isArmor(item)) {
                weight += (float) (getArmorValue(item) * 10);
                weight += (InventoryWeightUtil.ITEMS + (((float) maxDurability / 300) * 300));
            }
            if (isTool(item)) {
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

    private static boolean isTool(Item item) {
        return item instanceof ToolItem;
    }

    private static boolean isArmor(Item item) {
        return item instanceof ArmorItem;
    }

    private static int getArmorValue(Item item) {
        if (item instanceof ArmorItem) {
            return ((ArmorItem) item).getProtection();
        }
        return 0;
    }


}
