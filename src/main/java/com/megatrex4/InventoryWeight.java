package com.megatrex4;

import com.megatrex4.commands.InventoryWeightCommands;
import com.megatrex4.effects.InventoryWeightEffects;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.config.InventoryWeightConfigEvents;
import com.megatrex4.impl.data.WeightDataReloadListener;
import com.megatrex4.impl.player.PlayerWeightController;
import com.megatrex4.network.InventoryWeightNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InventoryWeight implements ModInitializer {
    public static final String MOD_ID = "inventoryweight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        String version = FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");

        LOGGER.info("==================================================");
        LOGGER.info("Starting Inventory Weight v{}", version);
        LOGGER.info("Environment: {}", FabricLoader.getInstance().getEnvironmentType());
        LOGGER.info("Minecraft weight systems are being registered now.");
        LOGGER.info("==================================================");

        LOGGER.info("[1/8] Registering fzzy_config live update listener...");
        InventoryWeightConfigEvents.register();

        LOGGER.info("[2/8] Registering default weight, capacity, pocket, and inventory providers...");
        InventoryWeightServices.registerDefaults();

        LOGGER.info("[3/8] Loading Inventory Weight add-on entrypoints...");
        InventoryWeightServices.loadAddonEntrypoints();

        LOGGER.info("[4/8] Registering status effects...");
        InventoryWeightEffects.register();
        LOGGER.info("Registered status effect: {}:overload", MOD_ID);

        LOGGER.info("[5/8] Registering datapack reload listeners...");
        WeightDataReloadListener.register();

        LOGGER.info("[6/8] Registering server networking...");
        InventoryWeightNetworking.registerServer();

        LOGGER.info("[7/8] Registering commands...");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            InventoryWeightCommands.register(dispatcher);
            LOGGER.info("Inventory Weight commands registered for environment: {}", environment);
        });

        LOGGER.info("[8/8] Registering server tick handler...");
        ServerTickEvents.END_SERVER_TICK.register(PlayerWeightController::tickServer);

        registerLifecycleDebugLogging();

        LOGGER.info("Inventory Weight initialized successfully. Active hooks: {}", InventoryWeightServices.describeRegisteredHooks());
        LOGGER.info("Datapack folders: data/<namespace>/inventory_weight/items/*.json and data/<namespace>/inventory_weight/pockets/*.json");
    }

    private static void registerLifecycleDebugLogging() {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                LOGGER.info("Inventory Weight: server is starting; datapack weight data will be loaded by the reload listener."));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> LOGGER.info(
                "Inventory Weight: server started. Online players: {}. Runtime is ready.",
                server.getPlayerManager().getPlayerList().size()
        ));

        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                LOGGER.info("Inventory Weight: server is stopping; clearing runtime state on normal Minecraft shutdown."));
    }
}
