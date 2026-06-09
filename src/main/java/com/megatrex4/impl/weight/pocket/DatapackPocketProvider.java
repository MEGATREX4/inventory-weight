package com.megatrex4.impl.weight.pocket;

import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class DatapackPocketProvider implements PocketProvider {
    @Override
    public OptionalInt getPockets(ItemStack stack, @Nullable PlayerEntity wearer) {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        Integer pockets = WeightDataStore.INSTANCE.getPockets(itemId);
        return pockets == null ? OptionalInt.empty() : OptionalInt.of(pockets);
    }
}
