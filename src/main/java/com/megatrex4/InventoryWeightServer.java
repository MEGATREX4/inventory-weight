package com.megatrex4;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.network.ConfigSyncPacket;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.api.ModInitializer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class InventoryWeightServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        // Register an event to send the config packet when the player joins
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // Create a JsonObject representing your config
            JsonObject config = createConfigObject();
            ConfigSyncPacket.send(handler.player, config);
        });
    }

    private JsonObject createConfigObject() {
        JsonObject config = new JsonObject();

        // Load the inventory_weights_server.json config
        try {
            if (Files.exists(ItemWeightsConfigServer.CONFIG_PATH)) {
                JsonObject serverConfig = JsonParser.parseReader(new FileReader(ItemWeightsConfigServer.CONFIG_PATH.toFile())).getAsJsonObject();
                config.add("serverConfig", serverConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the inventory_weights_items.json config
        try {
            if (Files.exists(ItemWeightConfigItems.CONFIG_PATH)) {
                JsonObject itemsConfig = JsonParser.parseReader(new FileReader(ItemWeightConfigItems.CONFIG_PATH.toFile())).getAsJsonObject();
                config.add("itemsConfig", itemsConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

}

