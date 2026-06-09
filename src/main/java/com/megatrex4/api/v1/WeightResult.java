package com.megatrex4.api.v1;

/**
 * Immutable result of a weight calculation.
 *
 * @param weight     effective weight used for inventory/capacity checks
 * @param baseWeight uncompressed/unmodified weight, useful for tooltips
 */
public record WeightResult(float weight, float baseWeight) {
    public static final WeightResult ZERO = new WeightResult(0.0f, 0.0f);

    public static WeightResult of(float weight) {
        return new WeightResult(weight, weight);
    }

    public static WeightResult of(float weight, float baseWeight) {
        return new WeightResult(weight, baseWeight);
    }

    public WeightResult add(WeightResult other) {
        return new WeightResult(weight + other.weight, baseWeight + other.baseWeight);
    }

    public WeightResult multiply(float multiplier) {
        return new WeightResult(weight * multiplier, baseWeight * multiplier);
    }

    public WeightResult sanitized() {
        return new WeightResult(Math.max(0.0f, weight), Math.max(0.0f, baseWeight));
    }

    public boolean hasModifier() {
        return Float.compare(weight, baseWeight) != 0;
    }
}
