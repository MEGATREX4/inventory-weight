package com.megatrex4.impl.compat.trinkets;

import com.megatrex4.api.v1.InventoryWeightRegistrar;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
//import dev.emi.trinkets.api.TrinketComponent;
//import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class TrinketsCompat {
    private TrinketsCompat() {}

//    public static void register(InventoryWeightRegistrar registrar) {
//        registrar.registerPlayerWeightSource(
//                Identifier.of(MOD_ID, "trinkets"),
//                1000,
//                (player, context, lookup) -> TrinketsApi.getTrinketComponent(player)
//                        .map(component -> calculate(component, context, lookup))
//                        .orElse(WeightResult.ZERO)
//        );
//    }
//
//    private static WeightResult calculate(TrinketComponent component, WeightContext context, WeightLookup lookup) {
//        WeightResult total = WeightResult.ZERO;
//        for (var pair : component.getAllEquipped()) {
//            ItemStack stack = pair.getRight();
//            if (!stack.isEmpty()) {
//                total = total.add(lookup.getWeight(stack, context).multiply(stack.getCount()));
//            }
//        }
//        return total;
//    }
}
