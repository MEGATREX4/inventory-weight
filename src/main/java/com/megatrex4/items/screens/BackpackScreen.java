package com.megatrex4.items.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {
    // Add identifiers for background and slot textures
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(MOD_ID, "textures/gui/backpack_background");
    private static final Identifier SLOT_TEXTURE = new Identifier(MOD_ID, "textures/gui/backpack_slot");

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(BACKGROUND_TEXTURE, 0, 0, 0, 0, width, height);

        // Cast handler to BackpackScreenHandler to access getRows()
        BackpackScreenHandler backpackHandler = (BackpackScreenHandler) handler;
        for (int i = 0; i < backpackHandler.getRows(); ++i) {
            for (int j = 0; j < 9; ++j) {
                context.drawTexture(SLOT_TEXTURE, 8 + j * 18, 18 + i * 18, 0, 0, 16, 16);
            }
        }

        drawPlayerInventory(context);
    }



    private void drawPlayerInventory(DrawContext context) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                context.drawTexture(SLOT_TEXTURE, 8 + j * 18, 0, 0, 84 + i * 18, 16, 16);
            }
        }
        for (int i = 0; i < 9; ++i) {
            context.drawTexture(SLOT_TEXTURE, 8 + i * 18, 142,0 , 0, 16, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context); // Draw the screen background
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY); // Draw tooltips
    }

}
