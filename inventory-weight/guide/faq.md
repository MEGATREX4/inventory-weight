---
title: "FAQ"
description: "Frequently asked questions about MT Inventory Weight, including installation, fzzy_config, Mod Menu, Trinkets, datapacks, add-ons, HUD customization, multiplayer, and support."
order: 3
---

# Frequently Asked Questions

## Who develops MT Inventory Weight?

MT Inventory Weight was originally developed by **MEGATREX4**. The mod started as a small experimental project built around the idea of adding a realistic inventory weight system to Minecraft.

The mod has since grown into a larger system with configuration, datapack support, multiplayer sync, compatibility features, and a public add-on API.

## Where can I download MT Inventory Weight?

You can download MT Inventory Weight from the official project pages:

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mt-inventory-weight)
- [Modrinth](https://modrinth.com/mod/inventory-weight)
- [GitHub](https://github.com/MEGATREX4/inventory-weight/)

::: warning
Only download the mod from trusted sources. Avoid reupload sites.
:::

## How do I install MT Inventory Weight?

See the [Installation](./installation.md) page for step-by-step instructions.

Easy version:

1. Use the [Modrinth App](https://modrinth.com/app) or [CurseForge App](https://www.curseforge.com/download/app).
2. Create a Fabric instance/profile for the correct Minecraft version.
3. Search for **Inventory Weight** or **MT Inventory Weight**.
4. Install the mod and let the app install dependencies when available.
5. Launch the instance/profile.

Manual version:

1. Install Fabric Loader for the correct Minecraft version.
2. Install Fabric API.
3. Install MT Inventory Weight and its required dependencies.
4. Put the `.jar` files into your `mods` folder.
5. Launch the game using the Fabric profile.

## What Minecraft versions are supported?

Check the version list on [Modrinth](https://modrinth.com/mod/inventory-weight) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mt-inventory-weight).

The refactored version discussed in this documentation targets **Minecraft 1.20.1 Fabric**.

## Does MT Inventory Weight use Cloth Config?

No.

MT Inventory Weight now uses [fzzy_config](https://modrinth.com/mod/fzzy-config) for configuration.

Cloth Config is no longer required by MT Inventory Weight. You only need Cloth Config if another mod in your modpack requires it.

## Does MT Inventory Weight support Mod Menu?

Yes.

[Mod Menu](https://modrinth.com/mod/modmenu) is optional, but recommended. It gives easier access to the mod configuration screen and project links.

## Does MT Inventory Weight support live config changes?

Yes.

Server settings changed through fzzy_config can be applied while the game/server is running. When a server setting changes, the mod updates runtime values, recalculates player weight data, and syncs updated settings to clients.

This applies to settings such as:

- max weight
- pocket weight
- base item weights
- realistic mode
- overload penalty strength

## Can I customize the HUD?

Yes.

The HUD is configurable through fzzy_config. With Mod Menu installed, this can usually be done from the in-game Mods menu.

HUD options include:

- sprite-style display using the old bundle icons
- simple bar display
- HUD position
- custom HUD offset
- text mode
- text position
- text color
- text shadow
- screen-edge clamping

Supported HUD text modes include:

```text
NONE
CURRENT
MAX
CURRENT_MAX
PERCENT
REMAINING
CURRENT_MAX_PERCENT
```

Supported HUD text positions include:

```text
BELOW
ABOVE
LEFT
RIGHT
INSIDE
CUSTOM
```

## Does MT Inventory Weight support Trinkets?

Yes.

[Trinkets](https://modrinth.com/mod/trinkets) is optional. If Trinkets is installed, equipped trinket items are counted toward the player's carried weight.

If Trinkets is not installed, this compatibility layer is skipped automatically.

## Can I use MT Inventory Weight with other mods?

Yes.

MT Inventory Weight is designed to work with modded items, blocks, tools, armor, storage items, and equipment.

The mod has automatic fallback calculations for many item types, and modpack authors can override weights through datapacks.

## Does MT Inventory Weight support backpacks and containers?

Yes.

Known or partially supported container types include:

- vanilla shulker boxes
- Traveler's Backpack
- Scout-style pouches and satchels
- Create toolboxes
- Extended Drawers-style storage
- Inmis-style backpacks
- Pack It Up-style backpacks
- Sophisticated Storage-style NBT storage, when contents are available on the item stack
- other containers with similar readable NBT structures

::: warning
Some backpack/container mods store contents outside the item stack, for example in world data or external storage managers. Those may need special add-on support.
:::

## How is container weight calculated?

Supported shulkers and backpacks use this rule:

```text
final container weight = empty container weight + weight inside / 2
```

Tooltips show both values:

```text
Weight inside: 10.0k
Weight: 5.1k
```

`Weight inside` is the full uncompressed weight of stored items. `Weight` is the effective weight used by the mod.

## Can item weights be changed?

Yes.

There are several ways to customize item weights:

1. Server config values for broad categories.
2. Datapack item weight overrides.
3. NBT-specific datapack rules.
4. Add-ons using the public API.

See the [Datapack Customization](./datapacks.md) page for examples.

## Can armor pocket values be changed?

Yes.

Armor pocket values can be changed through datapacks.

The mod checks pocket values in this order:

1. NBT-specific pocket definitions
2. datapack pocket definitions
3. default armor protection/toughness calculation

## Does MT Inventory Weight work in multiplayer?

Yes.

The server is authoritative for gameplay values. Player weight state, overload state, and server weight definitions are synced to clients.

Supported environments include:

- singleplayer
- LAN worlds
- dedicated servers
- modded multiplayer servers

## Does the mod need to be installed on both client and server?

For multiplayer, yes.

The server needs the mod for gameplay logic, player weight calculations, datapack loading, config handling, and overload effects. Clients need the mod for HUD rendering, tooltips, synced data, and client config.

## Can I make an add-on for MT Inventory Weight?

Yes.

MT Inventory Weight has a public API package:

```text
com.megatrex4.api.v1
```

Add-ons can register:

- item weight providers
- player weight sources
- capacity providers
- pocket providers
- events/listeners

See the [Add-on API](../compatibilities/addon-api.md) page.

## Are there known conflicts with other mods?

There are no major known conflicts at this time.

Potential compatibility issues are most likely with backpack/container mods that do not store item contents in normal item NBT. These mods may require dedicated compatibility add-ons.

If you find a conflict, please report it.

## How do I report bugs?

Report bugs on GitHub:

- [GitHub Issues](https://github.com/MEGATREX4/inventory-weight/issues)

When reporting a bug, please include:

- Minecraft version
- Fabric Loader version
- Fabric API version
- MT Inventory Weight version
- fzzy_config version
- full latest.log or crash report
- mod list
- steps to reproduce

## How can I request features?

Feature requests can be submitted on GitHub or discussed on Discord:

- [GitHub](https://github.com/MEGATREX4/inventory-weight/)
- [Discord](https://discord.gg/hc4XPRBEsq)

## Where can I find the source code?

The source code is available on GitHub:

- [https://github.com/MEGATREX4/inventory-weight/](https://github.com/MEGATREX4/inventory-weight/)

## How do I update the mod?

Download the latest version from Modrinth, CurseForge, or GitHub, then replace the old `.jar` file in your `mods` folder.

When updating, also check whether required dependencies need to be updated.

::: warning
Always back up your world before updating a modded Minecraft instance.
:::

## Do I need to delete old config files after updating?

Usually no.

fzzy_config supports config versioning and migration. However, if a config screen breaks or values look wrong after a major update, try regenerating the affected config file.

Client config is usually located at:

```text
config/inventoryweight/client-config.toml
```

Server/synced config is usually located at:

```text
config/inventoryweight/server-config.toml
```

## What are the system requirements?

MT Inventory Weight does not have special system requirements beyond Minecraft Java Edition and Fabric.

Performance impact should be small, but large inventories with many nested containers can increase calculation cost.
