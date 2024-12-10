package com.megatrex4.inventoryweight.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.megatrex4.inventoryweight.components.InventoryWeightComponents;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import com.megatrex4.inventoryweight.effect.InventoryWeightEffects;
import com.megatrex4.inventoryweight.util.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.megatrex4.inventoryweight.effect.OverloadEffect.BASE_PENALTY;

@Mixin(LivingEntity.class)
public abstract class PlayerEntityMixin {

	@ModifyReturnValue(method = "getJumpVelocity", at = @At("RETURN"))
	private float modifyJumpVelocity(float originalJumpVelocity) {
		if ((LivingEntity) (Object) this instanceof PlayerEntity self) {
			int weight = self.getComponent(InventoryWeightComponents.WEIGHT).getWeight();
			int maxWeight = InventoryWeightConfig.SERVER.maxWeight;

			if (!self.isCreative() && !self.isSpectator()) {
				if (weight > maxWeight) {
					if (self.hasStatusEffect(InventoryWeightEffects.OVERLOAD)) {
						return Math.max(
								originalJumpVelocity * 0.6f / (self.getStatusEffect(InventoryWeightEffects.OVERLOAD).getAmplifier() + 1) / InventoryWeightConfig.SERVER.overloadStrength,
								originalJumpVelocity * 0.2f
						);
					} else if (weight > 0.1 * maxWeight && InventoryWeightConfig.SERVER.realisticMode) {
						return Math.max(
								(float) (originalJumpVelocity * (1 - Util.getReductionFactor(weight, 0.1f, 0.1f) * BASE_PENALTY)) + 0.05f,
								originalJumpVelocity * 0.3f
						);
					}
				}
			}
		}
		return originalJumpVelocity;
	}
}
