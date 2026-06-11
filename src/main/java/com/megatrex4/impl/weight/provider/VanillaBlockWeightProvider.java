package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.ServerWeightSettings;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.ItemCategory;
import com.megatrex4.impl.weight.ItemCategoryClassifier;
import com.megatrex4.impl.weight.WeightMath;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

import java.util.Optional;

public final class VanillaBlockWeightProvider implements ItemWeightProvider {

    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return Optional.empty();
        }

        Block block = blockItem.getBlock();

        ServerWeightSettings settings = WeightSettings.get();

        ItemCategory category = ItemCategoryClassifier.classify(stack);

        float weight = category == ItemCategory.CREATIVE
                ? settings.creativeWeight()
                : settings.blockWeight();

        weight += block.defaultDestroyTime() * 10.0f;
        weight += Math.min(block.getExplosionResistance() * 50.0f, 10_000.0f);

        boolean transparent = !block.defaultBlockState().canOcclude();

        if (transparent) {
            weight *= 0.8f;
        }

        if (block instanceof EntityBlock) {
            weight += 50.0f;
        }

        if (block instanceof SlabBlock) {
            weight *= 0.5f;
        }

        if (block instanceof StairBlock) {
            weight *= 0.875f;
        }

        weight *= WeightMath.rarityMultiplier(stack);

        return Optional.of(WeightResult.of(WeightMath.finalFloor(weight)));
    }
}