package com.megatrex4.impl.weight.source;

import com.megatrex4.api.v1.PlayerWeightSource;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class VanillaInventoryWeightSource implements PlayerWeightSource {
    @Override
    public WeightResult getWeight(PlayerEntity player, WeightContext context, WeightLookup lookup) {
        WeightResult total = WeightResult.ZERO;

        for (ItemStack stack : player.getInventory().main) {
            total = addStack(total, stack, context, lookup);
        }
        for (ItemStack stack : player.getInventory().offHand) {
            total = addStack(total, stack, context, lookup);
        }
        for (ItemStack stack : player.getInventory().armor) {
            total = addStack(total, stack, context, lookup);
        }

        return total;
    }

    private static WeightResult addStack(WeightResult total, ItemStack stack, WeightContext context, WeightLookup lookup) {
        if (stack.isEmpty()) {
            return total;
        }
        return total.add(lookup.getWeight(stack, context).multiply(stack.getCount()));
    }
}
