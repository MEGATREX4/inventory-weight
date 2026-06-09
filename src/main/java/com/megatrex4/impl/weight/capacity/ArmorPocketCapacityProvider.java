package com.megatrex4.impl.weight.capacity;

import com.megatrex4.api.v1.CapacityModifier;
import com.megatrex4.api.v1.CapacityProvider;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ArmorPocketCapacityProvider implements CapacityProvider {
    @Override
    public CapacityModifier getCapacityModifier(ServerPlayerEntity player) {
        float capacity = 0.0f;
        for (ItemStack armorPiece : player.getInventory().armor) {
            int pockets = InventoryWeightServices.pocketService()
                    .getPockets(armorPiece, player)
                    .orElse(0);
            capacity += pockets * WeightSettings.get().pocketWeight();
        }
        return CapacityModifier.additive(capacity);
    }
}
