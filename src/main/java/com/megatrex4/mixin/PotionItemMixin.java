package com.megatrex4.mixin;

import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.util.ItemWeights;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {

    /**
     * Inject custom tooltip logic to display the weight of the item.
     */
    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
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

        // Cancel further tooltip modifications if needed
        ci.cancel();
    }
}
