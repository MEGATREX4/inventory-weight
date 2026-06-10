package com.megatrex4.mixin;

import com.megatrex4.api.v1.InventoryWeightAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttributeMixin {
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void inventoryweight$addMaxWeightAttribute(
            CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir
    ) {
        cir.getReturnValue().add(
                InventoryWeightAttributes.GENERIC_MAX_WEIGHT,
                0.0D
        );
    }
}