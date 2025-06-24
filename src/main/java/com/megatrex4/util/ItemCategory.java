package com.megatrex4.util;

public enum ItemCategory {
    BUCKETS(InventoryWeightUtil.BUCKETS),
    BOTTLES(InventoryWeightUtil.BOTTLES),
    BLOCKS(InventoryWeightUtil.BLOCKS),
    INGOTS(InventoryWeightUtil.INGOTS),
    NUGGETS(InventoryWeightUtil.NUGGETS),
    ITEMS(InventoryWeightUtil.ITEMS),
    CREATIVE(InventoryWeightUtil.CREATIVE);

    private final float baseWeight;

    ItemCategory(float baseWeight) {
        this.baseWeight = baseWeight;
    }

    public float getBaseWeight() {
        return baseWeight;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public static ItemCategory fromName(String name) {
        for (ItemCategory category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return ITEMS;
    }
}

