---
title: "Pufferfish's Skills"
description: "Pufferfish's Skills InventoryWeight connects Pufferfish's Skills with MT Inventory Weight. Skill nodes can grant the max-weight attribute directly, and the add-on can also scale max weight by a skill category's level. Includes a built-in, optional Carrying skill tree."
---

Pufferfish's Skills InventoryWeight connects **Pufferfish's Skills** with **MT Inventory Weight**.

It lets a player's skill progression increase their maximum inventory weight, in two ways:

1. **Datapack / attribute reward** — a skill node grants the Inventory Weight attribute
   directly (pure data, no Java required).
2. **Java event connector** — the add-on reads a skill category's experience level and
   modifies max weight through MT Inventory Weight's event.

It also ships an **optional, built-in "Carrying" skill tree** you can enable per world.

## Required Mods

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [Pufferfish's Skills](https://modrinth.com/mod/puffish-skills)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [fzzy_config](https://modrinth.com/mod/fzzy-config) — used for the add-on's config

::: info
Pufferfish's Skills is server-authoritative for skill levels, so this add-on runs on the
server. It works for both an integrated server (single player / Open to LAN) and a
dedicated server.
:::

## The Inventory Weight attribute

MT Inventory Weight registers a vanilla-style attribute:

```text
inventoryweight:generic.max_weight
```

Pufferfish's Skills can grant **any registered attribute** through its
`puffish_skills:attribute` reward, so a skill node can grant max inventory weight directly.

---

# How the Pufferfish + Inventory Weight API works

There are two integration paths. You can use either, or both.

## 1. Attribute reward (data-driven, no Java)

In a category's `definitions.json`, a skill definition lists **rewards**. Use the
`puffish_skills:attribute` reward type pointing at `inventoryweight:generic.max_weight`:

```json
{
    "max_weight+200": {
        "title": "+200 Max Weight",
        "icon": { "type": "item", "data": { "item": "minecraft:leather" } },
        "rewards": [
            {
                "type": "puffish_skills:attribute",
                "data": {
                    "attribute": "inventoryweight:generic.max_weight",
                    "value": 200.0,
                    "operation": "addition"
                }
            }
        ]
    }
}
```

When the player unlocks that skill, Pufferfish applies the attribute modifier and the
player's max weight increases.

### Operations

| `operation`                                   | Effect                                    |
| --------------------------------------------- | ----------------------------------------- |
| `addition` / `add` / `add_value`              | Flat add (e.g. `+200`). **Recommended.**  |
| `multiply_base` / `add_multiplied_base`       | Add a fraction of the attribute base.     |
| `multiply_total` / `add_multiplied_total`     | Multiply the running total.               |

::: warning
For `inventoryweight:generic.max_weight`, prefer `addition`. The attribute's base value is
typically `0`, so `multiply_base` / `multiply_total` may produce little or no effect.
:::

## 2. Java event connector (level-based scaling)

The add-on also listens to MT Inventory Weight's max-weight event and scales capacity by a
skill **category's experience level** (read via Pufferfish's `SkillsAPI`):

```text
SkillsAPI.getCategory(id) -> Category.getExperience() -> Experience.getLevel(player)
```

This is configured through the add-on's fzzy_config (`category` and `totalLevel` formulas).
See the config section below.

::: tip
If a node already grants the attribute (path 1) AND you scale the same value by level
(path 2), the bonuses **stack**. Pick one per effect unless stacking is intended.
:::

---

# The built-in "Carrying" skill tree

This add-on bundles an **optional** Pufferfish category called `carrying`.

- It is registered as a Fabric built-in pack, so it appears in the world-creation
  **Data Packs** screen, **disabled by default** (you enable it per world).
- Its nodes grant `inventoryweight:generic.max_weight` (flat and percent), plus a small
  movement-speed node so a heavy load slows you less, and a capstone.
- It earns experience from **mining blocks** and **crafting items**.

To enable it:

