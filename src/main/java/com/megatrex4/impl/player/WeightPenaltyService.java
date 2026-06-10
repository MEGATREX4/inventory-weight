package com.megatrex4.impl.player;

import com.megatrex4.effects.InventoryWeightEffects;
import com.megatrex4.impl.config.ServerWeightSettings;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public final class WeightPenaltyService {
    public static final double BASE_PENALTY = 0.50;
    public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a53f3d53-2b63-4a78-851f-4c5795876d8c");
    public static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("c7d4f84c-9e6e-45d0-888e-df63a7e3d206");
    public static final UUID DAMAGE_REDUCTION_MODIFIER_UUID = UUID.fromString("e87c1e8a-6d3f-4b3a-8cbb-d29048a85f0d");

    private WeightPenaltyService() {}

    public static void apply(ServerPlayerEntity player, float currentWeight, float maxWeight) {
        ServerWeightSettings settings = WeightSettings.get();
        if (settings.overloadPenaltyStrength() <= 0.0f) {
            clear(player);
            return;
        }

        if (currentWeight >= maxWeight) {
            applyOverload(player, currentWeight, maxWeight, settings);
            return;
        }

        player.removeStatusEffect(InventoryWeightEffects.OVERLOAD);

        if (settings.realisticMode() && currentWeight > maxWeight * 0.1f) {
            applyRealisticPenalties(player, currentWeight, maxWeight);
        } else {
            AttributeModifierManager.removeAllWeightModifiers(player);
        }
    }

    public static void clear(ServerPlayerEntity player) {
        player.removeStatusEffect(InventoryWeightEffects.OVERLOAD);
        AttributeModifierManager.removeAllWeightModifiers(player);
    }

    private static void applyOverload(ServerPlayerEntity player, float currentWeight, float maxWeight, ServerWeightSettings settings) {
        float percentageFull = maxWeight <= 0.0f ? 100.0f : (currentWeight / maxWeight) * 100.0f;
        int overloadLevel = Math.max(0, ((int) percentageFull - 100) / 10);
        overloadLevel = Math.min(overloadLevel, 32);

        int strengthAmplifier = player.hasStatusEffect(StatusEffects.STRENGTH)
                ? player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier()
                : 0;
        int hasteAmplifier = player.hasStatusEffect(StatusEffects.HASTE)
                ? player.getStatusEffect(StatusEffects.HASTE).getAmplifier()
                : 0;

        int adjustedLevel = Math.max(1, overloadLevel - (strengthAmplifier + hasteAmplifier));
        adjustedLevel = Math.min(adjustedLevel, 10);

        player.addStatusEffect(new StatusEffectInstance(
                InventoryWeightEffects.OVERLOAD,
                40,
                adjustedLevel - 1,
                true,
                false,
                false
        ));

        double speedDecrease = BASE_PENALTY + (0.05 * (adjustedLevel - 1)) * settings.overloadPenaltyStrength();
        double attackSpeedDecrease = BASE_PENALTY + (0.05 * (adjustedLevel - 1)) * settings.overloadPenaltyStrength();
        double damageReduction = BASE_PENALTY / 2.0 + (0.05 * (adjustedLevel - 1)) * settings.overloadPenaltyStrength();

        replaceModifiers(player, speedDecrease, attackSpeedDecrease, damageReduction, "overload");
    }

    private static void applyRealisticPenalties(ServerPlayerEntity player, float currentWeight, float maxWeight) {
        double factor = (currentWeight - (0.1 * maxWeight)) / (maxWeight - (0.1 * maxWeight));
        factor = Math.max(0.0, Math.min(1.0, factor));

        double speedDecrease = Math.min(factor * 0.9, 0.9);
        double attackSpeedDecrease = Math.min(factor * 0.9, 0.9);
        double damageReduction = Math.min(factor * 0.9, 0.9);

        speedDecrease = Math.min(speedDecrease, 0.9) - speedDecrease * 0.4;
        attackSpeedDecrease = Math.min(attackSpeedDecrease, 0.9) - attackSpeedDecrease * 0.3;
        damageReduction = Math.min(damageReduction, 0.9) - damageReduction * 0.3;

        replaceModifiers(player, speedDecrease, attackSpeedDecrease, damageReduction, "realistic_weight");
    }

    private static void replaceModifiers(PlayerEntity player, double speedDecrease, double attackSpeedDecrease, double damageReduction, String prefix) {
        speedDecrease = Math.min(speedDecrease, 0.9);
        attackSpeedDecrease = Math.min(attackSpeedDecrease, 0.9);
        damageReduction = Math.min(damageReduction, 0.9);

        AttributeModifierManager.replace(
                player,
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                SPEED_MODIFIER_UUID,
                prefix + "_speed_penalty",
                -speedDecrease,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        AttributeModifierManager.replace(
                player,
                EntityAttributes.GENERIC_ATTACK_SPEED,
                ATTACK_SPEED_MODIFIER_UUID,
                prefix + "_attack_speed_penalty",
                -attackSpeedDecrease,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        AttributeModifierManager.replace(
                player,
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                DAMAGE_REDUCTION_MODIFIER_UUID,
                prefix + "_damage_penalty",
                -damageReduction,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    public static float getJumpVelocity(PlayerEntity player, float baseJumpVelocity, float currentWeight, float maxWeight) {
        ServerWeightSettings settings = WeightSettings.get();
        if (settings.overloadPenaltyStrength() <= 0.0f || player.isCreative() || player.isSpectator()) {
            return baseJumpVelocity;
        }

        if (currentWeight >= maxWeight) {
            int amplifier = player.hasStatusEffect(InventoryWeightEffects.OVERLOAD)
                    ? player.getStatusEffect(InventoryWeightEffects.OVERLOAD).getAmplifier()
                    : 0;
            float reduced = baseJumpVelocity * 0.6f / (amplifier + 1.0f) / settings.overloadPenaltyStrength();
            return Math.max(reduced, baseJumpVelocity * 0.2f);
        }

        if (settings.realisticMode() && currentWeight > maxWeight * 0.1f) {
            double factor = (currentWeight - (0.1 * maxWeight)) / (maxWeight - (0.1 * maxWeight));
            factor = Math.max(0.0, Math.min(1.0, factor));
            float reduced = (float) (baseJumpVelocity * (1.0 - factor * BASE_PENALTY));
            return Math.max(reduced, baseJumpVelocity * 0.3f) + 0.05f;
        }

        return baseJumpVelocity;
    }
}
