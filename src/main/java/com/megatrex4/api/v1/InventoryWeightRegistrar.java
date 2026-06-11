package com.megatrex4.api.v1;

import net.minecraft.resources.Identifier;

public interface InventoryWeightRegistrar {
    void registerItemWeightProvider(Identifier id, int priority, ItemWeightProvider provider);

    void registerPlayerWeightSource(Identifier id, int priority, PlayerWeightSource source);

    void registerCapacityProvider(Identifier id, int priority, CapacityProvider provider);

    void registerPocketProvider(Identifier id, int priority, PocketProvider provider);
}
