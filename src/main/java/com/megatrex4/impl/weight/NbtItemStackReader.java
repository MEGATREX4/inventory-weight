package com.megatrex4.impl.weight;

import com.megatrex4.InventoryWeight;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Safe helpers for reading ItemStacks from third-party container NBT.
 *
 * Some mods store non-vanilla strings in places that look like item ids, for example
 * "[certus_quartz_wrench]". Constructing Identifier.of(...) with such strings crashes.
 * These helpers use Identifier.tryParse(...) and skip invalid/unknown ids instead.
 */
public final class NbtItemStackReader {
    private NbtItemStackReader() {}

    public static ItemStack fromNbtSafely(NbtCompound tag) {
        if (tag == null || !tag.contains("id", NbtElement.STRING_TYPE)) {
            return ItemStack.EMPTY;
        }

        int count = tag.contains("Count") ? tag.getInt("Count") : 1;
        NbtCompound itemNbt = tag.contains("tag", NbtElement.COMPOUND_TYPE) ? tag.getCompound("tag") : null;

        return fromIdCountSafely(tag.getString("id"), count, itemNbt);
    }

    public static ItemStack fromIdCountSafely(String rawItemId, int count, @Nullable NbtCompound itemNbt) {
        if (count <= 0) {
            return ItemStack.EMPTY;
        }

        Identifier itemId = parseItemId(rawItemId);
        if (itemId == null) {
            InventoryWeight.LOGGER.debug("Skipping container item with invalid item id '{}'.", rawItemId);
            return ItemStack.EMPTY;
        }

        if (!Registries.ITEM.containsId(itemId)) {
            InventoryWeight.LOGGER.debug("Skipping container item with unknown item id '{}'.", itemId);
            return ItemStack.EMPTY;
        }

        Item item = Registries.ITEM.get(itemId);
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

        // Some NBT string renderers include quotes around string values.
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
