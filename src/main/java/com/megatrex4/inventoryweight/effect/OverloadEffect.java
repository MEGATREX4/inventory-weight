package com.megatrex4.inventoryweight.effect;

import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import com.megatrex4.inventoryweight.util.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class OverloadEffect extends StatusEffect {

    public static final double BASE_PENALTY = 0.50;

    public OverloadEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF0000); //r red
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            float speedReduction = (float) (BASE_PENALTY + (0.05 * amplifier) * InventoryWeightConfig.SERVER.overloadStrength);
            float attackSpeedReduction = (float) (BASE_PENALTY + (0.05 * amplifier) * InventoryWeightConfig.SERVER.overloadStrength);
            float damageReduction = (float) (BASE_PENALTY / 1.5 + (0.05 * amplifier) * InventoryWeightConfig.SERVER.overloadStrength);

            speedReduction = (float) Math.min(speedReduction, 0.7);
            attackSpeedReduction = (float) Math.min(attackSpeedReduction, 0.6);
            damageReduction = (float) Math.min(damageReduction, 0.5);

            Util.applyWeightModifiers(player, speedReduction, attackSpeedReduction, damageReduction);
        }
    }
}
