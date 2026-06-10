package com.megatrex4.impl.player;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class AttributeModifierManager {
    private AttributeModifierManager() {}

    public static void removeAllWeightModifiers(PlayerEntity player) {
        remove(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, WeightPenaltyService.SPEED_MODIFIER_ID);
        remove(player, EntityAttributes.GENERIC_ATTACK_SPEED, WeightPenaltyService.ATTACK_SPEED_MODIFIER_ID);
        remove(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, WeightPenaltyService.DAMAGE_REDUCTION_MODIFIER_ID);
    }

    public static void replace(
            PlayerEntity player,
            RegistryEntry<EntityAttribute> attribute,
            Identifier id,
            String name,
            double value,
            EntityAttributeModifier.Operation operation
    ) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
        instance.addPersistentModifier(new EntityAttributeModifier(id, value, operation));
    }

    public static void remove(
            PlayerEntity player,
            RegistryEntry<EntityAttribute> attribute,
            Identifier id
    ) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);

        if (instance != null) {
            instance.removeModifier(id);
        }
    }
}