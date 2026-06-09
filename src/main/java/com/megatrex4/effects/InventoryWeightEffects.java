package com.megatrex4.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightEffects {
    public static final StatusEffect OVERLOAD = new OverloadEffect();

    private static boolean registered;

    private InventoryWeightEffects() {}

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "overload"), OVERLOAD);
    }
}
