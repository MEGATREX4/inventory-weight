package com.megatrex4.api.v1;

import com.megatrex4.InventoryWeight;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightAttributes {
    public static final Identifier GENERIC_MAX_WEIGHT_ID =
            new Identifier(MOD_ID, "generic.max_weight");

    public static final RegistryEntry<EntityAttribute> GENERIC_MAX_WEIGHT =
            Registry.registerReference(
                    Registries.ATTRIBUTE,
                    GENERIC_MAX_WEIGHT_ID,
                    new ClampedEntityAttribute(
                            "attribute.name.inventoryweight.generic.max_weight",
                            0.0D,
                            0.0D,
                            1_000_000_000.0D
                    ).setTracked(true)
            );

    private InventoryWeightAttributes() {}

    public static void register() {
        InventoryWeight.LOGGER.info(
                "Registered Inventory Weight attribute: {}",
                GENERIC_MAX_WEIGHT_ID
        );
    }

    @Nullable
    public static EntityAttributeInstance getInstance(PlayerEntity player) {
        return player.getAttributes().getCustomInstance(GENERIC_MAX_WEIGHT);
    }

    public static boolean hasAttribute(PlayerEntity player) {
        return player.getAttributes().hasAttribute(GENERIC_MAX_WEIGHT);
    }

    public static double getValue(PlayerEntity player) {
        EntityAttributeInstance instance = getInstance(player);
        return instance == null ? 0.0D : instance.getValue();
    }

    public static double getBaseValue(PlayerEntity player) {
        EntityAttributeInstance instance = getInstance(player);
        return instance == null ? 0.0D : instance.getBaseValue();
    }

    public static EntityAttributeModifier createModifier(
            UUID uuid,
            String name,
            double value,
            EntityAttributeModifier.Operation operation
    ) {
        return new EntityAttributeModifier(uuid, name, value, operation);
    }
}