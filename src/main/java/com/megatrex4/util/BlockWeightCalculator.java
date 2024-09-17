package com.megatrex4.util;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import static com.megatrex4.util.Rarity.getRarityWeight;

public class BlockWeightCalculator {
    public static float calculateBlockWeight(ItemStack stack, String category) {
        // Check if the item is a BlockItem
        if (stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) stack.getItem()).getBlock();

            float hardness = block.getHardness();
            float blastResistance = block.getBlastResistance();
            boolean isTransparent = !block.getDefaultState().isOpaque();

            float weight;
            if ("creative".equalsIgnoreCase(category)) {
                weight = InventoryWeightUtil.CREATIVE;
            } else {
                weight = InventoryWeightUtil.BLOCKS;
            }

            // Add Hardness and Blast Resistance values
            weight += (hardness * 10);
            weight += Math.min((blastResistance * 50), 10000);

            // Subtract a value if the block is transparent
            if (isTransparent) {
                weight -= 1000;
            }

            weight *= (getRarityWeight(stack) * 1.3f);

            return (int) Math.floor(Math.max(weight, InventoryWeightUtil.ITEMS));
        }
        // If not a block, return a default weight
        return InventoryWeightUtil.ITEMS;
    }
}
