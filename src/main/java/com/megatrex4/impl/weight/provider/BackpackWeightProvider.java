package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Optional;

public final class BackpackWeightProvider implements ItemWeightProvider {
    private static final String[] KNOWN_BACKPACK_NAMES = {
            "backpack", "large_backpack", "extreme_backpack",
            "iron_armorpack", "golden_armorpack", "netherite_armorpack",
            "blockpack", "orepack", "enderpack", "cactuspack",
            "plantpack", "magicpack", "lunchpack", "toolpack",
            "chest", "barrel", "drawer", "pouch", "satchel", "toolbox"
    };

    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase(Locale.ROOT);
        if (!isBackpack(itemId, stack)) {
            return Optional.empty();
        }

        NbtCompound tag = stack.getNbt();
        if (tag == null) {
            return Optional.of(WeightResult.of(WeightSettings.get().itemWeight()));
        }

        return Optional.of(calculateBackpackWeight(itemId, tag, context, lookup).sanitized());
    }

    public static boolean isTravelerBackpack(ItemStack stack) {
        TagKey<Item> travelerBackpackTag = TagKey.of(
                Registries.ITEM.getKey(),
                new Identifier("travelersbackpack", "custom_travelers_backpack")
        );
        return stack.isIn(travelerBackpackTag);
    }

    private static boolean isBackpack(String itemId, ItemStack stack) {
        if (itemId.contains("chestplate")) {
            return false;
        }
        if (isTravelerBackpack(stack)) {
            return true;
        }
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains("Inventory")) {
            return true;
        }
        for (String knownBackpack : KNOWN_BACKPACK_NAMES) {
            if (itemId.contains(knownBackpack)) {
                return true;
            }
        }
        return false;
    }

    private static WeightResult calculateBackpackWeight(String itemId, NbtCompound tag, WeightContext context, WeightLookup lookup) {
        if (isTravelersBackpackId(itemId)) {
            return calculateTravelersBackpackWeight(tag, context, lookup);
        }
        if (itemId.contains("pouch") || itemId.contains("satchel")) {
            return calculateScoutWeightFromNbtList(tag.getList("Items", NbtElement.COMPOUND_TYPE), context, lookup);
        }
        if (itemId.contains("toolbox")) {
            return calculateToolboxWeight(tag, context, lookup);
        }
        if (itemId.contains("drawer")) {
            NbtCompound blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("items")) {
                return calculateDrawerWeightFromNbtList(blockEntityTag.getList("items", NbtElement.COMPOUND_TYPE), context, lookup);
            }
        }
        if (tag.contains("pack_inventory")) {
            NbtCompound packInventoryTag = tag.getCompound("pack_inventory");
            return calculatePackItUpWeightFromNbtList(packInventoryTag.getList("stack_contents", NbtElement.COMPOUND_TYPE), context, lookup);
        }
        if (tag.contains("Inventory") && itemId.contains("inmis")) {
            return calculateInmisWeightFromNbtList(tag.getList("Inventory", NbtElement.COMPOUND_TYPE), context, lookup);
        }
        if (tag.contains("Inventory")) {
            return calculateStandardWeightFromNbtList(tag.getList("Inventory", NbtElement.COMPOUND_TYPE), context, lookup);
        }
        if (tag.contains("BlockEntityTag") && itemId.contains("sophisticatedstorage")) {
            NbtCompound blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("storageWrapper")) {
                NbtCompound storageWrapperTag = blockEntityTag.getCompound("storageWrapper");
                NbtCompound contentsTag = storageWrapperTag.getCompound("contents");
                NbtList itemList = contentsTag.getCompound("inventory").getList("Items", NbtElement.COMPOUND_TYPE);
                return calculateStandardWeightFromNbtList(itemList, context, lookup);
            }
        }
        if (tag.contains("BlockEntityTag")) {
            return calculateStandardWeightFromNbtList(tag.getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE), context, lookup);
        }
        return WeightResult.of(WeightSettings.get().itemWeight());
    }

    private static boolean isTravelersBackpackId(String itemId) {
        return itemId.contains("travelersbackpack") || itemId.contains("travellersbackpack");
    }

    private static WeightResult calculateTravelersBackpackWeight(NbtCompound tag, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        if (tag.contains("Inventory")) {
            NbtList inventoryItems = tag.getCompound("Inventory").getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < inventoryItems.size(); i++) {
                accumulator.addCompressed(stackFromStandardNbt(inventoryItems.getCompound(i)), context, lookup);
            }
        }

        if (tag.contains("LeftTank")) {
            accumulator.addEffective(tag.getCompound("LeftTank").getInt("amount") / 1000.0f);
        }
        if (tag.contains("RightTank")) {
            accumulator.addEffective(tag.getCompound("RightTank").getInt("amount") / 1000.0f);
        }

        return accumulator.result();
    }

    private static WeightResult calculateScoutWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            if (itemTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                accumulator.addCompressed(stackFromStandardNbt(itemTag.getCompound("Stack")), context, lookup);
            }
        }
        return accumulator.result();
    }

    private static WeightResult calculateToolboxWeight(NbtCompound tag, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        if (tag.contains("Inventory")) {
            NbtCompound inventoryTag = tag.getCompound("Inventory");
            NbtList itemList = inventoryTag.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < itemList.size(); i++) {
                accumulator.addCompressed(stackFromStandardNbt(itemList.getCompound(i)), context, lookup);
            }
        }
        return accumulator.result();
    }

    private static WeightResult calculateDrawerWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound drawerTag = itemList.getCompound(i);
            int amount = drawerTag.getInt("amount");
            NbtCompound itemTag = drawerTag.getCompound("item");
            String itemId = itemTag.getString("item");
            ItemConvertible item = Registries.ITEM.get(new Identifier(itemId));
            ItemStack stack = new ItemStack(item, amount);
            if (itemTag.contains("tag", NbtElement.COMPOUND_TYPE)) {
                stack.setNbt(itemTag.getCompound("tag"));
            }
            accumulator.addCompressed(stack, context, lookup);
        }
        return accumulator.result();
    }

    private static WeightResult calculateInmisWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            if (itemTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                accumulator.addCompressed(stackFromStandardNbt(itemTag.getCompound("Stack")), context, lookup);
            }
        }
        return accumulator.result();
    }

    private static WeightResult calculatePackItUpWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            String itemId = itemTag.getString("id");
            int count = itemTag.contains("count") ? itemTag.getShort("count") : itemTag.getInt("Count");
            ItemConvertible item = Registries.ITEM.get(new Identifier(itemId));
            ItemStack stack = new ItemStack(item, count);
            if (itemTag.contains("tag", NbtElement.COMPOUND_TYPE)) {
                stack.setNbt(itemTag.getCompound("tag"));
            }
            accumulator.addCompressed(stack, context, lookup);
        }
        return accumulator.result();
    }

    private static WeightResult calculateStandardWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            accumulator.addCompressed(ItemStack.fromNbt(itemList.getCompound(i)), context, lookup);
        }
        return accumulator.result();
    }

    private static ItemStack stackFromStandardNbt(NbtCompound tag) {
        if (tag.contains("id")) {
            return ItemStack.fromNbt(tag);
        }

        String itemId = tag.getString("id");
        int count = tag.contains("Count") ? tag.getInt("Count") : 1;
        if (itemId.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemConvertible item = Registries.ITEM.get(new Identifier(itemId));
        ItemStack stack = new ItemStack(item, count);
        if (tag.contains("tag", NbtElement.COMPOUND_TYPE)) {
            stack.setNbt(tag.getCompound("tag"));
        }
        return stack;
    }

    private static final class BackpackAccumulator {
        private float effective;
        private float base;

        private BackpackAccumulator(float baseWeight) {
            this.effective = baseWeight;
            this.base = baseWeight;
        }

        static BackpackAccumulator create() {
            return new BackpackAccumulator(WeightSettings.get().itemWeight());
        }

        void addEffective(float value) {
            effective += Math.max(0.0f, value);
        }

        void addCompressed(ItemStack stack, WeightContext context, WeightLookup lookup) {
            if (stack == null || stack.isEmpty()) {
                return;
            }
            WeightResult itemWeight = lookup.getWeight(stack, context.nested()).multiply(stack.getCount());
            float minimum = WeightSettings.get().itemWeight();
            effective += Math.max(minimum, itemWeight.weight() / 400.0f);
            base += itemWeight.weight();
        }

        WeightResult result() {
            return WeightResult.of(effective, base);
        }
    }
}
