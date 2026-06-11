package com.megatrex4.client;

import com.megatrex4.impl.data.WeightDataStore;
import com.megatrex4.network.WeightDataSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientInventoryWeightNetworking {

    private static boolean registered;

    private ClientInventoryWeightNetworking() {}

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;

        ClientPlayNetworking.registerGlobalReceiver(
                WeightDataSyncPayload.TYPE,
                (payload, context) -> {
                    WeightDataStore.INSTANCE.applySnapshot(payload.snapshot());
                }
        );
    }
}