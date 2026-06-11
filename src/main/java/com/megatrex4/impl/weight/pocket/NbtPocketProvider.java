package com.megatrex4.impl.weight.pocket;

import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.data.NbtPocketRule;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class NbtPocketProvider implements PocketProvider {
    @Override
    public OptionalInt getPockets(ItemStack stack, @Nullable Player wearer) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (NbtPocketRule rule : WeightDataStore.INSTANCE.getNbtPocketRules(itemId)) {
            OptionalInt pockets = rule.find(stack);
            if (pockets.isPresent()) {
                return pockets;
            }
        }
        return OptionalInt.empty();
    }
}
