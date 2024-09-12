package com.megatrex4;

import com.megatrex4.effects.InventoryWeightEffectRegister;
import com.megatrex4.effects.OverloadEffect;
import com.megatrex4.network.InventoryWeightPacket;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class InventoryWeightHandler {
    // Reference the correct instance of OverloadEffect
    private static final StatusEffect OVERLOAD_EFFECT = InventoryWeightEffectRegister.OVERLOAD_EFFECT;

    public static float calculateInventoryWeight(ServerPlayerEntity player) {
        float totalWeight = 0;

        // Iterate through the player's main inventory
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty()) {
                // Get the item ID for the stack
                Identifier itemId = Registries.ITEM.getId(stack.getItem());
                String itemIdString = (itemId != null) ? itemId.toString() : "unknown";

                // First, check if there is a custom weight for this item
                Float customWeight = ItemWeights.getCustomItemWeight(itemIdString);

                float itemWeight;
                if (customWeight != null) {
                    itemWeight = customWeight;
                } else {
                    String itemCategory = PlayerDataHandler.getItemCategory(stack);
                    itemWeight = ItemWeights.getItemWeight(itemCategory);
                }

                // Multiply the weight by the count of items in the stack
                totalWeight += itemWeight * stack.getCount();
            }
        }

        return totalWeight;
    }


    // Checks if the player's inventory weight exceeds the limit and applies/removes the overload effect as needed
    public static void checkWeight(ServerPlayerEntity player) {
        float maxWeight = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
        float inventoryWeight = calculateInventoryWeight(player);

        // debugging
        // player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("Inventory Weight: " + inventoryWeight + " / Max Weight: " + maxWeight)));

        // Check if the player's inventory weight exceeds the max weight and apply effects accordingly
        if (inventoryWeight >= maxWeight) {
            if (player.interactionManager.getGameMode() != GameMode.CREATIVE) {
                // debugging
                // System.out.println("Max Weight: " + maxWeight + " / " + inventoryWeight + " applying overload effect");
                applyOverloadEffect(player);
            }
        } else {
            removeOverloadEffect(player);
        }
    }

    // Applies the overload effect to the player
    private static void applyOverloadEffect(ServerPlayerEntity player) {
        StatusEffectInstance existingEffect = player.getStatusEffect(OVERLOAD_EFFECT);
        if (existingEffect == null || existingEffect.getDuration() < 100) {  // 100 ticks = 5 seconds
            player.addStatusEffect(new StatusEffectInstance(OVERLOAD_EFFECT, 200, 6, true, true, true));
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

        InventoryWeightPacket packet = new InventoryWeightPacket(inventoryWeight, maxWeight);
        InventoryWeightPacket.send(player, packet);

        checkWeight(player);
    }


    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            updatePlayerWeight(world, player);
        }
    }
}
