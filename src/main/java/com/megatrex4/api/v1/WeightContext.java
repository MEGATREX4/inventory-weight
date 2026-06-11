package com.megatrex4.api.v1;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record WeightContext(@Nullable Level level, @Nullable Entity holder, int depth) {
    public static WeightContext empty() {
        return new WeightContext(null, null, 0);
    }

    public WeightContext nested() {
        return new WeightContext(level, holder, depth + 1);
    }
}