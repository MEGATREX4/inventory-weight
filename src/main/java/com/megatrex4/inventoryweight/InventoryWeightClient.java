package com.megatrex4.inventoryweight;

import com.megatrex4.inventoryweight.hud.InventoryWeightHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class InventoryWeightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((context, tickDelta) -> InventoryWeightHUD.renderHUD(context));
		ItemTooltipCallback.EVENT.register(this::addWeightTooltip);
	}

	private void addWeightTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> texts) {

	}
}
