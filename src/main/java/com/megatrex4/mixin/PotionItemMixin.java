package com.megatrex4.mixin;

import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.util.ItemWeights;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

    /**
     * @author MEGATREX4
     * @reason I need to show the weight of the item in tooltips
     */
    @Overwrite
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Implement your custom tooltip logic directly

        // Get the item's ID
        String itemId = ItemWeights.getItemId(stack);

        // First, check if there is a custom weight for this item
        Float customWeight = ItemWeights.getCustomItemWeight(itemId);

        if (customWeight != null) {
            tooltip.add(Text.translatable("inventoryweight.tooltip.weight", customWeight).formatted(Formatting.GRAY));
        } else {
            // Use category to get the weight if custom weight is not available
            String itemCategory = PlayerDataHandler.getItemCategory(stack);
            Float itemWeight = ItemWeights.getItemWeight(itemCategory);

            tooltip.add(Text.translatable("inventoryweight.tooltip.weight", itemWeight).formatted(Formatting.GRAY));
        }
    }
}
