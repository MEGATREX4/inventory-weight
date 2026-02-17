package com.megatrex4.util;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

/**
 * Utility class for managing attribute modifiers applied to players.
 * Consolidates repetitive attribute modification logic.
 */
public class AttributeModifierManager {

    /**
     * Removes all weight-related attribute modifiers from a player
     */
    public static void removeAllWeightModifiers(PlayerEntity player, UUID speedModifierId, UUID attackSpeedModifierId, UUID damageModifierId) {
        removeSpeedModifier(player, speedModifierId);
        removeAttackSpeedModifier(player, attackSpeedModifierId);
        removeDamageModifier(player, damageModifierId);
    }

    /**
     * Removes movement speed modifier
     */
    public static void removeSpeedModifier(PlayerEntity player, UUID modifierId) {
        EntityAttributeInstance speedAttribute = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(modifierId);
        }
    }

    /**
     * Removes attack speed modifier
     */
    public static void removeAttackSpeedModifier(PlayerEntity player, UUID modifierId) {
        EntityAttributeInstance attackSpeedAttribute = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            attackSpeedAttribute.removeModifier(modifierId);
        }
    }

    /**
     * Removes damage modifier
     */
    public static void removeDamageModifier(PlayerEntity player, UUID modifierId) {
        EntityAttributeInstance damageAttribute = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (damageAttribute != null) {
            damageAttribute.removeModifier(modifierId);
        }
    }

    /**
     * Checks if a modifier is already applied to an attribute
     */
    public static boolean hasModifier(EntityAttributeInstance attribute, UUID modifierId) {
        return attribute.getModifiers().stream().anyMatch(modifier -> modifier.getId().equals(modifierId));
    }

    /**
     * Applies a modifier to movement speed if not already applied
     */
    public static void applySpeedModifierIfAbsent(PlayerEntity player, UUID modifierId, double value, EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance speedAttribute = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null && !hasModifier(speedAttribute, modifierId)) {
            speedAttribute.addPersistentModifier(new EntityAttributeModifier(modifierId, "inventoryweight_speed", value, operation));
        }
    }

    /**
     * Applies a modifier to attack speed if not already applied
     */
    public static void applyAttackSpeedModifierIfAbsent(PlayerEntity player, UUID modifierId, double value, EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance attackSpeedAttribute = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        if (attackSpeedAttribute != null && !hasModifier(attackSpeedAttribute, modifierId)) {
            attackSpeedAttribute.addPersistentModifier(new EntityAttributeModifier(modifierId, "inventoryweight_attack_speed", value, operation));
        }
    }

    /**
     * Applies a modifier to attack damage if not already applied
     */
    public static void applyDamageModifierIfAbsent(PlayerEntity player, UUID modifierId, double value, EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance damageAttribute = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (damageAttribute != null && !hasModifier(damageAttribute, modifierId)) {
            damageAttribute.addPersistentModifier(new EntityAttributeModifier(modifierId, "inventoryweight_damage", value, operation));
        }
    }
}
