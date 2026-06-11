package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.ItemStackData;
import com.megatrex4.impl.weight.NbtCompat;
import com.megatrex4.impl.weight.NbtItemStackReader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


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

        CompoundTag tag = ItemStackData.getCustomData(stack);

        if (tag == null) {
            return Optional.of(WeightResult.of(WeightSettings.get().itemWeight(), 0.0f));
        }

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase(Locale.ROOT);

        return Optional.of(calculateBackpackWeight(itemId, tag, context, lookup).sanitized());
    }

    public static boolean isBackpackStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase(Locale.ROOT);

        return isBackpack(itemId, stack);
    }

    public static boolean isTravelerBackpack(ItemStack stack) {
        TagKey<Item> travelerBackpackTag = TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath("travelersbackpack", "custom_travelers_backpack")
        );

        return stack.is(travelerBackpackTag);
    }

    private static boolean isBackpack(String itemId, ItemStack stack) {
        if (itemId.contains("chestplate")) {
            return false;
        }

        if (isTravelerBackpack(stack)) {
            return true;
        }

        CompoundTag tag = ItemStackData.getCustomData(stack);

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

    private static WeightResult calculateBackpackWeight(
            String itemId,
            CompoundTag tag,
            WeightContext context,
            WeightLookup lookup
    ) {
        if (isTravelersBackpackId(itemId)) {
            return calculateTravelersBackpackWeight(tag, context, lookup);
        }

        if (itemId.contains("pouch") || itemId.contains("satchel")) {
            return calculateScoutWeightFromListTag(NbtCompat.list(tag, "Items"), context, lookup);
        }

        if (itemId.contains("toolbox")) {
            return calculateToolboxWeight(tag, context, lookup);
        }

        if (itemId.contains("drawer")) {
            CompoundTag blockEntityTag = NbtCompat.compoundOrEmpty(tag, "BlockEntityTag");

            if (blockEntityTag.contains("items")) {
                return calculateDrawerWeightFromListTag(
                        NbtCompat.list(blockEntityTag, "items"),
                        context,
                        lookup
                );
            }
        }

        if (tag.contains("pack_inventory")) {
            CompoundTag packInventoryTag = NbtCompat.compoundOrEmpty(tag, "pack_inventory");

            return calculatePackItUpWeightFromListTag(
                    NbtCompat.list(packInventoryTag, "stack_contents"),
                    context,
                    lookup
            );
        }

        if (tag.contains("Inventory") && itemId.contains("inmis")) {
            return calculateInmisWeightFromListTag(
                    NbtCompat.list(tag, "Inventory"),
                    context,
                    lookup
            );
        }

        if (tag.contains("Inventory")) {
            return calculateStandardWeightFromListTag(
                    NbtCompat.list(tag, "Inventory"),
                    context,
                    lookup
            );
        }

        if (tag.contains("BlockEntityTag") && itemId.contains("sophisticatedstorage")) {
            CompoundTag blockEntityTag = NbtCompat.compoundOrEmpty(tag, "BlockEntityTag");

            if (blockEntityTag.contains("storageWrapper")) {
                CompoundTag storageWrapperTag = NbtCompat.compoundOrEmpty(blockEntityTag, "storageWrapper");
                CompoundTag contentsTag = NbtCompat.compoundOrEmpty(storageWrapperTag, "contents");
                CompoundTag inventoryTag = NbtCompat.compoundOrEmpty(contentsTag, "inventory");

                return calculateStandardWeightFromListTag(
                        NbtCompat.list(inventoryTag, "Items"),
                        context,
                        lookup
                );
            }
        }

        if (tag.contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = NbtCompat.compoundOrEmpty(tag, "BlockEntityTag");

            return calculateStandardWeightFromListTag(
                    NbtCompat.list(blockEntityTag, "Items"),
                    context,
                    lookup
            );
        }

        return WeightResult.of(WeightSettings.get().itemWeight(), 0.0f);
    }

    private static boolean isTravelersBackpackId(String itemId) {
        return itemId.contains("travelersbackpack") || itemId.contains("travellersbackpack");
    }

    private static WeightResult calculateTravelersBackpackWeight(
            CompoundTag tag,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        if (tag.contains("Inventory")) {
            CompoundTag inventoryTag = NbtCompat.compoundOrEmpty(tag, "Inventory");
            ListTag inventoryItems = NbtCompat.list(inventoryTag, "Items");

            for (int i = 0; i < inventoryItems.size(); i++) {
                accumulator.addInsideStack(
                        stackFromStandardNbt(NbtCompat.listCompound(inventoryItems, i)),
                        context,
                        lookup
                );
            }
        }

        CompoundTag leftTank = NbtCompat.compound(tag, "LeftTank");
        if (leftTank != null) {
            accumulator.addInsideWeight(NbtCompat.intValue(leftTank, "amount", 0) / 1000.0f);
        }

        CompoundTag rightTank = NbtCompat.compound(tag, "RightTank");
        if (rightTank != null) {
            accumulator.addInsideWeight(NbtCompat.intValue(rightTank, "amount", 0) / 1000.0f);
        }

        return accumulator.result();
    }

    private static WeightResult calculateScoutWeightFromListTag(
            ListTag itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag itemTag = NbtCompat.listCompound(itemList, i);

            if (itemTag == null) {
                continue;
            }

            CompoundTag stackTag = NbtCompat.compound(itemTag, "Stack");

            if (stackTag != null) {
                accumulator.addInsideStack(stackFromStandardNbt(stackTag), context, lookup);
            }
        }

        return accumulator.result();
    }

    private static WeightResult calculateToolboxWeight(
            CompoundTag tag,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        CompoundTag inventoryTag = NbtCompat.compound(tag, "Inventory");

        if (inventoryTag != null) {
            ListTag itemList = NbtCompat.list(inventoryTag, "Items");

            for (int i = 0; i < itemList.size(); i++) {
                accumulator.addInsideStack(
                        stackFromStandardNbt(NbtCompat.listCompound(itemList, i)),
                        context,
                        lookup
                );
            }
        }

        return accumulator.result();
    }

    private static WeightResult calculateDrawerWeightFromListTag(
            ListTag itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag drawerTag = NbtCompat.listCompound(itemList, i);

            if (drawerTag == null) {
                continue;
            }

            int amount = NbtCompat.intValue(drawerTag, "amount", 0);
            CompoundTag itemTag = NbtCompat.compound(drawerTag, "item");

            if (itemTag == null) {
                continue;
            }

            ItemStack stack = NbtItemStackReader.fromIdCountSafely(
                    NbtCompat.string(itemTag, "item"),
                    amount,
                    NbtCompat.compound(itemTag, "tag")
            );

            accumulator.addInsideStack(stack, context, lookup);
        }

        return accumulator.result();
    }

    private static WeightResult calculateInmisWeightFromListTag(
            ListTag itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag itemTag = NbtCompat.listCompound(itemList, i);

            if (itemTag == null) {
                continue;
            }

            CompoundTag stackTag = NbtCompat.compound(itemTag, "Stack");

            if (stackTag != null) {
                accumulator.addInsideStack(stackFromStandardNbt(stackTag), context, lookup);
            }
        }

        return accumulator.result();
    }

    private static WeightResult calculatePackItUpWeightFromListTag(
            ListTag itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag itemTag = NbtCompat.listCompound(itemList, i);

            if (itemTag == null) {
                continue;
            }

            String itemId = NbtCompat.string(itemTag, "id");

            int count = itemTag.contains("count")
                    ? NbtCompat.shortValue(itemTag, "count", (short) 1)
                    : NbtCompat.intValue(itemTag, "Count", 1);

            ItemStack stack = NbtItemStackReader.fromIdCountSafely(
                    itemId,
                    count,
                    NbtCompat.compound(itemTag, "tag")
            );

            accumulator.addInsideStack(stack, context, lookup);
        }

        return accumulator.result();
    }

    private static WeightResult calculateStandardWeightFromListTag(
            ListTag itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            accumulator.addInsideStack(
                    NbtItemStackReader.fromNbtSafely(NbtCompat.listCompound(itemList, i)),
                    context,
                    lookup
            );
        }

        return accumulator.result();
    }

    private static ItemStack stackFromStandardNbt(CompoundTag tag) {
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