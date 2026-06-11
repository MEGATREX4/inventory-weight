package com.megatrex4.config;

import com.megatrex4.impl.InventoryWeightDefaults;
import com.megatrex4.impl.config.InventoryWeightConfigEvents;
import com.megatrex4.impl.config.WeightSettings;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import static com.megatrex4.InventoryWeight.MOD_ID;

public final class InventoryWeightConfig {
    public static final Identifier SERVER_CONFIG_ID = Identifier.fromNamespaceAndPath(MOD_ID, "server-config");
    public static final Identifier CLIENT_CONFIG_ID = Identifier.fromNamespaceAndPath(MOD_ID, "client-config");

    private static Server SERVER_INSTANCE;
    private static Client CLIENT_INSTANCE;

    private InventoryWeightConfig() {}

    public static Server getServer() {
        if (SERVER_INSTANCE == null) {
            SERVER_INSTANCE = ConfigApiJava.registerAndLoadConfig(Server::new, RegisterType.SERVER);
        }
        return SERVER_INSTANCE;
    }

    public static Client getClient() {
        if (CLIENT_INSTANCE == null) {
            CLIENT_INSTANCE = ConfigApiJava.registerAndLoadConfig(Client::new, RegisterType.CLIENT);
        }
        return CLIENT_INSTANCE;
    }

    @Version(version = 1)
    public static class Server extends Config {
        public Server() {
            super(SERVER_CONFIG_ID);
        }

        @Comment("Maximum inventory weight capacity in grams")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float maxWeight = InventoryWeightDefaults.MAX_WEIGHT;

        @Comment("Weight capacity added by one armor pocket")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float pocketWeight = InventoryWeightDefaults.POCKET_WEIGHT;

        @Comment("Enable gradual penalties before overload")
        public boolean realisticMode = false;

        @Comment("Strength multiplier for overload penalties. 0 disables attribute/jump penalties.")
        @ValidatedFloat.Restrict(min = 0f, max = Float.MAX_VALUE)
        public float overloadPenaltyStrength = InventoryWeightDefaults.OVERLOAD_PENALTY_STRENGTH;

        @Comment("Base weight for bucket-like items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float bucketWeight = InventoryWeightDefaults.BUCKET_WEIGHT;

        @Comment("Base weight for bottle/potion-like items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float bottleWeight = InventoryWeightDefaults.BOTTLE_WEIGHT;

        @Comment("Base weight for blocks")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float blockWeight = InventoryWeightDefaults.BLOCK_WEIGHT;

        @Comment("Base weight for ingots/gems/alloys/shards")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float ingotWeight = InventoryWeightDefaults.INGOT_WEIGHT;

        @Comment("Base weight for nuggets")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float nuggetWeight = InventoryWeightDefaults.NUGGET_WEIGHT;

        @Comment("Base weight for generic items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float itemWeight = InventoryWeightDefaults.ITEM_WEIGHT;

        @Comment("Weight for creative/technical items")
        @ValidatedFloat.Restrict(min = 1f, max = Float.MAX_VALUE)
        public float creativeWeight = InventoryWeightDefaults.CREATIVE_WEIGHT;

        @Override
        public void onUpdateServer(ServerPlayer player) {
            if (player == null) {
                return;
            }

            InventoryWeightConfigEvents.applyServerConfigChange(
                    player.level().getServer(),
                    "fzzy_config direct Config.onUpdateServer"
            );
        }

        @Override
        public void onUpdateClient() {
            // Keep local tooltip calculations current after the client receives or edits the synced config.
            WeightSettings.refreshFromConfig();
        }
    }

    @Version(version = 2)
    public static class Client extends Config {
        public Client() {
            super(CLIENT_CONFIG_ID);
        }

        @Comment("HUD display style. SPRITE uses the old bundle icon display. BAR uses a simple rectangular bar.")
        public HudStyle hudStyle = HudStyle.SPRITE;

        @Comment("HUD display position")
        public HudPosition hudPosition = HudPosition.BOTTOM_RIGHT;

        @Comment("X offset for CUSTOM HUD position, 0.0 to 1.0")
        @ValidatedFloat.Restrict(min = 0f, max = 1f)
        public float xOffset = 0.5f;

        @Comment("Y offset for CUSTOM HUD position, 0.0 to 1.0")
        @ValidatedFloat.Restrict(min = 0f, max = 1f)
        public float yOffset = 0.5f;

        @Comment("Sprite HUD size in pixels. The source textures are 16x16 and are scaled to this size.")
        public int spriteSize = 16;

        @Comment("Bar HUD width in pixels")
        public int barWidth = 82;

        @Comment("Bar HUD height in pixels")
        public int barHeight = 10;

        @Comment("HUD text display mode")
        public HudTextMode hudTextMode = HudTextMode.CURRENT_MAX;

        @Comment("Text position relative to the HUD element")
        public HudTextPosition hudTextPosition = HudTextPosition.BELOW;

        @Comment("Extra horizontal text offset in pixels. Also used as the CUSTOM text X offset relative to the HUD element.")
        public int hudTextXOffset = 0;

        @Comment("Extra vertical text offset in pixels. Also used as the CUSTOM text Y offset relative to the HUD element.")
        public int hudTextYOffset = 0;

        @Comment("Prevent HUD text from rendering outside the screen. If ABOVE/BELOW/LEFT/RIGHT would leave the screen, the text is flipped or clamped.")
        public boolean keepHudTextOnScreen = true;

        @Comment("HUD text color as RGB integer, for example 16777215 is white")
        public int hudTextColor = 0xFFFFFF;

        @Comment("Draw a shadow behind HUD text")
        public boolean hudTextShadow = true;

        @Comment("Legacy option. If hudTextMode is unavailable during migration, this controls whether current/max text is shown.")
        public boolean showNumbers = true;

        @Comment("Legacy option. If hudTextMode is unavailable during migration and showNumbers is false, this controls whether percentage text is shown.")
        public boolean showPercentage = true;

        @Comment("Show item weight tooltips")
        public boolean showTooltips = true;
    }
}
