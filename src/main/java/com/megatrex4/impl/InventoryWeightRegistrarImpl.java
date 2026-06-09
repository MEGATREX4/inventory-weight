package com.megatrex4.impl;

import com.megatrex4.api.v1.*;
import com.megatrex4.impl.registry.PrioritizedRegistry;
import net.minecraft.util.Identifier;

public final class InventoryWeightRegistrarImpl implements InventoryWeightRegistrar {
    private final PrioritizedRegistry<ItemWeightProvider> itemWeightProviders;
    private final PrioritizedRegistry<PlayerWeightSource> playerWeightSources;
    private final PrioritizedRegistry<CapacityProvider> capacityProviders;
    private final PrioritizedRegistry<PocketProvider> pocketProviders;

    public InventoryWeightRegistrarImpl(
            PrioritizedRegistry<ItemWeightProvider> itemWeightProviders,
            PrioritizedRegistry<PlayerWeightSource> playerWeightSources,
            PrioritizedRegistry<CapacityProvider> capacityProviders,
            PrioritizedRegistry<PocketProvider> pocketProviders
    ) {
        this.itemWeightProviders = itemWeightProviders;
        this.playerWeightSources = playerWeightSources;
        this.capacityProviders = capacityProviders;
        this.pocketProviders = pocketProviders;
    }

    @Override
    public void registerItemWeightProvider(Identifier id, int priority, ItemWeightProvider provider) {
        itemWeightProviders.register(id, priority, provider);
    }

    @Override
    public void registerPlayerWeightSource(Identifier id, int priority, PlayerWeightSource source) {
        playerWeightSources.register(id, priority, source);
    }

    @Override
    public void registerCapacityProvider(Identifier id, int priority, CapacityProvider provider) {
        capacityProviders.register(id, priority, provider);
    }

    @Override
    public void registerPocketProvider(Identifier id, int priority, PocketProvider provider) {
        pocketProviders.register(id, priority, provider);
    }
}
