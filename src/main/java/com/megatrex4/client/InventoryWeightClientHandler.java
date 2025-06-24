package com.megatrex4.client;

import com.megatrex4.network.InventoryWeightPacket;
import com.megatrex4.network.ModMessages;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InventoryWeightClientHandler {

    private static float inventoryWeight = 0.0f;
    private static float maxWeight = 0.0f;
    private static float pocketWeight = 0.0f; // Add this field

    public static void receivePacket(MinecraftClient client, Identifier id, PacketByteBuf buf) {
        if (id.equals(ModMessages.INVENTORY_WEIGHT_SYNC)) {
            if (buf.readableBytes() >= 12) { // Adjusted to account for the extra float
                try {
                    InventoryWeightPacket packet = InventoryWeightPacket.fromPacket(buf);
                    inventoryWeight = packet.getInventoryWeight();
                    maxWeight = packet.getMaxWeight();
                    pocketWeight = packet.getPocketWeight(); // Read new field
                } catch (Exception e) {
                    client.inGameHud.setOverlayMessage(Text.literal("Error processing packet"), true);
                    //LogUtils.getLogger().error("Error processing packet", e);
                }
            } else {
                client.inGameHud.setOverlayMessage(Text.literal("Received invalid inventory weight packet"), true);
                //LogUtils.getLogger().warn("Received packet with insufficient data. Bytes available: " + buf.readableBytes());
            }
        }
    }

    public static float getInventoryWeight() {
        return inventoryWeight;
    }

    public static float getMaxWeight() {
        return maxWeight;
    }

    public static float getPocketWeight() { // Add getter
        return pocketWeight;
    }
}
