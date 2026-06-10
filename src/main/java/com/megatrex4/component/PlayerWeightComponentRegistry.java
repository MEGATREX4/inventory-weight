package com.megatrex4.component;


import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class PlayerWeightComponentRegistry implements EntityComponentInitializer {
    public static final ComponentKey<PlayerWeightComponent> PLAYER_WEIGHT = ComponentRegistry.getOrCreate(
            new Identifier(MOD_ID, "player_weight"),
            PlayerWeightComponent.class
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                PLAYER_WEIGHT,
                PlayerWeightComponentImpl::new,
                RespawnCopyStrategy.LOSSLESS_ONLY
        );
    }
}
