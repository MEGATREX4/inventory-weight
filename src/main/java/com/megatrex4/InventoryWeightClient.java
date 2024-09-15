package com.megatrex4;

import com.megatrex4.hud.InventoryWeightHUD;
import com.megatrex4.network.ConfigSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class InventoryWeightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the HUD rendering
        new InventoryWeightHUD().onInitializeClient(); // This initializes HUD
    }
}
