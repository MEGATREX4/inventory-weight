package com.megatrex4.effects;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightEffects {

    public static final Holder<MobEffect> OVERLOAD = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            Identifier.fromNamespaceAndPath(MOD_ID, "overload"),
            new OverloadEffect()
    );

    private static boolean registered;

    private InventoryWeightEffects() {}

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
    }
}