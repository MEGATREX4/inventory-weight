package com.megatrex4.inventoryweight.util;

import com.megatrex4.inventoryweight.InventoryWeight;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import com.megatrex4.inventoryweight.effect.InventoryWeightEffects;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.UUID;

public final class Util {

	public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a53f3d53-2b63-4a78-851f-4c5795876d8c");
	public static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("c7d4f84c-9e6e-45d0-888e-df63a7e3d206");
	public static final UUID DAMAGE_REDUCTION_MODIFIER_UUID = UUID.fromString("e87c1e8a-6d3f-4b3a-8cbb-d29048a85f0d");

	@SuppressWarnings("unchecked")
	public static int getWeight(ItemStack item) {
		if (isShulker(item) && item.hasNbt() && item.getNbt().contains("BlockEntityTag")) {
			return getShulkerWeight(item);
		}
		int weight = item.getItem() instanceof BlockItem ? 500 : 100;
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

	public static int getWeight(PlayerEntity player) {
		int weight = 0;
		for (ItemStack stack : player.getInventory().main.toArray(new ItemStack[0])) {
			if (stack.hasNbt() && stack.getNbt().contains("weight"))
				weight += stack.getNbt().getInt("weight") * stack.getCount();
			else
				weight += Util.getWeight(stack);
		}
		for (ItemStack stack : player.getInventory().player.getArmorItems()) {
			if (stack.hasNbt() && stack.getNbt().contains("weight"))
				weight += stack.getNbt().getInt("weight");
			else
				weight += Util.getWeight(stack);
		}
		for (ItemStack stack : player.getInventory().offHand.toArray(new ItemStack[0])) {
			if (stack.hasNbt() && stack.getNbt().contains("weight"))
				weight += stack.getNbt().getInt("weight") * stack.getCount();
			else
				weight += Util.getWeight(stack);
		}
		return weight;
	}

	public static int getShulkerWeight(ItemStack shulker) {
		NbtCompound nbt = shulker.getNbt();
		if (!shulker.hasNbt()) return getWeight(shulker);
		else if (!isShulker(shulker) || !nbt.contains("BlockEntityTag")) {
			return getWeight(shulker);
		}

		NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
		NbtList itemList = blockEntityTag.getList("Items", 10);
		int weight = 0;
		for (int i = 0; i < itemList.size(); i++) {
			NbtCompound itemTag = itemList.getCompound(i);
			ItemStack itemStack = ItemStack.fromNbt(itemTag);
			weight += getWeight(itemStack);
		}
		return weight + getWeight(shulker);
	}

	public static float getReductionFactor(int weight, float step, float stepValue) {
		return weight > InventoryWeightConfig.SERVER.maxWeight
				? (((weight - InventoryWeightConfig.SERVER.maxWeight) / (step * InventoryWeightConfig.SERVER.maxWeight)) * stepValue)
				: 0;
	}

	public static void applyWeightModifiers(PlayerEntity player, float speedReduction, float attackSpeedReduction, float damageReduction) {
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
		if (weight >= maxWeight) {
			player.addStatusEffect(new StatusEffectInstance(InventoryWeightEffects.OVERLOAD, 20, (int) Util.getReductionFactor(weight, 0.1f, 1), true, false, false));
		} else if (InventoryWeightConfig.SERVER.realisticMode) {
			float excessWeightRatio = (weight - (0.1f * maxWeight)) / (maxWeight - (0.1f * maxWeight));
			excessWeightRatio = MathHelper.clamp(excessWeightRatio, 0.01f, 1.0f);

			float speedReduction = 0.3f * excessWeightRatio;
			float attackSpeedReduction = 0.2f * excessWeightRatio;
			float damageReduction = 0.1f * excessWeightRatio;

			applyWeightModifiers(player, speedReduction, attackSpeedReduction, damageReduction);
		}
	}

	public static boolean isShulker(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
	}
}
