package com.megatrex4.api.v1;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface PlayerWeightSource {
    /**
     * Adds weight from one player inventory/equipment source.
     * Examples: vanilla inventory, Trinkets slots, custom belts, custom RPG equipment.
     */
    WeightResult getWeight(PlayerEntity player, WeightContext context, WeightLookup lookup);
}
