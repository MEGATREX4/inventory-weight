package com.megatrex4.util;

import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import static com.megatrex4.util.ItemWeights.getItemWeight;
import static com.megatrex4.util.Rarity.getRarityWeight;

public class BlockWeightCalculator {
    public static float calculateBlockWeight(ItemStack stack, String category) {

        if (stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) stack.getItem()).getBlock();

            String itemId = ItemWeights.getItemId(stack);
            if (BackpackWeightCalculator.isBackpack(itemId)){
                return BackpackWeightCalculator.calculateBackpackWeight(stack).totalWeight;
            }

            if (block instanceof ShulkerBoxBlock) {
                return ShulkerWeightCalculator.calculateShulkerBoxWeight(stack).totalWeight;
            }

            float hardness = block.getHardness();
            float blastResistance = block.getBlastResistance();
            boolean isTransparent = !block.getDefaultState().isOpaque();

            float weight;
            if ("creative".equalsIgnoreCase(category)) {
                weight = InventoryWeightUtil.CREATIVE;
            } else {
                weight = InventoryWeightUtil.BLOCKS;
            }

            weight += (hardness * 10);
            weight += Math.min((blastResistance * 50), 10000);

            if (isTransparent) {
                weight /= 5;
            }

            if (block instanceof BlockWithEntity) {
                weight -= 200;
            }

            weight *= (getRarityWeight(stack) * 1.3f);

            // Check if the block is a slab
            if (block instanceof SlabBlock) {
                weight /= 2;
            }
            //if block is stairs
            if (block instanceof StairsBlock) {
                weight *= 0.75f;
            }


            return (int) Math.floor(Math.max(weight, InventoryWeightUtil.ITEMS));
        }
        return InventoryWeightUtil.ITEMS;
    }





}
