package com.megatrex4.impl.weight;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public final class ItemStackData {

    private ItemStackData() {}

    @Nullable
    public static CompoundTag getCustomData(ItemStack stack) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);

        return component == null ? null : component.copyTag();
    }

    public static void setCustomData(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
            return;
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }
}