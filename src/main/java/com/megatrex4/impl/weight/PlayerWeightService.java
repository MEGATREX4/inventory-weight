package com.megatrex4.impl.weight;

import com.megatrex4.InventoryWeight;
import com.megatrex4.api.v1.InventoryWeightEvents;
import com.megatrex4.api.v1.PlayerWeightSource;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.registry.PrioritizedRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerWeightService {
    private final WeightService weightService;
    private final PrioritizedRegistry<PlayerWeightSource> sources;

    public PlayerWeightService(WeightService weightService, PrioritizedRegistry<PlayerWeightSource> sources) {
        this.weightService = weightService;
        this.sources = sources;
    }

    public WeightResult getInventoryWeight(PlayerEntity player) {
        WeightContext context = new WeightContext(player.getEntityWorld(), player, 0);
        WeightResult total = WeightResult.ZERO;

        for (PrioritizedRegistry.Entry<PlayerWeightSource> entry : sources.entries()) {
            try {
                total = total.add(entry.value().getWeight(player, context, weightService)).sanitized();
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Player weight source {} failed for {}", entry.id(), player.getName().getString(), e);
            }
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            total = InventoryWeightEvents.MODIFY_PLAYER_INVENTORY_WEIGHT.invoker()
                    .modify(serverPlayer, total)
                    .sanitized();
        }

        return total;
    }
}
