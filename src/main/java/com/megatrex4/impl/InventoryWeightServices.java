package com.megatrex4.impl;

import com.megatrex4.InventoryWeight;
import com.megatrex4.api.v1.*;
import com.megatrex4.impl.compat.trinkets.TrinketsCompat;
import com.megatrex4.impl.registry.PrioritizedRegistry;
import com.megatrex4.impl.weight.CapacityService;
import com.megatrex4.impl.weight.PlayerWeightService;
import com.megatrex4.impl.weight.PocketService;
import com.megatrex4.impl.weight.WeightService;
import com.megatrex4.impl.weight.capacity.ArmorPocketCapacityProvider;
import com.megatrex4.impl.weight.capacity.ComponentCapacityProvider;
import com.megatrex4.impl.weight.pocket.DatapackPocketProvider;
import com.megatrex4.impl.weight.pocket.NbtPocketProvider;
import com.megatrex4.impl.weight.pocket.VanillaArmorPocketProvider;
import com.megatrex4.impl.weight.provider.*;
import com.megatrex4.impl.weight.source.VanillaInventoryWeightSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.util.List;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightServices {
    private static final PrioritizedRegistry<ItemWeightProvider> ITEM_WEIGHT_PROVIDERS = new PrioritizedRegistry<>();
    private static final PrioritizedRegistry<PlayerWeightSource> PLAYER_WEIGHT_SOURCES = new PrioritizedRegistry<>();
    private static final PrioritizedRegistry<CapacityProvider> CAPACITY_PROVIDERS = new PrioritizedRegistry<>();
    private static final PrioritizedRegistry<PocketProvider> POCKET_PROVIDERS = new PrioritizedRegistry<>();

    private static final InventoryWeightRegistrarImpl REGISTRAR = new InventoryWeightRegistrarImpl(
            ITEM_WEIGHT_PROVIDERS,
            PLAYER_WEIGHT_SOURCES,
            CAPACITY_PROVIDERS,
            POCKET_PROVIDERS
    );

    private static final WeightService WEIGHT_SERVICE = new WeightService(ITEM_WEIGHT_PROVIDERS);
    private static final PlayerWeightService PLAYER_WEIGHT_SERVICE = new PlayerWeightService(WEIGHT_SERVICE, PLAYER_WEIGHT_SOURCES);
    private static final CapacityService CAPACITY_SERVICE = new CapacityService(CAPACITY_PROVIDERS);
    private static final PocketService POCKET_SERVICE = new PocketService(POCKET_PROVIDERS);

    private static boolean defaultsRegistered;
    private static boolean entrypointsLoaded;

    private InventoryWeightServices() {}

    public static InventoryWeightRegistrar registrar() {
        return REGISTRAR;
    }

    public static WeightService weightService() {
        return WEIGHT_SERVICE;
    }

    public static PlayerWeightService playerWeightService() {
        return PLAYER_WEIGHT_SERVICE;
    }

    public static CapacityService capacityService() {
        return CAPACITY_SERVICE;
    }

    public static PocketService pocketService() {
        return POCKET_SERVICE;
    }

    public static void registerDefaults() {
        if (defaultsRegistered) {
            InventoryWeight.LOGGER.debug("Inventory Weight default services were already registered; skipping duplicate registration.");
            return;
        }
        defaultsRegistered = true;

        InventoryWeight.LOGGER.info("Registering Inventory Weight default providers...");

        REGISTRAR.registerItemWeightProvider(id("nbt_item_weights"), 9500, new NbtItemWeightProvider());
        REGISTRAR.registerItemWeightProvider(id("datapack_item_weights"), 9000, new DatapackItemWeightProvider());
        REGISTRAR.registerItemWeightProvider(id("backpacks"), 8000, new BackpackWeightProvider());
        REGISTRAR.registerItemWeightProvider(id("shulker_boxes"), 7900, new ShulkerBoxWeightProvider());
        REGISTRAR.registerItemWeightProvider(id("vanilla_blocks"), 1000, new VanillaBlockWeightProvider());
        REGISTRAR.registerItemWeightProvider(id("vanilla_items"), 900, new VanillaItemWeightProvider());
        REGISTRAR.registerItemWeightProvider(id("fallback"), 0, new FallbackWeightProvider());

        REGISTRAR.registerPlayerWeightSource(id("vanilla_inventory"), 1000, new VanillaInventoryWeightSource());

        REGISTRAR.registerCapacityProvider(id("component_capacity_bonus"), 2000, new ComponentCapacityProvider());
        REGISTRAR.registerCapacityProvider(id("armor_pockets"), 1000, new ArmorPocketCapacityProvider());

        REGISTRAR.registerPocketProvider(id("nbt_pockets"), 9500, new NbtPocketProvider());
        REGISTRAR.registerPocketProvider(id("datapack_pockets"), 9000, new DatapackPocketProvider());
        REGISTRAR.registerPocketProvider(id("vanilla_armor_pockets"), 1000, new VanillaArmorPocketProvider());

        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            InventoryWeight.LOGGER.info("Trinkets detected; registering Inventory Weight Trinkets compatibility.");
            try {
                TrinketsCompat.register(REGISTRAR);
                InventoryWeight.LOGGER.info("Trinkets compatibility registered successfully.");
            } catch (Throwable throwable) {
                InventoryWeight.LOGGER.error("Failed to initialize Trinkets compatibility", throwable);
            }
        } else {
            InventoryWeight.LOGGER.info("Trinkets not detected; skipping Trinkets compatibility.");
        }

        InventoryWeight.LOGGER.info("Default provider registration complete: {}", describeRegisteredHooks());
    }

    public static void loadAddonEntrypoints() {
        if (entrypointsLoaded) {
            InventoryWeight.LOGGER.debug("Inventory Weight add-on entrypoints were already loaded; skipping duplicate load.");
            return;
        }
        entrypointsLoaded = true;

        List<InventoryWeightEntrypoint> entrypoints = FabricLoader.getInstance()
                .getEntrypoints("inventoryweight", InventoryWeightEntrypoint.class);

        if (entrypoints.isEmpty()) {
            InventoryWeight.LOGGER.info("No Inventory Weight add-ons found for entrypoint 'inventoryweight'.");
            return;
        }

        InventoryWeight.LOGGER.info("Found {} Inventory Weight add-on entr{} for entrypoint 'inventoryweight'.",
                entrypoints.size(), entrypoints.size() == 1 ? "y" : "ies");

        int loaded = 0;
        int failed = 0;

        for (InventoryWeightEntrypoint entrypoint : entrypoints) {
            String className = entrypoint.getClass().getName();
            InventoryWeight.LOGGER.info("Loading Inventory Weight add-on: {}", className);
            try {
                entrypoint.registerInventoryWeight(REGISTRAR);
                loaded++;
                InventoryWeight.LOGGER.info("Loaded Inventory Weight add-on: {}", className);
            } catch (Exception e) {
                failed++;
                InventoryWeight.LOGGER.error("Inventory Weight add-on entrypoint failed: {}", className, e);
            }
        }

        InventoryWeight.LOGGER.info("Inventory Weight add-on loading complete: {} loaded, {} failed. Registered hooks: {}",
                loaded, failed, describeRegisteredHooks());
    }

    public static String describeRegisteredHooks() {
        return ITEM_WEIGHT_PROVIDERS.entries().size() + " item weight providers, "
                + PLAYER_WEIGHT_SOURCES.entries().size() + " player weight sources, "
                + CAPACITY_PROVIDERS.entries().size() + " capacity providers, "
                + POCKET_PROVIDERS.entries().size() + " pocket providers";
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
