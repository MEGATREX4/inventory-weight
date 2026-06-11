package com.megatrex4.component;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public interface PlayerWeightComponent extends AutoSyncedComponent {
    float getCapacityBonus();

    void setCapacityBonus(float capacityBonus);

    float getCurrentInventoryWeight();

    float getMaxWeight();

    boolean isOverloaded();

    void setWeightState(float currentInventoryWeight, float maxWeight, boolean overloaded);

    @Override
    default void readData(ValueInput input) {
        setCapacityBonus(input.getFloatOr("capacityBonus", 0.0f));

        setWeightState(
                input.getFloatOr("currentInventoryWeight", 0.0f),
                input.getFloatOr("maxWeight", 0.0f),
                input.getBooleanOr("overloaded", false)
        );
    }

    @Override
    default void writeData(ValueOutput output) {
        output.putFloat("capacityBonus", getCapacityBonus());
        output.putFloat("currentInventoryWeight", getCurrentInventoryWeight());
        output.putFloat("maxWeight", getMaxWeight());
        output.putBoolean("overloaded", isOverloaded());
    }
}