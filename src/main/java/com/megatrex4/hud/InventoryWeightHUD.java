package com.megatrex4.hud;

import com.megatrex4.client.InventoryWeightClientHandler;
import com.megatrex4.config.ItemWeightsConfigClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class InventoryWeightHUD implements ClientModInitializer {

    private static final Identifier EMPTY_ICON = new Identifier(MOD_ID, "textures/gui/inventory_empty.png");
    private static final Identifier OVERLOAD_ICON = new Identifier(MOD_ID, "textures/gui/inventory_overload.png");

    private static final int ICON_SIZE = 16;




    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this::renderHUD);
    }

    private static final Identifier STRENGTH_ICON = new Identifier(MOD_ID, "textures/gui/inventory_strength.png");

    private void renderHUD(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.debugEnabled) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x = 0, y = 0;

        // Ensure you have defined these constants or use the String values directly
        switch (ItemWeightsConfigClient.hudPosition) {
            case "TOP_LEFT":
                x = 10;
                y = 10;
                break;
            case "TOP_RIGHT":
                x = screenWidth - ICON_SIZE - 10;
                y = 10;
                break;
            case "CENTER_LEFT":
                x = 10;
                y = screenHeight / 2 - ICON_SIZE / 2;
                break;
            case "CENTER_RIGHT":
                x = screenWidth - ICON_SIZE - 10;
                y = screenHeight / 2 - ICON_SIZE / 2;
                break;
            case "BOTTOM_LEFT":
                x = 10;
                y = screenHeight - ICON_SIZE - 10;
                break;
            case "BOTTOM_RIGHT":
                x = screenWidth - ICON_SIZE - 10;
                y = screenHeight - ICON_SIZE - 10;
                break;
            case "HOTBAR_LEFT":
                x = screenWidth / 2 - 91 - ICON_SIZE - 10;
                y = screenHeight - ICON_SIZE - 4;
                break;
            case "HOTBAR_RIGHT":
                x = screenWidth / 2 + 91 + 10;
                y = screenHeight - ICON_SIZE - 4;
                break;
            case "CENTER_HOTBAR":
                x = screenWidth / 2 - ICON_SIZE / 2;
                y = screenHeight - ICON_SIZE - 35;
                break;
            case "CUSTOM":
                x = (int) (screenWidth * ItemWeightsConfigClient.xOffset) - ICON_SIZE / 2;
                y = (int) (screenHeight * ItemWeightsConfigClient.yOffset) - ICON_SIZE / 2;
                break;
            default:
                x = 10;
                y = 10;
                break;
        }

        // Retrieve the cached weight data
        float inventoryWeight = InventoryWeightClientHandler.getInventoryWeight();
        float maxWeight = InventoryWeightClientHandler.getMaxWeight();

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, EMPTY_ICON); // Bind the correct texture
        RenderSystem.defaultBlendFunc();

        // Draw the empty icon (full icon as background)
        drawIcon(drawContext, EMPTY_ICON, x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);

        // Draw the overload icon if overloaded
        if (inventoryWeight >= maxWeight) {
            // Draw overload icon if the weight exceeds or equals the max weight
            drawIcon(drawContext, OVERLOAD_ICON, x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);
        } else if (inventoryWeight > 0) { // Only show filled if the weight is greater than 0
            // Determine which filled icon to use (from 1 to 12)
            int filledIndex = (int) Math.ceil((inventoryWeight / maxWeight) * 12);
            filledIndex = Math.max(1, Math.min(filledIndex, 12)); // Ensure the index is between 1 and 12

            // Dynamically set the filled icon based on the weight
            Identifier filledIcon = new Identifier(MOD_ID, "textures/gui/inventory_filled/inventory_filled_" + filledIndex + ".png");

            // Draw the corresponding filled icon
            drawIcon(drawContext, filledIcon, x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);
        }

        if (client.player.hasStatusEffect(StatusEffects.STRENGTH) || client.player.hasStatusEffect(StatusEffects.HASTE)) {
            drawIcon(drawContext, STRENGTH_ICON, x, y, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16);
        }

        RenderSystem.disableBlend();
    }

    // Helper method to draw the icon texture
    private void drawIcon(DrawContext drawContext, Identifier icon, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
        drawContext.drawTexture(icon, x, y, width, height, u, v, uWidth, vHeight, 16, 16);
    }
}
