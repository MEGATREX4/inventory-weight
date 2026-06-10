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
import java.util.Optional;

public record NbtWeightRule(
        Identifier itemId,
        String nbtKey,
        Map<String, Float> valueWeights
) {
    public Optional<Float> find(ItemStack stack) {
        if (!itemId.equals(Registries.ITEM.getId(stack.getItem()))) {
            return Optional.empty();
        }

        NbtCompound nbt = ItemStackData.getCustomData(stack);

        if (nbt == null || !nbt.contains(nbtKey)) {
            return Optional.empty();
        }

        NbtElement element = nbt.get(nbtKey);

        if (element instanceof NbtList list) {
            for (int i = 0; i < list.size(); i++) {
                NbtCompound compound = NbtCompat.listCompound(list, i);

                if (compound == null) {
                    continue;
                }

                Optional<Float> found = match(NbtCompat.string(compound, "id"));
                if (found.isPresent()) {
                    return found;
                }

                found = match(NbtCompat.string(compound, "item"));
                if (found.isPresent()) {
                    return found;
                }
            }

            return Optional.empty();
        }

        return match(NbtCompat.string(element));
    }

    private Optional<Float> match(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }

        Float direct = valueWeights.get(value);

        if (direct != null) {
            return Optional.of(direct);
        }

        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            Float unquoted = valueWeights.get(value.substring(1, value.length() - 1));

            if (unquoted != null) {
                return Optional.of(unquoted);
            }
        }

        return Optional.empty();
    }
}