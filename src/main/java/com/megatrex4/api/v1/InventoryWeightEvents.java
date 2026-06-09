package com.megatrex4.api.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class InventoryWeightEvents {
    private InventoryWeightEvents() {}

    public static final Event<ModifyItemWeight> MODIFY_ITEM_WEIGHT = EventFactory.createArrayBacked(
            ModifyItemWeight.class,
            listeners -> (stack, context, current) -> {
                WeightResult result = current;
                for (ModifyItemWeight listener : listeners) {
                    result = listener.modify(stack, context, result).sanitized();
                }
                return result;
            }
    );

    public static final Event<ModifyPlayerInventoryWeight> MODIFY_PLAYER_INVENTORY_WEIGHT = EventFactory.createArrayBacked(
            ModifyPlayerInventoryWeight.class,
            listeners -> (player, current) -> {
                WeightResult result = current;
                for (ModifyPlayerInventoryWeight listener : listeners) {
                    result = listener.modify(player, result).sanitized();
                }
                return result;
            }
    );

    public static final Event<ModifyMaxWeight> MODIFY_MAX_WEIGHT = EventFactory.createArrayBacked(
            ModifyMaxWeight.class,
            listeners -> (player, current) -> {
                float result = current;
                for (ModifyMaxWeight listener : listeners) {
                    result = Math.max(1.0f, listener.modify(player, result));
                }
                return result;
            }
    );

    public static final Event<OverloadChanged> OVERLOAD_CHANGED = EventFactory.createArrayBacked(
            OverloadChanged.class,
            listeners -> (player, overloaded) -> {
                for (OverloadChanged listener : listeners) {
                    listener.onOverloadChanged(player, overloaded);
                }
            }
    );

    @FunctionalInterface
    public interface ModifyItemWeight {
        WeightResult modify(ItemStack stack, WeightContext context, WeightResult current);
    }

    @FunctionalInterface
    public interface ModifyPlayerInventoryWeight {
        WeightResult modify(ServerPlayerEntity player, WeightResult current);
    }

    @FunctionalInterface
    public interface ModifyMaxWeight {
        float modify(ServerPlayerEntity player, float currentMaxWeight);
    }

    @FunctionalInterface
    public interface OverloadChanged {
        void onOverloadChanged(ServerPlayerEntity player, boolean overloaded);
    }
}
