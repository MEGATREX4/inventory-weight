package com.megatrex4.impl.weight.pocket;

import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class DatapackPocketProvider implements PocketProvider {
    @Override
    public OptionalInt getPockets(ItemStack stack, @Nullable Player wearer) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Integer pockets = WeightDataStore.INSTANCE.getPockets(itemId);
        return pockets == null ? OptionalInt.empty() : OptionalInt.of(pockets);
    }
}
