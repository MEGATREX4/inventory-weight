package com.megatrex4.util;

import net.minecraft.item.ItemStack;

public class Rarity {

    static float getRarityWeight(ItemStack stack) {
        net.minecraft.util.Rarity rarity = stack.getRarity();
        return switch (rarity) {
            case UNCOMMON -> 1.5f;
            case RARE -> 2.0f;
            case EPIC -> 3.0f;
            default -> 1.0f;
        };
    }

}
