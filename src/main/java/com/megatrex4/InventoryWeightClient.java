package com.megatrex4;

import com.megatrex4.network.ConfigSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class InventoryWeightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client-side packet registration
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, ConfigSyncPacket::receive);
    }
}

