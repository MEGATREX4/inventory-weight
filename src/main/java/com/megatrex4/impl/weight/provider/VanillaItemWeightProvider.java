package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.ServerWeightSettings;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.ItemCategory;
import com.megatrex4.impl.weight.ItemCategoryClassifier;
import com.megatrex4.impl.weight.WeightMath;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;

import java.util.Optional;

public final class VanillaItemWeightProvider implements ItemWeightProvider {
    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        Item item = stack.getItem();
        String itemId = Registries.ITEM.getId(item).toString();
        if (itemId.contains("air")) {
            return Optional.of(WeightResult.ZERO);
        }

        ServerWeightSettings settings = WeightSettings.get();
        ItemCategory category = ItemCategoryClassifier.classify(stack);
        float weight = WeightMath.baseWeight(category, settings);

        int maxStackSize = item.getMaxCount();
        int maxDurability = stack.getMaxDamage();

        if (maxStackSize > 1) {
            weight *= WeightMath.stackMultiplier(maxStackSize);
            FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
            if (foodComponent != null) {
                weight += WeightMath.foodWeight(foodComponent);
            }
            if (stack.contains(DataComponentTypes.FIRE_RESISTANT)) {
                weight *= 1.25f;
            }
        } else if (maxStackSize == 1 && maxDurability > 0) {
            if (item instanceof ArmorItem armorItem) {
                weight += WeightMath.armorProtectionWeight(armorItem.getProtection());
                weight += settings.itemWeight() + (((float) maxDurability / 300.0f) * 300.0f);
            }
            if (item instanceof ToolItem) {
                weight += settings.itemWeight() + WeightMath.toolDurabilityWeight(maxDurability);
            }
        }

        weight *= WeightMath.rarityMultiplier(stack);
        return Optional.of(WeightResult.of(WeightMath.finalFloor(weight)));
    }
}
