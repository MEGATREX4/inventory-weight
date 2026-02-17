package com.megatrex4;

import com.google.gson.Gson;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.datapack.DatapackPocketWeightLoader;
import com.megatrex4.util.InventoryWeightUtil;
import com.megatrex4.util.NbtPocketHandler;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class InventoryWeightArmor {
    private static float POCKET_WEIGHT = InventoryWeightUtil.POCKET_WEIGHT;
    private static final Gson GSON = new Gson();

    public static boolean hasPockets(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            int pockets = InventoryWeightArmor.getPocketsBasedOnProtection(armorItem);
            return pockets > 0;
        }
        return false;
    }

    public static int getPockets(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            return getPocketsWithNbtCheck(stack);
        }
        return 0;
    }


    public static float getPocketWeight() {
        return ItemWeightsConfigServer.pocketWeight;
    }

    public static void setPocketWeight(String pocketWeight, Float newValue) {
        ItemWeightsConfigServer.pocketWeight = newValue;
        InventoryWeightArmor.POCKET_WEIGHT = newValue;
        ItemWeightsConfigServer.saveConfig();
    }

    // Load datapack data using Minecraft's resource system
    public static void loadDatapackData(ResourceManager resourceManager) {
        // Load pocket weights if available, otherwise use default calculation
        DatapackPocketWeightLoader.loadDatapackPocketWeights(resourceManager);
    }


    // Get pockets based on datapack data or default
    public static int getPocketsBasedOnProtection(ArmorItem armorItem) {
        String itemId = Registries.ITEM.getId(armorItem).toString();
        
        // Try to get pockets from datapack
        Integer pocketsFromDatapack = DatapackPocketWeightLoader.getPocketsForItem(itemId);
        if (pocketsFromDatapack != null) {
            return pocketsFromDatapack;
        }
        
        // Default calculation if no datapack value
        int protectionValue = armorItem.getProtection();
        float toughnessValue = armorItem.getToughness();
        return (int) Math.max(1, 7 - (int)(protectionValue / 1.2) - toughnessValue);
    }

    // Get pockets based on NBT data (for enchanted armor, etc.)
    public static int getPocketsWithNbtCheck(ItemStack armorStack) {
        if (!(armorStack.getItem() instanceof ArmorItem armorItem)) {
            return 0;
        }
        
        String itemId = Registries.ITEM.getId(armorStack.getItem()).toString();
        
        // Check for NBT-specific pockets (enchanted armor, etc.)
        Integer pocketsFromNbt = com.megatrex4.util.NbtPocketHandler.getPocketsFromNbt(armorStack);
        if (pocketsFromNbt != null) {
            return pocketsFromNbt;
        }
        
        // Fall back to regular datapack/default calculation
        return getPocketsBasedOnProtection(armorItem);
    }

    // Calculate the total armor weight based on pockets
    public static float calculateArmorWeight(ServerPlayerEntity player) {
        float totalArmorWeight = 0;

        for (ItemStack armorPiece : player.getInventory().armor) {
            if (armorPiece.getItem() instanceof ArmorItem armorItem) {
                int pockets = getPocketsBasedOnProtection(armorItem);
                totalArmorWeight += pockets * POCKET_WEIGHT;
            }
        }

        return totalArmorWeight;
    }


}
