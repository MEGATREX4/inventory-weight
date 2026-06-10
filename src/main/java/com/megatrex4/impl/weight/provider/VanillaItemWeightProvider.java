package com.megatrex4.impl.weight.provider;

import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.config.ServerWeightSettings;
import com.megatrex4.impl.config.WeightSettings;
import com.megatrex4.impl.weight.ArmorAttributeHelper;
import com.megatrex4.impl.weight.ItemCategory;
import com.megatrex4.impl.weight.ItemCategoryClassifier;
import com.megatrex4.impl.weight.WeightMath;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;

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
            ConsumableComponent consumableComponent = stack.get(DataComponentTypes.CONSUMABLE);

            if (foodComponent != null) {
                weight += WeightMath.foodWeight(foodComponent, consumableComponent);
            }

            DamageResistantComponent damageResistant = stack.get(DataComponentTypes.DAMAGE_RESISTANT);

            if (damageResistant != null && damageResistant.types().equals(DamageTypeTags.IS_FIRE)) {
                weight *= 1.25f;
            }
        } else if (maxStackSize == 1 && maxDurability > 0) {
            if (item instanceof ArmorItem) {
                weight += WeightMath.armorProtectionWeight(ArmorAttributeHelper.getProtection(stack));
                weight += settings.itemWeight() + (((float) maxDurability / 300.0f) * 300.0f);
            }
            if (stack.contains(DataComponentTypes.TOOL)) {
                weight += settings.itemWeight() + WeightMath.toolDurabilityWeight(maxDurability);
            }
        }

        weight *= WeightMath.rarityMultiplier(stack);
        return Optional.of(WeightResult.of(WeightMath.finalFloor(weight)));
    }
}
