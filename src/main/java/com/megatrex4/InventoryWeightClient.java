package com.megatrex4;

import com.megatrex4.hud.InventoryWeightHUD;
import net.fabricmc.api.ClientModInitializer;

public class InventoryWeightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the HUD rendering
        new InventoryWeightHUD().onInitializeClient(); // This initializes HUD
    }
}
