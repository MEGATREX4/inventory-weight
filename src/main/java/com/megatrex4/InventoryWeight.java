package com.megatrex4;

import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigClient;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.network.ConfigSyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.megatrex4.effects.InventoryWeightEffectRegister.OVERLOAD_EFFECT;
import static com.megatrex4.effects.InventoryWeightEffectRegister.registerEffects;

public class InventoryWeight implements ModInitializer {
	public static final String MOD_ID = "inventoryweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info(MOD_ID + " mod initialized!");

		ItemWeightsConfigServer.loadConfig();
		ItemWeightConfigItems.loadConfig();
		ItemWeightsConfigClient.loadConfig();

		InventoryWeightEffectRegister.registerEffects();

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
