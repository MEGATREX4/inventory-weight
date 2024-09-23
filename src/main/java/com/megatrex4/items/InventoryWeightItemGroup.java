package com.megatrex4.items;
import com.megatrex4.InventoryWeight;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
public class InventoryWeightItemGroup {
    public static final ItemGroup INVENTORYWEIGHT = Registry.register(Registries.ITEM_GROUP,
            new Identifier(InventoryWeight.MOD_ID, "inventoryweight"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.inventoryweight.title"))
                    .icon(() -> new ItemStack(Items.ANVIL))
                    .entries((displayContext, entries) -> {
                        entries.add(ItemsRegistry.LEATHER_BACKPACK);
                    }).build());
    public static void registerItemGroups() {
        InventoryWeight.LOGGER.info("Registering Item Groups for " + InventoryWeight.MOD_ID);
    }
}