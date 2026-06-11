package com.megatrex4.impl.weight.source;

import com.megatrex4.api.v1.PlayerWeightSource;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class VanillaInventoryWeightSource implements PlayerWeightSource {

    @Override
    public WeightResult getWeight(Player player, WeightContext context, WeightLookup lookup) {
        WeightResult total = WeightResult.ZERO;

        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            total = addStack(total, stack, context, lookup);
        }

        total = addStack(total, player.getItemBySlot(EquipmentSlot.OFFHAND), context, lookup);
        total = addStack(total, player.getItemBySlot(EquipmentSlot.HEAD), context, lookup);
        total = addStack(total, player.getItemBySlot(EquipmentSlot.CHEST), context, lookup);
        total = addStack(total, player.getItemBySlot(EquipmentSlot.LEGS), context, lookup);
        total = addStack(total, player.getItemBySlot(EquipmentSlot.FEET), context, lookup);

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