package com.megatrex4.mixin;

import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.component.PlayerWeightComponentRegistry;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.player.WeightPenaltyService;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class PlayerJumpMixin {
    @Shadow
    protected abstract float getJumpVelocity();

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void inventoryweight$modifyJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) {
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

        float base = getJumpVelocity();
        float adjusted = WeightPenaltyService.getJumpVelocity(player, base, currentWeight, maxWeight);

        if (Float.compare(base, adjusted) == 0) {
            return;
        }

        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, adjusted, velocity.z);

        if (player.isSprinting() && currentWeight < maxWeight) {
            float yawRadians = player.getYaw() * 0.017453292F;
            player.setVelocity(player.getVelocity().add(-MathHelper.sin(yawRadians) * 0.2F, 0.0, MathHelper.cos(yawRadians) * 0.2F));
        }

        player.velocityDirty = true;
        ci.cancel();
    }
}
