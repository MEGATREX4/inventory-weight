package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.data.WeightDataStore;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class DatapackItemWeightProvider implements ItemWeightProvider {
    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        Float weight = WeightDataStore.INSTANCE.getItemWeight(itemId);
        return weight == null ? Optional.empty() : Optional.of(WeightResult.of(weight));
    }
}
