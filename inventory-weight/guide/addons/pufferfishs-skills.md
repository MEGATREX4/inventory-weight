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
- [fzzy_config](https://modrinth.com/mod/fzzy-config)

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

| `operation`                               | Effect                                  |
| ----------------------------------------- | --------------------------------------- |
| `addition` / `add` / `add_value`          | Flat add (e.g. `+200`). **Recommended.**|
| `multiply_base` / `add_multiplied_base`   | Add a fraction of the attribute base.   |
| `multiply_total` / `add_multiplied_total` | Multiply the running total.             |

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

This is configured through the add-on's fzzy_config `category` and `totalLevel` formulas).
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
   `carrying`) under the **same** namespace `puffish_skills`). Pufferfish loads packs in
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
A plain **datapack** only loads the `data/` side. Translations `assets/.../lang`) and
textures need the **resource pack** or **mod loader** method. This add-on uses the mod-jar
(built-in pack) method, so it ships both `data/` and `assets/` together.
:::

See the official tutorial:
[Loading skill trees](https://puffish.net/skillsmod/docs/creators/tutorials/loading-skill-trees)

## Category file structure

A category is a folder under `data/<namespace>/puffish_skills/categories/<category>/`. The
category folder name must only use `az` and `_`. Each category has these files
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
`combat`, `mining`, `carrying`, ...).
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

# Authoring skill trees with the Editor and Template Generator

Writing `skills.json` and `connections.json` by hand works for a single root node, but
gets painful fast — node coordinates, bidirectional connections, and root flags are easy
to typo. Pufferfish's Skills ships **two official web tools** that produce the exact same
files the mod expects:

- **Template Generator** — produces a starter datapack ZIP with the correct folder layout,
  `pack.mcmeta`, `config.json`, and (optionally) an example skill tree.
- **Editor** — a browser-based visual editor for placing nodes, drawing connections, and
  exporting `skills.json` + `connections.json` (and optionally `definitions.json`).

Links:

