package com.megatrex4;

import com.megatrex4.util.InventoryWeightUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class InventoryWeightState extends PersistentState {
    private float maxWeight = InventoryWeightUtil.MAXWEIGHT;

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

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putFloat("maxWeight", maxWeight);
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

        return state;
    }


}
