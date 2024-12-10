package com.megatrex4.inventoryweight.components;

import com.megatrex4.inventoryweight.config.InventoryWeightConfig;
import com.megatrex4.inventoryweight.effect.InventoryWeightEffects;
import com.megatrex4.inventoryweight.util.Util;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class WeightComponent implements ServerTickingComponent, AutoSyncedComponent {

	private final PlayerEntity player;
	private int weight;

	public WeightComponent(PlayerEntity player) {
		this.player = player;
        this.weight = 0;
	}

	public int getWeight() {
        return weight;
    }

	@Override
	public void readFromNbt(NbtCompound tag) {
		this.weight = tag.getInt("weight");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putInt("weight", weight);
	}

	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity player) {
		buf.writeVarInt(this.weight);
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		this.weight = buf.readVarInt();
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player) {
		return player == this.player;
	}

	@Override
	public void serverTick() {
		int weight = 0;
		for (ItemStack stack : player.getInventory().main.toArray(new ItemStack[0])) {
			if (stack.hasNbt() && stack.getNbt().contains("weight"))
				weight += stack.getNbt().getInt("weight") * stack.getCount();
			else
				weight += Util.getWeight(stack);
		}
		for (ItemStack stack : player.getInventory().player.getArmorItems()) {
			if (stack.hasNbt() && stack.getNbt().contains("weight"))
				weight += stack.getNbt().getInt("weight");
			else
				weight += Util.getWeight(stack);
		}
		for (ItemStack stack : player.getInventory().offHand.toArray(new ItemStack[0])) {
			if (stack.hasNbt() && stack.getNbt().contains("weight"))
				weight += stack.getNbt().getInt("weight") * stack.getCount();
			else
				weight += Util.getWeight(stack);
		}
		this.weight = weight;
		if (!player.isCreative() && !player.isSpectator()) {
			if (weight >= InventoryWeightConfig.SERVER.maxWeight) {
				player.addStatusEffect(new StatusEffectInstance(InventoryWeightEffects.OVERLOAD, 20, (int) Util.getReductionFactor(weight, 0.1f, 1), true, false, false));
			} else if (InventoryWeightConfig.SERVER.realisticMode) {
				Util.applyWeight(player, weight);
			}
			InventoryWeightComponents.WEIGHT.sync(player);
		} else {
			Util.applyWeight(player, 0, 0, 0);
		}
	}
}
