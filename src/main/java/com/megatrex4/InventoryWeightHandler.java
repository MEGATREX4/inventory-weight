package com.megatrex4;

import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.effects.OverloadEffect;
import com.megatrex4.network.InventoryWeightPacket;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import static com.megatrex4.effects.OverloadEffect.*;
import static com.megatrex4.util.InventoryWeightUtil.MAXWEIGHT;
import static com.megatrex4.util.ItemWeights.getItemWeight;

public class InventoryWeightHandler {

    private static final StatusEffect OVERLOAD_EFFECT = InventoryWeightEffectRegister.OVERLOAD_EFFECT;

    public static float calculateInventoryWeight(PlayerEntity player) {
        float totalWeight = 0;

        // Combine all inventory sections into a single list for easier iteration
        ItemStack[][] inventorySections = {
                player.getInventory().main.toArray(new ItemStack[0]),
                player.getInventory().offHand.toArray(new ItemStack[0]),
                player.getInventory().armor.toArray(new ItemStack[0])
        };

        for (ItemStack[] section : inventorySections) {
            for (ItemStack stack : section) {
                if (!stack.isEmpty()) {
                    totalWeight += getItemWeight(stack) * stack.getCount();
                }
            }
        }

        return totalWeight;
    }

    public static void checkWeight(ServerPlayerEntity player) {
        float maxWeight = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
        float inventoryWeight = calculateInventoryWeight(player);

        // Skip Creative mode players
        if (player.interactionManager.getGameMode() == GameMode.CREATIVE) {
            removeOverloadEffect(player);
            return;
        }

        // Apply or remove overload based on weight
        if (inventoryWeight >= maxWeight) {
            applyOverloadEffect(player, maxWeight, inventoryWeight);
        } else {
            removeOverloadEffect(player);
            applyWeightPenalties(player);
        }
    }

    private static void applyWeightPenalties(ServerPlayerEntity player) {
        float totalWeight = calculateInventoryWeight(player);

        if (totalWeight > 0.1 * MAXWEIGHT && !player.isCreative()) {
            double overloadFactor = (totalWeight - (0.1 * MAXWEIGHT)) / (MAXWEIGHT - (0.1 * MAXWEIGHT));

            // Adjust penalties using the overload factor and amplifier
            double speedDecrease = Math.min(overloadFactor * 0.9, 0.9);
            double attackSpeedDecrease = Math.min(overloadFactor * 0.9, 0.9);
            double damageReduction = Math.min(overloadFactor * 0.9, 0.9);

            speedDecrease = Math.min(speedDecrease, 0.9) - speedDecrease * 0.4;
            attackSpeedDecrease = Math.min(attackSpeedDecrease, 0.9) - attackSpeedDecrease * 0.3;
            damageReduction = Math.min(damageReduction, 0.9) - damageReduction * 0.3;

            // Remove existing modifiers
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .removeModifier(SPEED_MODIFIER_UUID);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .removeModifier(ATTACK_SPEED_MODIFIER_UUID);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .removeModifier(DAMAGE_REDUCTION_MODIFIER_UUID);

            // Apply new modifiers
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .addPersistentModifier(new EntityAttributeModifier(OverloadEffect.SPEED_MODIFIER_UUID,
                            "overload_speed_penalty", -speedDecrease, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .addPersistentModifier(new EntityAttributeModifier(OverloadEffect.ATTACK_SPEED_MODIFIER_UUID,
                            "overload_attack_speed_penalty", -attackSpeedDecrease, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .addPersistentModifier(new EntityAttributeModifier(OverloadEffect.DAMAGE_REDUCTION_MODIFIER_UUID,
                            "overload_damage_reduction_penalty", -damageReduction, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        } else {
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .removeModifier(OverloadEffect.SPEED_MODIFIER_UUID);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .removeModifier(OverloadEffect.ATTACK_SPEED_MODIFIER_UUID);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .removeModifier(OverloadEffect.DAMAGE_REDUCTION_MODIFIER_UUID);
        }
    }

    private static void applyOverloadEffect(ServerPlayerEntity player, float maxWeight, float inventoryWeight) {
        // Calculate percentage of weight
        float percentageFull = (inventoryWeight / maxWeight) * 100;

        // Each 10% above 100% adds 2 levels of overload
        int overloadLevel = Math.max(0, (int) ((percentageFull - 100) / 10) * 2);
        overloadLevel = Math.min(overloadLevel, 128); // Cap the overload level at 128

        // Reduce overload level if the player has Strength or Haste
        int strengthAmplifier = player.hasStatusEffect(StatusEffects.STRENGTH)
                ? player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier()
                : 0;
        int hasteAmplifier = player.hasStatusEffect(StatusEffects.HASTE)
                ? player.getStatusEffect(StatusEffects.HASTE).getAmplifier()
                : 0;
        int adjustedOverloadLevel = Math.max(1, overloadLevel - (strengthAmplifier + hasteAmplifier) * 2);

        // Cap the reduced level to 70
        adjustedOverloadLevel = Math.min(adjustedOverloadLevel, 70);

        // Only apply effect if not already present or about to expire
        StatusEffectInstance currentEffect = player.getStatusEffect(OVERLOAD_EFFECT);
        if (currentEffect == null || currentEffect.getDuration() < 20) {
            player.addStatusEffect(new StatusEffectInstance(OVERLOAD_EFFECT, 40, adjustedOverloadLevel - 1, true, false, false));
        }
    }


    private static void removeOverloadEffect(ServerPlayerEntity player) {
        // Ensure removal of attribute modifiers along with the effect
        if (player.hasStatusEffect(OVERLOAD_EFFECT)) {
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .removeModifier(OverloadEffect.SPEED_MODIFIER_UUID);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .removeModifier(OverloadEffect.ATTACK_SPEED_MODIFIER_UUID);
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .removeModifier(OverloadEffect.DAMAGE_REDUCTION_MODIFIER_UUID);

            player.removeStatusEffect(OVERLOAD_EFFECT);
        }
    }

    public static void updatePlayerWeight(ServerWorld world, ServerPlayerEntity player) {
        float inventoryWeight = calculateInventoryWeight(player);
        float maxWeight = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
        float pocketWeight = InventoryWeightArmor.getPocketWeight();

        InventoryWeightPacket packet = new InventoryWeightPacket(inventoryWeight, maxWeight, pocketWeight);
        InventoryWeightPacket.send(player, packet);

        checkWeight(player);
    }

    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            updatePlayerWeight(world, player);
        }
    }
}
