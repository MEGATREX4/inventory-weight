package com.megatrex4.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public class NbtWeightHandler {

	private static final Map<Identifier, NbtWeightHandler> handlers = new HashMap<>();

	// Existing fields and constructor
	public final String name;
	public final Identifier item;
	private final Map<String, Map<String, Integer>> valuesWhenNbt = new HashMap<>();

	public NbtWeightHandler(String name, Identifier item) {
		this.name = name;
		this.item = item;
	}

	public void addValue(String nbtKey, String value, int weight) {
		valuesWhenNbt.computeIfAbsent(nbtKey, k -> new HashMap<>()).put(value, weight);
	}

	public static void registerHandler(NbtWeightHandler handler) {
		handlers.put(handler.item, handler);
	}

	public static NbtWeightHandler parse(String key, JsonObject jsonObject) {
		Identifier item = Identifier.tryParse(jsonObject.get("item").getAsString());
		JsonObject weights = jsonObject.get("weightWhenNbt").getAsJsonObject();
		NbtWeightHandler nbt = new NbtWeightHandler(key, item);

		for (Map.Entry<String, JsonElement> entry : weights.entrySet()) {
			JsonObject keyObject = entry.getValue().getAsJsonObject();
			String value = keyObject.get("value").getAsString();
			int weight = keyObject.get("weight").getAsInt();
			nbt.addValue(entry.getKey(), value, weight);
		}

		return nbt;
	}



	/**
	 * Retrieves the weight of an item based on its NBT data.
	 * Example NBT data structure:
	 * "bundleWeight": {
	 *     "item": "minecraft:bundle",
	 *     "weightWhenNbt": {
	 *       "Items": {
	 *         "value": "minecraft:glass",
	 *         "weight": 100
	 *       }
	 *     }
	 *   }
	 *
	 * @param stack The ItemStack to retrieve the weight from.
	 * @return The weight of the ItemStack, or null if no matching NBT weight is found.
	 */

	public static Float getWeightFromNbt(ItemStack stack)
	{
		Identifier itemId = new Identifier(stack.getItem().toString());
		NbtWeightHandler handler = handlers.get(itemId);

		if (handler != null) {
			NbtCompound nbt = stack.getNbt();
			if (nbt != null) {
				for (Map.Entry<String, Map<String, Integer>> entry : handler.valuesWhenNbt.entrySet()) {
					String key = entry.getKey();
					if (nbt.contains(key)) {
						if (nbt.get(key) instanceof NbtList) {
							NbtList itemList = nbt.getList(key, NbtType.COMPOUND);
							for (int i = 0; i < itemList.size(); i++) {
								NbtCompound itemCompound = itemList.getCompound(i);
								String nbtValue = itemCompound.getString("id");
								Integer weight = entry.getValue().get(nbtValue);
								if (weight != null) {
									return weight.floatValue();
								}
							}
						} else {
							String nbtValue = nbt.getString(key);
							Integer weight = entry.getValue().get(nbtValue);
							if (weight != null) {
								return weight.floatValue();
							}
						}
					}
				}
			}
		}
		return null; // Return null if no matching NBT weight is found
	}
}
