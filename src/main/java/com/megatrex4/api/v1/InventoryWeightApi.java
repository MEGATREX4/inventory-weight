package com.megatrex4.api.v1;

import com.megatrex4.impl.InventoryWeightServices;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

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

    public static EntityAttribute getMaxWeightAttribute() {
        return InventoryWeightAttributes.GENERIC_MAX_WEIGHT;
    }

    @Nullable
    public static EntityAttributeInstance getMaxWeightAttributeInstance(PlayerEntity player) {
        return InventoryWeightAttributes.getInstance(player);
    }

    public static double getMaxWeightAttributeValue(PlayerEntity player) {
        return InventoryWeightAttributes.getValue(player);
    }

    /**
     * Returns only the attribute contribution that is added on top of server config maxWeight.
     */
    public static double getMaxWeightAttributeBonus(PlayerEntity player) {
        return InventoryWeightAttributes.getValue(player);
    }

    public static OptionalInt getPockets(ItemStack stack, PlayerEntity wearer) {
        return InventoryWeightServices.pocketService().getPockets(stack, wearer);
    }
}
