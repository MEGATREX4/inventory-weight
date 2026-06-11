package com.megatrex4.api.v1;


import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

@FunctionalInterface
public interface PocketProvider {
    OptionalInt getPockets(ItemStack stack, @Nullable Player wearer);
}
