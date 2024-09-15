package com.megatrex4.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class InventoryWeightPacket {
    private final float inventoryWeight;
    private final float maxWeight;
    private final float pocketWeight;

    public InventoryWeightPacket(float inventoryWeight, float maxWeight, float pocketWeight) {
        this.inventoryWeight = inventoryWeight;
        this.maxWeight = maxWeight;
        this.pocketWeight = pocketWeight;
    }

    public static InventoryWeightPacket fromPacket(PacketByteBuf buf) {
        float inventoryWeight = buf.readFloat();
        float maxWeight = buf.readFloat();
        float pocketWeight = buf.readFloat();
        return new InventoryWeightPacket(inventoryWeight, maxWeight, pocketWeight);
    }

    public float getInventoryWeight() {
        return inventoryWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public float getPocketWeight() {
        return pocketWeight;
    }

    public static void send(ServerPlayerEntity player, InventoryWeightPacket packet) {
        InventoryWeightPayload payload = new InventoryWeightPayload(
                packet.getInventoryWeight(),
                packet.getMaxWeight(),
                packet.getPocketWeight()
        );
        ServerPlayNetworking.send(player, payload);
    }
}

