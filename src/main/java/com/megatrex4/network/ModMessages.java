package com.megatrex4.network;

import com.google.gson.JsonObject;
import com.megatrex4.util.ItemWeights;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class ModMessages {
    public static final Identifier INVENTORY_WEIGHT_SYNC = new Identifier("inventoryweight", "inventory_weight_sync");
    public static final Identifier CONFIG_SYNC = new Identifier("inventoryweight", "config_sync");

    static {
        // Register payload types
        PayloadTypeRegistry.playS2C().register(InventoryWeightPayload.ID, InventoryWeightPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);

        // Register global receivers
        ClientPlayNetworking.registerGlobalReceiver(InventoryWeightPayload.ID, (buffer, context) -> {
            context.client().execute(() -> {
                // Handle InventoryWeightPayload
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(CONFIG_SYNC, (buffer, context) -> {
            context.client().execute(() -> {
                // Handle ConfigSyncPayload
                ConfigSyncPayload payload = ConfigSyncPayload.CODEC.decode(buffer);
                JsonObject config = payload.config();

                if (config.has("serverConfig")) {
                    JsonObject serverConfig = config.getAsJsonObject("serverConfig");
                    ItemWeights.loadWeightsFromConfig(serverConfig);
                }

                if (config.has("itemsConfig")) {
                    JsonObject itemsConfig = config.getAsJsonObject("itemsConfig");
                    ItemWeights.loadWeightsFromConfig(itemsConfig);
                }
            });
        });
    }
}
