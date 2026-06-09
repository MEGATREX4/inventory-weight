package com.megatrex4.api.v1;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

@FunctionalInterface
public interface PocketProvider {
    OptionalInt getPockets(ItemStack stack, @Nullable PlayerEntity wearer);
}
