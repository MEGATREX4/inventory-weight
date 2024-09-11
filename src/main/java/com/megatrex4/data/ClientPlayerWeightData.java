package com.megatrex4.data;

public class ClientPlayerWeightData {
    private int currentWeight;
    private int maxWeight;

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public void setWeights(int currentWeight, int maxWeight) {
        this.currentWeight = currentWeight;
        this.maxWeight = maxWeight;
    }
}
