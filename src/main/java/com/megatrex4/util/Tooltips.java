package com.megatrex4.util;

import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class Tooltips {

    public static void appendTooltip(ItemStack stack, List<Text> tooltip, TooltipContext context) {
        // Check if the armor has pockets
        int pockets = InventoryWeightArmor.getPockets(stack);

        String itemId = ItemWeights.getItemId(stack);
        if(BackpackWeightCalculator.isBackpack(itemId)){
            BackpackWeightCalculator.BackpackWeightResult backpackWeight = BackpackWeightCalculator.calculateBackpackWeight(stack);
            String formattedWeightWithModifier = formatWeight(backpackWeight.totalWeight);
            String formattedWeightWithoutModifier = formatWeight(backpackWeight.baseWeight);
            if (Screen.hasShiftDown()) {
                tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", (int) backpackWeight.totalWeight)
                        .formatted(Formatting.GRAY));
                tooltip.add(2, Text.translatable("inventoryweight.tooltip.weightinside", (int) backpackWeight.baseWeight)
                        .formatted(Formatting.GRAY));
            } else {
                tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", formattedWeightWithModifier)
                        .formatted(Formatting.GRAY));
                tooltip.add(2, Text.translatable("inventoryweight.tooltip.weightinside", formattedWeightWithoutModifier)
                        .formatted(Formatting.GRAY));
                if (backpackWeight.totalWeight > 1000 || backpackWeight.baseWeight > 1000) {
                    Text tooltipHint = Text.translatable("inventoryweight.tooltip.hint");
                    tooltip.add(3, tooltipHint);
                }
            }
        }

        // Handle tooltips for Shulker Boxes
        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
            BlockWeightCalculator.ShulkerBoxWeightResult shulkerWeight = BlockWeightCalculator.calculateShulkerBoxWeight(stack);
            String formattedWeightWithModifier = formatWeight(shulkerWeight.totalWeight);
            String formattedWeightWithoutModifier = formatWeight(shulkerWeight.baseWeight);

            if (Screen.hasShiftDown()) {
                tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", (int) shulkerWeight.totalWeight)
                        .formatted(Formatting.GRAY));

                tooltip.add(2, Text.translatable("inventoryweight.tooltip.weightinside", (int) shulkerWeight.baseWeight)
                        .formatted(Formatting.GRAY));
            } else {
                tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", formattedWeightWithModifier)
                        .formatted(Formatting.GRAY));

                tooltip.add(2, Text.translatable("inventoryweight.tooltip.weightinside", formattedWeightWithoutModifier)
                        .formatted(Formatting.GRAY));

                if (shulkerWeight.totalWeight > 1000 || shulkerWeight.baseWeight > 1000) {
                    Text tooltipHint = Text.translatable("inventoryweight.tooltip.hint");

                    // Replace the placeholders with formatted text
                    tooltip.add(3, Text.literal(tooltipHint.getString().replace("{0}", Formatting.YELLOW.toString())
                                    .replace("{1}", Formatting.RESET.toString()))
                            .formatted(Formatting.GRAY)); // Format the outer text as yellow
                }
            }
            return;
        }

        Float customWeight = ItemWeights.getCustomItemWeight(itemId);

        if (customWeight != null) {
            WeightTooltip(tooltip, customWeight, stack.getCount());
        } else {
            // Use getItemCategoryInfo to retrieve both category and item stack
            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(stack);
            String itemCategory = categoryInfo.getCategory();
            float itemWeight = ItemWeights.getItemWeight(stack);

            if (itemWeight == 0) {
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

        // Format both weights for display using 'k' notation if applicable
        String formattedItemWeight = formatWeight(itemWeight);
        String formattedTotalWeight = formatWeight(totalWeight);

        if (Screen.hasShiftDown()) {
            tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", Math.round(itemWeight))
                    .formatted(Formatting.GRAY));

            tooltip.add(2, Text.translatable("inventoryweight.tooltip.totalweight", (int) totalWeight)
                    .formatted(Formatting.GRAY));
        } else {
            tooltip.add(1, Text.translatable("inventoryweight.tooltip.weight", formattedItemWeight)
                    .formatted(Formatting.GRAY));

            tooltip.add(2, Text.translatable("inventoryweight.tooltip.totalweight", formattedTotalWeight)
                    .formatted(Formatting.GRAY));

            // Retrieve the translated string with placeholders
            if (itemWeight > 1000 || totalWeight > 1000) {
                // Retrieve the translated string with placeholders
                Text tooltipHint = Text.translatable("inventoryweight.tooltip.hint");

                // Replace the placeholders with formatted text
                tooltip.add(3, Text.literal(tooltipHint.getString().replace("{0}", Formatting.YELLOW.toString())
                                .replace("{1}", Formatting.RESET.toString()))
                        .formatted(Formatting.GRAY)); // Format the outer text as yellow
            }
        }

    }

    // Utility method to format weights, adding 'k' for values over 1000
    private static String formatWeight(float weight) {
        String suffixK = Text.translatable("inventoryweight.tooltip.k").getString();
        String suffixM = Text.translatable("inventoryweight.tooltip.m").getString();
        String suffixB = Text.translatable("inventoryweight.tooltip.b").getString();

        if (weight >= 1_000_000_000) {
            return String.format("%.1f" + suffixB, weight / 1_000_000_000); // e.g., 1,860,000,000 becomes 1.8B
        } else if (weight >= 1_000_000) {
            return String.format("%.1f" + suffixM, weight / 1_000_000); // e.g., 1,860,000 becomes 1.8M
        } else if (weight >= 1_000) {
            return String.format("%.1f" + suffixK, weight / 1_000); // e.g., 1,860 becomes 1.8K
        } else {
            return String.valueOf((int) weight); // Show as an integer if below 1000
        }
    }

}
