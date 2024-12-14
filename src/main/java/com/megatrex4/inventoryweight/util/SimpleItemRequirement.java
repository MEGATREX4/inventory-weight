package com.megatrex4.inventoryweight.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SimpleItemRequirement implements Requirement<Item, ItemStack> {
	private final Item item;

	public SimpleItemRequirement(Item item) {
		this.item = item;
	}

	@Override
	public Item getTarget() {
		return item;
	}

	@Override
	public Boolean isMet(ItemStack stack) {
		return stack.getItem() == getTarget();
	}

	@Override
	public String getId() {
		return "";
	}
}