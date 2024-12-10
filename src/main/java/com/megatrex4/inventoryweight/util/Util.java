package com.megatrex4.inventoryweight.util;

import com.megatrex4.inventoryweight.InventoryWeight;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.UUID;

public final class Util {

	public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a53f3d53-2b63-4a78-851f-4c5795876d8c");
	public static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("c7d4f84c-9e6e-45d0-888e-df63a7e3d206");
	public static final UUID DAMAGE_REDUCTION_MODIFIER_UUID = UUID.fromString("e87c1e8a-6d3f-4b3a-8cbb-d29048a85f0d");

	@SuppressWarnings("unchecked")
	public static int getWeight(ItemStack item) {
		int weight = 100;
		for (Map.Entry<Requirement<Item, ?>, Integer> entry : InventoryWeight.customItemWeights.entrySet()) {
			Requirement<Item, ?> requirement = entry.getKey();
			if (requirement.getTarget() == item.getItem()) {
				try {
					Requirement<Item, ItemStack> itemRequirement = (Requirement<Item, ItemStack>) requirement; // TODO: find a better solution (instanceof doesn't work)
					if (itemRequirement.isMet(item)) {
						return entry.getValue() * item.getCount();
					}
				} catch (ClassCastException ignored) {
				}
			}
		}
		return weight * item.getCount();
	}

	public static float getReductionFactor(int weight, float step, float stepValue) {
		return weight > InventoryWeightConfig.SERVER.maxWeight
				? (((weight - InventoryWeightConfig.SERVER.maxWeight) / (step * InventoryWeightConfig.SERVER.maxWeight)) * stepValue)
				: 0;
	}

	public static void applyWeight(PlayerEntity player, float speedReduction, float attackSpeedReduction, float damageReduction) {
		// speed
		if (player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
				.getModifier(SPEED_MODIFIER_UUID) != null) {
			player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
					.removeModifier(SPEED_MODIFIER_UUID);
		}
		player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
				.addPersistentModifier(new EntityAttributeModifier(
						SPEED_MODIFIER_UUID,
						"overload_speed_reduction",
						-speedReduction,
						EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

		// attack speed
		if (player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
				.getModifier(ATTACK_SPEED_MODIFIER_UUID) != null) {
			player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
					.removeModifier(ATTACK_SPEED_MODIFIER_UUID);
		}
		player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
				.addPersistentModifier(new EntityAttributeModifier(
						ATTACK_SPEED_MODIFIER_UUID,
						"overload_attack_speed_reduction",
						-attackSpeedReduction,
						EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

		// attack damage
		if (player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
				.getModifier(DAMAGE_REDUCTION_MODIFIER_UUID) != null) {
			player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
					.removeModifier(DAMAGE_REDUCTION_MODIFIER_UUID);
		}
		player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
				.addPersistentModifier(new EntityAttributeModifier(
						DAMAGE_REDUCTION_MODIFIER_UUID,
						"overload_damage_reduction",
						-damageReduction,
						EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
	}

	public static void applyWeight(PlayerEntity player, int weight) {
		int maxWeight = InventoryWeightConfig.SERVER.maxWeight;

		float excessWeightRatio = (weight - (0.1f * maxWeight)) / (maxWeight - (0.1f * maxWeight));
		excessWeightRatio = MathHelper.clamp(excessWeightRatio, 0.01f, 1.0f);

		float speedReduction = 0.5f * excessWeightRatio;
		float attackSpeedReduction = 0.4f * excessWeightRatio;
		float damageReduction = 0.2f * excessWeightRatio;

		applyWeight(player, speedReduction, attackSpeedReduction, damageReduction);
	}

}
