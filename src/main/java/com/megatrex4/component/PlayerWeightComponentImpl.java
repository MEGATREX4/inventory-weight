package com.megatrex4.component;

import com.megatrex4.impl.InventoryWeightDefaults;
import net.minecraft.entity.player.PlayerEntity;

public final class PlayerWeightComponentImpl implements PlayerWeightComponent {
    @SuppressWarnings("unused")
    private final PlayerEntity player;

    private float capacityBonus = 0.0f;
    private float currentInventoryWeight = 0.0f;
    private float maxWeight = InventoryWeightDefaults.MAX_WEIGHT;
    private boolean overloaded = false;

    public PlayerWeightComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public float getCapacityBonus() {
        return capacityBonus;
    }

    @Override
    public void setCapacityBonus(float capacityBonus) {
        this.capacityBonus = capacityBonus;
    }

    @Override
    public float getCurrentInventoryWeight() {
        return currentInventoryWeight;
    }

    @Override
    public float getMaxWeight() {
        return maxWeight;
    }

    @Override
    public boolean isOverloaded() {
        return overloaded;
    }

    @Override
    public void setWeightState(float currentInventoryWeight, float maxWeight, boolean overloaded) {
        this.currentInventoryWeight = currentInventoryWeight;
        this.maxWeight = maxWeight;
        this.overloaded = overloaded;
    }
}
