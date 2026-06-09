package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.Optional;

public final class ShulkerBoxWeightProvider implements ItemWeightProvider {
    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
            return Optional.empty();
        }

        float base = WeightSettings.get().itemWeight();
        WeightResult result = WeightResult.of(base);

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("BlockEntityTag")) {
            return Optional.of(result);
        }

        NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
        NbtList itemList = blockEntityTag.getList("Items", 10);

        for (int i = 0; i < itemList.size(); i++) {
            ItemStack contained = ItemStack.fromNbt(itemList.getCompound(i));
            if (!contained.isEmpty()) {
                result = result.add(lookup.getWeight(contained, context.nested()).multiply(contained.getCount()));
            }
        }

        return Optional.of(result.sanitized());
    }
}
