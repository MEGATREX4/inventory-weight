package com.megatrex4.api.v1;

import com.megatrex4.InventoryWeight;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightAttributes {
    public static final Identifier GENERIC_MAX_WEIGHT_ID =
            Identifier.fromNamespaceAndPath(MOD_ID, "generic.max_weight");

    public static final Holder<Attribute> GENERIC_MAX_WEIGHT =
            Registry.registerForHolder(
                    BuiltInRegistries.ATTRIBUTE,
                    GENERIC_MAX_WEIGHT_ID,
                    new RangedAttribute(
                            "attribute.name.inventoryweight.generic.max_weight",
                            0.0D,
                            0.0D,
                            1_000_000_000.0D
                    ).setSyncable(true)
            );

    private InventoryWeightAttributes() {}

    public static void register() {
        InventoryWeight.LOGGER.info(
                "Registered Inventory Weight attribute: {}",
                GENERIC_MAX_WEIGHT_ID
        );
    }

    @Nullable
    public static AttributeInstance getInstance(Player player) {
        return player.getAttributes().getInstance(GENERIC_MAX_WEIGHT);
    }

    public static boolean hasAttribute(Player player) {
        return player.getAttributes().hasAttribute(GENERIC_MAX_WEIGHT);
    }

    public static double getValue(Player player) {
        AttributeInstance instance = getInstance(player);
        return instance == null ? 0.0D : instance.getValue();
    }

    public static double getBaseValue(Player player) {
        AttributeInstance instance = getInstance(player);
        return instance == null ? 0.0D : instance.getBaseValue();
    }

    public static AttributeModifier createModifier(
            Identifier id,
            double value,
            AttributeModifier.Operation operation
    ) {
        return new AttributeModifier(id, value, operation);
    }
}