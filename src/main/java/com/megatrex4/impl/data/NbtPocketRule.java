package com.megatrex4.impl.data;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.OptionalInt;

public record NbtPocketRule(Identifier itemId, String nbtKey, Map<String, Integer> valuePockets) {
    public OptionalInt find(ItemStack stack) {
        if (!itemId.equals(Registries.ITEM.getId(stack.getItem()))) {
            return OptionalInt.empty();
        }
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(nbtKey)) {
            return OptionalInt.empty();
        }

        NbtElement element = nbt.get(nbtKey);
        if (element instanceof NbtList list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.getHeldType() == NbtType.COMPOUND) {
                    NbtCompound compound = list.getCompound(i);
                    OptionalInt found = match(compound.getString("id"));
                    if (found.isPresent()) return found;
                    found = match(compound.getString("item"));
                    if (found.isPresent()) return found;
                }
            }
            return OptionalInt.empty();
        }

        return match(element.asString());
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
