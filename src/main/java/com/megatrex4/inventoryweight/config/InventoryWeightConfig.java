package com.megatrex4.inventoryweight.config;

import com.megatrex4.inventoryweight.InventoryWeight;
import com.megatrex4.inventoryweight.hud.WeightHudPosition;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.minecraft.util.Identifier;

public class InventoryWeightConfig {

	public static Server SERVER = ConfigApiJava.registerAndLoadConfig(Server::new, RegisterType.BOTH);
	public static Client CLIENT = ConfigApiJava.registerAndLoadConfig(Client::new, RegisterType.CLIENT);

	@Version(version = 1)
	public static class Server extends Config {

		public Server() {
			super(new Identifier(InventoryWeight.MOD_ID, "server-config"));
		}

		public boolean realisticMode = false;

		@Comment("Maximum weight before overload is applied")
		public int maxWeight = 100_000;

		@Comment("The strength of the overload effect")
		@ValidatedFloat.Restrict(min = 0.1F, max = 10)
		public float overloadStrength = 1.0f;

		@Comment("The weight of each armor pocket")
		public int pocketWeight = 15000;
	}

	@Version(version = 1)
	public static class Client extends Config {

		public Client() {
			super(new Identifier(InventoryWeight.MOD_ID, "client-config"));
		}

		public IconSettings iconSettings = new IconSettings();
		public static class IconSettings extends ConfigSection {
			@Translation(prefix = "option.inventoryweight.config.weight_hud_position")
			@Comment("The position of the weight HUD icon")
			public WeightHudPosition weightHudPosition = WeightHudPosition.BOTTOM_RIGHT;

			@Comment("The X offset of the weight icon on custom mode")
			public int xOffset;
			@Comment("The Y offset of the weight icon on custom mode")
			public int yOffset;
		}

	}
}
