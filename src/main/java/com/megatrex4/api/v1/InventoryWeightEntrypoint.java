package com.megatrex4.api.v1;

/**
 * Add-ons register their Inventory Weight integration through this Fabric entrypoint:
 *
 * "entrypoints": {
 *   "inventoryweight": ["com.example.ExampleInventoryWeightAddon"]
 * }
 */
public interface InventoryWeightEntrypoint {
    void registerInventoryWeight(InventoryWeightRegistrar registrar);
}
