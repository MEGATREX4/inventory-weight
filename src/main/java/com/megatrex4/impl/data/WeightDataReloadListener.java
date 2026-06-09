package com.megatrex4.impl.data;

import com.megatrex4.InventoryWeight;
import com.megatrex4.network.InventoryWeightNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class WeightDataReloadListener {
    private static boolean registered;

    private WeightDataReloadListener() {}

    public static void register() {
        if (registered) {
            InventoryWeight.LOGGER.debug("Inventory Weight datapack reload listener was already registered; skipping duplicate registration.");
            return;
        }
        registered = true;

        InventoryWeight.LOGGER.info("Registering Inventory Weight datapack reload listener: {}:weight_data", InventoryWeight.MOD_ID);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(InventoryWeight.MOD_ID, "weight_data");
            }

            @Override
            public void reload(ResourceManager manager) {
                InventoryWeight.LOGGER.info("Loading Inventory Weight datapack data...");
                long start = System.nanoTime();

                WeightDataStore.INSTANCE.reload(manager);

                WeightDataSnapshot snapshot = WeightDataStore.INSTANCE.snapshot();
                double elapsedMs = (System.nanoTime() - start) / 1_000_000.0D;

                InventoryWeight.LOGGER.info(
                        "Loaded Inventory Weight datapack data in {} ms: {} item weights, {} pocket definitions, {} NBT weight rules, {} NBT pocket rules.",
                        String.format("%.2f", elapsedMs),
                        snapshot.itemWeights().size(),
                        snapshot.pockets().size(),
                        snapshot.nbtWeightRules().size(),
                        snapshot.nbtPocketRules().size()
                );
            }
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {
            if (success) {
                InventoryWeight.LOGGER.info(
                        "Inventory Weight datapack reload completed successfully; syncing weight data to {} player(s).",
                        server.getPlayerManager().getPlayerList().size()
                );
                InventoryWeightNetworking.sendDataToAll(server);
            } else {
                InventoryWeight.LOGGER.warn("Inventory Weight datapack reload failed; keeping previous weight data and skipping client sync.");
            }
        });
    }
}
