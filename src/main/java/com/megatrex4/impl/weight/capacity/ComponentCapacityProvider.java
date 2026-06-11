package com.megatrex4.impl.weight.capacity;

import com.megatrex4.api.v1.CapacityModifier;
import com.megatrex4.api.v1.CapacityProvider;
import com.megatrex4.component.PlayerWeightComponentRegistry;
import net.minecraft.server.level.ServerPlayer;

public final class ComponentCapacityProvider implements CapacityProvider {
    @Override
    public CapacityModifier getCapacityModifier(ServerPlayer player) {
        return PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player)
                .map(component -> CapacityModifier.additive(component.getCapacityBonus()))
                .orElseGet(CapacityModifier::none);
    }
}
