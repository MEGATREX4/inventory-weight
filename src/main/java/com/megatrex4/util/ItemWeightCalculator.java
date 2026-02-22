package com.megatrex4.util;

import net.minecraft.item.*;
import com.megatrex4.util.ItemCategory;
import com.megatrex4.config.InventoryWeightConfig;

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
                weight += (InventoryWeightConfig.getServer().itemWeight + (((float) maxDurability / 300) * 300));
            }
            if (ItemTypeChecker.isTool(item)) {
                weight += (InventoryWeightConfig.getServer().itemWeight + WeightModifierCalculator.calculateToolDurabilityWeight(maxDurability));
            }
        }

        weight *= (getRarityWeight(stack) * 1.3f);

        return (int) Math.floor(Math.max(weight, 1.0f));
    }

    private static float getCategoryBaseWeight(ItemCategory category) {
        return switch (category) {
            case INGOTS -> InventoryWeightConfig.getServer().ingotWeight;
            case NUGGETS -> InventoryWeightConfig.getServer().nuggetWeight;
            case BUCKETS -> InventoryWeightConfig.getServer().bucketWeight;
            case BOTTLES -> InventoryWeightConfig.getServer().bottleWeight;
            case BLOCKS -> InventoryWeightConfig.getServer().blockWeight;
            case CREATIVE -> InventoryWeightConfig.getServer().creativeWeight;
            default -> InventoryWeightConfig.getServer().itemWeight;
        };
    }
}
