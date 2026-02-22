package com.megatrex4.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class PlayerWeightComponentRegistry implements EntityComponentInitializer {
    public static final ComponentKey<PlayerWeightComponent> PLAYER_WEIGHT =
            ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "player_weight"), PlayerWeightComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Register the component for players
        registry.registerForPlayers(
                PLAYER_WEIGHT,
                player -> new PlayerWeightComponentImpl(player),
                RespawnCopyStrategy.LOSSLESS_ONLY
        );
    }
}
