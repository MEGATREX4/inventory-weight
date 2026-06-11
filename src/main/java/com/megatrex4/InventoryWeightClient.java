package com.megatrex4;

import com.megatrex4.client.*;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.config.WeightSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class InventoryWeightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InventoryWeight.LOGGER.info("Initializing Inventory Weight client features...");

        InventoryWeightServices.registerDefaults();

        InventoryWeight.LOGGER.info("Loading Inventory Weight client config...");
        InventoryWeightConfig.getClient();
        WeightSettings.refreshFromConfig();

        InventoryWeight.LOGGER.info("Registering client fzzy_config live update listener...");
        ClientInventoryWeightConfigEvents.register();

        InventoryWeight.LOGGER.info("Registering client datapack/config sync receiver...");
        ClientInventoryWeightNetworking.register();

        InventoryWeight.LOGGER.info("Registering Inventory Weight HUD renderer...");
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(InventoryWeight.MOD_ID, "weight_hud"),
                InventoryWeightHud::render
        );

        ClientKeyState.register();
        InventoryWeight.LOGGER.info("Registering Inventory Weight item tooltip handler...");
        ItemTooltipCallback.EVENT.register((stack, context, tooltipFlag, lines) ->
        WeightTooltipHandler.appendTooltip(stack, context, tooltipFlag, lines));

        InventoryWeight.LOGGER.info("Inventory Weight client initialized successfully.");
    }
}