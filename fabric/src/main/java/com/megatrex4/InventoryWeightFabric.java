package com.megatrex4;

import net.fabricmc.api.ModInitializer;

public class InventoryWeightFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        InventoryWeight.init();
    }
}
