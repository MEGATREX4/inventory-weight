package com.megatrex4.util;

import net.minecraft.item.*;
import com.megatrex4.util.ItemCategory;

import static com.megatrex4.util.Rarity.getRarityWeight;

public class ItemWeightCalculator {

    public static float calculateItemWeight(ItemStack stack, ItemCategory category) {
        Item item = stack.getItem();
        int maxStackSize = ItemTypeChecker.getMaxStackSize(item);
        int maxDurability = ItemTypeChecker.getMaxDurability(stack);

        float weight = getCategoryBaseWeight(category);

        String itemId = ItemWeights.getItemId(stack);

        if (itemId.contains("air")) {
            return 0;
        }

        if (BackpackWeightCalculator.isBackpack(itemId, stack) || BackpackWeightCalculator.isTravelerBackpack(stack)) {
            return BackpackWeightCalculator.calculateBackpackWeight(stack).totalWeight;
        }

        if (maxStackSize > 1) {
            float stackMultiplier = WeightModifierCalculator.calculateStackMultiplier(maxStackSize);
            weight *= stackMultiplier;

            if (ItemTypeChecker.isFood(item)) {
                FoodComponent foodComponent = item.getFoodComponent();
                weight += WeightModifierCalculator.calculateFoodComponentWeight(foodComponent);
            }

            if (ItemTypeChecker.isFireproof(item)) {
                weight *= WeightModifierCalculator.calculateFireproofMultiplier();
            }

        } else if (maxStackSize == 1 && maxDurability > 0) {
            if (ItemTypeChecker.isArmor(item)) {
                weight += WeightModifierCalculator.calculateArmorProtectionWeight(ItemTypeChecker.getArmorProtection(item));
                weight += (InventoryWeightUtil.ITEMS + (((float) maxDurability / 300) * 300));
            }
            if (ItemTypeChecker.isTool(item)) {
                weight += (InventoryWeightUtil.ITEMS + WeightModifierCalculator.calculateToolDurabilityWeight(maxDurability));
            }
        }

        weight *= (getRarityWeight(stack) * 1.3f);

        return (int) Math.floor(Math.max(weight, 1.0f));
    }

    private static float getCategoryBaseWeight(ItemCategory category) {
        return switch (category) {
            case INGOTS -> InventoryWeightUtil.INGOTS;
            case NUGGETS -> InventoryWeightUtil.NUGGETS;
            case BUCKETS -> InventoryWeightUtil.BUCKETS;
            case BOTTLES -> InventoryWeightUtil.BOTTLES;
            case BLOCKS -> InventoryWeightUtil.BLOCKS;
            case CREATIVE -> InventoryWeightUtil.CREATIVE;
            default -> InventoryWeightUtil.ITEMS;
        };
    }
}
