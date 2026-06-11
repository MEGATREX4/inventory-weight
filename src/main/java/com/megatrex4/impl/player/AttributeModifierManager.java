package com.megatrex4.impl.player;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class AttributeModifierManager {

    private AttributeModifierManager() {}

    public static void replace(
            Player player,
            Holder<Attribute> attribute,
            Identifier id,
            String name,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
        instance.addTransientModifier(new AttributeModifier(id, amount, operation));
    }

    public static void removeAllWeightModifiers(Player player) {
        remove(player, Attributes.MOVEMENT_SPEED, WeightPenaltyService.SPEED_MODIFIER_ID);
        remove(player, Attributes.ATTACK_SPEED, WeightPenaltyService.ATTACK_SPEED_MODIFIER_ID);
        remove(player, Attributes.ATTACK_DAMAGE, WeightPenaltyService.DAMAGE_REDUCTION_MODIFIER_ID);
    }

    private static void remove(Player player, Holder<Attribute> attribute, Identifier id) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(id);
        }
    }
}