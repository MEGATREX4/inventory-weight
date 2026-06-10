package com.megatrex4.impl.weight.capacity;

import com.megatrex4.api.v1.CapacityModifier;
import com.megatrex4.api.v1.CapacityProvider;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ArmorPocketCapacityProvider implements CapacityProvider {
    @Override
    public CapacityModifier getCapacityModifier(ServerPlayerEntity player) {
        float capacity = 0.0f;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack armorPiece = player.getEquippedStack(slot);

            int pockets = InventoryWeightServices.pocketService()
                    .getPockets(armorPiece, player)
                    .orElse(0);

            capacity += pockets * WeightSettings.get().pocketWeight();
        }

        return CapacityModifier.additive(capacity);
    }
}