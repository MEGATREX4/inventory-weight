package com.megatrex4.inventoryweight.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.megatrex4.inventoryweight.components.InventoryWeightComponents;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import com.megatrex4.inventoryweight.util.ArmorPockets;
import com.megatrex4.inventoryweight.effect.InventoryWeightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class PlayerEntityMixin {

	@ModifyReturnValue(method = "getJumpVelocity", at = @At("RETURN"))
	private float modifyJumpVelocity(float originalJumpVelocity) {
		if ((LivingEntity) (Object) this instanceof PlayerEntity self) {
			int weight = self.getComponent(InventoryWeightComponents.WEIGHT).getWeight();
                        int maxWeight = InventoryWeightConfig.SERVER.maxWeight + ArmorPockets.calculateArmorCapacity(self);

			if (!self.isCreative() && !self.isSpectator()) {
				if (self.hasStatusEffect(InventoryWeightEffects.OVERLOAD)) {
					int amplifier = self.getStatusEffect(InventoryWeightEffects.OVERLOAD).getAmplifier();
					float overloadLimit = Math.max(0.3f, 0.65f - (amplifier * 0.05f));
					return originalJumpVelocity * overloadLimit;

				} else if (weight > 0 && InventoryWeightConfig.SERVER.realisticMode) {
					float weightPercentage = (float) weight / maxWeight;
					float reductionFactor = Math.max(0.75f, 1 - weightPercentage * 0.25f);
					return originalJumpVelocity * reductionFactor;
				}
			}
		}
		return originalJumpVelocity;
	}
}