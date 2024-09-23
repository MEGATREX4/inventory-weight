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

            if (block instanceof ShulkerBoxBlock) {
                return BlockWeightCalculator.calculateShulkerBoxWeight(stack).totalWeight;
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

    // Updated method to return two values (with and without the modifier)
    public static ShulkerBoxWeightResult calculateShulkerBoxWeight(ItemStack shulkerBoxStack) {
        if (!(shulkerBoxStack.getItem() instanceof BlockItem) ||
                !(((BlockItem) shulkerBoxStack.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
            return new ShulkerBoxWeightResult(InventoryWeightUtil.ITEMS, InventoryWeightUtil.ITEMS);
        }

        NbtCompound shulkerBoxTag = shulkerBoxStack.getOrCreateNbt();
        if (!shulkerBoxTag.contains("BlockEntityTag")) {
            return new ShulkerBoxWeightResult(InventoryWeightUtil.ITEMS, InventoryWeightUtil.ITEMS);
        }

        NbtCompound blockEntityTag = shulkerBoxTag.getCompound("BlockEntityTag");
        NbtList itemList = blockEntityTag.getList("Items", 10);

        float totalWeight = InventoryWeightUtil.ITEMS; // With modifier
        float baseWeight = InventoryWeightUtil.ITEMS;  // Without modifier

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);

            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(itemStack);
            String category = categoryInfo.getCategory();

            float itemWeight = getItemWeight(itemStack) * itemStack.getCount();

            totalWeight += itemWeight / 1000; // With modifier
            baseWeight += itemWeight; // Without modifier
        }

        return new ShulkerBoxWeightResult(totalWeight, baseWeight);
    }

    // Custom class to hold both total and base weight values
    public static class ShulkerBoxWeightResult {
        public final float totalWeight;
        public final float baseWeight;

        public ShulkerBoxWeightResult(float totalWeight, float baseWeight) {
            this.totalWeight = totalWeight;
            this.baseWeight = baseWeight;
        }
    }

}