- [Editor](https://puffish.net/skillsmod/editor/)
- [Template Generator](https://puffish.net/skillsmod/docs/creators/template-generator)
- [Official tutorial: Creating custom skill trees](https://puffish.net/skillsmod/docs/creators/tutorials/creating-skill-trees)

## Template Generator

Use this to bootstrap a project — it gives you a clean datapack ZIP with the right folder
layout so you can skip the boilerplate.

**Settings:**

| Field | Required | Notes |
| --- | --- | --- |
| **Name** | Yes | Project name. Cannot be empty. |
| **Namespace** | Yes | Must be lowercase, digits, `_`, `-`. Make it unique per project to avoid collisions (e.g. `mymod`, not `puffish_skills`). |
| **Description** | Yes | Goes into `pack.mcmeta`. |
| **Example Skill Tree** | Optional | Generates a working `example` category so you can see the file layout before you write your own. **Recommended** for first-time authors. |
| **Game Version** | Yes | `1.21.3 or later` vs `1.18.2 to 1.21.2` — pick the one your world runs. Affects `pack_format` in `pack.mcmeta`. |
| **Support for Mod Loaders** | Optional | Adds the `mods/` folder + the metadata files so the resulting ZIP can be renamed to `.jar` and dropped into `mods/`. **Recommended** for distribution. |
| **Assets Folder** | Optional | Adds the `assets/` folder. Requires Mod Loader support to actually take effect. |

Click **Generate and download**, then:

1. Extract the ZIP into `<world>/datapacks/<your-project>/`.
2. Launch the world; you should see the example tree (if you enabled it) in the skills
   screen (default key **K**).

::: tip
If you only ever need data-only trees for personal worlds, just enable **Example Skill
Tree** and skip the mod-loader option. If you plan to distribute the tree to players
(your mod's built-in pack, a modpack, a public release), turn on **Support for Mod
Loaders** so the same ZIP doubles as a `.jar`.
:::

## Editor

A browser-based visual editor at [puffish.net/skillsmod/editor](https://puffish.net/skillsmod/editor/).
It does **not** define skills — only their **layout and connections**. Definitions still
live in `definitions.json` (you edit that file by hand, then re-import it).

### Panels and what each one imports/exports

The editor has **three import/export pairs** (plus a metadata toggle):

| Panel | File | What it controls |
| --- | --- | --- |
| **Project** | `category.json` | Category-wide metadata (title, icon, background, `unlocked_by_default`). |
| **Definitions** | `definitions.json` | Skill templates (title, icon, rewards, costs). |
| **Skills** | `skills.json` | Skill **nodes**: position on the grid, which definition they use, root flag. |
| **Connections** | `connections.json` | Links between nodes. |
| **Save metadata** *(toggle)* | — | When ON, exporting `skills.json` also embeds per-skill icon/title so the icons survive a browser-session reload. When OFF, only the bare layout (`x`, `y`, `definition`) is exported. |
| **Save connections in legacy format** *(toggle)* | — | Older pack format. Leave OFF for new projects. |

::: tip Workflow tip
For a new category, import **only `definitions.json`** — the editor will give you an empty
canvas. For an existing category you want to redesign, import all three (`definitions`,
`skills`, `connections`) so you don't lose anything.
:::

### Editor settings

These appear in the right sidebar:

**Grid**

- **Type**: `None` (free placement) / `Square` / `Hex` / `Radial`.
- **Orientation** (Hex only): `Flat` / `Pointy`.
- **Spacing**: distance between grid cells.
- **Size**: node size on the canvas.

**Connections**

- **Type**: `Normal` (any node can connect to any unlocked node) / `Exclusive` (only one
  branch can be active at a time, like a choice).
- **Direction**: `Bidirectional` / `Unidirectional` (one-way prerequisite).

**Definitions**

- **Theme**: `Automatic` / `Light` / `Dark`.
- **Show normal connections** / **Show exclusive connections**: visual toggles for the
  canvas.

### Mouse and keyboard shortcuts

| Action | Shortcut |
| --- | --- |
| Move skill | `Left-Click` drag |
| Pan view | `Middle-Click` drag |
| Zoom view | `Scroll` |
| Add skill | `A`, or `Right-Click` on empty canvas |
| Add / edit / delete skill | `T`, or `Right-Click` on existing skill |
| Edit skill | `E` |
| Edit selected skills | `Shift + E` |
| Delete skill | `D` |
| Delete selected skills | `Shift + D` |
| Set / unset root | `R` (or `Shift + R` for selected) |
| Add / delete connection | `C`, or `Middle-Click` between two skills |
| Delete all connections for skill | `X` (or `Shift + X` for selected) |

### Editing session vs. on-disk files

The editor keeps your work in browser session storage (cleared when you clear browser
data). **Always export `skills.json` and `connections.json` (and `definitions.json` if
you used "Save metadata") before closing the tab.** Exporting overwrites the files you
imported from, so re-import on next visit to restore your work.

## End-to-end workflow: a custom max-weight tree

The fastest path from zero to a working custom max-weight skill tree:

1. **Open the [Template Generator](https://puffish.net/skillsmod/docs/creators/template-generator)**.
   - Set **Namespace** to something unique to your project (e.g. `mymod`).
   - Enable **Example Skill Tree** if you want a known-good starting point to compare
     against.
   - Enable **Support for Mod Loaders** if this tree will ship inside a mod `.jar`.
   - Click **Generate and download**.
2. **Extract the ZIP** into `<world>/datapacks/<your-project>/`.
3. **Edit `definitions.json`** to add your max-weight skill templates. The
   [Minimal example](#minimal-example-a-max-weight-node) above is a good starting point —
   copy the `+200 Max Weight` definition, change the `value` and the icon.
4. **Open the [Editor](https://puffish.net/skillsmod/editor/)**.
5. **Import your `definitions.json`** (and optionally `skills.json` + `connections.json`
   from the example tree) into the corresponding panels.
6. **Lay out your tree** using the grid settings, then click to add skills, drag to
   position them, and middle-click between skills to draw connections. Mark the starting
   skill as a **root** (`R`).
7. **Export `skills.json` and `connections.json`**. Toggle **Save metadata** ON if you
   want icons to persist between editor sessions.
8. **Drop the exported files back** into `categories/<your-category>/` in your project,
   overwriting the editor-side copies.
9. **In-game**: `/reload` (or rejoin the world). Open the skills screen (**K**) and your
   tree should appear.
10. **Troubleshooting**: if you see "Invalid configuration, check game logs.", open
    `logs/latest.log` and search for `puffish_skills` — the most common errors are JSON
    syntax (missing comma/bracket), a typo in an attribute id, or `Unknown ...` because a
    referenced reward type isn't installed.

::: info
For the add-on's built-in `carrying` tree, the same workflow applies — the tree is
authored as a regular datapack and then placed under `src/main/resources/...` so Loom
packs it as a Fabric **built-in** resource/data pack (NORMAL, disabled by default).
Override any file you want by shipping your own datapack with the **same category id**;
use a **different** category id to add an independent tree side by side.
:::

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

### "Invalid configuration, check game logs."

Open `logs/latest.log` and search for `puffish_skills`. Common causes:

- **"Could not parse JSON due to malformed syntax"** — missing/misplaced comma or
  bracket. Validate the file with a JSON linter.
- **"Expected ..."** — the mod expected a different shape than what you provided.
  Cross-check the file against the relevant doc page.
- **"Invalid ..."** — the value is the right shape but doesn't match the documented
  set (typo in an operation name, wrong reward type, etc.).
- **"Unknown ..."** — usually a typo in a referenced id (attribute, category, item).
- **"Unused field ..."** — extra entry in the JSON that isn't part of the schema.
  Remove it.

## Related Documentation

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [Pufferfish's Skills](https://modrinth.com/mod/puffish-skills)
- [Pufferfish's Skills — Editor](https://puffish.net/skillsmod/editor/)
- [Pufferfish's Skills — Template Generator](https://puffish.net/skillsmod/docs/creators/template-generator)
- [Pufferfish's Skills — Creating custom skill trees tutorial](https://puffish.net/skillsmod/docs/creators/tutorials/creating-skill-trees)
- [Pufferfish's Skills — Loading skill trees](https://puffish.net/skillsmod/docs/creators/tutorials/loading-skill-trees)
- [Pufferfish's Skills — Categories](https://puffish.net/skillsmod/docs/creators/configuration/categories)
