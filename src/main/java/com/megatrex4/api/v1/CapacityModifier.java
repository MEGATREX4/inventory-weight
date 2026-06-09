package com.megatrex4.api.v1;

/**
 * Capacity modifiers are combined as: (base + totalAdditive) * totalMultiplier.
 */
public record CapacityModifier(float additive, float multiplier) {
    public static CapacityModifier none() {
        return new CapacityModifier(0.0f, 1.0f);
    }

    public static CapacityModifier additive(float value) {
        return new CapacityModifier(value, 1.0f);
    }

    public static CapacityModifier multiplier(float value) {
        return new CapacityModifier(0.0f, value);
    }

    public CapacityModifier sanitized() {
        return new CapacityModifier(additive, Math.max(0.0f, multiplier));
    }
}
