package com.megatrex4.util;

import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.impl.InventoryWeightServices;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

/**
 * Deprecated compatibility shim. Prefer InventoryWeightApi / ItemWeightProvider.
 */
@Deprecated(forRemoval = false)
public final class ItemWeights {
    private ItemWeights() {}

    public static String getItemId(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }

    public static float getItemWeight(ItemStack stack) {
        return InventoryWeightServices.weightService().getWeight(stack, WeightContext.empty()).weight();
    }
}
