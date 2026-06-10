package com.megatrex4.network;

import com.megatrex4.InventoryWeight;
import com.megatrex4.impl.player.PlayerWeightController;
import com.megatrex4.impl.data.WeightDataSnapshot;
import com.megatrex4.impl.data.WeightDataStore;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightNetworking {
    public static final Identifier WEIGHT_DATA_SYNC = Identifier.of(MOD_ID, "weight_data_sync");

    private static boolean serverRegistered;

    private InventoryWeightNetworking() {}

    public static void registerServer() {
        PayloadTypeRegistry.playS2C().register(
                WeightDataSyncPayload.ID,
                WeightDataSyncPayload.CODEC
        );
    }

    public static void sendDataTo(ServerPlayerEntity player) {
        WeightDataSnapshot snapshot = WeightDataStore.INSTANCE.snapshot();

        ServerPlayNetworking.send(player, new WeightDataSyncPayload(snapshot));

        InventoryWeight.LOGGER.debug(
                "Synced Inventory Weight data to {}: {} item weights, {} pocket definitions, {} NBT weight rules, {} NBT pocket rules.",
                player.getName().getString(),
                snapshot.itemWeights().size(),
                snapshot.pockets().size(),
                snapshot.nbtWeightRules().size(),
                snapshot.nbtPocketRules().size()
        );
    }

    public static void sendDataToAll(MinecraftServer server) {
        int playerCount = server.getPlayerManager().getPlayerList().size();
        InventoryWeight.LOGGER.info("Syncing Inventory Weight datapack data to {} player(s).", playerCount);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendDataTo(player);
        }
    }
}
