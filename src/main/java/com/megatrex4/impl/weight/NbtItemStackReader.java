package com.megatrex4.impl.weight;

import com.megatrex4.InventoryWeight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class NbtItemStackReader {
    private NbtItemStackReader() {}

    public static ItemStack fromNbtSafely(CompoundTag tag) {
        if (tag == null || !tag.contains("id")) {
            return ItemStack.EMPTY;
        }

        String id = NbtCompat.string(tag, "id");
        int count = NbtCompat.intValue(tag, "Count", 1);
        CompoundTag itemNbt = NbtCompat.compound(tag, "tag");

        return fromIdCountSafely(id, count, itemNbt);
    }

    public static ItemStack fromIdCountSafely(
            String rawItemId,
            int count,
            @Nullable CompoundTag itemNbt
    ) {
        if (count <= 0) {
            return ItemStack.EMPTY;
        }

        Identifier itemId = parseItemId(rawItemId);

        if (itemId == null) {
            InventoryWeight.LOGGER.debug("Skipping container item with invalid item id '{}'.", rawItemId);
            return ItemStack.EMPTY;
        }

        if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
            InventoryWeight.LOGGER.debug("Skipping container item with unknown item id '{}'.", itemId);
            return ItemStack.EMPTY;
        }

        Item item = BuiltInRegistries.ITEM.getValue(itemId);
        ItemStack stack = new ItemStack(item, count);

        if (itemNbt != null) {
            ItemStackData.setCustomData(stack, itemNbt);
        }

        return stack;
    }

    @Nullable
    private static Identifier parseItemId(String rawItemId) {
        if (rawItemId == null) {
            return null;
        }

        String itemId = rawItemId.trim();

        if (itemId.isEmpty()) {
            return null;
        }

        if (itemId.length() >= 2 && itemId.startsWith("\"") && itemId.endsWith("\"")) {
            itemId = itemId.substring(1, itemId.length() - 1).trim();
        }

        if (itemId.length() >= 2 && itemId.startsWith("[") && itemId.endsWith("]")) {
            String unwrapped = itemId.substring(1, itemId.length() - 1).trim();

            if (unwrapped.contains(",") || unwrapped.contains(" ")) {
                return null;
            }

            itemId = unwrapped;
        }

        return Identifier.tryParse(itemId);
    }
}