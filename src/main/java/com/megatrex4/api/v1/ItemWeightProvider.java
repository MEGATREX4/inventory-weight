package com.megatrex4.api.v1;



import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@FunctionalInterface
public interface ItemWeightProvider {
    /**
     * Return Optional.empty() when this provider does not know how to calculate this stack.
     */
    Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup);
}
