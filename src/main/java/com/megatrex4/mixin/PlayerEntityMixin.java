package com.megatrex4.mixin;

import com.megatrex4.InventoryWeightHandler;
import com.megatrex4.effects.InventoryWeightEffectRegister;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.megatrex4.effects.OverloadEffect.BASE_PENALTY;
import static com.megatrex4.util.InventoryWeightUtil.MAXWEIGHT;

@Mixin(LivingEntity.class)
public abstract class PlayerEntityMixin {

	@Shadow
	protected abstract float getJumpVelocity();

	@Inject(at = @At("HEAD"), method = "jump", cancellable = true)
	protected void overloadJumpEffect(CallbackInfo ci) {
		if ((LivingEntity) (Object) this instanceof PlayerEntity self) {
			float totalWeight = InventoryWeightHandler.calculateInventoryWeight(self);

			// Set the base jump velocity
			float baseJumpVelocity = this.getJumpVelocity();

			if (self.hasStatusEffect(InventoryWeightEffectRegister.OVERLOAD_EFFECT)) {
				float reducedJumpVelocity = baseJumpVelocity * 0.6f / (self.getStatusEffect(InventoryWeightEffectRegister.OVERLOAD_EFFECT).getAmplifier() + 1);

				// Cap the jump reduction at 20% of the base jump height
				if (reducedJumpVelocity < baseJumpVelocity * 0.2f) reducedJumpVelocity = baseJumpVelocity * 0.2f;

				// Set the velocity to the reduced value
				Vec3d vec3d = self.getVelocity();
				self.setVelocity(vec3d.x, reducedJumpVelocity, vec3d.z);
			} else if (totalWeight > 0.1 * MAXWEIGHT && !self.isCreative()) {
				double overloadFactor = (totalWeight - (0.1 * MAXWEIGHT)) / (MAXWEIGHT - (0.1 * MAXWEIGHT));

				float reducedJumpVelocity = (float) (baseJumpVelocity * (1 - overloadFactor * BASE_PENALTY));

				// Cap the jump reduction at 30% of the base jump height
				if (reducedJumpVelocity < baseJumpVelocity * 0.3f) reducedJumpVelocity = baseJumpVelocity * 0.3f;

				Vec3d vec3d = self.getVelocity();
				self.setVelocity(vec3d.x, reducedJumpVelocity + 0.05 /* don't make it too annoying */, vec3d.z);
			} else {
				Vec3d vec3d = self.getVelocity();
				self.setVelocity(vec3d.x, getJumpVelocity(), vec3d.z);

				if (self.isSprinting()) {
					float f = self.getYaw() * 0.017453292F;
					self.setVelocity(self.getVelocity().add(-MathHelper.sin(f) * 0.2F, 0.0, MathHelper.cos(f) * 0.2F));
				}
			}

			self.velocityDirty = true;
			ci.cancel(); // Cancel the default jump method since we've handled it
		}
	}


}