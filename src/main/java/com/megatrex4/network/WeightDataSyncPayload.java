package com.megatrex4.network;

import com.megatrex4.impl.data.WeightDataSnapshot;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public record WeightDataSyncPayload(WeightDataSnapshot snapshot) implements CustomPacketPayload {

    public static final Identifier PAYLOAD_ID =
            Identifier.fromNamespaceAndPath(MOD_ID, "weight_data_sync");

    public static final CustomPacketPayload.Type<WeightDataSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, WeightDataSyncPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public WeightDataSyncPayload decode(RegistryFriendlyByteBuf buf) {
                    return new WeightDataSyncPayload(WeightDataStore.decodeSnapshot(buf));
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, WeightDataSyncPayload payload) {
                    WeightDataStore.encodeSnapshot(buf, payload.snapshot());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}