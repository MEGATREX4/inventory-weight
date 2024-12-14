package com.megatrex4.inventoryweight.components;

import com.megatrex4.inventoryweight.util.Util;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
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
		this.weight = Util.getWeight(player);
		if (!player.isCreative() && !player.isSpectator()) {
			if (weight > 0) Util.applyWeight(player, weight);
			InventoryWeightComponents.WEIGHT.sync(player);
		} else {
			Util.applyWeightModifiers(player, 0, 0, 0);
		}
	}
}
