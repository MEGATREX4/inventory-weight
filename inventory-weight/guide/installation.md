---
title: Installing Fabric Mods
description: A step-by-step guide on how to install mods for Fabric.
---

# Installing Fabric Mods {#installing-mods}

This guide will walk you through the process of installing mods for Fabric using the Minecraft Launcher. For third-party launchers, consult their specific documentation.

## 1. Download the Mod {#1-download-the-mod}

::: warning
Only download mods from sources you trust.
:::

Most mods also require the Fabric API, which can be downloaded from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://curseforge.com/minecraft/mc-mods/fabric-api).

When downloading mods, ensure that:

- They are compatible with the Minecraft version you are using. For instance, a mod compatible with Minecraft 1.20 may not work with Minecraft 1.20.2.
- They are designed for Fabric and not another mod loader.
- They are meant for the Java Edition of Minecraft.

## 2. Move the Mod to the `mods` Folder {#2-move-the-mod-to-the-mods-folder}

The `mods` folder is located in different places depending on your operating system. You can paste these paths into your file explorer's address bar to navigate directly to the folder.

::: code-group

```:no-line-numbers [Windows]
%appdata%\.minecraft\mods
```

```:no-line-numbers [macOS]
~/Library/Application Support/minecraft/mods
```

```:no-line-numbers [Linux]
~/.minecraft/mods
```

:::

After locating the `mods` folder, move the mod `.jar` files into this directory.

## 3. Launch Minecraft {#3-launch-minecraft}

With the mods in place, open the Minecraft Launcher. Select the Fabric profile from the dropdown menu in the bottom left corner, and then click "Play" to start the game.

## Troubleshooting {#troubleshooting}

If you experience issues during installation, seek help in the [Fabric Discord](https://discord.gg/v6v4pMv) in the #player-support channel.

## Additional Resources

[Fabric API Installation Guide](https://docs.fabricmc.net/players/installing-mods)  by IMB11
