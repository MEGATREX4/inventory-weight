package com.megatrex4.inventoryweight;

import com.megatrex4.inventoryweight.effect.InventoryWeightEffects;
import com.megatrex4.inventoryweight.util.DatapackLoader;
import com.megatrex4.inventoryweight.util.Requirement;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class InventoryWeight implements ModInitializer {

	public static final String MOD_ID = "inventoryweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<Requirement<Item, ?>, Integer> customItemWeights = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> loadDatapacks(server.getResourceManager()));

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> loadDatapacks(resourceManager));

		InventoryWeightEffects.register();
	}

	private static void loadDatapacks(ResourceManager resourceManager) {
		new DatapackLoader(resourceManager).loadDatapacks();
	}

}
