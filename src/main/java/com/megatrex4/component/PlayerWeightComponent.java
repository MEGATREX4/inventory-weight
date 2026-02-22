package com.megatrex4.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public interface PlayerWeightComponent extends AutoSyncedComponent {
    /**
     * Get the player's weight multiplier (additional max weight)
     */
    float getWeightMultiplier();

    /**
     * Set the player's weight multiplier
     */
    void setWeightMultiplier(float multiplier);

    /**
     * Get the player's custom max weight
     */
    float getMaxWeight();

    /**
     * Set the player's custom max weight
     */
    void setMaxWeight(float maxWeight);

    /**
     * Get the current inventory weight of the player
     */
    float getCurrentInventoryWeight();

    /**
     * Set the current inventory weight
     */
    void setCurrentInventoryWeight(float weight);

    /**
     * Check if the player is overloaded
     */
    boolean isOverloaded();

    /**
     * Set the overloaded status
     */
    void setOverloaded(boolean overloaded);

    /**
     * Get the pocket weight of the player
     */
    float getPocketWeight();

    /**
     * Set the pocket weight
     */
    void setPocketWeight(float weight);

    @Override
    default void readFromNbt(NbtCompound tag) {
        if (tag.contains("weightMultiplier")) {
            setWeightMultiplier(tag.getFloat("weightMultiplier"));
        }
        if (tag.contains("maxWeight")) {
            setMaxWeight(tag.getFloat("maxWeight"));
        }
        if (tag.contains("currentInventoryWeight")) {
            setCurrentInventoryWeight(tag.getFloat("currentInventoryWeight"));
        }
        if (tag.contains("isOverloaded")) {
            setOverloaded(tag.getBoolean("isOverloaded"));
        }
        if (tag.contains("pocketWeight")) {
            setPocketWeight(tag.getFloat("pocketWeight"));
        }
    }

    @Override
    default void writeToNbt(NbtCompound tag) {
        tag.putFloat("weightMultiplier", getWeightMultiplier());
        tag.putFloat("maxWeight", getMaxWeight());
        tag.putFloat("currentInventoryWeight", getCurrentInventoryWeight());
        tag.putBoolean("isOverloaded", isOverloaded());
        tag.putFloat("pocketWeight", getPocketWeight());
    }
}
