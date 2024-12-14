package com.megatrex4.inventoryweight;

import com.megatrex4.inventoryweight.hud.InventoryWeightHUD;
import com.megatrex4.inventoryweight.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class InventoryWeightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((context, tickDelta) -> InventoryWeightHUD.renderHUD(context));
		ItemTooltipCallback.EVENT.register(this::addWeightTooltip);
	}

	private void addWeightTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> tooltip) {
		int weight = Util.getWeight(itemStack);

		int index = 1;

		String weightWithModifier = formatWeight(weight);

		if (Screen.hasShiftDown()) {
			tooltip.add(index++, Text.translatable("inventoryweight.tooltip.weight", weight).formatted(Formatting.GRAY));
			if (itemStack.getCount() > 1 || Util.isShulker(itemStack)) {
				tooltip.add(index++, Text.translatable("inventoryweight.tooltip.totalweight", weight).formatted(Formatting.GRAY));
			}
		} else {
			tooltip.add(index++, Text.translatable("inventoryweight.tooltip.weight", weightWithModifier).formatted(Formatting.GRAY));
			if (itemStack.getCount() > 1 || Util.isShulker(itemStack)) {
				tooltip.add(index++, Text.translatable("inventoryweight.tooltip.totalweight", weightWithModifier).formatted(Formatting.GRAY));
			}

			if (itemStack.getCount() > 1 || weight > 100) {
				Text tooltipHint = Text.translatable("inventoryweight.tooltip.hint");
				tooltip.add(index, Text.literal(tooltipHint.getString().replace("{0}", Formatting.YELLOW.toString())
						.replace("{1}", Formatting.RESET.toString())).formatted(Formatting.GRAY));
			}
		}
	}

	private static String formatWeight(float weight) {
		String suffixK = Text.translatable("inventoryweight.tooltip.k").getString();
		String suffixM = Text.translatable("inventoryweight.tooltip.m").getString();
		String suffixB = Text.translatable("inventoryweight.tooltip.b").getString();

		if (weight >= 1_000_000_000) {
			return String.format("%.1f" + suffixB, weight / 1_000_000_000);
		} else if (weight >= 1_000_000) {
			return String.format("%.1f" + suffixM, weight / 1_000_000);
		} else if (weight >= 1_000) {
			return String.format("%.1f" + suffixK, weight / 1_000);
		} else {
			return String.valueOf(weight);
		}
	}
}
