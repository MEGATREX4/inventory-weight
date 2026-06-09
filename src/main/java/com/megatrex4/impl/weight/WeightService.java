package com.megatrex4.impl.weight;

import com.megatrex4.InventoryWeight;
import com.megatrex4.api.v1.InventoryWeightEvents;
import com.megatrex4.api.v1.ItemWeightProvider;
import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightLookup;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.impl.registry.PrioritizedRegistry;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public final class WeightService implements WeightLookup {
    public static final int MAX_RECURSION_DEPTH = 8;

    private final PrioritizedRegistry<ItemWeightProvider> providers;

    public WeightService(PrioritizedRegistry<ItemWeightProvider> providers) {
        this.providers = providers;
    }

    @Override
    public WeightResult getWeight(ItemStack stack, WeightContext context) {
        if (stack == null || stack.isEmpty()) {
            return WeightResult.ZERO;
        }
        if (context.depth() > MAX_RECURSION_DEPTH) {
            InventoryWeight.LOGGER.warn("Maximum weight recursion depth exceeded while calculating {}", stack);
            return WeightResult.ZERO;
        }

        for (PrioritizedRegistry.Entry<ItemWeightProvider> entry : providers.entries()) {
            try {
                Optional<WeightResult> maybeResult = entry.value().getWeight(stack, context, this);
                if (maybeResult.isPresent()) {
                    WeightResult result = maybeResult.get().sanitized();
                    return InventoryWeightEvents.MODIFY_ITEM_WEIGHT.invoker()
                            .modify(stack, context, result)
                            .sanitized();
                }
            } catch (Exception e) {
                InventoryWeight.LOGGER.error("Weight provider {} failed for {}", entry.id(), stack, e);
            }
        }

        return WeightResult.ZERO;
    }
}
