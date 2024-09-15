package com.megatrex4.network;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConfigSyncPacket {
    public static final CustomPayload.Id<ConfigSyncPayload> ID = ConfigSyncPayload.ID;

    private final JsonObject config;

    public ConfigSyncPacket(JsonObject config) {
        this.config = config;
    }

    public JsonObject getConfig() {
        return config;
    }

    public static void send(ServerPlayerEntity player, JsonObject config) {
        ConfigSyncPayload payload = new ConfigSyncPayload(config);
        ServerPlayNetworking.send(player, payload); // Use the payload ID
    }
}
