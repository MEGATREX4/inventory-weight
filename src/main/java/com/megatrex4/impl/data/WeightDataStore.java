package com.megatrex4.impl.data;

import com.google.gson.*;
import com.megatrex4.InventoryWeight;
import com.megatrex4.impl.config.ServerWeightSettings;
import com.megatrex4.impl.config.WeightSettings;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class WeightDataStore {
    public static final WeightDataStore INSTANCE = new WeightDataStore();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<Identifier, Float> itemWeights = new HashMap<>();
    private final Map<Identifier, Integer> pockets = new HashMap<>();
    private final List<NbtWeightRule> nbtWeightRules = new ArrayList<>();
    private final List<NbtPocketRule> nbtPocketRules = new ArrayList<>();

    private final Map<Identifier, String> itemSources = new HashMap<>();
    private final Map<Identifier, String> pocketSources = new HashMap<>();
    private final Set<Identifier> conflictedItems = new HashSet<>();
    private final Set<Identifier> conflictedPockets = new HashSet<>();

    private WeightDataStore() {}

    public synchronized void reload(ResourceManager manager) {
        clearDatapackData();
        loadItemWeights(manager);
        loadPockets(manager);

        if (!conflictedItems.isEmpty()) {
            conflictedItems.forEach(id -> InventoryWeight.LOGGER.warn("Item weight for {} was defined more than once. Keeping first definition.", id));
        }
        if (!conflictedPockets.isEmpty()) {
            conflictedPockets.forEach(id -> InventoryWeight.LOGGER.warn("Pocket definition for {} was defined more than once. Keeping first definition.", id));
        }
    }

    public synchronized void clearDatapackData() {
        itemWeights.clear();
        pockets.clear();
        nbtWeightRules.clear();
        nbtPocketRules.clear();
        itemSources.clear();
        pocketSources.clear();
        conflictedItems.clear();
        conflictedPockets.clear();
    }

    private void loadItemWeights(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources("inventory_weight/items", path -> path.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier file = entry.getKey();
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root.isJsonObject()) {
                    JsonObject object = root.getAsJsonObject();
                    if (isSingleItemObject(object)) {
                        parseSingleItemWeightObject(object, file.toString(), null);
                    } else {
                        parseItemWeightMap(object, file.toString());
                    }
                } else if (root.isJsonArray()) {
                    for (JsonElement element : root.getAsJsonArray()) {
                        if (element.isJsonObject()) {
                            parseSingleItemWeightObject(element.getAsJsonObject(), file.toString(), null);
                        }
                    }
                }
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Failed to load item weights from {}", file, e);
            }
        }
    }

    private void loadPockets(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources("inventory_weight/pockets", path -> path.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier file = entry.getKey();
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root.isJsonObject()) {
                    JsonObject object = root.getAsJsonObject();
                    if (isSinglePocketObject(object)) {
                        parseSinglePocketObject(object, file.toString(), null);
                    } else {
                        parsePocketMap(object, file.toString());
                    }
                } else if (root.isJsonArray()) {
                    for (JsonElement element : root.getAsJsonArray()) {
                        if (element.isJsonObject()) {
                            parseSinglePocketObject(element.getAsJsonObject(), file.toString(), null);
                        }
                    }
                }
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Failed to load pocket definitions from {}", file, e);
            }
        }
    }

    private static boolean isSingleItemObject(JsonObject object) {
        return object.has("item") && (object.has("weight") || object.has("weightWhenNbt"));
    }

    private static boolean isSinglePocketObject(JsonObject object) {
        return object.has("item") && (object.has("pockets") || object.has("pocketsWhenNbt"));
    }

    private void parseItemWeightMap(JsonObject map, String source) {
        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                parseSingleItemWeightObject(value.getAsJsonObject(), source, entry.getKey());
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                registerItemWeight(entry.getKey(), value.getAsFloat(), source);
            }
        }
    }

    private void parseSingleItemWeightObject(JsonObject object, String source, String fallbackItemId) {
        String itemId = object.has("item") ? object.get("item").getAsString() : fallbackItemId;
        if (itemId == null) {
            InventoryWeight.LOGGER.error("Item weight object in {} is missing item id", source);
            return;
        }

        if (object.has("weightWhenNbt")) {
            parseNbtWeightRules(itemId, object.getAsJsonObject("weightWhenNbt"), source);
            return;
        }

        if (object.has("weight")) {
            registerItemWeight(itemId, object.get("weight").getAsFloat(), source);
        }
    }

    private void parsePocketMap(JsonObject map, String source) {
        for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                parseSinglePocketObject(value.getAsJsonObject(), source, entry.getKey());
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                registerPockets(entry.getKey(), value.getAsInt(), source);
            }
        }
    }

    private void parseSinglePocketObject(JsonObject object, String source, String fallbackItemId) {
        String itemId = object.has("item") ? object.get("item").getAsString() : fallbackItemId;
        if (itemId == null) {
            InventoryWeight.LOGGER.error("Pocket object in {} is missing item id", source);
            return;
        }

        if (object.has("pocketsWhenNbt")) {
            parseNbtPocketRules(itemId, object.getAsJsonObject("pocketsWhenNbt"), source);
            return;
        }

        if (object.has("pockets")) {
            registerPockets(itemId, object.get("pockets").getAsInt(), source);
        }
    }

    private void parseNbtWeightRules(String itemIdString, JsonObject weightWhenNbt, String source) {
        Identifier itemId = Identifier.tryParse(itemIdString);
        if (itemId == null) {
            InventoryWeight.LOGGER.error("Invalid item id '{}' in {}", itemIdString, source);
            return;
        }
        if (isItemConflict(itemId, source)) {
            return;
        }
        itemSources.putIfAbsent(itemId, source);

        for (Map.Entry<String, JsonElement> entry : weightWhenNbt.entrySet()) {
            Map<String, Float> values = new HashMap<>();
            JsonObject ruleObject = entry.getValue().getAsJsonObject();
            if (ruleObject.has("value") && ruleObject.has("weight")) {
                values.put(ruleObject.get("value").getAsString(), ruleObject.get("weight").getAsFloat());
            } else {
                for (Map.Entry<String, JsonElement> valueEntry : ruleObject.entrySet()) {
                    if (valueEntry.getValue().isJsonPrimitive() && valueEntry.getValue().getAsJsonPrimitive().isNumber()) {
                        values.put(valueEntry.getKey(), valueEntry.getValue().getAsFloat());
                    }
                }
            }
            if (!values.isEmpty()) {
                nbtWeightRules.add(new NbtWeightRule(itemId, entry.getKey(), Map.copyOf(values)));
            }
        }
    }

    private void parseNbtPocketRules(String itemIdString, JsonObject pocketsWhenNbt, String source) {
        Identifier itemId = Identifier.tryParse(itemIdString);
        if (itemId == null) {
            InventoryWeight.LOGGER.error("Invalid item id '{}' in {}", itemIdString, source);
            return;
        }
        if (isPocketConflict(itemId, source)) {
            return;
        }
        pocketSources.putIfAbsent(itemId, source);

        for (Map.Entry<String, JsonElement> entry : pocketsWhenNbt.entrySet()) {
            Map<String, Integer> values = new HashMap<>();
            JsonObject ruleObject = entry.getValue().getAsJsonObject();
            if (ruleObject.has("value") && ruleObject.has("pockets")) {
                values.put(ruleObject.get("value").getAsString(), ruleObject.get("pockets").getAsInt());
            } else {
                for (Map.Entry<String, JsonElement> valueEntry : ruleObject.entrySet()) {
                    if (valueEntry.getValue().isJsonPrimitive() && valueEntry.getValue().getAsJsonPrimitive().isNumber()) {
                        values.put(valueEntry.getKey(), valueEntry.getValue().getAsInt());
                    }
                }
            }
            if (!values.isEmpty()) {
                nbtPocketRules.add(new NbtPocketRule(itemId, entry.getKey(), Map.copyOf(values)));
            }
        }
    }

    private void registerItemWeight(String itemIdString, float weight, String source) {
        Identifier itemId = Identifier.tryParse(itemIdString);
        if (itemId == null) {
            InventoryWeight.LOGGER.error("Invalid item id '{}' in {}", itemIdString, source);
            return;
        }
        if (isItemConflict(itemId, source)) {
            return;
        }
        itemSources.putIfAbsent(itemId, source);
        itemWeights.putIfAbsent(itemId, weight);
    }

    private void registerPockets(String itemIdString, int pocketCount, String source) {
        Identifier itemId = Identifier.tryParse(itemIdString);
        if (itemId == null) {
            InventoryWeight.LOGGER.error("Invalid item id '{}' in {}", itemIdString, source);
            return;
        }
        if (isPocketConflict(itemId, source)) {
            return;
        }
        pocketSources.putIfAbsent(itemId, source);
        pockets.putIfAbsent(itemId, pocketCount);
    }

    private boolean isItemConflict(Identifier itemId, String source) {
        String previous = itemSources.get(itemId);
        if (previous != null && !previous.equals(source)) {
            conflictedItems.add(itemId);
            return true;
        }
        return false;
    }

    private boolean isPocketConflict(Identifier itemId, String source) {
        String previous = pocketSources.get(itemId);
        if (previous != null && !previous.equals(source)) {
            conflictedPockets.add(itemId);
            return true;
        }
        return false;
    }

    public synchronized Float getItemWeight(Identifier itemId) {
        return itemWeights.get(itemId);
    }

    public synchronized Integer getPockets(Identifier itemId) {
        return pockets.get(itemId);
    }

    public synchronized List<NbtWeightRule> getNbtWeightRules(Identifier itemId) {
        return nbtWeightRules.stream().filter(rule -> rule.itemId().equals(itemId)).toList();
    }

    public synchronized List<NbtPocketRule> getNbtPocketRules(Identifier itemId) {
        return nbtPocketRules.stream().filter(rule -> rule.itemId().equals(itemId)).toList();
    }

    public synchronized WeightDataSnapshot snapshot() {
        return new WeightDataSnapshot(
                ServerWeightSettings.fromConfig(),
                Map.copyOf(itemWeights),
                Map.copyOf(pockets),
                List.copyOf(nbtWeightRules),
                List.copyOf(nbtPocketRules)
        );
    }

    public synchronized void applySnapshot(WeightDataSnapshot snapshot) {
        clearDatapackData();
        itemWeights.putAll(snapshot.itemWeights());
        pockets.putAll(snapshot.pockets());
        nbtWeightRules.addAll(snapshot.nbtWeightRules());
        nbtPocketRules.addAll(snapshot.nbtPocketRules());
        WeightSettings.setSyncedServerSettings(snapshot.settings());
    }

    public static void encodeSnapshot(PacketByteBuf buf, WeightDataSnapshot snapshot) {
        writeSettings(buf, snapshot.settings());

        buf.writeVarInt(snapshot.itemWeights().size());
        snapshot.itemWeights().forEach((id, weight) -> {
            buf.writeIdentifier(id);
            buf.writeFloat(weight);
        });

        buf.writeVarInt(snapshot.pockets().size());
        snapshot.pockets().forEach((id, pocketCount) -> {
            buf.writeIdentifier(id);
            buf.writeVarInt(pocketCount);
        });

        buf.writeVarInt(snapshot.nbtWeightRules().size());
        for (NbtWeightRule rule : snapshot.nbtWeightRules()) {
            buf.writeIdentifier(rule.itemId());
            buf.writeString(rule.nbtKey());
            buf.writeVarInt(rule.valueWeights().size());
            rule.valueWeights().forEach((value, weight) -> {
                buf.writeString(value);
                buf.writeFloat(weight);
            });
        }

        buf.writeVarInt(snapshot.nbtPocketRules().size());
        for (NbtPocketRule rule : snapshot.nbtPocketRules()) {
            buf.writeIdentifier(rule.itemId());
            buf.writeString(rule.nbtKey());
            buf.writeVarInt(rule.valuePockets().size());
            rule.valuePockets().forEach((value, pocketCount) -> {
                buf.writeString(value);
                buf.writeVarInt(pocketCount);
            });
        }
    }

    public static WeightDataSnapshot decodeSnapshot(PacketByteBuf buf) {
        ServerWeightSettings settings = readSettings(buf);

        Map<Identifier, Float> itemWeights = new HashMap<>();
        int itemCount = buf.readVarInt();
        for (int i = 0; i < itemCount; i++) {
            itemWeights.put(buf.readIdentifier(), buf.readFloat());
        }

        Map<Identifier, Integer> pockets = new HashMap<>();
        int pocketCount = buf.readVarInt();
        for (int i = 0; i < pocketCount; i++) {
            pockets.put(buf.readIdentifier(), buf.readVarInt());
        }

        List<NbtWeightRule> weightRules = new ArrayList<>();
        int weightRuleCount = buf.readVarInt();
        for (int i = 0; i < weightRuleCount; i++) {
            Identifier itemId = buf.readIdentifier();
            String nbtKey = buf.readString();
            int valueCount = buf.readVarInt();
            Map<String, Float> values = new HashMap<>();
            for (int j = 0; j < valueCount; j++) {
                values.put(buf.readString(), buf.readFloat());
            }
            weightRules.add(new NbtWeightRule(itemId, nbtKey, Map.copyOf(values)));
        }

        List<NbtPocketRule> pocketRules = new ArrayList<>();
        int pocketRuleCount = buf.readVarInt();
        for (int i = 0; i < pocketRuleCount; i++) {
            Identifier itemId = buf.readIdentifier();
            String nbtKey = buf.readString();
            int valueCount = buf.readVarInt();
            Map<String, Integer> values = new HashMap<>();
            for (int j = 0; j < valueCount; j++) {
                values.put(buf.readString(), buf.readVarInt());
            }
            pocketRules.add(new NbtPocketRule(itemId, nbtKey, Map.copyOf(values)));
        }

        return new WeightDataSnapshot(settings, Map.copyOf(itemWeights), Map.copyOf(pockets), List.copyOf(weightRules), List.copyOf(pocketRules));
    }

    private static void writeSettings(PacketByteBuf buf, ServerWeightSettings settings) {
        buf.writeFloat(settings.maxWeight());
        buf.writeFloat(settings.pocketWeight());
        buf.writeBoolean(settings.realisticMode());
        buf.writeFloat(settings.overloadPenaltyStrength());
        buf.writeFloat(settings.bucketWeight());
        buf.writeFloat(settings.bottleWeight());
        buf.writeFloat(settings.blockWeight());
        buf.writeFloat(settings.ingotWeight());
        buf.writeFloat(settings.nuggetWeight());
        buf.writeFloat(settings.itemWeight());
        buf.writeFloat(settings.creativeWeight());
    }

    private static ServerWeightSettings readSettings(PacketByteBuf buf) {
        return new ServerWeightSettings(
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }
}
