package com.megatrex4.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public final class OverloadEffect extends StatusEffect {
    public OverloadEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
