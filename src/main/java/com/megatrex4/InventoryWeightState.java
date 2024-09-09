package com.megatrex4;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class InventoryWeightState extends PersistentState {
    private long maxWeight = 10000; // Default value

    public void setMaxWeight(long value) {
        this.maxWeight = value;
        markDirty();
    }

    public long getMaxWeight() {
        return this.maxWeight;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putLong("maxWeight", maxWeight);
        return tag;
    }

    public static InventoryWeightState fromNbt(NbtCompound tag) {
        InventoryWeightState state = new InventoryWeightState();
        if (tag.contains("maxWeight")) {
            state.maxWeight = tag.getLong("maxWeight");
        }
        return state;
    }
}

