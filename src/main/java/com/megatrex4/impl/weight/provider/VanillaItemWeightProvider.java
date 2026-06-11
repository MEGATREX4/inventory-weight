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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.DamageResistant;

import java.util.Optional;

public final class VanillaItemWeightProvider implements ItemWeightProvider {

    @Override
    public Optional<WeightResult> getWeight(ItemStack stack, WeightContext context, WeightLookup lookup) {
        Item item = stack.getItem();

        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

        if (itemId.contains("air")) {
            return Optional.of(WeightResult.ZERO);
        }

        ServerWeightSettings settings = WeightSettings.get();

        ItemCategory category = ItemCategoryClassifier.classify(stack);

        float weight = WeightMath.baseWeight(category, settings);

        int maxStackSize = stack.getMaxStackSize();
        int maxDurability = stack.getMaxDamage();

        if (maxStackSize > 1) {
            weight *= WeightMath.stackMultiplier(maxStackSize);

            FoodProperties foodComponent = stack.get(DataComponents.FOOD);
            Consumable consumableComponent = stack.get(DataComponents.CONSUMABLE);

            if (foodComponent != null) {
                weight += WeightMath.foodWeight(foodComponent, consumableComponent);
            }

            DamageResistant damageResistant = stack.get(DataComponents.DAMAGE_RESISTANT);

            if (damageResistant != null && damageResistant.types().equals(DamageTypeTags.IS_FIRE)) {
                weight *= 1.25f;
            }

        } else if (maxStackSize == 1 && maxDurability > 0) {
            if (ArmorAttributeHelper.isArmorStack(stack)) {
                weight += WeightMath.armorProtectionWeight(ArmorAttributeHelper.getProtection(stack));
                weight += settings.itemWeight() + (((float) maxDurability / 300.0f) * 300.0f);
            }

            if (stack.get(DataComponents.TOOL) != null) {
                weight += settings.itemWeight() + WeightMath.toolDurabilityWeight(maxDurability);
            }
        }

        weight *= WeightMath.rarityMultiplier(stack);

        return Optional.of(WeightResult.of(WeightMath.finalFloor(weight)));
    }
}