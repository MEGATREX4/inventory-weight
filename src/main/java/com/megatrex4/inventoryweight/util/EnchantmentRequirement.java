package com.megatrex4.inventoryweight.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class EnchantmentRequirement implements Requirement<Item, ItemStack> {
	private final Item item;
	private final String enchantment;
	private final int level;

	public EnchantmentRequirement(Item item, String enchantment, int level) {
		this.item = item;
		this.enchantment = enchantment;
		this.level = level;
	}

	@Override
	public Item getTarget() {
		return item;
	}

	@Override
	public Boolean isMet(ItemStack stack) {
		return EnchantmentHelper.get(stack).containsKey(Registries.ENCHANTMENT.get(new Identifier(enchantment))) &&
				EnchantmentHelper.get(stack).get(Registries.ENCHANTMENT.get(new Identifier(enchantment))) >= level;
	}

	@Override
	public String getId() {
		return "enchantment";
	}
}
