package com.megatrex4.api.v1;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Context passed through all weight calculations.
 * depth is used to stop infinite recursion when containers contain containers.
 */
public record WeightContext(@Nullable World world, @Nullable Entity holder, int depth) {
    public static WeightContext empty() {
        return new WeightContext(null, null, 0);
    }

    public WeightContext nested() {
        return new WeightContext(world, holder, depth + 1);
    }
}
