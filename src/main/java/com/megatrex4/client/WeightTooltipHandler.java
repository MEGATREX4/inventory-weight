package com.megatrex4.client;

import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.weight.WeightMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class WeightTooltipHandler {
    private WeightTooltipHandler() {}

    public static void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip) {
        if (!InventoryWeightConfig.getClient().showTooltips || stack == null || stack.isEmpty()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        WeightContext weightContext = new WeightContext(client.world, client.player, 0);
        WeightResult unit = InventoryWeightServices.weightService().getWeight(stack, weightContext);
        WeightResult total = unit.multiply(stack.getCount());

        boolean exact = Screen.hasShiftDown();
        String unitText = exact ? WeightMath.exact(unit.weight()) : WeightMath.compact(unit.weight());
        String totalText = exact ? WeightMath.exact(total.weight()) : WeightMath.compact(total.weight());
        String baseText = exact ? WeightMath.exact(unit.baseWeight()) : WeightMath.compact(unit.baseWeight());

        int index = Math.min(1, tooltip.size());
        tooltip.add(index++, Text.translatable("inventoryweight.tooltip.weight", unitText).formatted(Formatting.GRAY));

        if (unit.hasModifier()) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.base_weight", baseText).formatted(Formatting.DARK_GRAY));
        }

        if (stack.getCount() > 1 || unit.hasModifier()) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.total_weight", totalText).formatted(Formatting.GRAY));
        }

        int pockets = InventoryWeightServices.pocketService().getPockets(stack, client.player).orElse(0);
        if (pockets > 0) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.pockets", pockets).formatted(Formatting.BLUE));
        }

        if (!exact && (stack.getCount() > 1 || total.weight() >= 1000.0f || unit.hasModifier())) {
            tooltip.add(index, Text.translatable("inventoryweight.tooltip.shift_hint").formatted(Formatting.DARK_GRAY));
        }
    }
}
