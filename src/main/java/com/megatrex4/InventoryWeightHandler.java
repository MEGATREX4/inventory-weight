package com.megatrex4;

import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.effects.OverloadEffect;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class InventoryWeightHandler {
    // Reference the correct instance of OverloadEffect
    private static final StatusEffect OVERLOAD_EFFECT = InventoryWeightEffectRegister.OVERLOAD_EFFECT;
    public static float calculateInventoryWeight(ServerPlayerEntity player) {
        float totalWeight = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().toString(); // Simplify item name extraction as needed
                totalWeight += ItemWeights.getItemWeight(itemName) * stack.getCount();
            }
        }
        return totalWeight;
    }

    // Checks if the player's inventory weight exceeds the limit and applies/removes the overload effect as needed
    public static void checkWeight(ServerPlayerEntity player) {
        float maxWeight = PlayerDataHandler.getPlayerMaxWeight(player);
        float inventoryWeight = calculateInventoryWeight(player);

        if (inventoryWeight > maxWeight) {
            if (player.interactionManager.getGameMode() != GameMode.CREATIVE) {
                applyOverloadEffect(player);
            }
        } else {
            removeOverloadEffect(player);
        }
    }

    // Applies the overload effect to the player
    private static void applyOverloadEffect(ServerPlayerEntity player) {
        // Get the current effect instance
        StatusEffectInstance existingEffect = player.getStatusEffect(OVERLOAD_EFFECT);

        if (existingEffect != null) {
            // If the effect is already applied, extend its duration
            player.addStatusEffect(new StatusEffectInstance(OVERLOAD_EFFECT, 200, 0, true, true, true));
        } else {
            // Apply the effect with a duration of 200 ticks (10 seconds)
            player.addStatusEffect(new StatusEffectInstance(OVERLOAD_EFFECT, 200, 0, true, true, true));
        }
    }


    // Removes the overload effect from the player
    private static void removeOverloadEffect(ServerPlayerEntity player) {
        // Remove specific attribute modifiers associated with the overload effect
        if (player.hasStatusEffect(OVERLOAD_EFFECT)) {
            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .removeModifier(OverloadEffect.SPEED_MODIFIER_UUID);

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)
                    .removeModifier(OverloadEffect.ATTACK_SPEED_MODIFIER_UUID);

            player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR)
                    .removeModifier(OverloadEffect.DAMAGE_REDUCTION_MODIFIER_UUID);

            // Finally, remove the status effect itself
            player.removeStatusEffect(OVERLOAD_EFFECT);
        }
    }





    public static void updatePlayerWeight(ServerWorld world, ServerPlayerEntity player) {
        float inventoryWeight = calculateInventoryWeight(player);
        float maxWeight = PlayerDataHandler.getPlayerMaxWeight(player);

        if (inventoryWeight > maxWeight) {
            applyOverloadEffect(player);
        } else {
            removeOverloadEffect(player);
        }
    }


    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            updatePlayerWeight(world, player);
        }
    }
}
