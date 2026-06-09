package com.megatrex4.impl.player;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public final class AttributeModifierManager {
    private AttributeModifierManager() {}

    public static void removeAllWeightModifiers(PlayerEntity player) {
        remove(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, WeightPenaltyService.SPEED_MODIFIER_UUID);
        remove(player, EntityAttributes.GENERIC_ATTACK_SPEED, WeightPenaltyService.ATTACK_SPEED_MODIFIER_UUID);
        remove(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, WeightPenaltyService.DAMAGE_REDUCTION_MODIFIER_UUID);
    }

    public static void replace(PlayerEntity player, EntityAttribute attribute, UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(uuid);
        instance.addPersistentModifier(new EntityAttributeModifier(uuid, name, value, operation));
    }

    public static void remove(PlayerEntity player, EntityAttribute attribute, UUID uuid) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }
}
