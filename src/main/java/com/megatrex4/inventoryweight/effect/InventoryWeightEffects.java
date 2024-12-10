package com.megatrex4.inventoryweight.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.megatrex4.inventoryweight.InventoryWeight.MOD_ID;

public class InventoryWeightEffects {

    public static final StatusEffect OVERLOAD = new OverloadEffect();

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "overload"), OVERLOAD);
    }
}
