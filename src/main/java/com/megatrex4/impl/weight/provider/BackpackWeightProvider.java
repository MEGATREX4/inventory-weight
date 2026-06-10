package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.ItemStackData;
import com.megatrex4.impl.weight.NbtCompat;
import com.megatrex4.impl.weight.NbtItemStackReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

        NbtCompound tag = ItemStackData.getCustomData(stack);

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
                Identifier.of("travelersbackpack", "custom_travelers_backpack")
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

        NbtCompound tag = ItemStackData.getCustomData(stack);

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
            NbtCompound tag,
            WeightContext context,
            WeightLookup lookup
    ) {
        if (isTravelersBackpackId(itemId)) {
            return calculateTravelersBackpackWeight(tag, context, lookup);
        }

        if (itemId.contains("pouch") || itemId.contains("satchel")) {
            return calculateScoutWeightFromNbtList(NbtCompat.list(tag, "Items"), context, lookup);
        }

        if (itemId.contains("toolbox")) {
            return calculateToolboxWeight(tag, context, lookup);
        }

        if (itemId.contains("drawer")) {
            NbtCompound blockEntityTag = NbtCompat.compoundOrEmpty(tag, "BlockEntityTag");

            if (blockEntityTag.contains("items")) {
                return calculateDrawerWeightFromNbtList(
                        NbtCompat.list(blockEntityTag, "items"),
                        context,
                        lookup
                );
            }
        }

        if (tag.contains("pack_inventory")) {
            NbtCompound packInventoryTag = NbtCompat.compoundOrEmpty(tag, "pack_inventory");

            return calculatePackItUpWeightFromNbtList(
                    NbtCompat.list(packInventoryTag, "stack_contents"),
                    context,
                    lookup
            );
        }

        if (tag.contains("Inventory") && itemId.contains("inmis")) {
            return calculateInmisWeightFromNbtList(
                    NbtCompat.list(tag, "Inventory"),
                    context,
                    lookup
            );
        }

        if (tag.contains("Inventory")) {
            return calculateStandardWeightFromNbtList(
                    NbtCompat.list(tag, "Inventory"),
                    context,
                    lookup
            );
        }

        if (tag.contains("BlockEntityTag") && itemId.contains("sophisticatedstorage")) {
            NbtCompound blockEntityTag = NbtCompat.compoundOrEmpty(tag, "BlockEntityTag");

            if (blockEntityTag.contains("storageWrapper")) {
                NbtCompound storageWrapperTag = NbtCompat.compoundOrEmpty(blockEntityTag, "storageWrapper");
                NbtCompound contentsTag = NbtCompat.compoundOrEmpty(storageWrapperTag, "contents");
                NbtCompound inventoryTag = NbtCompat.compoundOrEmpty(contentsTag, "inventory");

                return calculateStandardWeightFromNbtList(
                        NbtCompat.list(inventoryTag, "Items"),
                        context,
                        lookup
                );
            }
        }

        if (tag.contains("BlockEntityTag")) {
            NbtCompound blockEntityTag = NbtCompat.compoundOrEmpty(tag, "BlockEntityTag");

            return calculateStandardWeightFromNbtList(
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
            NbtCompound tag,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        if (tag.contains("Inventory")) {
            NbtCompound inventoryTag = NbtCompat.compoundOrEmpty(tag, "Inventory");
            NbtList inventoryItems = NbtCompat.list(inventoryTag, "Items");

            for (int i = 0; i < inventoryItems.size(); i++) {
                accumulator.addInsideStack(
                        stackFromStandardNbt(NbtCompat.listCompound(inventoryItems, i)),
                        context,
                        lookup
                );
            }
        }

        NbtCompound leftTank = NbtCompat.compound(tag, "LeftTank");
        if (leftTank != null) {
            accumulator.addInsideWeight(NbtCompat.intValue(leftTank, "amount", 0) / 1000.0f);
        }

        NbtCompound rightTank = NbtCompat.compound(tag, "RightTank");
        if (rightTank != null) {
            accumulator.addInsideWeight(NbtCompat.intValue(rightTank, "amount", 0) / 1000.0f);
        }

        return accumulator.result();
    }

    private static WeightResult calculateScoutWeightFromNbtList(
            NbtList itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = NbtCompat.listCompound(itemList, i);

            if (itemTag == null) {
                continue;
            }

            NbtCompound stackTag = NbtCompat.compound(itemTag, "Stack");

            if (stackTag != null) {
                accumulator.addInsideStack(stackFromStandardNbt(stackTag), context, lookup);
            }
        }

        return accumulator.result();
    }

    private static WeightResult calculateToolboxWeight(
            NbtCompound tag,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        NbtCompound inventoryTag = NbtCompat.compound(tag, "Inventory");

        if (inventoryTag != null) {
            NbtList itemList = NbtCompat.list(inventoryTag, "Items");

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

    private static WeightResult calculateDrawerWeightFromNbtList(
            NbtList itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound drawerTag = NbtCompat.listCompound(itemList, i);

            if (drawerTag == null) {
                continue;
            }

            int amount = NbtCompat.intValue(drawerTag, "amount", 0);
            NbtCompound itemTag = NbtCompat.compound(drawerTag, "item");

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

    private static WeightResult calculateInmisWeightFromNbtList(
            NbtList itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = NbtCompat.listCompound(itemList, i);

            if (itemTag == null) {
                continue;
            }

            NbtCompound stackTag = NbtCompat.compound(itemTag, "Stack");

            if (stackTag != null) {
                accumulator.addInsideStack(stackFromStandardNbt(stackTag), context, lookup);
            }
        }

        return accumulator.result();
    }

    private static WeightResult calculatePackItUpWeightFromNbtList(
            NbtList itemList,
            WeightContext context,
            WeightLookup lookup
    ) {
        BackpackAccumulator accumulator = BackpackAccumulator.create();

        for (int i = 0; i < itemList.size(); i++) {
            NbtCompound itemTag = NbtCompat.listCompound(itemList, i);

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

    private static WeightResult calculateStandardWeightFromNbtList(
            NbtList itemList,
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