package com.megatrex4;

import net.fabricmc.api.DedicatedServerModInitializer;

public class InventoryWeightServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        // Cardinal Components are registered via EntityComponentInitializer entrypoint
        // Config syncing is no longer needed - datapacks are synchronized automatically by Minecraft
        // Item weights are loaded from datapacks instead of being synced as config
    }
}

