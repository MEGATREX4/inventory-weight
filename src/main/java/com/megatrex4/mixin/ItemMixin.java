package com.megatrex4.mixin;

import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.util.ItemWeights;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {

    /**
     * Injects custom tooltip logic to display the weight and pocket info of the item.
     */
    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
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

        // Add custom text to the tooltip if the item is armor or has pockets
        if (stack.getItem() instanceof ArmorItem || pockets > 0) {
            tooltip.add(Text.literal(" ").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("inventoryweight.tooltip.pockets", pockets).formatted(Formatting.BLUE));
        }

        // Cancel further tooltip modifications if needed
        ci.cancel();
    }
}
