package com.megatrex4.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface BackpackItemData {
    void writeNbt(NbtCompound nbt, ItemStack stack);

    void readNbt(NbtCompound nbt, ItemStack stack);
}
