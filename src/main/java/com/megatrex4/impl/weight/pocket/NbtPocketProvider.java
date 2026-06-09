package com.megatrex4.impl.weight.pocket;

import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.data.NbtPocketRule;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class NbtPocketProvider implements PocketProvider {
    @Override
    public OptionalInt getPockets(ItemStack stack, @Nullable PlayerEntity wearer) {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        for (NbtPocketRule rule : WeightDataStore.INSTANCE.getNbtPocketRules(itemId)) {
            OptionalInt pockets = rule.find(stack);
            if (pockets.isPresent()) {
                return pockets;
            }
        }
        return OptionalInt.empty();
    }
}
