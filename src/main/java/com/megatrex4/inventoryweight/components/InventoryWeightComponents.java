package com.megatrex4.inventoryweight.components;

import com.megatrex4.inventoryweight.InventoryWeight;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class InventoryWeightComponents implements EntityComponentInitializer {

	public static final ComponentKey<WeightComponent> WEIGHT =
			ComponentRegistry.getOrCreate(Identifier.of(InventoryWeight.MOD_ID, "weight"), WeightComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(PlayerEntity.class, WEIGHT, WeightComponent::new);
	}
}
