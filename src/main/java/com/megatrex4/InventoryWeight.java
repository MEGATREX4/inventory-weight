package com.megatrex4;

import com.megatrex4.client.InventoryWeightClientHandler;
import com.megatrex4.commands.CommandRegistry;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigClient;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.network.ModMessages;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.util.Tooltips;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.item.trim.ArmorTrim.appendTooltip;

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

		// Register tick event for updating player weights
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world != null && !world.isClient) {
				InventoryWeightHandler.tick((ServerWorld) world);
			}
		});
	}

	private void addCustomTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip) {
		World world = null; // Handle null check if world context is needed
		Tooltips.appendTooltip(stack, world, tooltip, context);
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