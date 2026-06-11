package com.megatrex4.impl.config;

import com.megatrex4.InventoryWeight;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.player.PlayerWeightController;
import com.megatrex4.network.InventoryWeightNetworking;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.event.api.OnUpdateServerListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class InventoryWeightConfigEvents {
    private static boolean registered;

    private InventoryWeightConfigEvents() {}

    public static void register() {
        if (registered) {
            InventoryWeight.LOGGER.debug("Inventory Weight fzzy_config update listeners were already registered; skipping duplicate registration.");
            return;
        }
        registered = true;

        // Force the synced/server config to be registered early so fzzy_config can track and sync it.
        InventoryWeightConfig.getServer();
        WeightSettings.refreshFromConfig();

        InventoryWeight.LOGGER.info("Registering Inventory Weight fzzy_config live update listener for {}.", InventoryWeightConfig.SERVER_CONFIG_ID);

        // Use the classic listener signature here because it is the one used by the 1.20.1-era
        // fzzy_config versions. Newer fzzy_config versions keep it as deprecated compatibility.
        ConfigApiJava.event().onUpdateServer((OnUpdateServerListener) (id, config, player) -> {
            InventoryWeight.LOGGER.info(
                    "fzzy_config server update observed: id={}, configClass={}, player={}",
                    id,
                    config == null ? "null" : config.getClass().getName(),
                    player == null ? "unknown" : player.getName().getString()
            );

            if (!isServerConfigUpdate(id, config)) {
                return;
            }

            if (player == null) {
                InventoryWeight.LOGGER.warn("Inventory Weight server config update had no player context; cannot access server from old fzzy_config listener.");
                return;
            }

            InventoryWeight.LOGGER.info("Inventory Weight server config changed by {}; applying live runtime update.", player.getName().getString());
            applyServerConfigChange(player.level().getServer(), "fzzy_config server update event");
        });
    }

    public static boolean isServerConfigUpdate(Identifier id, Config config) {
        if (config instanceof InventoryWeightConfig.Server) {
            return true;
        }

        if (InventoryWeightConfig.SERVER_CONFIG_ID.equals(id)) {
            return true;
        }

        if (id == null) {
            return false;
        }

        // fzzy_config logs scopes like inventoryweight.server-config, while Identifiers normally
        // print inventoryweight:server-config. This keeps the check tolerant across versions.
        String asString = id.toString();
        return "inventoryweight.server-config".equals(asString)
                || "inventoryweight:server-config".equals(asString)
                || (InventoryWeight.MOD_ID.equals(id.getNamespace()) && "server-config".equals(id.getPath()))
                || ("inventoryweight.server-config".equals(id.getNamespace()) && "server-config".equals(id.getPath()));
    }

    public static void applyServerConfigChange(MinecraftServer server, String reason) {
        if (server == null) {
            InventoryWeight.LOGGER.warn("Cannot apply Inventory Weight server settings live from {} because MinecraftServer is null.", reason);
            return;
        }

        ServerWeightSettings settings = WeightSettings.refreshFromConfig();

        InventoryWeight.LOGGER.info(
                "Applying Inventory Weight server settings live from {}: configuredDefaultMaxWeight={}, pocketWeight={}, realisticMode={}, overloadPenaltyStrength={}. Player max-weight attributes are not overwritten.",
                reason,
                settings.maxWeight(),
                settings.pocketWeight(),
                settings.realisticMode(),
                settings.overloadPenaltyStrength()
        );

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerWeightController.updatePlayer(player);
        }

        InventoryWeightNetworking.sendDataToAll(server);
    }
}
