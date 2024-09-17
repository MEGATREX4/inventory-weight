package com.megatrex4.util;

import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class Tooltips {

    public static void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        // Check if the armor has pockets
        int pockets = InventoryWeightArmor.getPockets(stack);

        // Add weight next to item name (first position in the tooltip)
        String itemId = ItemWeights.getItemId(stack);
        Float customWeight = ItemWeights.getCustomItemWeight(itemId);

        if (customWeight != null) {
            WeightTooltip(tooltip, customWeight, stack.getCount());
        } else {
            // Use getItemCategoryInfo to retrieve both category and item stack
            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(stack);
            String itemCategory = categoryInfo.getCategory();
            Float itemWeight = ItemWeights.getItemWeight(stack);

            if (itemWeight == null || itemWeight == 0) {
                itemWeight = 0f;
            }

            WeightTooltip(tooltip, itemWeight, stack.getCount());
        }

        // Add custom text for Pockets in the armor section
        if (stack.getItem() instanceof ArmorItem || pockets > 0) {
            tooltip.add(Text.translatable("inventoryweight.tooltip.pockets", pockets).formatted(Formatting.BLUE));
        }
    }

    private static void WeightTooltip(List<Text> tooltip, Float itemWeight, int stackCount) {
        // Calculate total weight (item weight multiplied by the stack size)
        float totalWeight = itemWeight * stackCount;

        if (totalWeight != itemWeight) {
            tooltip.add(1, Text.translatable("inventoryweight.tooltip.totalweight", itemWeight, totalWeight)
                    .formatted(Formatting.GRAY));
        } else {
            tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", itemWeight).formatted(Formatting.GRAY));
        }

    }
}
