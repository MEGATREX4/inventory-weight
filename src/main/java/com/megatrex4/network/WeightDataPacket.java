package com.megatrex4.network;

import com.megatrex4.data.ClientPlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

public class WeightDataPacket {
    public static final Identifier ID = new Identifier("inventoryweight", "weight_data");

    private final int currentWeight;
    private final int maxWeight;

    public WeightDataPacket(int currentWeight, int maxWeight) {
        this.currentWeight = currentWeight;
        this.maxWeight = maxWeight;
    }

    public static WeightDataPacket read(PacketByteBuf buf) {
        return new WeightDataPacket(buf.readInt(), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(currentWeight);
        buf.writeInt(maxWeight);
    }

    public void apply(ClientPlayPacketListener listener) {
        // Store the received data in the client's player data
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            ClientPlayerData.updateWeightData(currentWeight, maxWeight);
        }
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }
}
