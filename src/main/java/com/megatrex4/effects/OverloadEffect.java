package com.megatrex4.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class OverloadEffect extends StatusEffect {
    private static final double BASE_SPEED_DECREASE = 0.3; // Base decrease in speed (10%)
    private static final double BASE_ATTACK_SPEED_DECREASE = 0.3; // Base decrease in attack speed (10%)
    private static final double BASE_DAMAGE_REDUCTION = 0.3; // Base reduction in damage (10%)

    public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a53f3d53-2b63-4a78-851f-4c5795876d8c");
    public static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("c7d4f84c-9e6e-45d0-888e-df63a7e3d206");
    public static final UUID DAMAGE_REDUCTION_MODIFIER_UUID = UUID.fromString("e87c1e8a-6d3f-4b3a-8cbb-d29048a85f0d");

    public OverloadEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF0000); // Red color for the effect
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {

            // Calculate the effect values based on amplifier level
            double speedDecrease = BASE_SPEED_DECREASE * (amplifier); // Use amplifier + 1 for effect
            if (speedDecrease > 0.9) speedDecrease = 0.9; // Ensure the speed doesn't go below 10%

            double attackSpeedDecrease = BASE_ATTACK_SPEED_DECREASE * (amplifier);
            if (attackSpeedDecrease > 0.9) attackSpeedDecrease = 0.9; // Ensure the attack speed doesn't go below 10%

            double damageReduction = BASE_DAMAGE_REDUCTION * (amplifier);
            if (damageReduction > 0.9) damageReduction = 0.9; // Ensure the damage reduction doesn't go below 10%

            // Remove existing modifiers with these UUIDs
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .removeModifier(SPEED_MODIFIER_UUID);

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .removeModifier(ATTACK_SPEED_MODIFIER_UUID);

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .removeModifier(DAMAGE_REDUCTION_MODIFIER_UUID);

            // Apply new modifiers
            EntityAttributeModifier speedModifier = new EntityAttributeModifier(SPEED_MODIFIER_UUID, "overload_speed", -speedDecrease, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .addPersistentModifier(speedModifier);

            EntityAttributeModifier attackSpeedModifier = new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "overload_attack_speed", -attackSpeedDecrease, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .addPersistentModifier(attackSpeedModifier);

            EntityAttributeModifier damageReductionModifier = new EntityAttributeModifier(DAMAGE_REDUCTION_MODIFIER_UUID, "overload_damage_reduction", -damageReduction, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .addPersistentModifier(damageReductionModifier);
        }
    }
}
