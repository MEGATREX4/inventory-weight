package com.megatrex4.impl.weight.pocket;

import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.weight.ArmorAttributeHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class VanillaArmorPocketProvider implements PocketProvider {
    @Override
    public OptionalInt getPockets(ItemStack stack, @Nullable PlayerEntity wearer) {
        if (!(stack.getItem() instanceof ArmorItem armorItem)) {
            return OptionalInt.empty();
        }

        int protection = ArmorAttributeHelper.getProtection(stack);
        float toughness = ArmorAttributeHelper.getToughness(stack);
        int pockets = (int) Math.max(1, 7 - (int) (protection / 1.2f) - toughness);
        return OptionalInt.of(pockets);
    }
}
