package com.megatrex4.hud;

import com.megatrex4.client.InventoryWeightClientHandler;
import com.megatrex4.data.ClientPlayerData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class InventoryWeightHUD implements ClientModInitializer {

    private static final Identifier EMPTY_ICON = new Identifier(MOD_ID, "textures/gui/inventory_empty.png");
    private static final Identifier FILLED_ICON = new Identifier(MOD_ID, "textures/gui/inventory_filled.png");
    private static final Identifier OVERLOAD_ICON = new Identifier(MOD_ID, "textures/gui/inventory_overload.png");

    private static final int ICON_SIZE = 16;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this::renderHUD);
    }

    private void renderHUD(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.debugEnabled) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth - ICON_SIZE - 10;
        int y = screenHeight - ICON_SIZE - 10;

        // Retrieve the cached weight data
        float inventoryWeight = InventoryWeightClientHandler.getInventoryWeight();
        float maxWeight = InventoryWeightClientHandler.getMaxWeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        drawIcon(drawContext, EMPTY_ICON, x, y);

        if (inventoryWeight>= maxWeight) {
            drawIcon(drawContext, OVERLOAD_ICON, x, y);
        } else {
            int filledHeight = (int) ((double) inventoryWeight/ maxWeight * ICON_SIZE);
            drawIcon(drawContext, FILLED_ICON, x, y + (ICON_SIZE - filledHeight), ICON_SIZE, filledHeight);
        }

        RenderSystem.disableBlend();
    }

    private void drawIcon(DrawContext drawContext, Identifier icon, int x, int y) {
        drawIcon(drawContext, icon, x, y, ICON_SIZE, ICON_SIZE);
    }

    private void drawIcon(DrawContext drawContext, Identifier icon, int x, int y, int width, int height) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getTextureManager().bindTexture(icon);
        MatrixStack matrixStack = drawContext.getMatrices();

        matrixStack.push();
        drawContext.drawTexture(icon, x, y, 0, 0, width, height, width, height);
        matrixStack.pop();
    }
}
