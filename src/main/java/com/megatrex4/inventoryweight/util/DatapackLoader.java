package com.megatrex4.inventoryweight.util;

import com.google.gson.*;
import com.megatrex4.inventoryweight.InventoryWeight;
import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DatapackLoader {

	private final ResourceManager resourceManager;
	public final Map<Requirement<Item, ?>, Integer> customItemWeights = new HashMap<>();

	public DatapackLoader(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void loadDatapacks() {
		System.out.println("Loading datapacks...");
		customItemWeights.clear();
                try {
                        loadWeightConfigurations();
                        ArmorPockets.load(resourceManager);
                        loadGlobalConfigurations();
                } catch (IOException | JsonIOException | JsonSyntaxException e) {
                        InventoryWeight.LOGGER.error("Failed to load weight configurations", e);
                }
        }

        private void loadWeightConfigurations() throws IOException {
                System.out.println("Loading weight configurations...");
                loadWeightResources("item_weights");
                loadWeightResources("inventoryweight/items");
        }

        private void loadWeightResources(String path) throws IOException {
                for (Map.Entry<Identifier, List<Resource>> entry : resourceManager.findAllResources(path, id -> id.getPath().endsWith(".json")).entrySet()) {
                        Identifier identifier = entry.getKey();

                        if (identifier.getNamespace().equals(InventoryWeight.MOD_ID)) {
                                for (Resource resource : entry.getValue()) {
                                        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                                                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                                                for (Map.Entry<String, JsonElement> itemEntry : jsonObject.entrySet()) {
                                                        if (itemEntry.getKey().equals("requires")) {
                                                                if (itemEntry.getValue() instanceof JsonPrimitive mod) {
                                                                        if (!FabricLoader.getInstance().isModLoaded(mod.getAsString())) return;
                                                                }
                                                        }
                                                        Optional<Identifier> itemId = Optional.ofNullable(Identifier.tryParse(itemEntry.getKey()));
                                                        itemId.ifPresent(id -> processWeight(id, itemEntry.getValue()));
                                                }
                                        } catch (Exception e) {
                                                InventoryWeight.LOGGER.error("Failed to process resource: {} - {}", resource, e.getMessage());
                                        }
                                }
                        }
                }
        }

	private void processWeight(Identifier id, JsonElement jsonElement) {
		if (!Registries.ITEM.contains(RegistryKey.of(Registries.ITEM.getKey(), id))) {
			InventoryWeight.LOGGER.warn("Could not find item {} from item weight configuration", id);
			return;
		}

		Item item = Registries.ITEM.get(id);

		if (jsonElement instanceof JsonObject jsonObject) {
			int weight = jsonObject.get("weight").getAsInt();

			if (jsonObject.has("requirements")) {
				for (Map.Entry<String, JsonElement> requirementEntry :jsonObject.getAsJsonObject("requirements").entrySet()) {
					RequirementRegistry.REGISTRY.get(requirementEntry.getKey()).ifPresent(requirement -> {
						switch (requirement.getId()) {
							case "nbt" -> processNbtRequirement(item, weight, requirementEntry.getValue().getAsJsonObject());
							case "enchantment" -> processEnchantmentRequirement(item, weight, requirementEntry.getValue().getAsJsonObject());
							case "durability" -> processDurabilityRequirement(item, weight, requirementEntry.getValue().getAsJsonObject());
							default -> InventoryWeight.LOGGER.error("Unknown requirement: {}", requirement.getId());
						}
					});
				}
			} else {
				customItemWeights.put(new SimpleItemRequirement(item), weight);
			}
		} else if (jsonElement instanceof JsonPrimitive primitive) {
			customItemWeights.put(new SimpleItemRequirement(item), primitive.getAsInt());
		} else {
			InventoryWeight.LOGGER.error("Invalid weight configuration for item: {}", id);
		}
	}

	private void processNbtRequirement(Item item, int weight, JsonObject nbtRequirement) {
		String tag = nbtRequirement.get("tag").getAsString();
		NbtElement value = nbtRequirement.has("value")
				? parseNbtElement(nbtRequirement.get("value"))
				: null;

		customItemWeights.put(new NbtRequirement(item, tag, value), weight);
	}

	private void processEnchantmentRequirement(Item item, int weight, JsonObject enchantmentRequirement) {
		String enchantment = enchantmentRequirement.get("enchantment").getAsString();
		int level = enchantmentRequirement.get("level").getAsInt();

		customItemWeights.put(new EnchantmentRequirement(item, enchantment, level), weight);
	}

	private void processDurabilityRequirement(Item item, int weight, JsonObject durabilityRequirement) {
		int min = durabilityRequirement.get("min").getAsInt();
		int max = durabilityRequirement.get("max").getAsInt();
		boolean smoothRange = durabilityRequirement.has("smoothRange") && durabilityRequirement.get("smoothRange").getAsBoolean();

		customItemWeights.put(new DurabilityRequirement(item, min, max, smoothRange), weight);
	}

	private void loadGlobalConfigurations() throws IOException {
		Map<Identifier, Resource> resources = resourceManager.findResources(
				"inventoryweight",
				path -> path.getPath().equals("config.json")
		);

		for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {
			try (InputStreamReader reader = new InputStreamReader(resourceEntry.getValue().getInputStream())) {
				JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
				if (jsonObject.has("maxWeight")) {
					InventoryWeightConfig.SERVER.maxWeight = jsonObject.get("maxWeight").getAsInt();
				} else if (jsonObject.has("overloadStrength")) {
					InventoryWeightConfig.SERVER.overloadStrength = jsonObject.get("overloadStrength").getAsFloat();
				}
			}
		}
	}

        static NbtElement parseNbtElement(JsonElement element) {
		if (element instanceof JsonPrimitive primitive) {
			if (primitive.isString()) {
				return NbtString.of(primitive.getAsString());
			} else if (primitive.isNumber()) {
				return NbtInt.of(primitive.getAsInt());
			} else if (primitive.isBoolean()) {
				return NbtByte.of(primitive.getAsBoolean());
			}
		} else if (element instanceof JsonObject object) {
			NbtCompound compound = new NbtCompound();
			for (String key : object.keySet()) {
				compound.put(key, parseNbtElement(object.get(key)));
			}
			return compound;
		}
		InventoryWeight.LOGGER.error("Unsupported NBT value type: {}", element);
		return NbtString.of(element.toString());
	}
}