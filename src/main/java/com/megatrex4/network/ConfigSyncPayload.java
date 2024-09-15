package com.megatrex4.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ConfigSyncPayload(JsonObject config) implements CustomPayload {
    public static final CustomPayload.Id<ConfigSyncPayload> ID = new CustomPayload.Id<>(ModMessages.CONFIG_SYNC);

    // Define the codec for encoding and decoding the payload
    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.of(
            // Encoder: Write the JsonObject as a string
            (buf, payload) -> buf.toString(payload.config.toString()),

            // Decoder: Read a string from the buffer and convert it to a JsonObject
            buf -> {
                String jsonString = buf.readString();
                return new ConfigSyncPayload(new Gson().fromJson(jsonString, JsonObject.class));
            }
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
