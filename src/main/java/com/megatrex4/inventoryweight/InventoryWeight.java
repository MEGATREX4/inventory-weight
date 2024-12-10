package com.megatrex4.inventoryweight;

import com.google.gson.*;
import com.megatrex4.inventoryweight.effect.InventoryWeightEffects;
import com.megatrex4.inventoryweight.util.NbtRequirement;
import com.megatrex4.inventoryweight.util.Requirement;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryWeight implements ModInitializer {

	public static final String MOD_ID = "inventoryweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<Requirement<Item, ?>, Integer> customItemWeights = new HashMap<>();
	public static final Map<Requirement<?, ?>, Float> weightMultipliers = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> loadDatapacks(server.getResourceManager()));

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, resourceManager) -> loadDatapacks(resourceManager));

		InventoryWeightEffects.register();
	}

	private static void loadDatapacks(ResourceManager resourceManager) {
		try {
			for (Map.Entry<Identifier, Resource> resourceEntry : resourceManager.findResources("inventoryweight", path -> path.getPath().equals("items_weights.json")).entrySet()) {
				Resource resource = resourceEntry.getValue();
				try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
					JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
					for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
						if (entry.getValue() instanceof JsonPrimitive primitive) {
							Optional<Identifier> item = Optional.ofNullable(Identifier.tryParse(entry.getKey()));
							item.ifPresent(id -> {
								if (primitive.isNumber()) {
									Requirement<Item, ?> requirement = new Requirement<>() {
										@Override
										public Item getTarget() {
											return Registries.ITEM.get(id);
										}
										@Override
										public Boolean isMet(Object check) {
											return true;
										}
									};
									customItemWeights.put(requirement, primitive.getAsInt());
								}
							});
						} else if (entry.getValue() instanceof JsonObject object) {
							Optional<Identifier> itemId = Optional.ofNullable(Identifier.tryParse(entry.getKey()));
							itemId.ifPresent(id -> {
								Item item = Registries.ITEM.get(id);
								int weight = object.get("weight").getAsInt();
								if (object.has("requirements")) {
									JsonObject requirements = object.get("requirements").getAsJsonObject();
									for (Map.Entry<String, JsonElement> requirementEntry : requirements.entrySet()) {
										if (requirementEntry.getKey().equals("nbt")) {
											JsonObject nbtRequirement = requirementEntry.getValue().getAsJsonObject();
											String tag = nbtRequirement.get("tag").getAsString();
											NbtElement value = null;
											if (nbtRequirement.has("value")) {
												value = parseNbtElement(nbtRequirement.get("value"));
											}
											NbtRequirement requirement = new NbtRequirement(item, tag, value);
											customItemWeights.put(requirement, weight);
										}
									}
								} else {
									Requirement<Item, ?> requirement = new Requirement<>() {
										@Override
										public Item getTarget() {
											return Registries.ITEM.get(id);
										}
										@Override
										public Boolean isMet(Object check) {
											return true;
										}
									};
									customItemWeights.put(requirement, weight);
								}
							});
						}
					}
				}
			}
		} catch (IOException | JsonIOException | JsonSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static NbtElement parseNbtElement(JsonElement element) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isNumber()) {
				if (primitive.getAsNumber() instanceof Integer i) {
					return NbtInt.of(i);
				} else if (primitive.getAsNumber() instanceof Short s) {
					return NbtShort.of(s);
				} else if (primitive.getAsNumber() instanceof Long l) {
					return NbtLong.of(l);
				} else if (primitive.getAsNumber() instanceof Float f) {
					return NbtFloat.of(f);
				} else if (primitive.getAsNumber() instanceof Double d) {
					return NbtDouble.of(d);
				}
			} else if (primitive.isBoolean()) {
				return NbtByte.of(primitive.getAsBoolean());
			} else if (primitive.isString()) {
				return NbtString.of(primitive.getAsString());
			}
		}
		throw new IllegalArgumentException("Unsupported NBT value: " + element);
	}

}
