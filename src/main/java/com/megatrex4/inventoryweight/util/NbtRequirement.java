package com.megatrex4.inventoryweight.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

public class NbtRequirement implements Requirement<Item, ItemStack> {

	private final Item item;
	private final String requiredTag;
	private final NbtElement expectedValue;

	public NbtRequirement(Item item, String requiredTag, @Nullable NbtElement expectedValue) {
		this.item = item;
		this.requiredTag = requiredTag;
		this.expectedValue = expectedValue;
	}

	@Override
	public Item getTarget() {
		return item;
	}

	@Override
	public Boolean isMet(ItemStack stack) {
		if (stack.hasNbt()) {
			NbtCompound nbt = stack.getNbt();
			if (nbt != null && nbt.contains(requiredTag)) {
				if (expectedValue == null) {
					return true;
				}
				NbtElement actualValue = nbt.get(requiredTag);
				return expectedValue.equals(actualValue);
			}
		}
		return false;
	}
}
