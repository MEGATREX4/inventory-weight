package com.megatrex4.items;
import com.megatrex4.InventoryWeight;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
public class ItemsRegistry {
    public static Item LEATHER_BACKPACK;
    public static Item IRON_BACKPACK;
    public static Item GOLD_BACKPACK;
    public static Item DIAMOND_BACKPACK;
    public static Item OBSIDIAN_BACKPACK;
    public static Item NETHERITE_BACKPACK;

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(InventoryWeight.MOD_ID, name.toLowerCase()), item);
    }
    public static void registerModItems() {
        InventoryWeight.LOGGER.info("Registering Mod Items for " + InventoryWeight.MOD_ID);
        LEATHER_BACKPACK = registerItem("leather_backpack", new BackpackItem(new FabricItemSettings().maxCount(1), 1));
        IRON_BACKPACK = registerItem("iron_backpack", new BackpackItem(new FabricItemSettings().maxCount(1), 2));
        GOLD_BACKPACK = registerItem("gold_backpack", new BackpackItem(new FabricItemSettings().maxCount(1), 3));
        DIAMOND_BACKPACK = registerItem("diamond_backpack", new BackpackItem(new FabricItemSettings().maxCount(1), 4));
        OBSIDIAN_BACKPACK = registerItem("obsidian_backpack", new BackpackItem(new FabricItemSettings().maxCount(1), 5));
        NETHERITE_BACKPACK = registerItem("netherite_backpack", new BackpackItem(new FabricItemSettings().maxCount(1), 6));
    }
}