package com.megatrex4.mixin;

import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.component.PlayerWeightComponentRegistry;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.player.WeightPenaltyService;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class PlayerJumpMixin {

    @Dynamic("Minecraft 26.1 mappings: LivingEntity#jumpFromGround exists in runtime/decompiled sources, but IDE Mixin plugin may not resolve it.")
    @Inject(method = "jumpFromGround", at = @At("RETURN"))
    private void inventoryweight$modifyJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player player)) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        float currentWeight;
        float maxWeight;

        var component = PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player);

        if (component.isPresent()) {
            currentWeight = component.get().getCurrentInventoryWeight();
            maxWeight = component.get().getMaxWeight();
        } else {
            WeightResult calculated = InventoryWeightServices.playerWeightService().getInventoryWeight(player);
            currentWeight = calculated.weight();
            maxWeight = 90_000.0f;
        }

        if (maxWeight <= 0.0f) {
            return;
        }

        Vec3 velocity = player.getDeltaMovement();

        float baseJumpVelocity = (float) velocity.y;
        float adjustedJumpVelocity = WeightPenaltyService.getJumpVelocity(
                player,
                baseJumpVelocity,
                currentWeight,
                maxWeight
        );

        if (Float.compare(baseJumpVelocity, adjustedJumpVelocity) == 0) {
            return;
        }

        player.setDeltaMovement(
                velocity.x,
                adjustedJumpVelocity,
                velocity.z
        );
    }
}