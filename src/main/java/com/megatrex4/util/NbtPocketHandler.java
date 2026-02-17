package com.megatrex4.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public class NbtPocketHandler {

	private static final Map<Identifier, NbtPocketHandler> handlers = new HashMap<>();

	public final String name;
	public final Identifier item;
	private final Map<String, Map<String, Integer>> pocketsWhenNbt = new HashMap<>();

	public NbtPocketHandler(String name, Identifier item) {
		this.name = name;
		this.item = item;
	}

	public void addValue(String nbtKey, String value, int pockets) {
		pocketsWhenNbt.computeIfAbsent(nbtKey, k -> new HashMap<>()).put(value, pockets);
	}

	public static void registerHandler(NbtPocketHandler handler) {
		handlers.put(handler.item, handler);
	}

	public static NbtPocketHandler parse(String key, JsonObject jsonObject) {
		Identifier item = Identifier.tryParse(jsonObject.get("item").getAsString());
		JsonObject pockets = jsonObject.get("pocketsWhenNbt").getAsJsonObject();
		NbtPocketHandler nbt = new NbtPocketHandler(key, item);

		for (Map.Entry<String, JsonElement> entry : pockets.entrySet()) {
			JsonObject keyObject = entry.getValue().getAsJsonObject();
			String value = keyObject.get("value").getAsString();
			int pocketCount = keyObject.get("pockets").getAsInt();
			nbt.addValue(entry.getKey(), value, pocketCount);
		}

		return nbt;
	}

	/**
	 * Retrieves the pocket count for armor based on its NBT data.
	 * Useful for enchanted armor that may have different pocket capacities.
	 *
	 * @param stack The ItemStack to retrieve pocket count from.
	 * @return The pocket count for the ItemStack, or null if no matching NBT pocket definition is found.
	 */
	public static Integer getPocketsFromNbt(ItemStack stack) {
		Identifier itemId = new Identifier(stack.getItem().toString());
		NbtPocketHandler handler = handlers.get(itemId);

		if (handler != null) {
			NbtCompound nbt = stack.getNbt();
			if (nbt != null) {
				for (Map.Entry<String, Map<String, Integer>> entry : handler.pocketsWhenNbt.entrySet()) {
					String key = entry.getKey();
					if (nbt.contains(key)) {
						if (nbt.get(key) instanceof NbtList) {
							NbtList itemList = nbt.getList(key, NbtType.COMPOUND);
							for (int i = 0; i < itemList.size(); i++) {
								NbtCompound itemCompound = itemList.getCompound(i);
								String nbtValue = itemCompound.getString("id");
								Integer pockets = entry.getValue().get(nbtValue);
								if (pockets != null) {
									return pockets;
								}
							}
						} else {
							String nbtValue = nbt.getString(key);
							Integer pockets = entry.getValue().get(nbtValue);
							if (pockets != null) {
								return pockets;
							}
						}
					}
				}
			}
		}
		return null; // Return null if no matching NBT pocket definition is found
	}
}
