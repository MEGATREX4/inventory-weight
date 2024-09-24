package com.megatrex4.items.screens;

import com.megatrex4.items.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BackpackScreenHandler extends ScreenHandler {
    private final int padding = 8; // Padding around the slots
    private final int titleSpace = 10; // Space for the title
    private final int rows;
    private final DefaultedList<ItemStack> backpackItems;
    private final ItemStack backpackStack;

    // Constructor
    public BackpackScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(type, syncId);
        this.rows = ((BackpackItem) stack.getItem()).getRows();
        this.backpackStack = stack;
        this.backpackItems = DefaultedList.ofSize(rows * 9, ItemStack.EMPTY);

        addBackpackSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public int getRows() {
        return this.rows;
    }

    // Method to add backpack slots
    private void addBackpackSlots() {
        Dimension dimension = getDimension(); // Use Dimension for proper screen layout
        Inventory backpackInventory = new SimpleInventory(rows * 9);

        // Add the backpack slots dynamically based on rows and columns
        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < 9; ++x) {
                Point backpackSlotPosition = getBackpackSlotPosition(dimension, x, y);
                this.addSlot(new Slot(backpackInventory, x + y * 9, backpackSlotPosition.x + 1, backpackSlotPosition.y + 1));
            }
        }
    }

    // Method to add player inventory slots
    private void addPlayerInventory(PlayerInventory playerInventory) {
        Dimension dimension = getDimension(); // Use Dimension for proper screen layout
        for (int y = 0; y < 3; ++y) { // Player inventory rows (3 rows)
            for (int x = 0; x < 9; ++x) { // 9 columns for each row
                Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, y);
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
            }
        }
    }

    // Method to add hotbar slots
    private void addPlayerHotbar(PlayerInventory playerInventory) {
        Dimension dimension = getDimension(); // Use Dimension for proper screen layout
        for (int x = 0; x < 9; ++x) { // Hotbar always has 1 row, 9 slots
            Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, 3);
            this.addSlot(new Slot(playerInventory, x, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
        }
    }

    // Get dimensions based on the number of rows and columns in the backpack
    public Dimension getDimension() {
            return new Dimension(padding * 2 + 9 * 18, padding * 2 + titleSpace * 2 + 8 + (rows + 4) * 18);
    }

    // Calculate backpack slot position
    public Point getBackpackSlotPosition(Dimension dimension, int x, int y) {
        return new Point((int) (dimension.getWidth() / 2 - 9 * 9 + x * 18), (int) (padding + titleSpace + y * 18));
    }

    // Calculate player inventory slot position
    public Point getPlayerInvSlotPosition(Dimension dimension, int x, int y) {
        return new Point((int) (dimension.getWidth() / 2 - 9 * 9 + x * 18),
                (int) (dimension.getHeight() - padding - 4 * 18 - 3 + y * 18 + (y == 3 ? 4 : 0)));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY; // Implement item movement logic if needed
    }
}