- New world → **Create World** → **Data Packs** → move "Carrying Skill Tree" to the active
  side, or
- Existing world → `/datapack list` then `/datapack enable "<id>"`.

Open the skills screen (default key **K**) to see the **Carrying** category.

## Default pack vs. your own pack

This mod ships the `carrying` tree as a **built-in default**, but you are not locked into it.
There are three levels of control:

1. **Use the default as-is** — enable the bundled "Carrying Skill Tree" built-in pack. No
   files needed.
2. **Override the default** — ship your own datapack that defines the **same** category id
   (`carrying`) under the **same** namespace (`puffish_skills`). Pufferfish loads packs in
   order, and a later pack's files replace the built-in ones, so your `definitions.json` /
   `skills.json` / etc. win. Keep the built-in pack **disabled** (or override every file) to
   avoid mixing.
3. **Add a separate, independent tree** — create your **own** category with a **different**
   id (and ideally your own namespace), and leave the built-in `carrying` pack off (or on,
   if you want both). This is the cleanest option for modpacks because it never collides
   with the mod's defaults.

::: tip For modpack devs / server admins
- Disable the bundled built-in pack if you want full control, then provide your own pack.
- A separate category id avoids ALL conflicts with the default `carrying` tree.
- Remember the `config.json` rule below: whichever pack's `config.json` wins must list
  every category you want loaded.
- All your nodes still grant capacity the same way — via the
  `inventoryweight:generic.max_weight` attribute reward (see the API section above).
:::

### How "default + override" actually resolves

Pufferfish reads each category's files from the active packs. For a given category id:

- If only the **built-in** pack is enabled → the mod's default `carrying` tree loads.
- If a **later** pack also provides `categories/carrying/...` → those files override the
  built-in ones (last pack wins per file).
- If you use a **different** category id → it loads independently, side by side.

Because the built-in pack is registered as **NORMAL** (off by default), a server admin who
does nothing gets no carrying tree; one who enables the built-in pack gets the default; and
one who ships their own pack gets theirs.

Open the skills screen (default key **K**) to see the loaded categories.

---

# Making your own skill tree that grants max weight

You don't need this add-on's Java code to add max-weight skills — a plain Pufferfish's
Skills datapack is enough.

## How Pufferfish loads data-driven skill trees

Pufferfish's Skills reads categories from datapacks/resource locations. There are several
ways to load them (from the official docs):

| Method | Where it goes | Custom assets (lang/textures)? | Good for |
| --- | --- | --- | --- |
| **Data packs only** | `<world>/datapacks/` | No | Quick dev, no custom assets |
| **Data packs + resource packs** | `<world>/datapacks/` + `resourcepacks/` | Yes (assets in the resource pack) | Trees with textures/lang |
| **Mod loaders** (code-less mod) | `mods/` (zip renamed to `.jar`) | Yes | Easy distribution, data + assets in one file |
| **Global packs** (extra mod) | that mod's folder | Yes | Modpacks (auto-applied to all worlds) |
| **Config folder** | `config/puffish_skills/` | No | Dev only |

::: info
A plain **datapack** only loads the `data/` side. Translations (`assets/.../lang`) and
textures need the **resource pack** or **mod loader** method. This add-on uses the mod-jar
(built-in pack) method, so it ships both `data/` and `assets/` together.
:::

