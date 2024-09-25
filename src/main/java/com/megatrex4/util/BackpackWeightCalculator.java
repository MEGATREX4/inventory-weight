package com.megatrex4.util;

import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static com.megatrex4.util.ItemWeights.getItemWeight;

public class BackpackWeightCalculator {

    public static boolean isBackpack(String itemId) {
        // Check if itemId matches any known backpack type
        String[] knownBackpacks = {
                "Backpack", "Large_Backpack", "Extreme_Backpack",
                "Iron_Armorpack", "Golden_Armorpack", "Netherite_Armorpack",
                "Blockpack", "Orepack", "Enderpack", "Cactuspack",
                "Plantpack", "Magicpack", "Lunchpack", "Toolpack"
        };

        for (String backpackName : knownBackpacks) {
            if (itemId.equalsIgnoreCase(backpackName) || itemId.contains(backpackName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }


    public static BackpackWeightResult calculateBackpackWeight(ItemStack backpackStack) {
        NbtCompound backpackTag = backpackStack.getOrCreateNbt();
        String itemId = ItemWeights.getItemId(backpackStack);
        // Check for `pack_it_up` backpack structure
        if (backpackTag.contains("pack_inventory")) {
            NbtCompound packInventoryTag = backpackTag.getCompound("pack_inventory");
            NbtList itemList = packInventoryTag.getList("stack_contents", 10);
            return PackItUpcalculateWeightFromNbtList(itemList);
        }

        // Check for `inmis` backpack structure
        if (backpackTag.contains("Inventory") && itemId.contains("inmis")) {
            NbtList itemList = backpackTag.getList("Inventory", 10); // Ensure the correct type
            return calculateInmisWeightFromNbtList(itemList);
        }

        // Check for standard structure
        if (backpackTag.contains("Inventory")) {
            NbtList itemList = backpackTag.getList("Inventory", 10); // Ensure the correct type
            return calculateWeightFromNbtList(itemList);
        }

        return new BackpackWeightResult(InventoryWeightUtil.ITEMS, InventoryWeightUtil.ITEMS);
    }

    // Method to calculate weight from `inmis` inventory structure
    private static BackpackWeightResult calculateInmisWeightFromNbtList(NbtList itemList) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);

            // Extract the stack information
            if (itemTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                NbtCompound stackTag = itemTag.getCompound("Stack");
                String itemId = stackTag.getString("id");
                int count = stackTag.getByte("Count"); // Using getByte for Count since it's 1b

                // Create an ItemStack from the extracted data
                ItemConvertible item = Registries.ITEM.get(new Identifier(itemId));
                ItemStack itemStack = new ItemStack(item, count);

                if (stackTag.contains("tag", NbtElement.COMPOUND_TYPE)) {
                    NbtCompound itemNbt = stackTag.getCompound("tag");
                    itemStack.setNbt(itemNbt);
                }

                PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(itemStack);
                String category = categoryInfo.getCategory();

                float itemWeight = getItemWeight(itemStack) * itemStack.getCount();
                totalWeight += (float) Math.max(InventoryWeightUtil.ITEMS, itemWeight * 0.85);
                baseWeight += itemWeight;
            }
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
    }

    // Helper method to calculate weight from `pack_it_up` NBT list
    private static BackpackWeightResult PackItUpcalculateWeightFromNbtList(NbtList itemList) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);

            // Extract relevant data from the NbtCompound
            String itemId = itemTag.getString("id");
            int count = itemTag.getShort("count");

            // Create an ItemStack from the extracted data
            ItemConvertible item = Registries.ITEM.get(new Identifier(itemId));
            ItemStack itemStack = new ItemStack(item, count);

            if (itemTag.contains("tag", NbtElement.COMPOUND_TYPE)) {
                NbtCompound itemNbt = itemTag.getCompound("tag");
                itemStack.setNbt(itemNbt);
            }

            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(itemStack);
            String category = categoryInfo.getCategory();

            float itemWeight = getItemWeight(itemStack) * itemStack.getCount();
            totalWeight += Math.max(InventoryWeightUtil.ITEMS, itemWeight / 400);
            baseWeight += itemWeight;
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
    }

    // Helper method to calculate weight from `travelersbackpack` NBT list
    private static BackpackWeightResult calculateWeightFromNbtList(NbtList itemList) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);

            // Extract the ItemStack properly from the NBT tag
            ItemStack itemStack = ItemStack.fromNbt(itemTag);  // Proper way to create ItemStack from NbtCompound

            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(itemStack);
            String category = categoryInfo.getCategory();

            float itemWeight = getItemWeight(itemStack) * itemStack.getCount();
            totalWeight += Math.max(InventoryWeightUtil.ITEMS, itemWeight / 400);
            baseWeight += itemWeight;
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
    }

    public static class BackpackWeightResult {
        public final float totalWeight;
        public final float baseWeight;

        public BackpackWeightResult(float totalWeight, float baseWeight) {
            this.totalWeight = totalWeight;
            this.baseWeight = baseWeight;
        }
    }
}
