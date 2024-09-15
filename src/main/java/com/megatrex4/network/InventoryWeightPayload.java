package com.megatrex4.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record InventoryWeightPayload(float inventoryWeight, float maxWeight, float pocketWeight) implements CustomPayload {
    public static final CustomPayload.Id<InventoryWeightPayload> ID = new CustomPayload.Id<>(ModMessages.INVENTORY_WEIGHT_SYNC);
    public static final PacketCodec<RegistryByteBuf, InventoryWeightPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, InventoryWeightPayload::inventoryWeight,
            PacketCodecs.FLOAT, InventoryWeightPayload::maxWeight,
            PacketCodecs.FLOAT, InventoryWeightPayload::pocketWeight,
            InventoryWeightPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
