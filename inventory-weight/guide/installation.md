---
title: "Installing MT Inventory Weight"
description: "A step-by-step guide for installing MT Inventory Weight on Fabric, including required and optional dependencies."
---

# Installing MT Inventory Weight

This guide explains how to install MT Inventory Weight for Fabric.

For third-party launchers such as Prism Launcher, Modrinth App, CurseForge App, MultiMC, or ATLauncher, the general steps are the same, but the interface may be different.

::: warning
Only download mods from trusted sources such as Modrinth, CurseForge, or the official GitHub repository.
:::

## Requirements

MT Inventory Weight requires Fabric.

For the current refactored version, use the Minecraft version listed on the download page. The version discussed in this documentation targets:

```text
Minecraft
Fabric Loader
```

## Required Dependencies

Install the dependencies listed on the MT Inventory Weight download page.

Common required dependencies include:

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [fzzy_config](https://modrinth.com/mod/fzzy-config)
- [Cardinal Components API](https://modrinth.com/mod/cardinal-components-api), if not bundled in your downloaded build
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin), if required by your fzzy_config version and not pulled automatically

::: tip
If you install through the Modrinth App or CurseForge App, dependencies may be downloaded automatically. If you install manually, check the dependency list on the download page.
:::

## Optional Dependencies

These are not required, but add extra functionality:

- [Mod Menu](https://modrinth.com/mod/modmenu) — easier access to the config screen
- [Trinkets](https://modrinth.com/mod/trinkets) — equipped trinkets count toward inventory weight

Cloth Config is **not** required by MT Inventory Weight anymore.

## Easy Installation with Modrinth App or CurseForge App

The easiest way to install MT Inventory Weight is with a launcher that can automatically manage modpacks and dependencies.

You can use:

- [Modrinth App](https://modrinth.com/app)
- [CurseForge App](https://www.curseforge.com/download/app)

### Modrinth App

1. Open the Modrinth App.
2. Create or open a Fabric instance for the Minecraft version you want to play.
3. Go to **Add Content**.
4. Search for **Inventory Weight** or **MT Inventory Weight**.
5. Install the mod.
6. Install required dependencies if the app does not add them automatically.
7. Launch the instance.

### CurseForge App

1. Open the CurseForge App.
2. Create or open a Fabric profile for the Minecraft version you want to play.
3. Click **Add More Content**.
4. Search for **MT Inventory Weight**.
5. Install the mod.
6. Install required dependencies if the app does not add them automatically.
7. Launch the profile.

::: tip
Modrinth App and CurseForge App usually handle Fabric API and other dependencies automatically, but you should still check the dependency list on the mod page.
:::

## Manual Installation

If you do not use Modrinth App, CurseForge App, or another launcher with dependency management, follow the manual installation steps below.

## 1. Install Fabric Loader

Download and install Fabric Loader from:

- [https://fabricmc.net/use/installer/](https://fabricmc.net/use/installer/)

Choose the Minecraft version supported by the MT Inventory Weight build you downloaded.

After installation, the Minecraft Launcher should have a Fabric profile.

## 2. Download MT Inventory Weight

Download the mod from one of the official sources:

- [Modrinth](https://modrinth.com/mod/inventory-weight)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mt-inventory-weight)
- [GitHub](https://github.com/MEGATREX4/inventory-weight/)

Make sure the file matches:

- your Minecraft version
- Fabric mod loader
- Java Edition

## 3. Download Dependencies

Download the required dependencies for the same Minecraft version.

At minimum, most setups need:

```text
fabric-api
fzzy_config
```

Depending on the published build and dependency metadata, you may also need:

```text
cardinal-components-api
fabric-language-kotlin
```

Optional but recommended:

```text
modmenu
```

Optional compatibility:

```text
trinkets
```

## 4. Move Files to the `mods` Folder

The `mods` folder location depends on your operating system.

::: code-group

```text:no-line-numbers [Windows]
%appdata%\.minecraft\mods
```

```text:no-line-numbers [macOS]
~/Library/Application Support/minecraft/mods
```

```text:no-line-numbers [Linux]
~/.minecraft/mods
```

:::

Move the MT Inventory Weight `.jar` and all required dependency `.jar` files into the `mods` folder.

## 5. Launch Minecraft

Open the Minecraft Launcher.

Select the Fabric profile and click **Play**.

If the game starts successfully, MT Inventory Weight should load and print startup messages in the log.

You can check for a line similar to:

```text
Starting Inventory Weight
```

## 6. Configure the Mod

MT Inventory Weight uses fzzy_config.

If Mod Menu is installed:

1. Open the Mods menu.
2. Find MT Inventory Weight.
3. Open its config screen.
4. Change server or client settings.

Server settings include:

- max weight
- pocket weight
- realistic mode
- overload penalty strength
- base category weights

Client settings include:

- HUD style
- HUD position
- HUD text mode
- HUD text position
- tooltip visibility

Server settings can update live while the game/server is running.

## Dedicated Server Installation

For a dedicated server:

1. Install Fabric Server.
2. Put MT Inventory Weight and required dependencies in the server `mods` folder.
3. Put the same mod and required client-side dependencies in each player's client `mods` folder.
4. Start the server.
5. Edit config files or use in-game config tools if available.

::: warning
Clients should also install MT Inventory Weight. The server handles gameplay, but the client needs the mod for HUD, tooltips, and synced display data.
:::

## Updating the Mod

To update:

1. Stop the game/server.
2. Back up your world.
3. Remove the old MT Inventory Weight `.jar`.
4. Add the new `.jar`.
5. Update dependencies if required.
6. Start the game/server.

fzzy_config should migrate config files when possible.

If you have issues after a major update, try regenerating the config files:

```text
config/inventoryweight/client-config.toml
config/inventoryweight/server-config.toml
```

## Troubleshooting

### The game crashes on startup

Check that:

- you installed Fabric Loader, not Forge/NeoForge
- you installed the correct Minecraft version
- Fabric API is installed
- fzzy_config is installed
- required dependencies are installed
- all mods are for the same Minecraft version

### Config screen does not appear

Install Mod Menu:

- [https://modrinth.com/mod/modmenu](https://modrinth.com/mod/modmenu)

Also make sure fzzy_config is installed.

### Trinkets items are not counted

Make sure Trinkets is installed on both client and server.

If Trinkets is not installed, MT Inventory Weight will skip Trinkets compatibility automatically.

### Server config changes do not apply

The current version supports live fzzy_config updates. If changes do not apply:

- make sure you are editing the server/synced config, not only the client HUD config
- check the log for `Applying Inventory Weight server settings live`
- try `/reload`
- restart the world/server if needed
- report the issue with your log file

### Where do I get help?

Use:

- [GitHub Issues](https://github.com/MEGATREX4/inventory-weight/issues)
- [Discord](https://discord.gg/hc4XPRBEsq)
- [Fabric Player Support](https://discord.gg/v6v4pMv)

## Additional Resources

- [Fabric installing mods guide](https://docs.fabricmc.net/players/installing-mods)
- [Fabric API on Modrinth](https://modrinth.com/mod/fabric-api)
- [fzzy_config on Modrinth](https://modrinth.com/mod/fzzy-config)
- [Mod Menu on Modrinth](https://modrinth.com/mod/modmenu)
