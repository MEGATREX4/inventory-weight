package com.megatrex4.impl.weight;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public final class ArmorAttributeHelper {
    private ArmorAttributeHelper() {}

    public static int getProtection(ItemStack stack) {
        return (int) Math.round(getAdditiveAttributeValue(stack, EntityAttributes.ARMOR));
    }

    public static float getToughness(ItemStack stack) {
        return (float) getAdditiveAttributeValue(stack, EntityAttributes.ARMOR_TOUGHNESS);
    }

    private static double getAdditiveAttributeValue(
            ItemStack stack,
            RegistryEntry<EntityAttribute> attribute
    ) {
        AttributeModifiersComponent component = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

        if (component == null) {
            return 0.0D;
        }

        double total = 0.0D;

        for (AttributeModifiersComponent.Entry entry : component.modifiers()) {
            if (!entry.attribute().equals(attribute)) {
                continue;
            }

            EntityAttributeModifier modifier = entry.modifier();

            if (modifier.operation() == EntityAttributeModifier.Operation.ADD_VALUE) {
                total += modifier.value();
            }
        }

        return total;
    }
}