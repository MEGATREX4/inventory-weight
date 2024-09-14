package com.megatrex4.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.InventoryWeight;
import com.megatrex4.util.ItemWeights;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConfigSyncPacket {
    public static final Identifier ID = new Identifier(InventoryWeight.MOD_ID, "config_sync");

    public static void send(ServerPlayerEntity player, JsonObject config) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(config.toString()); // Write the JSON string to the buffer
        ServerPlayNetworking.send(player, ID, buf); // Send packet to the player
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        String jsonString = buf.readString();
        JsonObject config = JsonParser.parseString(jsonString).getAsJsonObject();

        client.execute(() -> {
            // Load the server config on the client side
            if (config.has("serverConfig")) {
                JsonObject serverConfig = config.getAsJsonObject("serverConfig");
                ItemWeights.loadWeightsFromConfig(serverConfig);
            }

            // Load the items config on the client side
            if (config.has("itemsConfig")) {
                JsonObject itemsConfig = config.getAsJsonObject("itemsConfig");
                ItemWeights.loadWeightsFromConfig(itemsConfig);
            }
        });
    }
}
