package com.megatrex4.util;

import com.megatrex4.data.PlayerDataHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static com.megatrex4.util.ItemWeights.getItemWeight;

public class BackpackWeightCalculator {

    public static boolean isTravelerBackpack(ItemStack itemStack) {
        // Define the custom tag as a TagKey<Item>
        TagKey<Item> travelerBackpackTag = TagKey.of(Registries.ITEM.getKey(), new Identifier("travelersbackpack", "custom_travelers_backpack"));

        // Check if the item has the tag
        return itemStack.isIn(travelerBackpackTag);
    }

    public static boolean isBackpack(String itemId, ItemStack backpackStack) {
        if (!backpackStack.hasNbt()) { // Ensure the backpack has NBT before retrieving
            return false;
        }

        NbtCompound backpackTag = backpackStack.getNbt(); // Use getNbt instead of getOrCreateNbt

        if (backpackTag != null && backpackTag.contains("Inventory")) {
            return true;
        }

        if (itemId.contains("chestplate")) {
            return false;
        }

        if (isTravelerBackpack(backpackStack)){
            return true;
        }

        String[] knownBackpacks = {
                "Backpack", "Large_Backpack", "Extreme_Backpack",
                "Iron_Armorpack", "Golden_Armorpack", "Netherite_Armorpack",
                "Blockpack", "Orepack", "Enderpack", "Cactuspack",
                "Plantpack", "Magicpack", "Lunchpack", "Toolpack",
                "Chest", "barrel", "drawer", "pouch", "satchel"
        };

        // Add toolbox check
        if (itemId.contains("toolbox")) {
            return true;
        }

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

        boolean isPounch = itemId.contains("pouch") || itemId.contains("satchel");

        if (isTravelerBackpack(backpackStack)) {
            return calculateTravelersBackpackWeight(backpackTag);
        }

        if (isPounch) {
            NbtList itemList = backpackTag.getList("Items", NbtElement.COMPOUND_TYPE);
            return calculateScoutWeightFromNbtList(itemList);
        }

        if (itemId.contains("toolbox")) {
            return calculateToolboxWeight(backpackTag);
        }

        if (itemId.contains("drawer")) {
            NbtCompound blockEntityTag = backpackTag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("items")) {
                NbtList itemList = blockEntityTag.getList("items", NbtElement.COMPOUND_TYPE);
                return calculateDrawerWeightFromNbtList(itemList);
            }
        }

        // Existing structure checks remain unchanged
        if (backpackTag.contains("pack_inventory")) {
            NbtCompound packInventoryTag = backpackTag.getCompound("pack_inventory");
            NbtList itemList = packInventoryTag.getList("stack_contents", 10);
            return PackItUpcalculateWeightFromNbtList(itemList);
        }

        if (backpackTag.contains("Inventory") && itemId.contains("inmis")) {
            NbtList itemList = backpackTag.getList("Inventory", 10);
            return calculateInmisWeightFromNbtList(itemList);
        }

        if (backpackTag.contains("Inventory")) {
            NbtList itemList = backpackTag.getList("Inventory", 10);
            return calculateWeightFromNbtList(itemList);
        }


        // added to support backpacks from the "sophisticated storage" mod
        // but this works only if it is an item with an NBT+ modifier,
        // if you take it simply, it does not store data about the item inside it,
        // it works only if we get the repository UUID,
        // and then only pass the values through the repository
        // UUID to the code, deeper compatibility is needed

