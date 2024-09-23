package com.megatrex4.items.screens;

import com.megatrex4.items.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class BackpackScreenHandler extends ScreenHandler {
    private final int rows;
    private final DefaultedList<ItemStack> backpackItems;


    public int getRows() {
        return this.rows;
    }

    public BackpackScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(type, syncId);
        this.rows = ((BackpackItem) stack.getItem()).getRows();
        this.backpackItems = DefaultedList.ofSize(rows * 9, ItemStack.EMPTY);
        addBackpackSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }





    private void addBackpackSlots() {
        Inventory backpackInventory = new SimpleInventory(rows * 9); // Create an Inventory with size based on rows
        int slotHeight = 18;
        int gap = 3;

        // Calculate the initial Y position for backpack slots
        int initialSlotY = (slotHeight * 4) - (rows * slotHeight + gap);

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < 9; ++j) {
                // Positioning the slots above the player inventory
                this.addSlot(new Slot(backpackInventory, j + i * 9, 8 + j * 18, initialSlotY + i * slotHeight)); // Corrected position
            }
        }
    }




    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null; // Implement item movement logic if needed
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true; // Adjust if necessary
    }
}
