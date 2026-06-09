package com.megatrex4.impl.data;

import com.megatrex4.impl.config.ServerWeightSettings;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record WeightDataSnapshot(
        ServerWeightSettings settings,
        Map<Identifier, Float> itemWeights,
        Map<Identifier, Integer> pockets,
        List<NbtWeightRule> nbtWeightRules,
        List<NbtPocketRule> nbtPocketRules
) {}
