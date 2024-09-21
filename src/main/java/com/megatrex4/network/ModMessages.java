package com.megatrex4.network;

import com.megatrex4.client.InventoryWeightClientHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class ModMessages {
    public static final Identifier INVENTORY_WEIGHT_SYNC = new Identifier(MOD_ID, "inventory_weight_sync");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(INVENTORY_WEIGHT_SYNC, (client, handler, buf, responseSender) -> {
            //LogUtils.getLogger().info("Received packet on client side");
            InventoryWeightClientHandler.receivePacket(client, INVENTORY_WEIGHT_SYNC, buf);
        });
    }


}

