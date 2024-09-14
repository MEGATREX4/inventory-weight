package com.megatrex4.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryWeightPacket {
    private final float inventoryWeight;
    private final float maxWeight;
    private final float pocketWeight; // Add this field

    public InventoryWeightPacket(float inventoryWeight, float maxWeight, float pocketWeight) {
        this.inventoryWeight = inventoryWeight;
        this.maxWeight = maxWeight;
        this.pocketWeight = pocketWeight; // Initialize new field
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeFloat(inventoryWeight);
        buf.writeFloat(maxWeight);
        buf.writeFloat(pocketWeight); // Write new field
    }

    public static InventoryWeightPacket fromPacket(PacketByteBuf buf) {
        if (buf.readableBytes() < 12) { // Adjusted to account for the extra float
            return new InventoryWeightPacket(0.0f, 0.0f, 0.0f); // Handle error as appropriate
        }
        float weight = buf.readFloat();
        float max = buf.readFloat();
        float pocket = buf.readFloat(); // Read new field
        return new InventoryWeightPacket(weight, max, pocket);
    }

    public float getInventoryWeight() {
        return inventoryWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public float getPocketWeight() { // Add getter
        return pocketWeight;
    }

    public static void send(ServerPlayerEntity player, InventoryWeightPacket packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        packet.toPacket(buf);
        ServerPlayNetworking.send(player, ModMessages.INVENTORY_WEIGHT_SYNC, buf);
    }
}
