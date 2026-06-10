package com.megatrex4.network;

import com.megatrex4.impl.data.WeightDataSnapshot;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public record WeightDataSyncPayload(WeightDataSnapshot snapshot) implements CustomPayload {
    public static final Id<WeightDataSyncPayload> ID =
            new CustomPayload.Id<>(Identifier.of(MOD_ID, "weight_data_sync"));

    public static final PacketCodec<RegistryByteBuf, WeightDataSyncPayload> CODEC =
            PacketCodec.of(WeightDataSyncPayload::write, WeightDataSyncPayload::new);

    private WeightDataSyncPayload(RegistryByteBuf buf) {
        this(WeightDataStore.decodeSnapshot(buf));
    }

    private void write(RegistryByteBuf buf) {
        WeightDataStore.encodeSnapshot(buf, snapshot);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}