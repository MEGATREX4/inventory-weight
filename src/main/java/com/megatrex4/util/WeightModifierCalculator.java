package com.megatrex4.util;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

/**
 * Utility class for calculating weight modifiers and multipliers.
 * Consolidates repeated multiplier calculation logic.
 */
public class WeightModifierCalculator {

    /**
     * Calculates stack size multiplier for stackable items
     * More stackable items = higher multiplier
     */
    public static float calculateStackMultiplier(int maxStackSize) {
        if (maxStackSize <= 1) return 1.0f;
        return 1 + (10f / maxStackSize);
    }

    /**
     * Calculates rarity weight modifier
     */
    public static float calculateRarityModifier(net.minecraft.util.Rarity rarity) {
        return switch (rarity) {
            case UNCOMMON -> 1.5f;
            case RARE -> 2.0f;
            case EPIC -> 3.0f;
            default -> 1.0f;
        };
    }

    /**
     * Calculates food component weight addition
     */
    public static float calculateFoodComponentWeight(FoodComponent foodComponent) {
        if (foodComponent == null) return 0;

        float weight = foodComponent.getHunger();

        // Reduce weight for snacks
        if (foodComponent.isSnack()) {
            weight /= 2;
        }

        weight += foodComponent.getSaturationModifier() * 20;
        return weight;
    }

    /**
     * Calculates hardness-based weight contribution
     */
    public static float calculateHardnessWeight(float hardness) {
        return hardness * 10;
    }

    /**
     * Calculates blast resistance weight contribution
     */
    public static float calculateBlastResistanceWeight(float blastResistance) {
        return Math.min(blastResistance * 50, 10000);
    }

    /**
     * Calculates transparency modifier
     */
    public static float calculateTransparencyModifier(boolean isTransparent) {
        return isTransparent ? 0.8f : 1.0f;
    }

    /**
     * Calculates durability-based weight for tools
     */
    public static float calculateToolDurabilityWeight(int maxDurability) {
        return (maxDurability / 1500.0f) * 300;
    }

    /**
     * Calculates armor protection weight
     */
    public static float calculateArmorProtectionWeight(int protection) {
        return protection * 10;
    }

    /**
     * Calculates slab weight reduction (slabs are half-blocks)
     */
    public static float calculateSlabModifier() {
        return 0.5f;
    }

    /**
     * Calculates stairs weight reduction (stairs are 7/8 of a block)
     */
    public static float calculateStairsModifier() {
        return 0.875f;
    }

    /**
     * Calculates fireproof item multiplier
     */
    public static float calculateFireproofMultiplier() {
        return 1.25f;
    }
}
