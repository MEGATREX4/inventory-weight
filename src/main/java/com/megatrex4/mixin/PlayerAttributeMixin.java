package com.megatrex4.mixin;

import com.megatrex4.api.v1.InventoryWeightAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerAttributeMixin {

    @Dynamic("Minecraft 26.1 mappings: Player#createAttributes exists in runtime/decompiled sources, but IDE Mixin plugin may not resolve it.")
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void inventoryweight$addMaxWeightAttribute(
            CallbackInfoReturnable<AttributeSupplier.Builder> cir
    ) {
        cir.getReturnValue().add(
                InventoryWeightAttributes.GENERIC_MAX_WEIGHT,
                0.0D
        );
    }
}