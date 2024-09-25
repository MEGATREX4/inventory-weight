package com.megatrex4.util;

import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import static com.megatrex4.util.ItemWeights.getItemWeight;

public class ShulkerWeightCalculator {

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
