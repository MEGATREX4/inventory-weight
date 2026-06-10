package com.megatrex4.impl.weight;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public final class ItemStackData {
    private ItemStackData() {}

    @Nullable
    public static NbtCompound getCustomData(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        return component == null ? null : component.copyNbt();
    }

    public static void setCustomData(ItemStack stack, @Nullable NbtCompound nbt) {
        if (nbt == null || nbt.isEmpty()) {
            stack.remove(DataComponentTypes.CUSTOM_DATA);
            return;
        }

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}