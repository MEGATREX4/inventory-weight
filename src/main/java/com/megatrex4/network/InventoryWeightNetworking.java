package com.megatrex4.network;

import com.megatrex4.InventoryWeight;
import com.megatrex4.impl.data.WeightDataSnapshot;
import com.megatrex4.impl.data.WeightDataStore;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightNetworking {

    public static final Identifier WEIGHT_DATA_SYNC =
            Identifier.fromNamespaceAndPath(MOD_ID, "weight_data_sync");

    private InventoryWeightNetworking() {}

    public static void registerServer() {
        PayloadTypeRegistry.clientboundPlay().register(
                WeightDataSyncPayload.TYPE,
                WeightDataSyncPayload.CODEC
        );
    }

    public static void sendDataTo(ServerPlayer player) {
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
        int playerCount = server.getPlayerList().getPlayers().size();

        InventoryWeight.LOGGER.info(
                "Syncing Inventory Weight datapack data to {} player(s).",
                playerCount
        );

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendDataTo(player);
        }
    }
}