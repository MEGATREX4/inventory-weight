package com.megatrex4.api.v1;

import com.megatrex4.impl.InventoryWeightServices;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class InventoryWeightApi {
    private InventoryWeightApi() {}

    public static WeightResult getWeight(ItemStack stack, WeightContext context) {
        return InventoryWeightServices.weightService().getWeight(stack, context);
    }

    public static WeightResult getPlayerInventoryWeight(Player player) {
        return InventoryWeightServices.playerWeightService().getInventoryWeight(player);
    }

    public static float getMaxWeight(ServerPlayer player) {
        return InventoryWeightServices.capacityService().getMaxWeight(player);
    }

    public static Holder<Attribute> getMaxWeightAttribute() {
        return InventoryWeightAttributes.GENERIC_MAX_WEIGHT;
    }

    @Nullable
    public static AttributeInstance getMaxWeightAttributeInstance(Player player) {
        return InventoryWeightAttributes.getInstance(player);
    }

    public static double getMaxWeightAttributeValue(Player player) {
        return InventoryWeightAttributes.getValue(player);
    }

    public static double getMaxWeightAttributeBonus(Player player) {
        return InventoryWeightAttributes.getValue(player);
    }

    public static OptionalInt getPockets(ItemStack stack, Player wearer) {
        return InventoryWeightServices.pocketService().getPockets(stack, wearer);
    }
}