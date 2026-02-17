package com.megatrex4.util;

/**
 * Record class to represent weight calculation results with both total and base weight.
 * Replaces multiple custom result classes throughout the codebase.
 *
 * @param totalWeight The weight including all modifiers
 * @param baseWeight The weight without modifiers
 */
public record WeightCalculationResult(float totalWeight, float baseWeight) {

    /**
     * Gets the difference between total and base weight (modifier impact)
     */
    public float getModifierDifference() {
        return totalWeight - baseWeight;
    }

    /**
     * Gets the modifier multiplier (totalWeight / baseWeight)
     */
    public float getModifierMultiplier() {
        if (baseWeight == 0) return 1.0f;
        return totalWeight / baseWeight;
    }

    /**
     * Checks if the weight has modifiers applied
     */
    public boolean hasModifiers() {
        return totalWeight != baseWeight;
    }

    /**
     * Creates a result with only base weight (no modifiers)
     */
    public static WeightCalculationResult ofBase(float weight) {
        return new WeightCalculationResult(weight, weight);
    }

    /**
     * Creates a result with both total and base weight
     */
    public static WeightCalculationResult of(float total, float base) {
        return new WeightCalculationResult(total, base);
    }
}