See the official tutorial:
[Loading skill trees](https://puffish.net/skillsmod/docs/creators/tutorials/loading-skill-trees)

## Category file structure

A category is a folder under `data/<namespace>/puffish_skills/categories/<category>/`. The
category folder name must only use `a`–`z` and `_`. Each category has these files
(from the official docs):

```text
data/<namespace>/puffish_skills/
├── config.json                 (lists which categories are loaded)
└── categories/<category>/
    ├── category.json           (title, icon, background, unlocked_by_default)
    ├── definitions.json        (skill templates + rewards)
    ├── skills.json             (nodes: position + which definition)
    ├── connections.json        (links between nodes)
    └── experience.json         (optional: how the category gains XP/levels)
```

`config.json` registers the category:

```json
{
    "version": 3,
    "categories": [
        "carrying"
    ]
}
```

::: warning config.json conflicts
Pufferfish loads a single `puffish_skills/config.json` listing ALL categories. If two
packs each ship this file, only one wins. If you add `carrying` alongside another pack
(e.g. Default Skill Trees), make sure your `config.json` lists every category you want
(`combat`, `mining`, `carrying`, ...).
:::

See the official docs:
[Categories](https://puffish.net/skillsmod/docs/creators/configuration/categories)

## Minimal example: a +max-weight node

`config.json`

```json
{ "version": 3, "categories": ["carrying"] }
```

`categories/carrying/definitions.json`

```json
{
    "root": {
        "title": "Strong Back",
        "icon": { "type": "item", "data": { "item": "minecraft:bundle" } },
        "rewards": [
            {
                "type": "puffish_skills:attribute",
                "data": {
                    "attribute": "inventoryweight:generic.max_weight",
                    "value": 200.0,
                    "operation": "addition"
                }
            }
        ]
    }
}
```

`categories/carrying/skills.json`

```json
{ "root": { "x": 0, "y": 0, "definition": "root", "root": true } }
```

`categories/carrying/connections.json`

```json
{ "normal": { "bidirectional": [] } }
```

`category.json` and `experience.json` round out the category (title/icon and XP).

---

# Add-on configuration (Java event path)

Synced server config via fzzy_config, id `pufferfishs_skills_inventoryweight:server-config`.

### General

| Option | Default | Description |
| --- | ---: | --- |
| `enabled` | `true` | Master switch. |
| `minimumMaxWeight` | `1.0` | Floor for the final max weight. |

### `category` (default on)

Scales by one configured category's experience level.

| Option | Default | Description |
| --- | --- | --- |
| `category.enabled` | `true` | Enable category-based scaling. |
| `category.categoryId` | `puffish_skills:adventure` | Category id (namespace defaults to `puffish_skills`). |
| `category.additivePerLevel` | `0.0` | Flat bonus per level. |
| `category.multiplierPerLevel` | `0.05` | `0.05` = +5% per level. |

### `totalLevel` (default off)

Scales by the sum of all categories' experience levels.

| Option | Default | Description |
| --- | ---: | --- |
| `totalLevel.enabled` | `false` | Enable combined-level scaling. |
| `totalLevel.additivePerLevel` | `0.0` | Flat bonus per combined level. |
| `totalLevel.multiplierPerLevel` | `0.0` | `0.01` = +1% per combined level. |

### Formula

```text
result = (currentMaxWeight + additive) * multiplier
result = max(minimumMaxWeight, result)
```

`currentMaxWeight` is what MT Inventory Weight already computed, so bonuses stack with the
base config, armor pockets, and other Inventory Weight add-ons.

## Troubleshooting

### A skill grants no weight

- Confirm MT Inventory Weight and Pufferfish's Skills are installed.
- The attribute id must be exactly `inventoryweight:generic.max_weight`.
- Prefer `operation: addition` (percent ops may be ineffective with a base of 0).
- After editing a reward, you may need to reset the skill so the modifier reapplies.

### The built-in Carrying tree doesn't appear

- It is **disabled by default** (NORMAL): enable it in the Data Packs screen or via
  `/datapack enable`.
- Check the log for `Registered built-in 'carrying' datapack: true`.

### "Unknown variable `true`" / "Expected ... a number" in experience.json

- Conditions must reference a **defined variable**, not the literal `true`. For
  unconditional XP, use a bare string expression like `"experience": "1"`.

## Related Documentation

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [Pufferfish's Skills](https://modrinth.com/mod/puffish-skills)
- [Pufferfish's Skills — Loading skill trees](https://puffish.net/skillsmod/docs/creators/tutorials/loading-skill-trees)
- [Pufferfish's Skills — Categories](https://puffish.net/skillsmod/docs/creators/configuration/categories)
