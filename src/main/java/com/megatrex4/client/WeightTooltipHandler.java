package com.megatrex4.client;

import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.weight.WeightMath;
import com.megatrex4.impl.weight.provider.BackpackWeightProvider;
import com.megatrex4.impl.weight.provider.ShulkerBoxWeightProvider;
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
        boolean container = isWeightContainer(stack);

        int index = Math.min(1, tooltip.size());

        if (container) {
            index = appendContainerTooltip(stack, tooltip, index, exact, unit, total);
        } else {
            index = appendRegularTooltip(stack, tooltip, index, exact, unit, total);
        }

        int pockets = InventoryWeightServices.pocketService().getPockets(stack, client.player).orElse(0);
        if (pockets > 0) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.pockets", pockets).formatted(Formatting.BLUE));
        }

        if (!exact && shouldShowHint(stack, unit, total, container)) {
            tooltip.add(index, Text.translatable("inventoryweight.tooltip.shift_hint").formatted(Formatting.DARK_GRAY));
        }
    }

    private static int appendContainerTooltip(ItemStack stack, List<Text> tooltip, int index, boolean exact, WeightResult unit, WeightResult total) {
        float insideWeight = unit.baseWeight();
        float totalInsideWeight = insideWeight * stack.getCount();

        String insideText = format(insideWeight, exact);
        String unitText = format(unit.weight(), exact);
        String totalText = format(total.weight(), exact);
        String totalInsideText = format(totalInsideWeight, exact);

        tooltip.add(index++, Text.translatable("inventoryweight.tooltip.weight_inside", insideText).formatted(Formatting.DARK_GRAY));
        tooltip.add(index++, Text.translatable("inventoryweight.tooltip.weight", unitText).formatted(Formatting.GRAY));

        if (stack.getCount() > 1) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.total_weight_inside", totalInsideText).formatted(Formatting.DARK_GRAY));
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.total_weight", totalText).formatted(Formatting.GRAY));
        }

        return index;
    }

    private static int appendRegularTooltip(ItemStack stack, List<Text> tooltip, int index, boolean exact, WeightResult unit, WeightResult total) {
        String unitText = format(unit.weight(), exact);
        String totalText = format(total.weight(), exact);
        String baseText = format(unit.baseWeight(), exact);

        tooltip.add(index++, Text.translatable("inventoryweight.tooltip.weight", unitText).formatted(Formatting.GRAY));

        if (unit.hasModifier()) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.base_weight", baseText).formatted(Formatting.DARK_GRAY));
        }

        if (stack.getCount() > 1 || unit.hasModifier()) {
            tooltip.add(index++, Text.translatable("inventoryweight.tooltip.total_weight", totalText).formatted(Formatting.GRAY));
        }

        return index;
    }

    private static boolean isWeightContainer(ItemStack stack) {
        return ShulkerBoxWeightProvider.isShulkerBox(stack) || BackpackWeightProvider.isBackpackStack(stack);
    }

    private static boolean shouldShowHint(ItemStack stack, WeightResult unit, WeightResult total, boolean container) {
        return stack.getCount() > 1
                || total.weight() >= 1000.0f
                || unit.hasModifier()
                || (container && unit.baseWeight() >= 1000.0f);
    }

    private static String format(float weight, boolean exact) {
        return exact ? WeightMath.exact(weight) : WeightMath.compact(weight);
    }
}
