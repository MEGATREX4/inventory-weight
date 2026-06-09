package com.megatrex4.api.v1;

import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface CapacityProvider {
    CapacityModifier getCapacityModifier(ServerPlayerEntity player);
}