        if (backpackTag.contains("BlockEntityTag") && itemId.contains("sophisticatedstorage")) {
            NbtCompound blockEntityTag = backpackTag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("storageWrapper")) {
                NbtCompound storageWrapperTag = blockEntityTag.getCompound("storageWrapper");
                NbtCompound contentsTag = storageWrapperTag.getCompound("contents");
                NbtList itemList = contentsTag.getCompound("inventory").getList("Items", 10);
                return calculateWeightFromNbtList(itemList);
            }
        }

        if (backpackTag.contains("BlockEntityTag")) {
            NbtList itemList = backpackTag.getCompound("BlockEntityTag").getList("Items", 10);
            return calculateWeightFromNbtList(itemList);
        }

        return new BackpackWeightResult(InventoryWeightUtil.ITEMS, InventoryWeightUtil.ITEMS);
    }


    // New method to calculate weight for Traveler's Backpack mod
    private static BackpackWeightResult calculateTravelersBackpackWeight(NbtCompound backpackTag) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        if (backpackTag.contains("Inventory")) {
            NbtList inventoryItems = backpackTag.getCompound("Inventory").getList("Items", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < inventoryItems.size(); i++) {
                NbtCompound itemTag = inventoryItems.getCompound(i);
                String itemId = itemTag.getString("id");
                int count = itemTag.getInt("Count");

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
        }

        // Process other backpack specific data like fluid tanks, tools
        if (backpackTag.contains("LeftTank") && backpackTag.contains("RightTank")) {
            float leftTankWeight = backpackTag.getCompound("LeftTank").getInt("amount") / 1000f; // Adjust the scale
            float rightTankWeight = backpackTag.getCompound("RightTank").getInt("amount") / 1000f;
            totalWeight += leftTankWeight + rightTankWeight;
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
    }


    private static BackpackWeightResult calculateScoutWeightFromNbtList(NbtList itemList) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);

            if (itemTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                NbtCompound stackTag = itemTag.getCompound("Stack");
                String itemId = stackTag.getString("id");
                int count = stackTag.getInt("Count");

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
                totalWeight += Math.max(InventoryWeightUtil.ITEMS, itemWeight / 400);
                baseWeight += itemWeight;
            }
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
    }


    // New method to calculate weight for the toolbox
    private static BackpackWeightResult calculateToolboxWeight(NbtCompound backpackTag) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        if (backpackTag.contains("Inventory")) {
            NbtCompound inventoryTag = backpackTag.getCompound("Inventory");
            NbtList itemList = inventoryTag.getList("Items", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < itemList.size(); i++) {
                NbtCompound itemTag = itemList.getCompound(i);
                String itemId = itemTag.getString("id");
                int count = itemTag.getInt("Count"); // Adjust according to your item's NBT structure

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
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
    }


    // New method to calculate weight from Drawer NBT structure
    private static BackpackWeightResult calculateDrawerWeightFromNbtList(NbtList itemList) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound drawerTag = itemList.getCompound(i);

            // Extract the amount and item information
            int amount = drawerTag.getInt("amount"); // Get the amount of the item
            NbtCompound itemTag = drawerTag.getCompound("item");
            String itemId = itemTag.getString("item");

            // Create an ItemStack from the extracted data
            ItemConvertible item = Registries.ITEM.get(new Identifier(itemId));
            ItemStack itemStack = new ItemStack(item, amount);

            if (itemTag.contains("tag", NbtElement.COMPOUND_TYPE)) {
                NbtCompound itemNbt = itemTag.getCompound("tag");
                itemStack.setNbt(itemNbt);
            }

            PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(itemStack);
            String category = categoryInfo.getCategory();

            float itemWeight = getItemWeight(itemStack) * itemStack.getCount();
            totalWeight += Math.max(InventoryWeightUtil.ITEMS, itemWeight / 400);
            baseWeight += itemWeight; // Assuming base weight calculation remains the same
        }

        return new BackpackWeightResult(totalWeight, baseWeight);
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
                totalWeight += Math.max(InventoryWeightUtil.ITEMS, itemWeight / 400);
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

    private static BackpackWeightResult calculateWeightFromNbtList(NbtList itemList) {
        float totalWeight = InventoryWeightUtil.ITEMS;
        float baseWeight = InventoryWeightUtil.ITEMS;

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);

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
