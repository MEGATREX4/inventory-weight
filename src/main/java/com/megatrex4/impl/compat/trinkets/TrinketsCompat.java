package com.megatrex4.impl.compat.trinkets;

import com.megatrex4.api.v1.InventoryWeightRegistrar;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class TrinketsCompat {

    private TrinketsCompat() {}

    public static void register(InventoryWeightRegistrar registrar) {
        registrar.registerPlayerWeightSource(
                Identifier.fromNamespaceAndPath(MOD_ID, "trinkets"),
                1000,
                (player, context, lookup) -> calculate(
                        TrinketsApi.getAttachment(player),
                        context,
                        lookup
                )
        );
    }

    private static WeightResult calculate(
            TrinketAttachment attachment,
            WeightContext context,
            WeightLookup lookup
    ) {
        WeightResult total = WeightResult.ZERO;

        for (var pair : attachment.getAllEquipped()) {
            ItemStack stack = pair.getB();

            if (!stack.isEmpty()) {
                total = total.add(
                        lookup.getWeight(stack, context)
                                .multiply(stack.getCount())
                );
            }
        }

        return total;
    }
}