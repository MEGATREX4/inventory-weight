package com.megatrex4;

import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.player.AttributeModifierManager;
import com.megatrex4.impl.player.PlayerWeightController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Deprecated compatibility shim for older code. New integrations should use com.megatrex4.api.v1.InventoryWeightApi.
 */
@Deprecated(forRemoval = false)
public final class InventoryWeightHandler {
    private InventoryWeightHandler() {}

    public static float calculateInventoryWeight(PlayerEntity player) {
        return InventoryWeightServices.playerWeightService().getInventoryWeight(player).weight();
    }

    public static WeightResult calculateInventoryWeightResult(PlayerEntity player) {
        return InventoryWeightServices.playerWeightService().getInventoryWeight(player);
    }

    public static void removeAttributes(PlayerEntity player) {
        AttributeModifierManager.removeAllWeightModifiers(player);
    }

    public static void checkWeight(ServerPlayerEntity player) {
        PlayerWeightController.updatePlayer(player);
    }

    public static void updatePlayerWeight(ServerWorld world, ServerPlayerEntity player) {
        PlayerWeightController.updatePlayer(player);
    }

    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            PlayerWeightController.updatePlayer(player);
        }
    }
}
