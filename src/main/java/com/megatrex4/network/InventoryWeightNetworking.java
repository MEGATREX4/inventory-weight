package com.megatrex4.network;

import com.megatrex4.InventoryWeight;
import com.megatrex4.impl.player.PlayerWeightController;
import com.megatrex4.impl.data.WeightDataSnapshot;
import com.megatrex4.impl.data.WeightDataStore;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightNetworking {
    public static final Identifier WEIGHT_DATA_SYNC = new Identifier(MOD_ID, "weight_data_sync");

    private static boolean serverRegistered;

    private InventoryWeightNetworking() {}

    public static void registerServer() {
        if (serverRegistered) {
            InventoryWeight.LOGGER.debug("Inventory Weight server networking was already registered; skipping duplicate registration.");
            return;
        }
        serverRegistered = true;

        InventoryWeight.LOGGER.info("Registering Inventory Weight server networking channel: {}", WEIGHT_DATA_SYNC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            InventoryWeight.LOGGER.info("Player {} joined; syncing Inventory Weight datapack data.", handler.player.getName().getString());
            PlayerWeightController.updatePlayer(handler.player);
            sendDataTo(handler.player);
        });
    }

    public static void sendDataTo(ServerPlayerEntity player) {
        WeightDataSnapshot snapshot = WeightDataStore.INSTANCE.snapshot();
        PacketByteBuf buf = PacketByteBufs.create();
        WeightDataStore.encodeSnapshot(buf, snapshot);
        ServerPlayNetworking.send(player, WEIGHT_DATA_SYNC, buf);

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
