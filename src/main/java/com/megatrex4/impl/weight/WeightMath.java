package com.megatrex4.impl.weight;

import com.megatrex4.impl.config.ServerWeightSettings;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public final class WeightMath {
    private WeightMath() {}

    public static float baseWeight(ItemCategory category, ServerWeightSettings settings) {
        return switch (category) {
            case BUCKETS -> settings.bucketWeight();
            case BOTTLES -> settings.bottleWeight();
            case BLOCKS -> settings.blockWeight();
            case INGOTS -> settings.ingotWeight();
            case NUGGETS -> settings.nuggetWeight();
            case CREATIVE -> settings.creativeWeight();
            case ITEMS -> settings.itemWeight();
        };
    }

    public static float stackMultiplier(int maxStackSize) {
        if (maxStackSize <= 1) {
            return 1.0f;
        }
        return 1.0f + (10.0f / maxStackSize);
    }

    public static float rarityMultiplier(ItemStack stack) {
        Rarity rarity = stack.getRarity();
        float rarityWeight = switch (rarity) {
            case UNCOMMON -> 1.5f;
            case RARE -> 2.0f;
            case EPIC -> 3.0f;
            default -> 1.0f;
        };
        return rarityWeight * 1.3f;
    }

    public static float foodWeight(
            FoodComponent foodComponent,
            ConsumableComponent consumableComponent
    ) {
        if (foodComponent == null) {
            return 0.0f;
        }

        float weight = foodComponent.nutrition();

        if (consumableComponent != null && consumableComponent.consumeSeconds() <= 0.8f) {
            weight /= 2.0f;
        }

        if (foodComponent.canAlwaysEat()) {
            weight += 2.0f;
        }

        weight += foodComponent.saturation();

        return weight;
    }

    public static float toolDurabilityWeight(int maxDurability) {
        return (maxDurability / 1500.0f) * 300.0f;
    }

    public static float armorProtectionWeight(int protection) {
        return protection * 10.0f;
    }

    public static float finalFloor(float weight) {
        return (float) Math.floor(Math.max(weight, 1.0f));
    }

    public static String compact(float weight) {
        if (weight >= 1_000_000_000f) return String.format("%.1fB", weight / 1_000_000_000f);
        if (weight >= 1_000_000f) return String.format("%.1fM", weight / 1_000_000f);
        if (weight >= 1_000f) return String.format("%.1fk", weight / 1_000f);
        return String.valueOf((int) weight);
    }

    public static String exact(float weight) {
        return String.format("%,d", (int) weight);
    }
}
