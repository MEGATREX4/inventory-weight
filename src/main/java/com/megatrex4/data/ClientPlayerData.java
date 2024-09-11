package com.megatrex4.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClientPlayerData {
    private static ClientPlayerWeightData weightData = new ClientPlayerWeightData();

    public static ClientPlayerWeightData getWeightData() {
        return weightData;
    }

    public static void updateWeightData(int currentWeight, int maxWeight) {
        weightData.setWeights(currentWeight, maxWeight);
    }
}
