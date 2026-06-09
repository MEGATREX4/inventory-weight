package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.NbtItemStackReader;
import net.minecraft.item.Item;
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
    private static final float CONTENT_WEIGHT_DIVISOR = 2.0f;

    private static final String[] KNOWN_BACKPACK_NAMES = {
            "backpack", "large_backpack", "extreme_backpack",
            "iron_armorpack", "golden_armorpack", "netherite_armorpack",
            "blockpack", "orepack", "enderpack", "cactuspack",
            "plantpack", "magicpack", "lunchpack", "toolpack",
            "chest", "barrel", "drawer", "pouch", "satchel", "toolbox"
    };

    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        if (!isBackpackStack(stack)) {
            return Optional.empty();
        }

        NbtCompound tag = stack.getNbt();
        if (tag == null) {
            return Optional.of(WeightResult.of(WeightSettings.get().itemWeight(), 0.0f));
        }

        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase(Locale.ROOT);
        return Optional.of(calculateBackpackWeight(itemId, tag, context, lookup).sanitized());
    }

    public static boolean isBackpackStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        String itemId = Registries.ITEM.getId(stack.getItem()).toString().toLowerCase(Locale.ROOT);
        return isBackpack(itemId, stack);
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
        return WeightResult.of(WeightSettings.get().itemWeight(), 0.0f);
    }

    private static boolean isTravelersBackpackId(String itemId) {
        return itemId.contains("travelersbackpack") || itemId.contains("travellersbackpack");
    }

    private static WeightResult calculateTravelersBackpackWeight(NbtCompound tag, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        if (tag.contains("Inventory")) {
            NbtList inventoryItems = tag.getCompound("Inventory").getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < inventoryItems.size(); i++) {
                accumulator.addInsideStack(stackFromStandardNbt(inventoryItems.getCompound(i)), context, lookup);
            }
        }

        if (tag.contains("LeftTank")) {
            accumulator.addInsideWeight(tag.getCompound("LeftTank").getInt("amount") / 1000.0f);
        }
        if (tag.contains("RightTank")) {
            accumulator.addInsideWeight(tag.getCompound("RightTank").getInt("amount") / 1000.0f);
        }

        return accumulator.result();
    }

    private static WeightResult calculateScoutWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            if (itemTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                accumulator.addInsideStack(stackFromStandardNbt(itemTag.getCompound("Stack")), context, lookup);
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
                accumulator.addInsideStack(stackFromStandardNbt(itemList.getCompound(i)), context, lookup);
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
            ItemStack stack = NbtItemStackReader.fromIdCountSafely(
                    itemTag.getString("item"),
                    amount,
                    itemTag.contains("tag", NbtElement.COMPOUND_TYPE) ? itemTag.getCompound("tag") : null
            );
            accumulator.addInsideStack(stack, context, lookup);
        }
        return accumulator.result();
    }

    private static WeightResult calculateInmisWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = itemList.getCompound(i);
            if (itemTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                accumulator.addInsideStack(stackFromStandardNbt(itemTag.getCompound("Stack")), context, lookup);
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
            ItemStack stack = NbtItemStackReader.fromIdCountSafely(
                    itemId,
                    count,
                    itemTag.contains("tag", NbtElement.COMPOUND_TYPE) ? itemTag.getCompound("tag") : null
            );
            accumulator.addInsideStack(stack, context, lookup);
        }
        return accumulator.result();
    }

    private static WeightResult calculateStandardWeightFromNbtList(NbtList itemList, WeightContext context, WeightLookup lookup) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();
        for (int i = 0; i < itemList.size(); i++) {
            accumulator.addInsideStack(NbtItemStackReader.fromNbtSafely(itemList.getCompound(i)), context, lookup);
        }
        return accumulator.result();
    }

    private static ItemStack stackFromStandardNbt(NbtCompound tag) {
        return NbtItemStackReader.fromNbtSafely(tag);
    }

    private static final class BackpackAccumulator {
        private final float emptyContainerWeight;
        private float insideWeight;

        private BackpackAccumulator(float emptyContainerWeight) {
            this.emptyContainerWeight = emptyContainerWeight;
        }

        static BackpackAccumulator create() {
            return new BackpackAccumulator(WeightSettings.get().itemWeight());
        }

        void addInsideWeight(float value) {
            insideWeight += Math.max(0.0f, value);
        }

        void addInsideStack(ItemStack stack, WeightContext context, WeightLookup lookup) {
            if (stack == null || stack.isEmpty()) {
                return;
            }

            WeightResult itemWeight = lookup.getWeight(stack, context.nested()).multiply(stack.getCount());
            insideWeight += itemWeight.weight();
        }

        WeightResult result() {
            float effectiveWeight = emptyContainerWeight + (insideWeight / CONTENT_WEIGHT_DIVISOR);

            return WeightResult.of(effectiveWeight, insideWeight);
        }
    }
}
