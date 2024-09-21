package com.megatrex4.effects;

import com.megatrex4.InventoryWeightHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class OverloadEffect extends StatusEffect {

    public  static final double BASE_PENALTY = 0.50;

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
            double speedDecrease = BASE_PENALTY + (0.05 * amplifier);  // Start with 50% + 5% per amplifier level
            double attackSpeedDecrease = BASE_PENALTY + (0.05 * amplifier); // Same for attack speed
            double damageReduction = BASE_PENALTY / 2 + (0.05 * amplifier); // Reduce damage by 30% + 5% per level

            // Ensure the values do not exceed 90%
            speedDecrease = Math.min(speedDecrease, 0.9);
            attackSpeedDecrease = Math.min(attackSpeedDecrease, 0.9);
            damageReduction = Math.min(damageReduction, 0.9);

            InventoryWeightHandler.removeAttributes(player);

            // Apply the updated modifiers
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .addPersistentModifier(new EntityAttributeModifier(
                            SPEED_MODIFIER_UUID,
                            "overload_speed",
                            -speedDecrease,
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .addPersistentModifier(new EntityAttributeModifier(
                            ATTACK_SPEED_MODIFIER_UUID,
                            "overload_attack_speed",
                            -attackSpeedDecrease,
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .addPersistentModifier(new EntityAttributeModifier(
                            DAMAGE_REDUCTION_MODIFIER_UUID,
                            "overload_damage_reduction",
                            -damageReduction,
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

}
