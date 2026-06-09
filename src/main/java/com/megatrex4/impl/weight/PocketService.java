package com.megatrex4.impl.weight;

import com.megatrex4.InventoryWeight;
import com.megatrex4.api.v1.PocketProvider;
import com.megatrex4.impl.registry.PrioritizedRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public final class PocketService {
    private final PrioritizedRegistry<PocketProvider> providers;

    public PocketService(PrioritizedRegistry<PocketProvider> providers) {
        this.providers = providers;
    }

    public OptionalInt getPockets(ItemStack stack, @Nullable PlayerEntity wearer) {
        if (stack == null || stack.isEmpty()) {
            return OptionalInt.empty();
        }

        for (PrioritizedRegistry.Entry<PocketProvider> entry : providers.entries()) {
            try {
                OptionalInt pockets = entry.value().getPockets(stack, wearer);
                if (pockets.isPresent()) {
                    return OptionalInt.of(Math.max(0, pockets.getAsInt()));
                }
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Pocket provider {} failed for {}", entry.id(), stack, e);
            }
        }

        return OptionalInt.empty();
    }
}
