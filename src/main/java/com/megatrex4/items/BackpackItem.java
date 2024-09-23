package com.megatrex4.items;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class BackpackItem extends TrinketItem {
    public BackpackItem(Settings settings) {
        super(settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        // Safely retrieve slot and ensure valid reference
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = super.getModifiers(stack, slot, entity, uuid);

        if (slot != null && slot.inventory() != null && slot.inventory().size() > 0) {
            // Example: Adding extra storage or custom backpack attributes
            SlotAttributes.addSlotModifier(modifiers, "chest/backpack", uuid, 1, EntityAttributeModifier.Operation.ADDITION);
        } else {
            System.out.println("Invalid slot reference or inventory is empty.");
        }

        return modifiers;
    }
}
