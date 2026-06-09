package com.megatrex4.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public interface PlayerWeightComponent extends AutoSyncedComponent {
    float getCapacityBonus();

    void setCapacityBonus(float capacityBonus);

    float getCurrentInventoryWeight();

    float getMaxWeight();

    boolean isOverloaded();

    void setWeightState(float currentInventoryWeight, float maxWeight, boolean overloaded);

    @Override
    default void readFromNbt(NbtCompound tag) {
        setCapacityBonus(tag.getFloat("capacityBonus"));
        setWeightState(
                tag.getFloat("currentInventoryWeight"),
                tag.getFloat("maxWeight"),
                tag.getBoolean("overloaded")
        );
    }

    @Override
    default void writeToNbt(NbtCompound tag) {
        tag.putFloat("capacityBonus", getCapacityBonus());
        tag.putFloat("currentInventoryWeight", getCurrentInventoryWeight());
        tag.putFloat("maxWeight", getMaxWeight());
        tag.putBoolean("overloaded", isOverloaded());
    }
}
