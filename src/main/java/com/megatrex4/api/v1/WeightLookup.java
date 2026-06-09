package com.megatrex4.api.v1;

import net.minecraft.item.ItemStack;

public interface WeightLookup {
    WeightResult getWeight(ItemStack stack, WeightContext context);
}
