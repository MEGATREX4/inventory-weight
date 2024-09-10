package com.megatrex4;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import com.megatrex4.config.ItemWeightsConfigServer;

public class InventoryWeightState extends PersistentState {
    private float maxWeight = ItemWeightsConfigServer.getMaxWeightFromConfig();

    public void setMaxWeight(float value) {
        this.maxWeight = value;
        markDirty();
    }

    public float getMaxWeight() {
        return this.maxWeight;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putFloat("maxWeight", maxWeight);
        return tag;
    }

    public static InventoryWeightState fromNbt(NbtCompound tag) {
        InventoryWeightState state = new InventoryWeightState();
        if (tag != null && tag.contains("maxWeight")) {
            state.maxWeight = tag.getFloat("maxWeight");
        }
        return state;
    }
}
