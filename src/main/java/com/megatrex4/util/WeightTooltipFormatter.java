package com.megatrex4.util;

import net.minecraft.ChatFormatting;

public final class WeightTooltipFormatter {
    private WeightTooltipFormatter() {}

    public static String formatPercentage(float current, float max) {
        if (max <= 0.0f) return "0%";
        return String.format("%.1f%%", (current / max) * 100.0f);
    }

    public static ChatFormatting getWeightPercentageColor(float current, float max) {
        if (max <= 0.0f) return ChatFormatting.GRAY;
        float percentage = (current / max) * 100.0f;
        if (percentage >= 100.0f) return ChatFormatting.RED;
        if (percentage >= 80.0f) return ChatFormatting.GOLD;
        if (percentage >= 50.0f) return ChatFormatting.YELLOW;
        return ChatFormatting.GREEN;
    }

    public static boolean isOverloaded(float current, float max) {
        return current >= max;
    }

    public static boolean isNearCapacity(float current, float max) {
        return max > 0.0f && (current / max) >= 0.8f;
    }

    public static int getWeightStatusLevel(float current, float max) {
        if (max <= 0.0f) return 0;
        float percentage = (current / max) * 100.0f;
        if (percentage >= 150.0f) return 4;
        if (percentage >= 100.0f) return 3;
        if (percentage >= 80.0f) return 2;
        if (percentage >= 50.0f) return 1;
        return 0;
    }

    public static String formatNumericWeight(float weight) {
        return String.format("%,d", (int) weight);
    }

    public static float getRemainingCapacity(float current, float max) {
        return Math.max(0.0f, max - current);
    }

    public static float getAvailableCapacityPercent(float current, float max) {
        if (max <= 0.0f) return 0.0f;
        return Math.max(0.0f, (max - current) / max) * 100.0f;
    }
}
