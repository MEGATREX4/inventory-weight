package com.megatrex4;

import com.megatrex4.hud.InventoryWeightHUD;
import com.megatrex4.items.screens.BackpackScreen;
import com.megatrex4.items.screens.ScreenHandlersRegistry;
import com.megatrex4.network.ConfigSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class InventoryWeightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register client-side packet receiver
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, ConfigSyncPacket::receive);
        HandledScreens.register(ScreenHandlersRegistry.BACKPACK_SCREEN_HANDLER_TYPE, BackpackScreen::new);

        // Register the HUD rendering
        new InventoryWeightHUD().onInitializeClient(); // This initializes HUD
    }
}
