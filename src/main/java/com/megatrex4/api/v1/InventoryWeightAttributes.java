package com.megatrex4.api.v1;

import com.megatrex4.InventoryWeight;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.megatrex4.InventoryWeight.MOD_ID;

/**
 * Public attribute API for MT Inventory Weight.
 *
 * This attribute is an additional max-weight contribution, not the whole final max weight.
 *
 * Final max weight calculation:
 *
 * server config maxWeight
 * + inventoryweight:generic.max_weight attribute value
 * + CCA capacity bonus
 * + armor pocket capacity
 * + add-on capacity providers
 * = final max weight
 *
 * Other mods can add normal Minecraft attribute modifiers to GENERIC_MAX_WEIGHT to increase or
 * reduce a player's max carry weight. This is useful for level systems, origins/classes, trinkets,
 * equipment, skills, and other progression mods.
 */
public final class InventoryWeightAttributes {
    public static final Identifier GENERIC_MAX_WEIGHT_ID = new Identifier(MOD_ID, "generic.max_weight");

    public static final EntityAttribute GENERIC_MAX_WEIGHT = Registry.register(
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

    /**
     * Forces the class to load and the attribute to be registered.
     */
    public static void register() {
        InventoryWeight.LOGGER.info("Registered Inventory Weight attribute: {}", GENERIC_MAX_WEIGHT_ID);
    }

    @Nullable
    public static EntityAttributeInstance getInstance(PlayerEntity player) {
        return player.getAttributes().getCustomInstance(GENERIC_MAX_WEIGHT);
    }

    public static boolean hasAttribute(PlayerEntity player) {
        return player.getAttributes().hasAttribute(GENERIC_MAX_WEIGHT);
    }

    /**
     * Returns the current value of the Inventory Weight max-weight attribute contribution.
     * This is added on top of the configured server maxWeight.
     */
    public static double getValue(PlayerEntity player) {
        EntityAttributeInstance instance = getInstance(player);
        return instance == null ? 0.0D : instance.getValue();
    }

    /**
     * Returns the base value of the Inventory Weight max-weight attribute contribution.
     */
    public static double getBaseValue(PlayerEntity player) {
        EntityAttributeInstance instance = getInstance(player);
        return instance == null ? 0.0D : instance.getBaseValue();
    }

    public static EntityAttributeModifier createModifier(UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) {
        return new EntityAttributeModifier(uuid, name, value, operation);
    }
}
