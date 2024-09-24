package com.megatrex4.items.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.awt.*;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {

    // Add identifiers for background and slot textures
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(MOD_ID, "textures/gui/backpack_background.png");
    private static final Identifier SLOT_TEXTURE = new Identifier(MOD_ID, "textures/gui/backpack_slot.png");

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, title, titleX, titleY, 0x00000, false);
        context.drawText(this.textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 0x00000, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Get the dimensions from the handler
        Dimension dimension = getScreenHandler().getDimension();
        int x = (this.width - dimension.width) / 2;
        int y = (this.height - dimension.height) / 2;

        renderBackgroundTexture(context, new Rectangle(x, y, dimension.width, dimension.height), delta, 0xFFFFFFFF);
        for (Slot slot : getScreenHandler().slots) {
            context.drawTexture(SLOT_TEXTURE, x + slot.x - 1, y + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }
    }

    public void renderBackgroundTexture(DrawContext context, Rectangle bounds, float delta, int color) {
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        RenderSystem.clearColor(red, green, blue, alpha);
        int x = bounds.x, y = bounds.y, width = bounds.width, height = bounds.height;
        int xTextureOffset = 0;
        int yTextureOffset = 66;

        // 9 Patch Texture

        // Four Corners
        context.drawTexture(BACKGROUND_TEXTURE, x, y, 106 + xTextureOffset, 124 + yTextureOffset, 8, 8);
        context.drawTexture(BACKGROUND_TEXTURE, x + width - 8, y, 248 + xTextureOffset, 124 + yTextureOffset, 8, 8);
        context.drawTexture(BACKGROUND_TEXTURE, x, y + height - 8, 106 + xTextureOffset, 182 + yTextureOffset, 8, 8);
        context.drawTexture(BACKGROUND_TEXTURE, x + width - 8, y + height - 8, 248 + xTextureOffset, 182 + yTextureOffset, 8, 8);

        // Sides
        drawTexturedQuad(context, BACKGROUND_TEXTURE, x + 8, x + width - 8, y, y + 8, getZOffset(), (114 + xTextureOffset) / 256f, (248 + xTextureOffset) / 256f, (124 + yTextureOffset) / 256f,
                (132 + yTextureOffset) / 256f);
        drawTexturedQuad(context, BACKGROUND_TEXTURE, x + 8, x + width - 8, y + height - 8, y + height, getZOffset(), (114 + xTextureOffset) / 256f, (248 + xTextureOffset) / 256f,
                (182 + yTextureOffset) / 256f, (190 + yTextureOffset) / 256f);
        drawTexturedQuad(context, BACKGROUND_TEXTURE, x, x + 8, y + 8, y + height - 8, getZOffset(), (106 + xTextureOffset) / 256f, (114 + xTextureOffset) / 256f, (132 + yTextureOffset) / 256f,
                (182 + yTextureOffset) / 256f);
        drawTexturedQuad(context, BACKGROUND_TEXTURE, x + width - 8, x + width, y + 8, y + height - 8, getZOffset(), (248 + xTextureOffset) / 256f, (256 + xTextureOffset) / 256f,
                (132 + yTextureOffset) / 256f, (182 + yTextureOffset) / 256f);

        // Center
        drawTexturedQuad(context, BACKGROUND_TEXTURE, x + 8, x + width - 8, y + 8, y + height - 8, getZOffset(), (114 + xTextureOffset) / 256f, (248 + xTextureOffset) / 256f, (132 + yTextureOffset) / 256f,
                (182 + yTextureOffset) / 256f);
    }

    private int getZOffset() {
        return 0;
    }


    private static void drawTexturedQuad(DrawContext context, Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context); // Draw the screen background
        super.render(context, mouseX, mouseY, delta); // Render the default elements, including the title
        this.drawMouseoverTooltip(context, mouseX, mouseY); // Draw tooltips
    }
}
