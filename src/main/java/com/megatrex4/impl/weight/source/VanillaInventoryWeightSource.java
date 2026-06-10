package com.megatrex4.impl.weight.source;

import com.megatrex4.api.v1.PlayerWeightSource;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class VanillaInventoryWeightSource implements PlayerWeightSource {
    @Override
    public WeightResult getWeight(PlayerEntity player, WeightContext context, WeightLookup lookup) {
        WeightResult total = WeightResult.ZERO;

        for (ItemStack stack : player.getInventory().getMainStacks()) {
            total = addStack(total, stack, context, lookup);
        }

        total = addStack(total, player.getEquippedStack(EquipmentSlot.OFFHAND), context, lookup);

        total = addStack(total, player.getEquippedStack(EquipmentSlot.HEAD), context, lookup);
        total = addStack(total, player.getEquippedStack(EquipmentSlot.CHEST), context, lookup);
        total = addStack(total, player.getEquippedStack(EquipmentSlot.LEGS), context, lookup);
        total = addStack(total, player.getEquippedStack(EquipmentSlot.FEET), context, lookup);

        return total;
    }

    private static WeightResult addStack(
            WeightResult total,
            ItemStack stack,
            WeightContext context,
            WeightLookup lookup
    ) {
        if (stack == null || stack.isEmpty()) {
            return total;
        }

        return total.add(lookup.getWeight(stack, context).multiply(stack.getCount()));
    }
}