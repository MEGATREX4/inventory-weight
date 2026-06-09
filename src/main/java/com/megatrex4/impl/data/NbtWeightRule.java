package com.megatrex4.impl.data;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

public record NbtWeightRule(Identifier itemId, String nbtKey, Map<String, Float> valueWeights) {
    public Optional<Float> find(ItemStack stack) {
        if (!itemId.equals(Registries.ITEM.getId(stack.getItem()))) {
            return Optional.empty();
        }
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(nbtKey)) {
            return Optional.empty();
        }

        NbtElement element = nbt.get(nbtKey);
        if (element instanceof NbtList list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.getHeldType() == NbtType.COMPOUND) {
                    NbtCompound compound = list.getCompound(i);
                    Optional<Float> found = match(compound.getString("id"));
                    if (found.isPresent()) return found;
                    found = match(compound.getString("item"));
                    if (found.isPresent()) return found;
                }
            }
            return Optional.empty();
        }

        return match(element.asString());
    }

    private Optional<Float> match(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }
        Float direct = valueWeights.get(value);
        if (direct != null) {
            return Optional.of(direct);
        }
        // Some NBT string renderings include quotes.
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            Float unquoted = valueWeights.get(value.substring(1, value.length() - 1));
            if (unquoted != null) {
                return Optional.of(unquoted);
            }
        }
        return Optional.empty();
    }
}
