package com.megatrex4.inventoryweight.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.megatrex4.inventoryweight.InventoryWeight;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.entity.player.PlayerEntity;
import com.megatrex4.inventoryweight.util.Requirement;
import com.megatrex4.inventoryweight.util.SimpleItemRequirement;
import com.megatrex4.inventoryweight.util.NbtRequirement;
import net.minecraft.nbt.NbtElement;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ArmorPockets {

    public static int POCKET_WEIGHT = InventoryWeightConfig.SERVER.pocketWeight;
    private static final Map<Requirement<Item, ?>, Integer> ITEM_POCKETS = new HashMap<>();

    public static void load(ResourceManager manager) {
        ITEM_POCKETS.clear();
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("inventoryweight/armor", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("requires") && json.get("requires") instanceof JsonPrimitive mod &&
                        !FabricLoader.getInstance().isModLoaded(mod.getAsString())) {
                    continue;
                }
                for (Map.Entry<String, JsonElement> e : json.entrySet()) {
                    if (e.getKey().equals("requires")) continue;
                    Identifier id = Identifier.tryParse(e.getKey());
                    if (id == null || !Registries.ITEM.containsId(id)) {
                        InventoryWeight.LOGGER.warn("Could not find item {} from armor pocket configuration", e.getKey());
                        continue;
                    }
                    Item item = Registries.ITEM.get(id);
                    processEntry(item, e.getValue());
                }
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Failed to load armor pockets {}", entry.getKey(), e);
            }
        }
    }

    private static void processEntry(Item item, JsonElement value) {
        if (value.isJsonPrimitive()) {
            ITEM_POCKETS.put(new SimpleItemRequirement(item), value.getAsInt());
        } else if (value.isJsonObject()) {
            JsonObject obj = value.getAsJsonObject();
            int pockets = obj.get("pockets").getAsInt();
            if (obj.has("requirements") && obj.get("requirements").isJsonObject()) {
                JsonObject reqObj = obj.getAsJsonObject("requirements");
                if (reqObj.has("nbt")) {
                    JsonObject nbt = reqObj.getAsJsonObject("nbt");
                    String tag = nbt.get("tag").getAsString();
                    NbtElement tagVal = nbt.has("value") ? DatapackLoader.parseNbtElement(nbt.get("value")) : null;
                    ITEM_POCKETS.put(new NbtRequirement(item, tag, tagVal), pockets);
                    return;
                }
            }
            ITEM_POCKETS.put(new SimpleItemRequirement(item), pockets);
        }
    }

    @SuppressWarnings("unchecked")
    public static int getPockets(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem armor)) return 0;
        for (Map.Entry<Requirement<Item, ?>, Integer> entry : ITEM_POCKETS.entrySet()) {
            Requirement<Item, ItemStack> req = (Requirement<Item, ItemStack>) entry.getKey();
            if (req.getTarget() == stack.getItem() && req.isMet(stack)) {
                return entry.getValue();
            }
        }
        return getPockets(armor);
    }

    public static int getPockets(ArmorItem armorItem) {
        for (Map.Entry<Requirement<Item, ?>, Integer> entry : ITEM_POCKETS.entrySet()) {
            if (entry.getKey() instanceof SimpleItemRequirement simple && simple.getTarget() == armorItem) {
                return entry.getValue();
            }
        }
        return defaultPockets(armorItem);
    }

    private static int defaultPockets(ArmorItem armorItem) {
        int protection = armorItem.getProtection();
        float toughness = armorItem.getToughness();
        return (int) Math.max(1, 7 - (int)(protection / 1.2f) - toughness);
    }

    public static int calculateArmorCapacity(PlayerEntity player) {
        int pockets = 0;
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getItem() instanceof ArmorItem armor) {
                pockets += getPockets(armor);
            }
        }
        return (int) (pockets * POCKET_WEIGHT);
    }
}