package com.megatrex4.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class NbtWeightHandler {

	public final String name;
	public final Identifier item;
	private final Map<String, Map<String, Integer>> valuesWhenNbt = new HashMap<>();

	public NbtWeightHandler(String name, Identifier item) {
		this.name = name;
		this.item = item;
	}

	public void addValue(String nbtKey, String value, int weight) {
		Map<String, Integer> map = new HashMap<>();
		map.put(value, weight);
		valuesWhenNbt.put(nbtKey, map);
	}

	public void removeValue(String nbtKey, String value) {
		valuesWhenNbt.remove(nbtKey);
	}

	public static NbtWeightHandler parse (String key, JsonObject jsonObject) {
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
}
