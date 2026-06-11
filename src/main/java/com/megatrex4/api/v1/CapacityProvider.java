package com.megatrex4.api.v1;

import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface CapacityProvider {
    CapacityModifier getCapacityModifier(ServerPlayer player);
}