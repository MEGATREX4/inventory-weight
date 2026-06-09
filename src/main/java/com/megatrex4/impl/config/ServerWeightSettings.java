package com.megatrex4.impl.config;

import com.megatrex4.config.InventoryWeightConfig;

public record ServerWeightSettings(
        float maxWeight,
        float pocketWeight,
        boolean realisticMode,
        float overloadPenaltyStrength,
        float bucketWeight,
        float bottleWeight,
        float blockWeight,
        float ingotWeight,
        float nuggetWeight,
        float itemWeight,
        float creativeWeight
) {
    public static ServerWeightSettings fromConfig() {
        InventoryWeightConfig.Server server = InventoryWeightConfig.getServer();
        return new ServerWeightSettings(
                server.maxWeight,
                server.pocketWeight,
                server.realisticMode,
                server.overloadPenaltyStrength,
                server.bucketWeight,
                server.bottleWeight,
                server.blockWeight,
                server.ingotWeight,
                server.nuggetWeight,
                server.itemWeight,
                server.creativeWeight
        );
    }
}
