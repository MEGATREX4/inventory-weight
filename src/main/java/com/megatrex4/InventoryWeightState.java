package com.megatrex4;

import com.megatrex4.util.InventoryWeightUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;

public class InventoryWeightState extends PersistentState {
    private float maxWeight = InventoryWeightUtil.MAXWEIGHT;

    // Declare playerMultipliers as a map of player UUIDs to their respective multipliers
    private final Map<String, Float> playerMultipliers = new HashMap<>();

    public static void setClientMaxWeight(float maxWeight) {
        InventoryWeightState clientState = new InventoryWeightState();
        clientState.maxWeight = maxWeight;
        clientState.markDirty();
    }


    public float getMaxWeight() {
        return maxWeight;
    }

    public static void setMaxWeight(MinecraftServer server, float maxWeight) {
        for (ServerWorld world : server.getWorlds()) {
            InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                    InventoryWeightState::fromNbt,
                    InventoryWeightState::new,
                    "inventoryweight_data"
            );
            state.maxWeight = maxWeight;
            state.markDirty();
        }
    }


    // Set multiplier for a specific player
    public void setPlayerMultiplier(MinecraftServer server, String playerUuid, float multiplier) {
        for (ServerWorld world : server.getWorlds()) {
            InventoryWeightState state = world.getPersistentStateManager().getOrCreate(
                    InventoryWeightState::fromNbt,
                    InventoryWeightState::new,
                    "inventoryweight_data"
            );
            state.playerMultipliers.put(playerUuid, multiplier);
            state.markDirty();
        }
    }


    // Get multiplier for a specific player
    public float getPlayerMultiplier(String playerUuid) {
        return this.playerMultipliers.getOrDefault(playerUuid, 0.0f); // Default is 1.0
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putFloat("maxWeight", maxWeight);
        // Save player multipliers to NBT
        NbtCompound multipliersTag = new NbtCompound();
        for (Map.Entry<String, Float> entry : playerMultipliers.entrySet()) {
            multipliersTag.putFloat(entry.getKey(), entry.getValue());
        }
        tag.put("playerMultipliers", multipliersTag);
        return tag;
    }


    public static InventoryWeightState fromNbt(NbtCompound tag) {
        InventoryWeightState state = new InventoryWeightState();

        if (tag == null) {
            return state;
        }

        if (tag.contains("maxWeight")) {
            state.maxWeight = tag.getFloat("maxWeight");
        }

        // Load player multipliers
        if (tag.contains("playerMultipliers")) {
            NbtCompound multipliersTag = tag.getCompound("playerMultipliers");
            for (String key : multipliersTag.getKeys()) {
                state.playerMultipliers.put(key, multipliersTag.getFloat(key));
            }
        }

        return state;
    }


}
