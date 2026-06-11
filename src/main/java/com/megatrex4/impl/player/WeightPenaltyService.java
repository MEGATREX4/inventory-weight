package com.megatrex4.impl.player;

import com.megatrex4.InventoryWeight;
import com.megatrex4.effects.InventoryWeightEffects;
import com.megatrex4.impl.config.ServerWeightSettings;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.UUID;

public final class WeightPenaltyService {
    public static final double BASE_PENALTY = 0.50;
    public static final Identifier SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(InventoryWeight.MOD_ID, "overload_speed_penalty");

    public static final Identifier ATTACK_SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(InventoryWeight.MOD_ID, "overload_attack_speed_penalty");

    public static final Identifier DAMAGE_REDUCTION_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(InventoryWeight.MOD_ID, "overload_damage_penalty");

    private WeightPenaltyService() {}

    public static void apply(ServerPlayer player, float currentWeight, float maxWeight) {
        ServerWeightSettings settings = WeightSettings.get();
        if (settings.overloadPenaltyStrength() <= 0.0f) {
            clear(player);
            return;
        }

        if (currentWeight >= maxWeight) {
            applyOverload(player, currentWeight, maxWeight, settings);
            return;
        }

        player.removeEffect(InventoryWeightEffects.OVERLOAD);

        if (settings.realisticMode() && currentWeight > maxWeight * 0.1f) {
            applyRealisticPenalties(player, currentWeight, maxWeight);
        } else {
            AttributeModifierManager.removeAllWeightModifiers(player);
        }
    }

    public static void clear(ServerPlayer player) {
        player.removeEffect(InventoryWeightEffects.OVERLOAD);
        AttributeModifierManager.removeAllWeightModifiers(player);
    }

    private static void applyOverload(ServerPlayer player, float currentWeight, float maxWeight, ServerWeightSettings settings) {
        float percentageFull = maxWeight <= 0.0f ? 100.0f : (currentWeight / maxWeight) * 100.0f;
        int overloadLevel = Math.max(0, ((int) percentageFull - 100) / 10);
        overloadLevel = Math.min(overloadLevel, 32);

        int strengthAmplifier = player.hasEffect(MobEffects.STRENGTH)
                ? player.getEffect(MobEffects.STRENGTH).getAmplifier()
                : 0;
        int hasteAmplifier = player.hasEffect(MobEffects.HASTE)
                ? player.getEffect(MobEffects.HASTE).getAmplifier()
                : 0;

        int adjustedLevel = Math.max(1, overloadLevel - (strengthAmplifier + hasteAmplifier));
        adjustedLevel = Math.min(adjustedLevel, 10);

        player.addEffect(new MobEffectInstance(
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

    private static void applyRealisticPenalties(ServerPlayer player, float currentWeight, float maxWeight) {
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

    private static void replaceModifiers(Player player, double speedDecrease, double attackSpeedDecrease, double damageReduction, String prefix) {
        speedDecrease = Math.min(speedDecrease, 0.9);
        attackSpeedDecrease = Math.min(attackSpeedDecrease, 0.9);
        damageReduction = Math.min(damageReduction, 0.9);

        AttributeModifierManager.replace(
                player,
                Attributes.MOVEMENT_SPEED,
                SPEED_MODIFIER_ID,
                prefix + "_speed_penalty",
                -speedDecrease,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        AttributeModifierManager.replace(
                player,
                Attributes.ATTACK_SPEED,
                ATTACK_SPEED_MODIFIER_ID,
                prefix + "_attack_speed_penalty",
                -attackSpeedDecrease,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        AttributeModifierManager.replace(
                player,
                Attributes.ATTACK_DAMAGE,
                DAMAGE_REDUCTION_MODIFIER_ID,
                prefix + "_damage_penalty",
                -damageReduction,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    public static float getJumpVelocity(Player player, float baseJumpVelocity, float currentWeight, float maxWeight) {
        ServerWeightSettings settings = WeightSettings.get();
        if (settings.overloadPenaltyStrength() <= 0.0f || player.isCreative() || player.isSpectator()) {
            return baseJumpVelocity;
        }

        if (currentWeight >= maxWeight) {
            int amplifier = player.hasEffect(InventoryWeightEffects.OVERLOAD)
                    ? player.getEffect(InventoryWeightEffects.OVERLOAD).getAmplifier()
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
