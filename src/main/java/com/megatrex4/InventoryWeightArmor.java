package com.megatrex4;

import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Deprecated compatibility shim. New code should use PocketProvider/CapacityProvider through the API.
 */
@Deprecated(forRemoval = false)
public final class InventoryWeightArmor {
    private InventoryWeightArmor() {}

    public static boolean hasPockets(ItemStack stack) {
        return getPockets(stack) > 0;
    }

    public static int getPockets(ItemStack stack) {
        return InventoryWeightServices.pocketService().getPockets(stack, null).orElse(0);
    }

    public static float getPocketWeight() {
        return WeightSettings.get().pocketWeight();
    }

    public static int getPocketsBasedOnProtection(ArmorItem armorItem) {
        int protection = armorItem.getProtection();
        float toughness = armorItem.getToughness();
        return (int) Math.max(1, 7 - (int) (protection / 1.2f) - toughness);
    }

    public static int getPocketsWithNbtCheck(ItemStack armorStack) {
        return getPockets(armorStack);
    }

    public static float calculateArmorWeight(ServerPlayerEntity player) {
        float total = 0.0f;
        for (ItemStack armorPiece : player.getInventory().armor) {
            total += getPockets(armorPiece) * getPocketWeight();
        }
        return total;
    }
}
