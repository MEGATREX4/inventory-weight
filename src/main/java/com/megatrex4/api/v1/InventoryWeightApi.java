package com.megatrex4.api.v1;

import com.megatrex4.impl.InventoryWeightServices;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.OptionalInt;

/**
 * Stable public access point for add-ons.
 */
public final class InventoryWeightApi {
    private InventoryWeightApi() {}

    public static WeightResult getWeight(ItemStack stack, WeightContext context) {
        return InventoryWeightServices.weightService().getWeight(stack, context);
    }

    public static WeightResult getPlayerInventoryWeight(PlayerEntity player) {
        return InventoryWeightServices.playerWeightService().getInventoryWeight(player);
    }

    public static float getMaxWeight(ServerPlayerEntity player) {
        return InventoryWeightServices.capacityService().getMaxWeight(player);
    }

    public static OptionalInt getPockets(ItemStack stack, PlayerEntity wearer) {
        return InventoryWeightServices.pocketService().getPockets(stack, wearer);
    }
}
