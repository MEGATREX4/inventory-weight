package com.megatrex4.items.screens;

import com.megatrex4.InventoryWeight;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlersRegistry {
    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER_TYPE =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(InventoryWeight.MOD_ID, "backpack_screen_handler"),
                    new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> {
                        ItemStack stack = buf.readItemStack(); // Read the item stack from the buffer
                        return new BackpackScreenHandler(ScreenHandlersRegistry.BACKPACK_SCREEN_HANDLER_TYPE, syncId, playerInventory, stack); // Pass the ScreenHandlerType here
                    }));

    public static void registerModScreenHandlers() {
        InventoryWeight.LOGGER.info("Registering Mod Screen Handlers for " + InventoryWeight.MOD_ID);
    }
}