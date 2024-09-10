package com.megatrex4;

import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigClient;
import com.megatrex4.config.ItemWeightsConfigServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryWeight implements ModInitializer {
	public static final String MOD_ID = "inventoryweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info(MOD_ID + " mod initialized!");

		ItemWeightsConfigServer.loadConfig();
		ItemWeightConfigItems.loadConfig();
		ItemWeightsConfigClient.loadConfig();

		// Register commands using CommandRegistrationCallback
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandRegistry.registerCommands(dispatcher);
		});

		// Register tick event for updating player weights
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world != null && !world.isClient) {
				InventoryWeightHandler.tick((ServerWorld) world);
			}
		});
	}
}
