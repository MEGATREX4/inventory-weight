package com.megatrex4.impl.weight;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public final class ArmorAttributeHelper {

    private ArmorAttributeHelper() {}

    public static boolean isArmorStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

        if (equippable == null) {
            return false;
        }

        EquipmentSlot slot = equippable.slot();

        return slot == EquipmentSlot.HEAD
                || slot == EquipmentSlot.CHEST
                || slot == EquipmentSlot.LEGS
                || slot == EquipmentSlot.FEET;
    }

    public static int getProtection(ItemStack stack) {
        return (int) Math.round(getAdditiveAttributeValue(stack, Attributes.ARMOR));
    }

    public static float getToughness(ItemStack stack) {
        return (float) getAdditiveAttributeValue(stack, Attributes.ARMOR_TOUGHNESS);
    }

    private static double getAdditiveAttributeValue(
            ItemStack stack,
            Holder<Attribute> attribute
    ) {
        if (stack == null || stack.isEmpty()) {
            return 0.0D;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

        if (equippable == null) {
            return 0.0D;
        }

        EquipmentSlot slot = equippable.slot();

        double[] total = {0.0D};

        stack.forEachModifier(slot, (modifierAttribute, modifier) -> {
            if (!modifierAttribute.equals(attribute)) {
                return;
            }

            if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                total[0] += modifier.amount();
            }
        });

        return total[0];
    }
}