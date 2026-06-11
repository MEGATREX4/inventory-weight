package com.megatrex4.impl.weight.pocket;

import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.weight.ArmorAttributeHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class VanillaArmorPocketProvider implements PocketProvider {
    @Override
    public OptionalInt getPockets(ItemStack stack, @Nullable Player wearer) {
        if (!ArmorAttributeHelper.isArmorStack(stack)) {
            return OptionalInt.empty();
        }

        int protection = ArmorAttributeHelper.getProtection(stack);
        float toughness = ArmorAttributeHelper.getToughness(stack);

        int pockets = (int) Math.max(1, 7 - (int) (protection / 1.2f) - toughness);

        return OptionalInt.of(pockets);
    }
}