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

        // Handle tooltips for backpacks
        if (BackpackWeightCalculator.isBackpack(itemId)) {
            BackpackWeightCalculator.BackpackWeightResult backpackWeight = BackpackWeightCalculator.calculateBackpackWeight(stack);
            handleWeightTooltip(
                    tooltip,
                    backpackWeight.baseWeight,
                    backpackWeight.totalWeight,
                    "inventoryweight.tooltip.weightinside",
                    "inventoryweight.tooltip.weight",
                    Formatting.GRAY,
                    stack.getCount(),
                    stack
            );
            return;
        }

        // Handle tooltips for Shulker Boxes
        if (isShulker(stack)) {
            BlockWeightCalculator.ShulkerBoxWeightResult shulkerWeight = BlockWeightCalculator.calculateShulkerBoxWeight(stack);
            handleWeightTooltip(
                    tooltip,
                    shulkerWeight.baseWeight,
                    shulkerWeight.totalWeight,
                    "inventoryweight.tooltip.weightinside",
                    "inventoryweight.tooltip.weight",
                    Formatting.GRAY,
                    stack.getCount(),
                    stack
            );
            return;
        }

        // Handle custom item weights
        Float customWeight = ItemWeights.getCustomItemWeight(itemId);
        if (customWeight != null) {
            WeightTooltip(tooltip, customWeight, stack.getCount(), stack);
        } else {
            // Handle standard item weights
            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(stack);
            float itemWeight = ItemWeights.getItemWeight(stack);

            WeightTooltip(tooltip, itemWeight, stack.getCount(), stack);
        }

        // Add custom text for Pockets in the armor section
        if (stack.getItem() instanceof ArmorItem || pockets > 0) {
            // Check if advanced tooltips are enabled
            int pocketIndex;
            if (context.isAdvanced()) {
                // Position pockets before NBT/advanced tooltips
                pocketIndex = tooltip.size() - 4;
            } else {
                // Position pockets with other armor attributes
                pocketIndex = tooltip.size() - 2;
            }
            tooltip.add(pocketIndex, Text.translatable("inventoryweight.tooltip.pockets", pockets).formatted(Formatting.BLUE));
        }

    }

    private static boolean isShulker(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }

    // Generalized function to handle weight tooltips for backpacks and shulker boxes
    private static void handleWeightTooltip(List<Text> tooltip, float totalWeight, float baseWeight, String totalWeightKey, String baseWeightKey, Formatting formatting, int stackCount, ItemStack stack) {
        String formattedWeightWithModifier = formatWeight(totalWeight);
        String formattedWeightWithoutModifier = formatWeight(baseWeight);

        String formattedNumericWeightWithModifier = formatNumericWeight(totalWeight);
        String formattedNumericWeightWithoutModifier = formatNumericWeight(baseWeight);

        // Start the index for inserting tooltips after any existing elements in the list
        int index = 1;

        if (Screen.hasShiftDown()) {
            tooltip.add(index++, Text.translatable(baseWeightKey, formattedNumericWeightWithoutModifier).formatted(formatting));
            // Add total weight only if there is more than 1 item in the stack and it is not a backpack or shulker
            if (stackCount > 1 || (BackpackWeightCalculator.isBackpack(ItemWeights.getItemId(stack)) || isShulker(stack))) {
                tooltip.add(index++, Text.translatable(totalWeightKey, formattedNumericWeightWithModifier).formatted(formatting));
            }

        } else {
            tooltip.add(index++, Text.translatable(baseWeightKey, formattedWeightWithoutModifier).formatted(formatting));
            // Add total weight only if there is more than 1 item in the stack and it is not a backpack or shulker
            if (stackCount > 1 || (BackpackWeightCalculator.isBackpack(ItemWeights.getItemId(stack)) || isShulker(stack))) {
                tooltip.add(index++, Text.translatable(totalWeightKey, formattedWeightWithModifier).formatted(formatting));
            }
            // Display hint only if there is more than 1 item in the stack or large weights
            if (stackCount > 1 || (totalWeight > 1000 || baseWeight > 1000)) {
                addTooltipHint(tooltip, index);
            }
        }
    }

    // Tooltip for standard item weights
    private static void WeightTooltip(List<Text> tooltip, Float itemWeight, int stackCount, ItemStack stack) {
        float totalWeight = itemWeight * stackCount;
        // Display the total weight tooltip only if the stack count is greater than 1
        handleWeightTooltip(tooltip, totalWeight, itemWeight, "inventoryweight.tooltip.totalweight", "inventoryweight.tooltip.weight", Formatting.GRAY, stackCount, stack);
    }

    // Add tooltip hint for large weights
    private static void addTooltipHint(List<Text> tooltip, int index) {
        Text tooltipHint = Text.translatable("inventoryweight.tooltip.hint");
        tooltip.add(index, Text.literal(tooltipHint.getString().replace("{0}", Formatting.YELLOW.toString())
                .replace("{1}", Formatting.RESET.toString())).formatted(Formatting.GRAY));
    }

    private static String formatNumericWeight(float weight) {
        return String.format("%,d", (int) weight);
    }

    // Utility method to format weights, adding 'k', 'M', or 'B' for large values
    private static String formatWeight(float weight) {
        String suffixK = Text.translatable("inventoryweight.tooltip.k").getString();
        String suffixM = Text.translatable("inventoryweight.tooltip.m").getString();
        String suffixB = Text.translatable("inventoryweight.tooltip.b").getString();

        if (weight >= 1_000_000_000) {
            return String.format("%.1f" + suffixB, weight / 1_000_000_000);
        } else if (weight >= 1_000_000) {
            return String.format("%.1f" + suffixM, weight / 1_000_000);
        } else if (weight >= 1_000) {
            return String.format("%.1f" + suffixK, weight / 1_000);
        } else {
            return String.valueOf((int) weight);
        }
    }
}
