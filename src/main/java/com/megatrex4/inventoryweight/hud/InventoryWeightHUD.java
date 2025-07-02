package com.megatrex4.inventoryweight.hud;

import com.megatrex4.inventoryweight.components.InventoryWeightComponents;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import com.megatrex4.inventoryweight.util.ArmorPockets;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static com.megatrex4.inventoryweight.InventoryWeight.MOD_ID;

public class InventoryWeightHUD  {

    private static final Identifier EMPTY_ICON = new Identifier(MOD_ID, "textures/gui/inventory_empty.png");
    private static final Identifier STRENGTH_ICON = new Identifier(MOD_ID, "textures/gui/inventory_strength.png");

    private static final int ICON_SIZE = 16;

    public static void renderHUD(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.debugEnabled) {
            return;
        }
        if (client.player.isCreative() || client.player.isSpectator()) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x, y;

        switch (InventoryWeightConfig.CLIENT.iconSettings.weightHudPosition) {
            case TOP_RIGHT:
                x = screenWidth - ICON_SIZE - 10;
                y = 10;
                break;
            case BOTTOM_RIGHT:
                x = screenWidth - ICON_SIZE - 10;
                y = screenHeight - ICON_SIZE - 10;
                break;
            case BOTTOM_LEFT:
                x = 10;
                y = screenHeight - ICON_SIZE - 10;
                break;
            case HOTBAR_LEFT:
                x = screenWidth / 2 - 91 - ICON_SIZE - 10;
                y = screenHeight - ICON_SIZE - 4;
                break;
            case HOTBAR_RIGHT:
                x = screenWidth / 2 + 91 + 10;
                y = screenHeight - ICON_SIZE - 4;
                break;
            case CUSTOM:
                x = (screenWidth * InventoryWeightConfig.CLIENT.iconSettings.xOffset) - ICON_SIZE / 2;
                y = (screenHeight * InventoryWeightConfig.CLIENT.iconSettings.yOffset) - ICON_SIZE / 2;
                break;
            default:
                x = 10;
                y = 10;
                break;
        }

        int inventoryWeight = client.player.getComponent(InventoryWeightComponents.WEIGHT).getWeight();

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, EMPTY_ICON);
        RenderSystem.defaultBlendFunc();

        drawIcon(drawContext, EMPTY_ICON, x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);

        if (getIcon(client.player, inventoryWeight) != null) {
            drawIcon(drawContext, getIcon(client.player, inventoryWeight), x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);
        }

        if (client.player.hasStatusEffect(StatusEffects.STRENGTH) || client.player.hasStatusEffect(StatusEffects.HASTE)) {
            drawIcon(drawContext, STRENGTH_ICON, x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);
        }

        RenderSystem.disableBlend();
    }

    private static @Nullable Identifier getIcon(PlayerEntity player, float inventoryWeight) {
        int maxWeight = InventoryWeightConfig.SERVER.maxWeight + ArmorPockets.calculateArmorCapacity(player);
        if (inventoryWeight >= maxWeight) {
            return Identifier.of(MOD_ID, "textures/gui/inventory_overload.png");
        } else if (inventoryWeight > 0) {
            int filledIndex = (int) Math.ceil((inventoryWeight / maxWeight) * 12);
            filledIndex = Math.max(1, Math.min(filledIndex, 12));
            return new Identifier(MOD_ID, "textures/gui/inventory_filled/inventory_filled_" + filledIndex + ".png");
        }
        return null;
    }

    private static void drawIcon(DrawContext drawContext, Identifier icon, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
        drawContext.drawTexture(icon, x, y, width, height, u, v, uWidth, vHeight, 16, 16);
    }
}