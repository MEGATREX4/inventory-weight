package com.megatrex4.util;

import java.util.Map;

/**
 * Record representing a single NBT-based weight configuration entry.
 * Used to standardize NBT weight definitions across the system.
 *
 * @param itemId The identifier of the item
 * @param nbtKey The NBT key to check
 * @param nbtValueWeights Map of NBT value to weight
 */
public record NbtWeightDefinition(String itemId, String nbtKey, Map<String, Float> nbtValueWeights) {

    /**
     * Gets the weight for a specific NBT value
     */
    public Float getWeightForValue(String nbtValue) {
        return nbtValueWeights.get(nbtValue);
    }

    /**
     * Checks if this definition has a weight for the given NBT value
     */
    public boolean hasWeight(String nbtValue) {
        return nbtValueWeights.containsKey(nbtValue);
    }

    /**
     * Gets all NBT values defined in this configuration
     */
    public java.util.Set<String> getDefinedValues() {
        return nbtValueWeights.keySet();
    }
}
