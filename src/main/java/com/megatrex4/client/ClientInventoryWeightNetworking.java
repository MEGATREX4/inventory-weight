package com.megatrex4.client;

import com.megatrex4.InventoryWeight;
import com.megatrex4.impl.data.WeightDataSnapshot;
import com.megatrex4.impl.data.WeightDataStore;
import com.megatrex4.network.InventoryWeightNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientInventoryWeightNetworking {
    private static boolean registered;

    private ClientInventoryWeightNetworking() {}

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        ClientPlayNetworking.registerGlobalReceiver(InventoryWeightNetworking.WEIGHT_DATA_SYNC, (client, handler, buf, responseSender) -> {
            WeightDataSnapshot snapshot;
            try {
                snapshot = WeightDataStore.decodeSnapshot(buf);
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Failed to decode Inventory Weight data sync packet", e);
                return;
            }
            client.execute(() -> WeightDataStore.INSTANCE.applySnapshot(snapshot));
        });
    }
}
