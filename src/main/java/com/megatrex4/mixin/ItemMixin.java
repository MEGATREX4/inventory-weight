package com.megatrex4.mixin;

import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.util.ItemWeights;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.client.item.TooltipContext;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {

    /**
     * @author MEGATREX4
     * @reason I need to show the weight of the item in tooltips
     */
    @Overwrite
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
            // Check if the armor has pockets
            int pockets = InventoryWeightArmor.getPockets(stack);



            // Add weight info
            String itemId = ItemWeights.getItemId(stack);
            Float customWeight = ItemWeights.getCustomItemWeight(itemId);

            if (customWeight != null) {
                tooltip.add(Text.translatable("inventoryweight.tooltip.weight", customWeight).formatted(Formatting.GRAY));
            } else {
                String itemCategory = PlayerDataHandler.getItemCategory(stack);
                Float itemWeight = ItemWeights.getItemWeight(itemCategory);

                tooltip.add(Text.translatable("inventoryweight.tooltip.weight", itemWeight).formatted(Formatting.GRAY));
            }
        // Add custom text to the tooltip
        if (stack.getItem() instanceof ArmorItem || pockets > 0) {
            tooltip.add(Text.literal(" ").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("inventoryweight.tooltip.pockets", pockets).formatted(Formatting.BLUE));
        }
        }
}
