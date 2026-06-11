package com.megatrex4.impl.player;

import com.megatrex4.api.v1.InventoryWeightEvents;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.component.PlayerWeightComponentRegistry;
import com.megatrex4.impl.InventoryWeightServices;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerWeightController {
    private PlayerWeightController() {}

    public static void tickServer(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            updatePlayer(player);
        }
    }

    public static void updatePlayer(ServerPlayer player) {
        WeightResult inventoryWeight = InventoryWeightServices.playerWeightService().getInventoryWeight(player);
        float maxWeight = InventoryWeightServices.capacityService().getMaxWeight(player);
        boolean ignored = player.isCreative() || player.isSpectator();
        boolean overloaded = !ignored && inventoryWeight.weight() >= maxWeight;

        PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player).ifPresent(component -> {
            boolean oldOverloaded = component.isOverloaded();
            component.setWeightState(inventoryWeight.weight(), maxWeight, overloaded);
            PlayerWeightComponentRegistry.PLAYER_WEIGHT.sync(player);

            if (oldOverloaded != overloaded) {
                InventoryWeightEvents.OVERLOAD_CHANGED.invoker().onOverloadChanged(player, overloaded);
            }
        });

        if (ignored) {
            WeightPenaltyService.clear(player);
        } else {
            WeightPenaltyService.apply(player, inventoryWeight.weight(), maxWeight);
        }
    }
}
