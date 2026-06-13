package com.megatrex4.client;

import com.megatrex4.component.PlayerWeightComponentRegistry;
import com.megatrex4.config.HudPosition;
import com.megatrex4.config.HudStyle;
import com.megatrex4.config.HudTextMode;
import com.megatrex4.config.HudTextPosition;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.weight.WeightMath;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;

import java.util.Locale;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightHud {
    private static final int DEFAULT_BAR_WIDTH = 82;
    private static final int DEFAULT_BAR_HEIGHT = 10;
    private static final int DEFAULT_ICON_SIZE = 16;
    private static final int TEXTURE_SIZE = 16;
    private static final int EDGE_MARGIN = 2;
    private static final int TEXT_GAP = 3;

    private static final Identifier EMPTY_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/inventory_empty.png");

    private static final Identifier OVERLOAD_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/inventory_overload.png");

    private static final Identifier STRENGTH_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/inventory_strength.png");

    private InventoryWeightHud() {}

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.options.hideGui
                || minecraft.getDebugOverlay().showDebugScreen()) {
            return;
        }

        var component = PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(minecraft.player);

        if (component.isEmpty()) {
            return;
        }

        float current = component.get().getCurrentInventoryWeight();
        float max = component.get().getMaxWeight();

        if (max <= 0.0f) {
            return;
        }

        InventoryWeightConfig.Client config = InventoryWeightConfig.getClient();
        HudStyle style = config.hudStyle == null ? HudStyle.SPRITE : config.hudStyle;

        int elementWidth = style == HudStyle.SPRITE
                ? safePositive(config.spriteSize, DEFAULT_ICON_SIZE)
                : safePositive(config.barWidth, DEFAULT_BAR_WIDTH);

        int elementHeight = style == HudStyle.SPRITE
                ? safePositive(config.spriteSize, DEFAULT_ICON_SIZE)
                : safePositive(config.barHeight, DEFAULT_BAR_HEIGHT);

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int[] xy = position(config, screenWidth, screenHeight, elementWidth, elementHeight, style);
        int x = xy[0];
        int y = xy[1];

        if (style == HudStyle.SPRITE) {
            renderSpriteHud(
                    graphics,
                    minecraft,
                    x,
                    y,
                    elementWidth,
                    elementHeight,
                    current,
                    max,
                    component.get().isOverloaded()
            );
        } else {
            renderBarHud(
                    graphics,
                    x,
                    y,
                    elementWidth,
                    elementHeight,
                    current,
                    max,
                    component.get().isOverloaded()
            );
        }

        renderHudText(
                graphics,
                minecraft.font,
                config,
                x,
                y,
                elementWidth,
                elementHeight,
                screenWidth,
                screenHeight,
                current,
                max
        );
    }

    private static void renderSpriteHud(
            GuiGraphicsExtractor graphics,
            Minecraft minecraft,
            int x,
            int y,
            int width,
            int height,
            float current,
            float max,
            boolean overloaded
    ) {
        drawIcon(graphics, EMPTY_ICON, x, y, width, height);

        if (overloaded || current >= max) {
            drawIcon(graphics, OVERLOAD_ICON, x, y, width, height);
        } else if (current > 0.0f) {
            drawIcon(graphics, getFilledIcon(current, max), x, y, width, height);
        }

        if (minecraft.player != null
                && (minecraft.player.hasEffect(MobEffects.STRENGTH)
                || minecraft.player.hasEffect(MobEffects.HASTE))) {
            drawIcon(graphics, STRENGTH_ICON, x, y, width, height);
        }
    }

    private static void renderBarHud(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int width,
            int height,
            float current,
            float max,
            boolean overloaded
    ) {
        float ratio = Mth.clamp(current / max, 0.0f, 1.0f);
        int fill = Math.round(width * ratio);
        int color = color(current, max);

        graphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, 0x99000000);
        graphics.fill(x, y, x + width, y + height, 0xFF2A2A2A);
        graphics.fill(x, y, x + fill, y + height, color);

        if (overloaded || current >= max) {
            graphics.outline(x - 2, y - 2, width + 4, height + 4, 0xFFFF3030);
        } else {
            graphics.outline(x - 2, y - 2, width + 4, height + 4, 0xAAFFFFFF);
        }
    }

    private static void renderHudText(
            GuiGraphicsExtractor graphics,
            Font font,
            InventoryWeightConfig.Client config,
            int elementX,
            int elementY,
            int elementWidth,
            int elementHeight,
            int screenWidth,
            int screenHeight,
            float current,
            float max
    ) {
        String text = hudText(config, current, max);

        if (text.isEmpty()) {
            return;
        }

        int textWidth = font.width(text);
        int textHeight = font.lineHeight;

        int[] xy = textPosition(
                config,
                elementX,
                elementY,
                elementWidth,
                elementHeight,
                textWidth,
                textHeight,
                screenWidth,
                screenHeight
        );

        int color = 0xFF000000 | (config.hudTextColor & 0xFFFFFF);

        graphics.text(font, text, xy[0], xy[1], color, config.hudTextShadow);
    }

    private static String hudText(
            InventoryWeightConfig.Client config,
            float current,
            float max
    ) {
        HudTextMode mode = config.hudTextMode == null
                ? HudTextMode.CURRENT_MAX
                : config.hudTextMode;

        float percent = max <= 0.0f ? 0.0f : (current / max) * 100.0f;

        return switch (mode) {
            case NONE -> "";
            case CURRENT -> WeightMath.compact(current);
            case MAX -> WeightMath.compact(max);
            case PERCENT -> String.format(Locale.ROOT, "%.0f%%", percent);
            case REMAINING -> WeightMath.compact(Math.max(0.0f, max - current));
            case CURRENT_MAX_PERCENT -> WeightMath.compact(current)
                    + "/"
                    + WeightMath.compact(max)
                    + " ("
                    + String.format(Locale.ROOT, "%.0f%%", percent)
                    + ")";
            case CURRENT_MAX -> WeightMath.compact(current)
                    + "/"
                    + WeightMath.compact(max);
        };
    }

    private static int[] textPosition(
            InventoryWeightConfig.Client config,
            int elementX,
            int elementY,
            int elementWidth,
            int elementHeight,
            int textWidth,
            int textHeight,
            int screenWidth,
            int screenHeight
    ) {
        HudTextPosition requested = config.hudTextPosition == null
                ? HudTextPosition.BELOW
                : config.hudTextPosition;

        int x = elementX + (elementWidth - textWidth) / 2;
        int y = elementY + elementHeight + TEXT_GAP;

        switch (requested) {
            case ABOVE -> {
                x = elementX + (elementWidth - textWidth) / 2;
                y = elementY - textHeight - TEXT_GAP;
            }
            case LEFT -> {
                x = elementX - textWidth - 4;
                y = elementY + (elementHeight - textHeight) / 2;
            }
            case RIGHT -> {
                x = elementX + elementWidth + 4;
                y = elementY + (elementHeight - textHeight) / 2;
            }
            case INSIDE -> {
                x = elementX + (elementWidth - textWidth) / 2;
                y = elementY + (elementHeight - textHeight) / 2;
            }
            case CUSTOM -> {
                x = elementX;
                y = elementY;
            }
            case BELOW -> {
                x = elementX + (elementWidth - textWidth) / 2;
                y = elementY + elementHeight + TEXT_GAP;
            }
        }

        if (config.keepHudTextOnScreen) {
            if (requested == HudTextPosition.BELOW && y + textHeight > screenHeight) {
                y = elementY - textHeight - TEXT_GAP;
            } else if (requested == HudTextPosition.ABOVE && y < 0) {
                y = elementY + elementHeight + TEXT_GAP;
            } else if (requested == HudTextPosition.LEFT && x < 0) {
                x = elementX + elementWidth + 4;
            } else if (requested == HudTextPosition.RIGHT && x + textWidth > screenWidth) {
                x = elementX - textWidth - 4;
            }
        }

        x += config.hudTextXOffset;
        y += config.hudTextYOffset;

        if (config.keepHudTextOnScreen) {
            x = clampToScreen(x, textWidth, screenWidth, EDGE_MARGIN);
            y = clampToScreen(y, textHeight, screenHeight, EDGE_MARGIN);
        }

        return new int[]{x, y};
    }

    private static int[] position(
            InventoryWeightConfig.Client config,
            int screenWidth,
            int screenHeight,
            int elementWidth,
            int elementHeight,
            HudStyle style
    ) {
        HudPosition hudPosition = config.hudPosition == null
                ? HudPosition.BOTTOM_RIGHT
                : config.hudPosition;

        int bottomMargin = style == HudStyle.SPRITE ? 10 : 24;

        int x;
        int y;

        switch (hudPosition) {
            case TOP_RIGHT -> {
                x = screenWidth - elementWidth - 10;
                y = 10;
            }
            case CENTER_LEFT -> {
                x = 10;
                y = screenHeight / 2 - elementHeight / 2;
            }
            case CENTER_RIGHT -> {
                x = screenWidth - elementWidth - 10;
                y = screenHeight / 2 - elementHeight / 2;
            }
            case BOTTOM_LEFT -> {
                x = 10;
                y = screenHeight - elementHeight - bottomMargin;
            }
            case HOTBAR_LEFT -> {
                x = screenWidth / 2 - 91 - elementWidth - 10;
                y = screenHeight - elementHeight - 4;
            }
            case HOTBAR_RIGHT -> {
                x = screenWidth / 2 + 91 + 10;
                y = screenHeight - elementHeight - 4;
            }
            case CENTER_HOTBAR -> {
                x = screenWidth / 2 - elementWidth / 2;
                y = screenHeight - elementHeight - 35;
            }
            case CUSTOM -> {
                x = (int) (screenWidth * config.xOffset) - elementWidth / 2;
                y = (int) (screenHeight * config.yOffset) - elementHeight / 2;
            }
            case BOTTOM_RIGHT -> {
                x = screenWidth - elementWidth - 10;
                y = screenHeight - elementHeight - bottomMargin;
            }
            case TOP_LEFT -> {
                x = 10;
                y = 10;
            }
            default -> {
                x = screenWidth - elementWidth - 10;
                y = screenHeight - elementHeight - bottomMargin;
            }
        }

        return new int[]{x, y};
    }

    private static Identifier getFilledIcon(float current, float max) {
        int filledIndex = max <= 0.0f
                ? 1
                : (int) Math.ceil((current / max) * 12.0f);

        filledIndex = Math.max(1, Math.min(filledIndex, 12));

        return Identifier.fromNamespaceAndPath(
                MOD_ID,
                "textures/gui/inventory_filled/inventory_filled_" + filledIndex + ".png"
        );
    }

    private static void drawIcon(
            GuiGraphicsExtractor graphics,
            Identifier icon,
            int x,
            int y,
            int width,
            int height
    ) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                icon,
                x,
                y,
                0,
                0,
                width,
                height,
                TEXTURE_SIZE,
                TEXTURE_SIZE
        );
    }

    private static int color(float current, float max) {
        float percent = max <= 0.0f ? 0.0f : (current / max) * 100.0f;

        if (percent >= 100.0f) {
            return 0xFFFF3030;
        }

        if (percent >= 80.0f) {
            return 0xFFFFAA00;
        }

        if (percent >= 50.0f) {
            return 0xFFFFFF55;
        }

        return 0xFF55FF55;
    }

    private static int safePositive(int value, int fallback) {
        return value > 0 ? value : fallback;
    }

    private static int clampToScreen(
            int value,
            int size,
            int screenSize,
            int margin
    ) {
        int min = Math.max(0, margin);
        int max = Math.max(min, screenSize - size - margin);

        return Math.max(min, Math.min(value, max));
    }
}