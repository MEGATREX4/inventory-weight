package com.megatrex4.client;

import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.weight.WeightMath;
import com.megatrex4.impl.weight.provider.BackpackWeightProvider;
import com.megatrex4.impl.weight.provider.ShulkerBoxWeightProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class WeightTooltipHandler {

    private WeightTooltipHandler() {}

    public static void appendTooltip(
            ItemStack stack,
            Item.TooltipContext context,
            TooltipFlag type,
            List<Component> tooltip
    ) {
        if (!InventoryWeightConfig.getClient().showTooltips || stack == null || stack.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;

        if (level == null || player == null) {
            return;
        }

        WeightContext weightContext = new WeightContext(level, player, 0);

        WeightResult unit = InventoryWeightServices.weightService().getWeight(stack, weightContext);
        WeightResult total = unit.multiply(stack.getCount());

        boolean exact = isShiftDown();
        boolean container = isWeightContainer(stack);

        int index = Math.min(1, tooltip.size());

        if (container) {
            index = appendContainerTooltip(stack, tooltip, index, exact, unit, total);
        } else {
            index = appendRegularTooltip(stack, tooltip, index, exact, unit, total);
        }

        int pockets = InventoryWeightServices.pocketService().getPockets(stack, player).orElse(0);

        if (pockets > 0) {
            tooltip.add(index++, Component.translatable("inventoryweight.tooltip.pockets", pockets)
                    .withStyle(ChatFormatting.BLUE));
        }

        if (!exact && shouldShowHint(stack, unit, total, container)) {
            tooltip.add(index, Component.translatable("inventoryweight.tooltip.shift_hint")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static int appendContainerTooltip(
            ItemStack stack,
            List<Component> tooltip,
            int index,
            boolean exact,
            WeightResult unit,
            WeightResult total
    ) {
        float insideWeight = unit.baseWeight();
        float totalInsideWeight = insideWeight * stack.getCount();

        String insideText = format(insideWeight, exact);
        String unitText = format(unit.weight(), exact);
        String totalText = format(total.weight(), exact);
        String totalInsideText = format(totalInsideWeight, exact);

        tooltip.add(index++, Component.translatable("inventoryweight.tooltip.weight_inside", insideText)
                .withStyle(ChatFormatting.DARK_GRAY));

        tooltip.add(index++, Component.translatable("inventoryweight.tooltip.weight", unitText)
                .withStyle(ChatFormatting.GRAY));

        if (stack.getCount() > 1) {
            tooltip.add(index++, Component.translatable("inventoryweight.tooltip.total_weight_inside", totalInsideText)
                    .withStyle(ChatFormatting.DARK_GRAY));

            tooltip.add(index++, Component.translatable("inventoryweight.tooltip.total_weight", totalText)
                    .withStyle(ChatFormatting.GRAY));
        }

        return index;
    }

    private static int appendRegularTooltip(
            ItemStack stack,
            List<Component> tooltip,
            int index,
            boolean exact,
            WeightResult unit,
            WeightResult total
    ) {
        String unitText = format(unit.weight(), exact);
        String totalText = format(total.weight(), exact);
        String baseText = format(unit.baseWeight(), exact);

        tooltip.add(index++, Component.translatable("inventoryweight.tooltip.weight", unitText)
                .withStyle(ChatFormatting.GRAY));

        if (unit.hasModifier()) {
            tooltip.add(index++, Component.translatable("inventoryweight.tooltip.base_weight", baseText)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        if (stack.getCount() > 1 || unit.hasModifier()) {
            tooltip.add(index++, Component.translatable("inventoryweight.tooltip.total_weight", totalText)
                    .withStyle(ChatFormatting.GRAY));
        }

        return index;
    }

    private static boolean isWeightContainer(ItemStack stack) {
        return ShulkerBoxWeightProvider.isShulkerBox(stack)
                || BackpackWeightProvider.isBackpackStack(stack);
    }

    private static boolean shouldShowHint(ItemStack stack, WeightResult unit, WeightResult total, boolean container) {
        return stack.getCount() > 1
                || total.weight() >= 1000.0f
                || unit.hasModifier()
                || container && unit.baseWeight() >= 1000.0f;
    }

    private static String format(float weight, boolean exact) {
        return exact ? WeightMath.exact(weight) : WeightMath.compact(weight);
    }

    private static boolean isShiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }
}