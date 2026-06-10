package com.megatrex4.component;

import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public interface PlayerWeightComponent extends AutoSyncedComponent {
    float getCapacityBonus();

    void setCapacityBonus(float capacityBonus);

    float getCurrentInventoryWeight();

    float getMaxWeight();

    boolean isOverloaded();

    void setWeightState(float currentInventoryWeight, float maxWeight, boolean overloaded);

    @Override
    default void readData(ReadView readView) {
        setCapacityBonus(readView.getFloat("capacityBonus", 0.0f));

        setWeightState(
                readView.getFloat("currentInventoryWeight", 0.0f),
                readView.getFloat("maxWeight", 0.0f),
                readView.getBoolean("overloaded", false)
        );
    }

    @Override
    default void writeData(WriteView writeView) {
        writeView.putFloat("capacityBonus", getCapacityBonus());
        writeView.putFloat("currentInventoryWeight", getCurrentInventoryWeight());
        writeView.putFloat("maxWeight", getMaxWeight());
        writeView.putBoolean("overloaded", isOverloaded());
    }
}