package com.megatrex4;

import com.megatrex4.data.PlayerDataHandler;
import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.effects.OverloadEffect;
import com.megatrex4.network.InventoryWeightPacket;
import com.megatrex4.util.ItemWeights;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import static com.megatrex4.util.ItemWeights.getItemWeight;

public class InventoryWeightHandler {
    // Reference the correct instance of OverloadEffect
    private static final StatusEffect OVERLOAD_EFFECT = InventoryWeightEffectRegister.OVERLOAD_EFFECT;

    public static float calculateInventoryWeight(ServerPlayerEntity player) {
        float totalWeight = 0;

        // Define an array of ItemStack arrays for each inventory section
        ItemStack[][] inventorySections = {
                player.getInventory().main.toArray(new ItemStack[0]),
                player.getInventory().offHand.toArray(new ItemStack[0]),
                player.getInventory().armor.toArray(new ItemStack[0])
        };

        // Iterate through each section of the inventory
        for (ItemStack[] section : inventorySections) {
            for (ItemStack stack : section) {
                if (!stack.isEmpty()) {
                    totalWeight += getItemWeight(stack) * stack.getCount();
                }
            }
        }

        return totalWeight;
    }



    // Checks if the player's inventory weight exceeds the limit and applies/removes the overload effect as needed
    public static void checkWeight(ServerPlayerEntity player) {
        float maxWeight = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
        float inventoryWeight = calculateInventoryWeight(player);

        // Check if the player is in Creative Mode
        if (player.interactionManager.getGameMode() == GameMode.CREATIVE) {
            removeOverloadEffect(player);
            return; // Exit the method since Creative players are not affected by weight
        }

        // Check if the player's inventory weight exceeds or equals the max weight
        if (inventoryWeight >= maxWeight) {
            // Apply overload effect if weight exceeds the max limit
            applyOverloadEffect(player);
        } else {
            // Remove the overload effect if weight is below the max limit
            removeOverloadEffect(player);
        }
    }

    // Applies the overload effect to the player
    private static void applyOverloadEffect(ServerPlayerEntity player) {
        float maxWeight = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
        float inventoryWeight = calculateInventoryWeight(player);

        // Calculate the overload level based on the percentage of inventory fullness
        float percentageFull = (inventoryWeight / maxWeight) * 100;

        // Calculate overload levels: every 25% above 100% adds 3 levels
        int overloadLevel = 0;
        if (percentageFull > 100) {
            overloadLevel = (int) Math.max(1, ((percentageFull - 100) / 25) * 2);
        }

        // Cap overload level at a maximum of 70
        overloadLevel = Math.min(overloadLevel, 70);

        // Check if the player has Strength or Haste effects
        boolean hasStrengthEffect = player.hasStatusEffect(StatusEffects.STRENGTH);
        int strengthAmplifier = hasStrengthEffect ? player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() : 0;
        boolean hasHasteEffect = player.hasStatusEffect(StatusEffects.HASTE);
        int hasteAmplifier = hasHasteEffect ? player.getStatusEffect(StatusEffects.HASTE).getAmplifier() : 0;

        // Adjust overload level based on Strength and Haste effects
        int adjustedOverloadLevel = overloadLevel;
        if (hasStrengthEffect || hasHasteEffect) {
            // Reduce amplifier by 1, but not below 1
            int effectiveAmplifier = Math.max(1, adjustedOverloadLevel - Math.max(1, Math.min((strengthAmplifier + hasteAmplifier) * 2, 10)));
            adjustedOverloadLevel = Math.min(effectiveAmplifier, 70);
        }



        // Check if the player already has the effect, and apply if necessary
        StatusEffectInstance existingEffect = player.getStatusEffect(OVERLOAD_EFFECT);
        if (existingEffect == null || existingEffect.getDuration() < 20) {  // 100 ticks = 5 seconds
            // Debugging output
//            System.out.println("Debug Info: ");
//            System.out.println("Max Weight: " + maxWeight);
//            System.out.println("Inventory Weight: " + inventoryWeight);
//            System.out.println("Percentage Full: " + percentageFull);
//            System.out.println("Calculated Overload Level: " + overloadLevel);
//            System.out.println("Adjusted Overload Level: " + adjustedOverloadLevel);
            player.addStatusEffect(new StatusEffectInstance(OVERLOAD_EFFECT, 40, adjustedOverloadLevel - 1, true, false, false));
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
