package com.megatrex4.util;

import net.minecraft.util.Formatting;

/**
 * Utility class for weight calculations and color selection in tooltips.
 * Translation-dependent formatting is handled in Tooltips.java to preserve localization.
 */
public class WeightTooltipFormatter {

    /**
     * Formats a percentage value (no translation needed)
     * Example: 1500 / 2000 -> "75.0%"
     */
    public static String formatPercentage(float current, float max) {
        if (max == 0) return "0%";
        return String.format("%.1f%%", (current / max) * 100);
    }

    /**
     * Gets a color based on weight percentage for visual feedback
     * - RED (>= 100%) - Overloaded
     * - GOLD (80-100%) - Near capacity  
     * - YELLOW (50-80%) - Half capacity
     * - GREEN (< 50%) - Light load
     */
    public static Formatting getWeightPercentageColor(float current, float max) {
        if (max == 0) return Formatting.GRAY;

        float percentage = (current / max) * 100;

        if (percentage >= 100) {
            return Formatting.RED;          // Overloaded
        } else if (percentage >= 80) {
            return Formatting.GOLD;         // Near capacity
        } else if (percentage >= 50) {
            return Formatting.YELLOW;       // Half capacity
        } else {
            return Formatting.GREEN;        // Light load
        }
    }

    /**
     * Checks if weight is overloaded (at or above max)
     */
    public static boolean isOverloaded(float current, float max) {
        return current >= max;
    }

    /**
     * Checks if weight is near capacity (80% or more)
     */
    public static boolean isNearCapacity(float current, float max) {
        if (max == 0) return false;
        return (current / max) >= 0.8f;
    }

    /**
     * Gets weight status level as a number (0-4)
     * 0 = Light (< 50%)
     * 1 = Moderate (50-80%)
     * 2 = Heavy (80-100%)
     * 3 = Overloaded (>= 100%)
     * 4 = Critical (>= 150%)
     */
    public static int getWeightStatusLevel(float current, float max) {
        if (max == 0) return 0;
        
        float percentage = (current / max) * 100;
        
        if (percentage >= 150) return 4;
        if (percentage >= 100) return 3;
        if (percentage >= 80) return 2;
        if (percentage >= 50) return 1;
        return 0;
    }

    /**
     * Formats numeric weight as comma-separated (no translation)
     * Example: 1500.5 -> "1,500"
     */
    public static String formatNumericWeight(float weight) {
        return String.format("%,d", (int) weight);
    }

    /**
     * Calculates how much weight capacity remains
     */
    public static float getRemainingCapacity(float current, float max) {
        return Math.max(0, max - current);
    }

    /**
     * Calculates the percentage of capacity available
     */
    public static float getAvailableCapacityPercent(float current, float max) {
        if (max == 0) return 0;
        return Math.max(0, (max - current) / max) * 100;
    }
}
