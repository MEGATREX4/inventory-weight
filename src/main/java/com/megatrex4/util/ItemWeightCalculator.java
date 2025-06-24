package com.megatrex4.util;

import net.minecraft.item.*;
<<<<<<< HEAD
=======
import com.megatrex4.util.ItemCategory;
>>>>>>> testrepo/main

import static com.megatrex4.util.Rarity.getRarityWeight;

public class ItemWeightCalculator {

<<<<<<< HEAD
    public static float calculateItemWeight(ItemStack stack, String category) {
=======
    public static float calculateItemWeight(ItemStack stack, ItemCategory category) {
>>>>>>> testrepo/main
        Item item = stack.getItem();
            int maxStackSize = item.getMaxCount();
            int maxDurability = stack.getMaxDamage();

            float weight = getCategoryBaseWeight(category);

            String itemId = ItemWeights.getItemId(stack);

            if(itemId.contains("air")){
                return 0;
            }

            if (BackpackWeightCalculator.isBackpack(itemId, stack) || BackpackWeightCalculator.isTravelerBackpack(stack)){
                return BackpackWeightCalculator.calculateBackpackWeight(stack).totalWeight;
            }

            if (maxStackSize > 1) {
                float stackMultiplier = 1 + (10f / maxStackSize);
                weight *= stackMultiplier;

                if (item.isFood()) {
                    FoodComponent foodComponent = item.getFoodComponent();
                    if (foodComponent != null) {
                        weight += foodComponent.getHunger();

                        // Reduce weight if the item is a snack
                        if (foodComponent.isSnack()) {
                            weight /= 2;
                        }
                        weight += foodComponent.getSaturationModifier() * 20;
                    }
                }

                if (item.isFireproof()){
                    weight *= 1.25f;
                }

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

<<<<<<< HEAD
    private static float getCategoryBaseWeight(String category) {
        return switch (category) {
            case "ingots" -> InventoryWeightUtil.INGOTS;
            case "nuggets" -> InventoryWeightUtil.NUGGETS;
            case "buckets" -> InventoryWeightUtil.BUCKETS;
            case "bottles" -> InventoryWeightUtil.BOTTLES;
            case "blocks" -> InventoryWeightUtil.BLOCKS;
            case "creative" -> InventoryWeightUtil.CREATIVE;
=======
    private static float getCategoryBaseWeight(ItemCategory category) {
        return switch (category) {
            case INGOTS -> InventoryWeightUtil.INGOTS;
            case NUGGETS -> InventoryWeightUtil.NUGGETS;
            case BUCKETS -> InventoryWeightUtil.BUCKETS;
            case BOTTLES -> InventoryWeightUtil.BOTTLES;
            case BLOCKS -> InventoryWeightUtil.BLOCKS;
            case CREATIVE -> InventoryWeightUtil.CREATIVE;
>>>>>>> testrepo/main
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
