package com.megatrex4.impl.config;

/**
 * Facade used by calculation code. On dedicated servers it reads fzzy_config.
 * On clients it can use the latest server-synced values for correct tooltips.
 */
public final class WeightSettings {
    private static volatile ServerWeightSettings syncedServerSettings;

    private WeightSettings() {}

    public static ServerWeightSettings get() {
        ServerWeightSettings synced = syncedServerSettings;
        return synced != null ? synced : ServerWeightSettings.fromConfig();
    }

    public static void setSyncedServerSettings(ServerWeightSettings settings) {
        syncedServerSettings = settings;
    }

    public static void clearSyncedServerSettings() {
        syncedServerSettings = null;
    }
}
