package com.megatrex4;

import com.megatrex4.client.InventoryWeightClientHandler;
import com.megatrex4.commands.CommandRegistry;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigClient;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.network.ModMessages;
import com.megatrex4.util.Tooltips;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InventoryWeight implements ModInitializer {


	public static final String MOD_ID = "inventoryweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info(MOD_ID + " mod initialized!");

		ModMessages.registerS2CPackets();
		ClientPlayNetworking.registerGlobalReceiver(ModMessages.INVENTORY_WEIGHT_SYNC, (client, handler, buf, responseSender) -> {
			InventoryWeightClientHandler.receivePacket(client, ModMessages.INVENTORY_WEIGHT_SYNC, buf);
		});

		ItemWeightsConfigServer.loadConfig();
		ItemWeightConfigItems.loadConfig();
		ItemWeightsConfigClient.loadConfig();

		loadDatapack();

		ItemTooltipCallback.EVENT.register(this::addCustomTooltip);

		InventoryWeightEffectRegister.registerEffects();

		// Register commands using CommandRegistrationCallback
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandRegistry.registerCommands(dispatcher);
		});

		ServerTickEvents.START_SERVER_TICK.register(server -> {
			for (World world : server.getWorlds()) {
				// Check if the world is not client-side
				if (!world.isClient) {
					// Retrieve the max weight from the server configuration
					float maxWeight = ItemWeightsConfigServer.loadMaxWeight();
					// Set the max weight in the InventoryWeightState for each player
					world.getPlayers().forEach(player -> {
						InventoryWeightState.setMaxWeight(server, maxWeight);
					});
				}
			}
		});


		// Register tick event for updating player weights
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world != null && !world.isClient) {
				InventoryWeightHandler.tick(world);
			}
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ItemWeightsConfigServer.saveConfig();
		});
	}

	private void addCustomTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip) {
		Tooltips.appendTooltip(stack, tooltip, context);
	}


	public static void loadDatapack () {
		// Register server starting event
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			// Load datapack data when the server starts
			InventoryWeightArmor.loadDatapackData(server.getResourceManager());
		});

		// Correctly register START_DATA_PACK_RELOAD
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> {
			InventoryWeightArmor.loadDatapackData(resourceManager);
		});
	}


}
