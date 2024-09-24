package com.megatrex4.items.screens;

import com.megatrex4.items.BackpackItem;
import com.megatrex4.util.BackpackInventoryUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BackpackScreenHandler extends ScreenHandler {
    private final int padding = 8;
    private final int titleSpace = 10;
    private final int rows;
    private final ItemStack backpackStack;

    // Constructor
    public BackpackScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(type, syncId);
        this.rows = ((BackpackItem) stack.getItem()).getRows();
        this.backpackStack = stack;

        setupContainer(playerInventory, backpackStack);
    }

    public int getRows() {
        return this.rows;
    }

    private void setupContainer(PlayerInventory playerInventory, ItemStack backpackStack) {
        Dimension dimension = getDimension();
        int rowWidth = 9;
        int numberOfRows = rows;

        NbtList tag = backpackStack.getOrCreateNbt().getList("Inventory", NbtElement.COMPOUND_TYPE);
        BackpackInventory inventory = new BackpackInventory(rowWidth * numberOfRows) {
            @Override
            public void markDirty() {
                backpackStack.getOrCreateNbt().put("Inventory", BackpackInventoryUtil.toTag(this));
                super.markDirty();
            }
        };

        BackpackInventoryUtil.fromTag(tag, inventory);

        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < rowWidth; x++) {
                Point backpackSlotPosition = getBackpackSlotPosition(dimension, x, y);
                addSlot(new BackpackLockedSlot(inventory, y * rowWidth + x, backpackSlotPosition.x + 1, backpackSlotPosition.y + 1));
            }
        }

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, y);
                this.addSlot(new BackpackLockedSlot(playerInventory, x + y * 9 + 9, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
            }
        }

        for (int x = 0; x < 9; ++x) {
            Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, 3);
            this.addSlot(new BackpackLockedSlot(playerInventory, x, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
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
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private class BackpackLockedSlot extends Slot {

        public BackpackLockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return stackMovementIsAllowed(getStack());
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stackMovementIsAllowed(stack);
        }

        private boolean stackMovementIsAllowed(ItemStack stack) {
            return !(stack.getItem() instanceof BackpackItem) && stack != backpackStack;
        }
    }
    public static class BackpackInventory extends SimpleInventory {

        public BackpackInventory(int slots) {
            super(slots);
        }
    }
}
