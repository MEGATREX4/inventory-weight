package com.megatrex4.client;

import com.megatrex4.InventoryWeight;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.config.InventoryWeightConfigEvents;
import com.megatrex4.impl.config.WeightSettings;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.event.api.OnUpdateClientListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public final class ClientInventoryWeightConfigEvents {
    private static boolean registered;

    private ClientInventoryWeightConfigEvents() {}

    public static void register() {
        if (registered) {
            InventoryWeight.LOGGER.debug("Inventory Weight client fzzy_config update listener was already registered; skipping duplicate registration.");
            return;
        }
        registered = true;

        InventoryWeight.LOGGER.info("Registering Inventory Weight client fzzy_config live update listener.");

        ConfigApiJava.event().onUpdateClient((OnUpdateClientListener) (id, config) -> {
            InventoryWeight.LOGGER.info(
                    "fzzy_config client update observed: id={}, configClass={}",
                    id,
                    config == null ? "null" : config.getClass().getName()
            );

            if (!isInventoryWeightConfig(id, config)) {
                return;
            }

            WeightSettings.refreshFromConfig();

            if (!InventoryWeightConfigEvents.isServerConfigUpdate(id, config)) {
                return;
            }

            Minecraft minecraft = Minecraft.getInstance();
            MinecraftServer integratedServer = minecraft.level.getServer();

            if (integratedServer == null) {
                InventoryWeight.LOGGER.info(
                        "Inventory Weight server config changed on the client. No integrated server is present, so the remote server must apply and sync the update."
                );
                return;
            }

            InventoryWeight.LOGGER.info("Inventory Weight server config changed through the client GUI in integrated server; scheduling live server update.");
            integratedServer.execute(() -> InventoryWeightConfigEvents.applyServerConfigChange(
                    integratedServer,
                    "fzzy_config client update in integrated server"
            ));
        });
    }

    private static boolean isInventoryWeightConfig(Identifier id, Config config) {
        if (config instanceof InventoryWeightConfig.Server || config instanceof InventoryWeightConfig.Client) {
            return true;
        }

        if (id == null) {
            return false;
        }

        String asString = id.toString();
        return asString.startsWith("inventoryweight:")
                || asString.startsWith("inventoryweight.")
                || InventoryWeight.MOD_ID.equals(id.getNamespace())
                || id.getNamespace().startsWith("inventoryweight.");
    }
}
