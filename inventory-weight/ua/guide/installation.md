---
title: Встановлення модів для Fabric
description: Покрокова інструкція з встановлення модів для Fabric.
---

# Встановлення модів для Fabric {#installing-mods}

Цей посібник проведе вас через процес встановлення модів для Fabric за допомогою Minecraft Launcher. Для сторонніх лаунчерів зверніться до їхньої конкретної документації.

## 1. Завантажте мод {#1-download-the-mod}

::: warning
Завантажуйте моди тільки з джерел, яким ви довіряєте.
:::

Більшість модів також потребують Fabric API, який можна завантажити з [Modrinth](https://modrinth.com/mod/fabric-api) або [CurseForge](https://curseforge.com/minecraft/mc-mods/fabric-api).

При завантаженні модів переконайтеся, що:

- Вони сумісні з версією Minecraft, яку ви використовуєте. Наприклад, мод, сумісний з Minecraft 1.20, може не працювати з Minecraft 1.20.2.
- Вони призначені для Fabric, а не для іншого мод-лоадера.
- Вони призначені для Java Edition Minecraft.

## 2. Перемістіть мод до папки `mods` {#2-move-the-mod-to-the-mods-folder}

Папка `mods` розташована в різних місцях залежно від вашої операційної системи. Ви можете вставити ці шляхи в рядок адреси вашого файлового провідника, щоб безпосередньо перейти до папки.

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

Після того, як ви знайшли папку `mods`, перемістіть файли модів `.jar` у цей каталог.

## 3. Запустіть Minecraft {#3-launch-minecraft}

З модами на місці відкрийте Minecraft Launcher. Виберіть профіль Fabric зі спадного меню в нижньому лівому куті, а потім натисніть "Play", щоб розпочати гру.

## Усунення неполадок {#troubleshooting}

Якщо ви стикаєтеся з проблемами під час встановлення, зверніться за допомогою до [Fabric Discord](https://discord.gg/v6v4pMv) у каналі #player-support.

## Додаткові ресурси

[Посібник з встановлення Fabric API](https://docs.fabricmc.net/players/installing-mods) від IMB11
