package com.megatrex4.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static com.megatrex4.InventoryWeight.MOD_ID;

public class InventoryWeightConfig {

    private static Server SERVER_INSTANCE = null;
    private static Client CLIENT_INSTANCE = null;

    public static Server getServer() {
        if (SERVER_INSTANCE == null) {
            SERVER_INSTANCE = ConfigApiJava.registerAndLoadConfig(Server::new, RegisterType.BOTH);
        }
        return SERVER_INSTANCE;
    }

    public static Client getClient() {
        if (CLIENT_INSTANCE == null) {
            CLIENT_INSTANCE = ConfigApiJava.registerAndLoadConfig(Client::new, RegisterType.BOTH);
        }
        return CLIENT_INSTANCE;
    }

    @Version(version = 1)
    public static class Server extends Config {

        public Server() {
            super(new Identifier(MOD_ID, "server-config"));
        }

        @Comment("Maximum inventory weight capacity in grams")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float maxWeight = 90_000.0f;

        @Comment("Weight of a single pocket slot in grams")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float pocketWeight = 9_000.0f;

        @Comment("Enable realistic weight calculations")
        public boolean realisticMode = false;

        @Comment("Strength multiplier for overload penalties (0.0 to disable)")
        @ValidatedFloat.Restrict(min = 0f, max = Float.MAX_VALUE)
        public float overloadPenaltyStrength = 1.0f;

        @Comment("Base weight for bucket items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float bucketWeight = 120.0f;

        @Comment("Base weight for bottle items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float bottleWeight = 60.0f;

        @Comment("Base weight for block items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float blockWeight = 240.0f;

        @Comment("Base weight for ingot items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float ingotWeight = 90.0f;

        @Comment("Base weight for nugget items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float nuggetWeight = 10.0f;

        @Comment("Base weight for generic items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float itemWeight = 50.0f;

        @Comment("Weight for creative items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float creativeWeight = 30_000.0f;
    }

    @Version(version = 1)
    public static class Client extends Config {

        public Client() {
            super(new Identifier(MOD_ID, "client-config"));
        }

        @Comment("HUD display position (BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT, TOP_LEFT, CUSTOM)")
        public String hudPosition = "BOTTOM_RIGHT";

        @Comment("X offset for custom HUD position (0.0 to 1.0)")
        @ValidatedFloat.Restrict(min = 0f, max = 1f)
        public float xOffset = 0.5f;

        @Comment("Y offset for custom HUD position (0.0 to 1.0)")
        @ValidatedFloat.Restrict(min = 0f, max = 1f)
        public float yOffset = 0.5f;

        @Comment("Show weight percentage in HUD")
        public boolean showPercentage = true;

        @Comment("Show remaining capacity in HUD")
        public boolean showRemaining = true;
    }
}
