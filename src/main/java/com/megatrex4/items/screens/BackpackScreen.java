package com.megatrex4.items.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {

    private static final int SLOT_HEIGHT = 18;
    private static final int GAP = 3;

    // Add identifiers for background and slot textures
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(MOD_ID, "textures/gui/backpack_background.png");
    private static final Identifier SLOT_TEXTURE = new Identifier(MOD_ID, "textures/gui/backpack_slot.png");

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int titleY = 0;

        int rows = handler.getRows();

        if (rows < 3){
            titleY+=18+1;
        }
        if (rows < 2){
            titleY+=18+1;
        }
        if (rows > 3){
            for (int i = 3; i < rows; i++ ){
                titleY-=18;
            }
        }

        context.drawText(this.textRenderer, this.title, this.playerInventoryTitleX, titleY, 4210752, false);

        // Optionally, draw player inventory title
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Fixed dimensions of the texture (150x66)
        int backgroundWidth = 150;
        int backgroundHeight = 66;

        // Calculate center position for the background texture
        int textureX = (this.width - backgroundWidth) / 2; // Center horizontally
        int textureY = (this.height - backgroundHeight) / 2; // Center vertically

        // Draw the background texture at the center without scaling
        context.drawTexture(BACKGROUND_TEXTURE, textureX, textureY, 0, 0, backgroundWidth, backgroundHeight);

        // Draw the slots without shifting them based on the background size
        for (Slot slot : getScreenHandler().slots) {
            context.drawTexture(SLOT_TEXTURE, x + slot.x - 1, y + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context); // Draw the screen background
        super.render(context, mouseX, mouseY, delta); // Render the default elements, including the title
        this.drawMouseoverTooltip(context, mouseX, mouseY); // Draw tooltips
    }
}
