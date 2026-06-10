package com.megatrex4.impl.data;

import com.megatrex4.impl.weight.ItemStackData;
import com.megatrex4.impl.weight.NbtCompat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.OptionalInt;

public record NbtPocketRule(
        Identifier itemId,
        String nbtKey,
        Map<String, Integer> valuePockets
) {
    public OptionalInt find(ItemStack stack) {
        if (!itemId.equals(Registries.ITEM.getId(stack.getItem()))) {
            return OptionalInt.empty();
        }

        NbtCompound nbt = ItemStackData.getCustomData(stack);

        if (nbt == null || !nbt.contains(nbtKey)) {
            return OptionalInt.empty();
        }

        NbtElement element = nbt.get(nbtKey);

        if (element instanceof NbtList list) {
            for (int i = 0; i < list.size(); i++) {
                NbtCompound compound = NbtCompat.listCompound(list, i);

                if (compound == null) {
                    continue;
                }

                OptionalInt found = match(NbtCompat.string(compound, "id"));
                if (found.isPresent()) {
                    return found;
                }

                found = match(NbtCompat.string(compound, "item"));
                if (found.isPresent()) {
                    return found;
                }
            }

            return OptionalInt.empty();
        }

        return match(NbtCompat.string(element));
    }

    private OptionalInt match(String value) {
        if (value == null || value.isEmpty()) {
            return OptionalInt.empty();
        }

        Integer direct = valuePockets.get(value);

        if (direct != null) {
            return OptionalInt.of(direct);
        }

        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            Integer unquoted = valuePockets.get(value.substring(1, value.length() - 1));

            if (unquoted != null) {
                return OptionalInt.of(unquoted);
            }
        }

        return OptionalInt.empty();
    }
}