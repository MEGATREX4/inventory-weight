package com.megatrex4.util;

import net.minecraft.item.Item;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItem;
import net.minecraft.block.Block;

/**
 * Utility class for checking item types and categories.
 * Consolidates repeated type-checking logic throughout the codebase.
 */
public class ItemTypeChecker {

    /**
     * Checks if an item is a tool
     */
    public static boolean isTool(Item item) {
        return item instanceof ToolItem;
    }

    /**
     * Checks if an item is armor
     */
    public static boolean isArmor(Item item) {
        return item instanceof ArmorItem;
    }

    /**
     * Checks if an item is a block item
     */
    public static boolean isBlockItem(Item item) {
        return item instanceof BlockItem;
    }

    /**
     * Gets the armor protection value
     */
    public static int getArmorProtection(Item item) {
        if (item instanceof ArmorItem) {
            return ((ArmorItem) item).getProtection();
        }
        return 0;
    }

    /**
     * Gets the block from a block item stack
     */
    public static Block getBlockFromStack(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }
        return null;
    }

    /**
     * Checks if item stack contains a valid block
     */
    public static boolean isValidBlockStack(ItemStack stack) {
        return getBlockFromStack(stack) != null;
    }

    /**
     * Check if item is a food item
     */
    public static boolean isFood(Item item) {
        return item.isFood();
    }

    /**
     * Check if item is fireproof
     */
    public static boolean isFireproof(Item item) {
        return item.isFireproof();
    }

    /**
     * Gets the max durability of an item
     */
    public static int getMaxDurability(ItemStack stack) {
        return stack.getMaxDamage();
    }

    /**
     * Gets the max stack size of an item
     */
    public static int getMaxStackSize(Item item) {
        return item.getMaxCount();
    }

    /**
     * Checks if an item can stack
     */
    public static boolean canStack(Item item) {
        return getMaxStackSize(item) > 1;
    }

    /**
     * Checks if an item is durable (has durability)
     */
    public static boolean isDurable(ItemStack stack) {
        return getMaxDurability(stack) > 0;
    }
}
