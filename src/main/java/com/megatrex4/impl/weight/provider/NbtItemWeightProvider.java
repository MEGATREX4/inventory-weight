package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.data.NbtWeightRule;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class NbtItemWeightProvider implements ItemWeightProvider {
    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        for (NbtWeightRule rule : WeightDataStore.INSTANCE.getNbtWeightRules(itemId)) {
            Optional<Float> weight = rule.find(stack);
            if (weight.isPresent()) {
                return Optional.of(WeightResult.of(weight.get()));
            }
        }
        return Optional.empty();
    }
}
