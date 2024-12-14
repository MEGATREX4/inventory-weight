package com.megatrex4.inventoryweight.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DurabilityRequirement implements Requirement<Item, ItemStack> {
	private final Item item;
	private final int min, max;
	private final boolean smoothRange;

	public DurabilityRequirement(Item item, int min, int max, boolean smoothRange) {
		this.item = item;
		this.min = min;
		this.max = max;
		this.smoothRange = smoothRange;
	}

	@Override
	public Item getTarget() {
		return item;
	}

	@Override
	public Boolean isMet(ItemStack stack) {
		if (!stack.isDamageable()) return false;
		int durability = stack.getMaxDamage() - stack.getDamage();
		if (smoothRange) {
			int range = max - min;
			return durability >= min && durability <= max && range > 0;
		}
		return durability >= min && durability <= max;
	}

	@Override
	public String getId() {
		return "durability";
	}
}
