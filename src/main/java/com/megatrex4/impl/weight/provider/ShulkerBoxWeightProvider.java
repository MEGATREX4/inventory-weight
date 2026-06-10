package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.ItemStackData;
import com.megatrex4.impl.weight.NbtItemStackReader;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.Optional;

public final class ShulkerBoxWeightProvider implements ItemWeightProvider {
    private static final float CONTENT_WEIGHT_DIVISOR = 2.0f;

    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        if (!isShulkerBox(stack)) {
            return Optional.empty();
        }

        float emptyContainerWeight = WeightSettings.get().itemWeight();
        float insideWeight = calculateInsideWeight(stack, context, lookup);
        float effectiveWeight = emptyContainerWeight + (insideWeight / CONTENT_WEIGHT_DIVISOR);

        return Optional.of(WeightResult.of(effectiveWeight, insideWeight).sanitized());
    }

    public static boolean isShulkerBox(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    private static float calculateInsideWeight(
            ItemStack shulkerBoxStack,
            WeightContext context,
            WeightLookup lookup
    ) {
        ContainerComponent container = shulkerBoxStack.getOrDefault(
                DataComponentTypes.CONTAINER,
                ContainerComponent.DEFAULT
        );

        float insideWeight = 0.0f;

        for (ItemStack contained : container.iterateNonEmpty()) {
            if (!contained.isEmpty()) {
                insideWeight += lookup.getWeight(contained, context.nested())
                        .multiply(contained.getCount())
                        .weight();
            }
        }

        return insideWeight;
    }
}
