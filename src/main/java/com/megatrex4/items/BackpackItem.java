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

    //simple trinket to make a render for backpack, you can change it later if you don't need it.
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        SlotAttributes.addSlotModifier(modifiers, "backpack", uuid, 1, EntityAttributeModifier.Operation.ADDITION);
        return modifiers;
    }

}
