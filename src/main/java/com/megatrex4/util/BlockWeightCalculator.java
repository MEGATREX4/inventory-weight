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
    public static float calculateBlockWeight(ItemStack stack, ItemCategory category) {

        if (stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) stack.getItem()).getBlock();

            String itemId = ItemWeights.getItemId(stack);
            if (BackpackWeightCalculator.isBackpack(itemId, stack) || BackpackWeightCalculator.isTravelerBackpack(stack)) {
                return BackpackWeightCalculator.calculateBackpackWeight(stack).totalWeight;
            }

            if (ItemWeights.getItemId(stack).contains("shulker_box")) {
                return BlockWeightCalculator.calculateShulkerBoxWeight(stack).totalWeight();
            }

            float hardness = block.getHardness();
            float blastResistance = block.getBlastResistance();
            boolean isTransparent = !block.getDefaultState().isOpaque();
            float weight = category == ItemCategory.CREATIVE ?
                    InventoryWeightUtil.CREATIVE : InventoryWeightUtil.BLOCKS;

            weight += WeightModifierCalculator.calculateHardnessWeight(hardness);
            weight += WeightModifierCalculator.calculateBlastResistanceWeight(blastResistance);

            weight *= WeightModifierCalculator.calculateTransparencyModifier(isTransparent);

            if (block instanceof BlockWithEntity) {
                weight += 50;
            }

            weight *= (getRarityWeight(stack) * 1.3f);

            // Check if the block is a slab
            if (block instanceof SlabBlock) {
                weight *= WeightModifierCalculator.calculateSlabModifier();
            }
            //if block is stairs
            if (block instanceof StairsBlock) {
                weight *= WeightModifierCalculator.calculateStairsModifier();
            }

            return (int) Math.floor(Math.max(weight, 1.0f));
        }
        return InventoryWeightUtil.ITEMS;
    }

    /**
     * Calculate shulker box weight with both total and base weight
     */
    public static WeightCalculationResult calculateShulkerBoxWeight(ItemStack shulkerBoxStack) {
        if (!ItemTypeChecker.isValidBlockStack(shulkerBoxStack) ||
                !(ItemTypeChecker.getBlockFromStack(shulkerBoxStack) instanceof ShulkerBoxBlock)) {
            return WeightCalculationResult.ofBase(InventoryWeightUtil.ITEMS);
        }
        NbtCompound shulkerBoxTag = shulkerBoxStack.getOrCreateNbt();
        if (!shulkerBoxTag.contains("BlockEntityTag")) {
            return WeightCalculationResult.ofBase(InventoryWeightUtil.ITEMS);
        }
        NbtCompound blockEntityTag = shulkerBoxTag.getCompound("BlockEntityTag");
        NbtList itemList = blockEntityTag.getList("Items", 10);
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);
            float itemWeight = getItemWeight(itemStack) * itemStack.getCount();
            totalWeight += itemWeight;
            baseWeight += itemWeight;
        }
        return WeightCalculationResult.of(totalWeight, baseWeight);
    }
}