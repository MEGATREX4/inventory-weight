package com.megatrex4.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class InventoryWeightEffectRegister {

    public static final StatusEffect OVERLOAD_EFFECT = new OverloadEffect(); // Initialized directly

    public static void registerEffects() {
        // Register the effect with the registry
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "overload"), OVERLOAD_EFFECT);
    }
}
