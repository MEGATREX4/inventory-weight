package com.megatrex4;

import com.megatrex4.client.ClientInventoryWeightNetworking;
import com.megatrex4.client.InventoryWeightHud;
import com.megatrex4.client.WeightTooltipHandler;
import com.megatrex4.impl.InventoryWeightServices;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class InventoryWeightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InventoryWeight.LOGGER.info("Initializing Inventory Weight client features...");

        InventoryWeightServices.registerDefaults();

        InventoryWeight.LOGGER.info("Registering client datapack/config sync receiver...");
        ClientInventoryWeightNetworking.register();

        InventoryWeight.LOGGER.info("Registering Inventory Weight HUD renderer...");
        HudRenderCallback.EVENT.register(InventoryWeightHud::render);

        InventoryWeight.LOGGER.info("Registering Inventory Weight item tooltip handler...");
        ItemTooltipCallback.EVENT.register(WeightTooltipHandler::appendTooltip);

        InventoryWeight.LOGGER.info("Inventory Weight client initialized successfully.");
    }
}
