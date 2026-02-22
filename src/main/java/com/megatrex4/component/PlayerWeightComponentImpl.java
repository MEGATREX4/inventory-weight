package com.megatrex4.component;

import com.megatrex4.util.InventoryWeightUtil;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerWeightComponentImpl implements PlayerWeightComponent {
    private float weightMultiplier = 0.0f;
    private float maxWeight = InventoryWeightUtil.MAXWEIGHT;
    private float currentInventoryWeight = 0.0f;
    private boolean isOverloaded = false;
    private float pocketWeight = 0.0f;
    
    private final PlayerEntity player;

    public PlayerWeightComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public float getWeightMultiplier() {
        return weightMultiplier;
    }

    @Override
    public void setWeightMultiplier(float multiplier) {
        this.weightMultiplier = multiplier;
    }

    @Override
    public float getMaxWeight() {
        return maxWeight;
    }

    @Override
    public void setMaxWeight(float maxWeight) {
        this.maxWeight = maxWeight;
    }

    @Override
    public float getCurrentInventoryWeight() {
        return currentInventoryWeight;
    }

    @Override
    public void setCurrentInventoryWeight(float weight) {
        this.currentInventoryWeight = weight;
    }

    @Override
    public boolean isOverloaded() {
        return isOverloaded;
    }

    @Override
    public void setOverloaded(boolean overloaded) {
        this.isOverloaded = overloaded;
    }

    @Override
    public float getPocketWeight() {
        return pocketWeight;
    }

    @Override
    public void setPocketWeight(float weight) {
        this.pocketWeight = weight;
    }
}
