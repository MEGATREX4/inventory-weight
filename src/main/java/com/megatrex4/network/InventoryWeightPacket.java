package com.megatrex4.network;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryWeightPacket {
    private final float inventoryWeight;
    private final float maxWeight;

    public InventoryWeightPacket(float inventoryWeight, float maxWeight) {
        this.inventoryWeight = inventoryWeight;
        this.maxWeight = maxWeight;
    }

    public void receive(PacketByteBuf buf) {
        buf.writeFloat(inventoryWeight);
        buf.writeFloat(maxWeight);
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeFloat(inventoryWeight);
        buf.writeFloat(maxWeight);
    }

    public static InventoryWeightPacket fromPacket(PacketByteBuf buf) {
        if (buf.readableBytes() < 8) {
            //LogUtils.getLogger().warn("Insufficient bytes in buffer. Bytes available: " + buf.readableBytes());
            return new InventoryWeightPacket(0.0f, 0.0f); // Or handle error as appropriate
        }
        float weight = buf.readFloat();
        float max = buf.readFloat();
        //LogUtils.getLogger().info("Read InventoryWeightPacket - Weight: " + weight + ", Max: " + max);
        return new InventoryWeightPacket(weight, max);
    }


    public float getInventoryWeight() {
        return inventoryWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public static void send(ServerPlayerEntity player, InventoryWeightPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.toPacket(buf);
        //LogUtils.getLogger().info("Sending InventoryWeightPacket - Weight: " + packet.getInventoryWeight() + ", Max: " + packet.getMaxWeight());
        ServerPlayNetworking.send(player, ModMessages.INVENTORY_WEIGHT_SYNC, buf);
    }

}
