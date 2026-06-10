package com.megatrex4.impl.weight;

import com.megatrex4.InventoryWeight;
import com.megatrex4.api.v1.CapacityModifier;
import com.megatrex4.api.v1.CapacityProvider;
import com.megatrex4.api.v1.InventoryWeightAttributes;
import com.megatrex4.api.v1.InventoryWeightEvents;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.registry.PrioritizedRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public final class CapacityService {
    private final PrioritizedRegistry<CapacityProvider> providers;

    public CapacityService(PrioritizedRegistry<CapacityProvider> providers) {
        this.providers = providers;
    }

    public float getMaxWeight(ServerPlayerEntity player) {
        float configuredBase = WeightSettings.get().maxWeight();
        float attributeBonus = (float) InventoryWeightAttributes.getValue(player);
        float additive = attributeBonus;
        float multiplier = 1.0f;

        for (PrioritizedRegistry.Entry<CapacityProvider> entry : providers.entries()) {
            try {
                CapacityModifier modifier = entry.value().getCapacityModifier(player).sanitized();
                additive += modifier.additive();
                multiplier *= modifier.multiplier();
            } catch (Exception e) {
                InventoryWeight.LOGGER.error(
                        "Capacity provider {} failed for {}",
                        entry.id(),
                        player.getName().getString(),
                        e
                );
            }
        }

        float result = Math.max(1.0f, (configuredBase + additive) * multiplier);
        return InventoryWeightEvents.MODIFY_MAX_WEIGHT
                .invoker()
                .modify(player, result);
    }
}
