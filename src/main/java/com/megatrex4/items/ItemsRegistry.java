package com.megatrex4.items;
import com.megatrex4.InventoryWeight;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
public class ItemsRegistry {
    public static Item LEATHER_BACKPACK;
    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(InventoryWeight.MOD_ID, name.toLowerCase()), item);
    }
    public static void registerModItems() {
        InventoryWeight.LOGGER.info("Registering Mod Items for " + InventoryWeight.MOD_ID);
        LEATHER_BACKPACK = registerItem("leather_backpack", new BackpackItem(new FabricItemSettings(), 1));
    }
}