package com.megatrex4.impl.config;

/**
 * Facade used by calculation code.
 *
 * The server normally reads the live fzzy_config values. Clients use the latest server-synced
 * snapshot for tooltips and display values. In singleplayer/integrated-server environments the
 * same static JVM state can be visible from both logical sides, so server config update handlers
 * refresh this snapshot too.
 */
public final class WeightSettings {
    private static volatile ServerWeightSettings syncedServerSettings;

    private WeightSettings() {}

    public static ServerWeightSettings get() {
        ServerWeightSettings synced = syncedServerSettings;
        return synced != null ? synced : ServerWeightSettings.fromConfig();
    }

    public static ServerWeightSettings refreshFromConfig() {
        ServerWeightSettings settings = ServerWeightSettings.fromConfig();
        syncedServerSettings = settings;
        return settings;
    }

    public static void setSyncedServerSettings(ServerWeightSettings settings) {
        syncedServerSettings = settings;
    }

    public static void clearSyncedServerSettings() {
        syncedServerSettings = null;
    }
}
