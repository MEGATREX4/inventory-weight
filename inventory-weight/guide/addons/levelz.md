---
title: "LevelZ"
description: "Detailed documentation for the LevelZ Inventory Weight add-on (Minecraft 1.21 and 1.20.1), including dependencies, configuration, capacity modes, the Carrying skill, dynamic skill creation, GUI hiding, and troubleshooting."
order: 10
---

# LevelZ Inventory Weight Add-on

**LevelZ Inventory Weight** is an add-on that integrates **MT Inventory Weight** with **LevelZ**.

You can download the add-on from Modrinth:

- [LevelZ Inventory Weight on Modrinth](https://modrinth.com/mod/levelz-inventory-weight)

It allows the player's **LevelZ overall character level** and/or a configured **LevelZ skill** to increase the player's maximum inventory weight capacity.

This add-on is **not standalone**. It requires both **LevelZ** and **MT Inventory Weight**.

::: danger 1.21 and 1.20.1 behave differently
This page covers **both** the **1.21** and the **1.20.1** builds. They share the same idea
but the underlying LevelZ API is very different, so several features exist **only on 1.21**.

| Feature                                        | 1.21 | 1.20.1 |
| ---------------------------------------------- | :--: | :----: |
| Overall-level scaling                          | Yes  |  Yes   |
| Skill-level scaling                            | Yes  |  Yes   |
| Custom `carrying` skill                        | Yes  |  No    |
| Uses one of the 12 **built-in** LevelZ skills  | Yes* |  Yes   |
| Dynamic skill creation (`dynamicSkill.*`)      | Yes  |  No    |
| `LEVELZ_ATTRIBUTE` capacity mode               | Yes  |  No    |
| `skill.capacityMode` option                    | Yes  |  No    |
| `skill.showInLevelZScreen` / GUI hiding        | Yes  |  No    |
| `skill.skillIdOverride`                        | Yes  |  No    |
| Datapack skill JSON (`data/levelz/skill/…`)    | Yes  |  No    |

\* On 1.21 you can use any skill key, including the custom `carrying` skill or a built-in one.

**On 1.20.1, LevelZ skills are a fixed enum with no registration API.** That means there is
no `carrying` skill, no dynamic creation, no attribute mode, and no GUI hiding. The 1.20.1
build instead reads **one of the 12 existing built-in skills** (config `skill.skillKey`,
default `strength`) plus the overall level. Sections that are 1.21-only are clearly marked
below.
:::

## Required Dependencies

You must install:

- [LevelZ](https://modrinth.com/mod/levelz)
- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
- [fzzy_config](https://modrinth.com/mod/fzzy-config)

## What This Add-on Does

LevelZ Inventory Weight can increase max inventory weight from:

1. The player's total LevelZ character level. *(1.21 and 1.20.1)*
2. One configured LevelZ skill. *(1.21 and 1.20.1)*
3. A dynamically created LevelZ skill, if enabled and missing. *(1.21 only)*
4. A LevelZ skill attribute using `inventoryweight:generic.max_weight`, if `LEVELZ_ATTRIBUTE` mode is selected. *(1.21 only)*

On **1.21**, the recommended setup is to use a custom LevelZ skill called:

```text
carrying
```

This skill can be shown in the LevelZ skill screen or hidden through the add-on config.

::: danger 1.20.1: no custom skill — use a built-in one
There is **no `carrying` skill** on 1.20.1, and you **cannot create one**. Pick one of the
fixed built-in skills via `skill.skillKey` instead (default `strength`).

Valid `skill.skillKey` values on 1.20.1 (case-insensitive):

```text
health, strength, agility, defense, stamina, luck,
archery, trade, smithing, mining, farming, alchemy
```

Thematic suggestions: **strength** (default, fits "carry more"), **stamina** (endurance),
or **defense** (tanky builds). On 1.21 you may also use any of these built-in keys instead
of `carrying`.
:::

## Capacity Formula

When using the recommended `EVENT_MODIFIER` mode (1.21) — or the only mode on 1.20.1 — the add-on modifies max weight with this formula:

```text
final max weight = (current max weight + additive bonuses) * multiplier bonuses
```

The `current max weight` is the value already calculated by MT Inventory Weight before this add-on changes it.

This means the add-on can stack with:

- MT Inventory Weight server config max weight
- armor pockets
- other Inventory Weight add-ons
- attribute bonuses
- future capacity providers

### Example

```text
Base max weight: 100
Overall LevelZ level: 10
Skill level (carrying on 1.21, e.g. strength on 1.20.1): 10

Overall multiplier per level: 0.01
Skill multiplier per level: 0.05

Overall multiplier: 1 + 10 * 0.01 = 1.10
Skill multiplier:   1 + 10 * 0.05 = 1.50

Final max weight: 100 * 1.10 * 1.50 = 165
```

## Configuration File

The add-on uses a synced server config through **fzzy_config**.

The config id is:

```text
levelz_inventoryweight:server-config
```

The config is registered as a synced server/client config, so server gameplay settings can be synchronized to clients.

::: info
Config values are server-authoritative. In multiplayer, the server decides how much capacity LevelZ levels and skills give.
:::

## Main Config Options

### General

| Option             | Default | Description                              |
| ------------------ | ------: | ---------------------------------------- |
| `enabled`          | `true`  | Master switch for the integration.       |
| `minimumMaxWeight` | `1.0`   | Safety minimum for the final max weight. |

### Overall Level Formula

The `overallLevel` section controls how the player's total LevelZ character level affects max inventory weight.

| Option                            | Default | Description                                                        |
| --------------------------------- | ------: | ------------------------------------------------------------------ |
| `overallLevel.enabled`            | `true`  | Enables overall-level scaling.                                     |
| `overallLevel.additivePerLevel`   | `0.0`   | Flat max-weight bonus per total LevelZ level.                      |
| `overallLevel.multiplierPerLevel` | `0.01`  | Multiplier bonus per total LevelZ level. `0.01` means +1% per level. |

Example:

```text
overallLevel.multiplierPerLevel = 0.01
overall level = 20
multiplier = 1.20
```

### Skill Formula

The `skill` section controls how one configured LevelZ skill affects max inventory weight.

| Option                     | Default          | Description                                                        | Version    |
| -------------------------- | ---------------- | ----------------------------------------------------------------- | ---------- |
| `skill.enabled`            | `true`           | Enables skill-based scaling.                                      | both       |
| `skill.skillKey`           | `carrying` *(1.21)* / `strength` *(1.20.1)* | LevelZ skill key to use.               | both       |
| `skill.additivePerLevel`   | `0.0`            | Flat max-weight bonus per configured skill level.                | both       |
| `skill.multiplierPerLevel` | `0.05`           | Multiplier bonus per configured skill level. `0.05` = +5%/level. | both       |
| `skill.showInLevelZScreen` | `true`           | Shows or hides the configured skill in the LevelZ screen.        | 1.21 only  |
| `skill.capacityMode`       | `EVENT_MODIFIER` | Determines how the skill affects capacity.                       | 1.21 only  |
| `skill.skillIdOverride`    | `-1`             | Optional numeric skill id override. `-1` resolves by skill key.  | 1.21 only  |

::: tip
Use `skillKey` instead of hardcoding the skill id when possible. LevelZ skills can be changed by datapacks, so resolving by key is safer.
:::

::: warning 1.20.1: smaller config
On 1.20.1 the keys `skill.capacityMode`, `skill.showInLevelZScreen`, and
`skill.skillIdOverride` **do not exist**, and the entire `dynamicSkill` section is gone.
If you copy a 1.21 config, those keys are simply ignored on 1.20.1. Also set
`skill.skillKey` to a **built-in** skill — `carrying` resolves to nothing on 1.20.1 and
contributes level `0`.
:::

## Capacity Modes

::: warning 1.21 only
Capacity modes are a **1.21** feature. **1.20.1 has only the event-modifier behavior** and
no `capacityMode` option to choose — LevelZ 1.20.1 has no skill-attribute system, so the
`inventoryweight:generic.max_weight` attribute cannot be attached to a LevelZ skill.
:::

### EVENT_MODIFIER

`EVENT_MODIFIER` is the recommended mode (and matches what 1.20.1 always does).

In this mode, LevelZ Inventory Weight reads the configured LevelZ skill level and modifies MT Inventory Weight's max capacity through the MT Inventory Weight event system.

This mode supports:

- flat bonuses
- multiplier bonuses
- overall level scaling
- skill level scaling

Example:

```text
skill.capacityMode = EVENT_MODIFIER
skill.skillKey = carrying
skill.multiplierPerLevel = 0.05
```

This means:

```text
+5% max inventory weight per Carrying level
```

At Carrying level 20:

```text
1 + 20 * 0.05 = 2.0
```

So the configured skill gives a 2x max-weight multiplier.

### LEVELZ_ATTRIBUTE

::: warning 1.21 only
`LEVELZ_ATTRIBUTE` does **not** exist on 1.20.1.
:::

`LEVELZ_ATTRIBUTE` lets the LevelZ skill use the MT Inventory Weight max-weight attribute directly:

```text
inventoryweight:generic.max_weight
```

This is best for flat attribute bonuses.

Example:

```text
dynamicSkill.attributeValuePerLevel = 10.0
dynamicSkill.attributeOperation = ADD_VALUE
```

This means:

```text
+10 max inventory weight per skill level
```

At skill level 20:

```text
+200 max inventory weight
```

::: warning
For `inventoryweight:generic.max_weight`, `ADD_VALUE` is recommended.

`ADD_MULTIPLIED_BASE` and `ADD_MULTIPLIED_TOTAL` are usually not useful for this attribute because the Inventory Weight attribute base value is normally `0`.
:::

## Carrying Skill

::: warning 1.21 only
The custom `carrying` skill, its datapack JSON, translations, and icon are a **1.21**
feature. On 1.20.1 there is no custom skill and `data/levelz/skill/inventory_weight.json`
is **not loaded** — use a built-in skill via `skill.skillKey` instead.
:::

The add-on is designed to work well with a custom LevelZ skill called:

```text
carrying
```

If you want to define or modify this skill using LevelZ datapacks, add this file:

```text
data/levelz/skill/inventory_weight.json
```

For mod/project resources, the equivalent development path is:

```text
src/main/resources/data/levelz/skill/inventory_weight.json
```

If a developer or modpack author needs to modify the Carrying skill, they should create or override this exact file and use this exact base structure:

```json
{
  "carrying": {
    "replace": false,
    "id": 12,
    "key": "carrying",
    "level": 20,
    "attributes": []
  }
}
```

The default LevelZ skills normally use IDs `0` through `11`, so `12` is the normal next id for the custom `carrying` skill.

::: warning
LevelZ skill IDs should be continuous. If another datapack or mod also adds a skill with id `12`, you may need to change the Carrying skill id.

To resolve an id conflict, create/override:

```text
data/levelz/skill/inventory_weight.json
```

or, inside a mod project:

```text
src/main/resources/data/levelz/skill/inventory_weight.json
```

Then change the skill `id` inside that JSON file and also change the matching skill id in the LevelZ Inventory Weight config.
:::

### Skill Translation and Icon *(1.21 only)*

To display the skill name and info in the LevelZ GUI, add translations:

```text
assets/levelz/lang/en_us.json
```

Example:

```json
{
  "skill.levelz.carrying": "Carrying",
  "skill.levelz.carrying.0": "Increases your maximum inventory weight.",
  "skill.levelz.carrying.1": "This skill is used by LevelZ Inventory Weight.",
  "skill.levelz.carrying.2": "Depending on server config, it can use either formula scaling or the Inventory Weight attribute."
}
```

LevelZ also expects a 16x16 icon:

```text
assets/levelz/textures/gui/sprites/carrying.png
```

::: info
If the icon is missing, the LevelZ GUI may show a missing-texture icon for the skill.
:::

## Dynamic Skill Creation

::: warning 1.21 only
Dynamic skill creation does **not** exist on 1.20.1. LevelZ 1.20.1 has no API to register
or modify skills at runtime, so the entire `dynamicSkill.*` section is absent on that build.
:::

The add-on can dynamically create the configured LevelZ skill if it does not already exist.

This is controlled by:

```text
dynamicSkill.createIfMissing
```

If enabled, the add-on checks whether LevelZ already has a skill with the configured `skill.skillKey`. If not, it creates one.

Dynamic skill config options:

| Option                                | Default     | Description                                                              |
| ------------------------------------- | ----------- | ----------------------------------------------------------------------- |
| `dynamicSkill.createIfMissing`        | `true`      | Creates the configured skill if LevelZ does not already have it.        |
| `dynamicSkill.skillId`                | `12`        | Skill id for the dynamically created skill. `-1` uses the next free id. |
| `dynamicSkill.maxLevel`               | `20`        | Max level of the dynamic skill.                                         |
| `dynamicSkill.attributeDisplayId`     | `-1`        | Attribute display id for LevelZ's attribute panel. `-1` hides it.       |
| `dynamicSkill.attributeBaseValue`     | `-10000.0`  | Base value for `LEVELZ_ATTRIBUTE` mode. `-10000.0` keeps the normal base. |
| `dynamicSkill.attributeValuePerLevel` | `10.0`      | Attribute value per skill level in `LEVELZ_ATTRIBUTE` mode.             |
| `dynamicSkill.attributeOperation`     | `ADD_VALUE` | Attribute modifier operation in `LEVELZ_ATTRIBUTE` mode.                |

::: warning
If you already include `data/levelz/skill/inventory_weight.json`, then LevelZ loads the `carrying` skill from the datapack file. In that case, dynamic creation normally does nothing because the skill already exists.
:::

## Hiding the Carrying Skill from LevelZ Screen

::: warning 1.21 only
GUI hiding does **not** exist on 1.20.1. There is no custom skill to hide, and the 1.20.1
LevelZ screen classes differ (`SkillScreen` / `SkillInfoScreen` / `SkillListScreen`
instead of `LevelScreen`), so the client mixins are not present on that build.
:::

The add-on can hide the configured skill from the LevelZ skill screen using a client-side mixin.

The skill is hidden when:

```text
skill.enabled = false
```

or when:

```text
skill.showInLevelZScreen = false
```

This is useful if the skill exists from a datapack file but the server disables the carrying integration.

::: warning
Hiding is safest when the hidden skill is the final skill id.

For the default LevelZ skill list, `carrying` should normally use id `12`, after the default ids `0` through `11`.

Do not hide a middle skill id unless you know the LevelZ GUI can handle it.
:::

## Datapack Attribute Example

::: warning 1.21 only
This applies to the 1.21 datapack skill JSON, which 1.20.1 does not load.
:::

If you want the Carrying skill to directly use the Inventory Weight attribute, the LevelZ skill JSON can include:

```json
{
  "carrying": {
    "replace": false,
    "id": 12,
    "key": "carrying",
    "level": 20,
    "attributes": [
      {
        "type": "inventoryweight:generic.max_weight",
        "operation": "ADD_VALUE",
        "value": 10.0
      }
    ]
  }
}
```

This gives:

```text
+10 max inventory weight per Carrying level
```

::: tip
If you use the attribute directly in LevelZ JSON, avoid also using `EVENT_MODIFIER` skill scaling for the same skill, otherwise the bonus may be applied twice.
:::

## Recommended Setup

### On 1.21

For most modpacks, use:

```text
skill.capacityMode = EVENT_MODIFIER
skill.skillKey = carrying
skill.additivePerLevel = 0.0
skill.multiplierPerLevel = 0.05
```

And keep the Carrying skill JSON with no attributes:

```json
{
  "carrying": {
    "replace": false,
    "id": 12,
    "key": "carrying",
    "level": 20,
    "attributes": []
  }
}
```

This gives multiplier-based scaling while keeping the LevelZ skill itself simple.

### On 1.20.1

There is no custom skill, no capacity mode, and no datapack file. Use a built-in skill:

```text
enabled = true

overallLevel.enabled = true
overallLevel.multiplierPerLevel = 0.01

skill.enabled = true
skill.skillKey = strength
skill.additivePerLevel = 0.0
skill.multiplierPerLevel = 0.05
```

This gives multiplier-based scaling from the existing **Strength** skill plus a small
overall-level bonus — no extra resources needed.

## Multiplayer Support

LevelZ Inventory Weight supports multiplayer.

The server is authoritative for:

- whether the integration is enabled
- overall level formula
- skill formula
- capacity mode *(1.21 only)*
- dynamic skill creation *(1.21 only)*
- final max weight calculation

On **1.21**, clients use synced config data for GUI-related behavior, such as hiding the
configured LevelZ skill from the LevelZ screen. On **1.20.1** there is no client-side GUI
behavior to sync (no skill hiding); the client only needs the synced numeric config.

## Troubleshooting

### The Carrying skill does not appear *(1.21)*

Check:

- `skill.enabled` is `true`
- `skill.showInLevelZScreen` is `true`
- the skill exists through datapack JSON or dynamic creation
- the skill id does not conflict with another LevelZ skill
- the LevelZ datapack was loaded successfully

### The Carrying skill appears even when disabled *(1.21)*

If you include:

```text
data/levelz/skill/inventory_weight.json
```

LevelZ will still load the skill. The add-on uses a client-side mixin to hide it from the LevelZ screen when disabled.

Make sure the add-on mixin file is included in `fabric.mod.json`.

### Attribute mode gives no multiplier effect *(1.21)*

This is expected in most cases.

`inventoryweight:generic.max_weight` normally has a base value of `0`, so multiplied attribute operations may not produce useful results.

Use:

```text
EVENT_MODIFIER
```

for multiplier scaling.

Use:

```text
LEVELZ_ATTRIBUTE + ADD_VALUE
```

for flat per-level bonuses.

### Skill level changes but max weight does not change

Check:

- `enabled = true`
- `skill.enabled = true`
- `skill.skillKey` matches the LevelZ skill key (on 1.20.1, a **built-in** key like `strength`)
- `skill.capacityMode` is correct *(1.21 only)*
- `skill.multiplierPerLevel` or `skill.additivePerLevel` is above `0`
- MT Inventory Weight is installed and working

### Dynamic skill creation does nothing *(1.21)*

This usually means the skill already exists.

If this file exists:

```text
data/levelz/skill/inventory_weight.json
```

then LevelZ creates the `carrying` skill before dynamic creation runs, so the add-on skips dynamic creation.

### On 1.20.1, `skill.skillKey = carrying` does nothing

Expected — `carrying` does not exist on 1.20.1, so the skill contributes level `0`. Set
`skill.skillKey` to one of: `health, strength, agility, defense, stamina, luck, archery,
trade, smithing, mining, farming, alchemy`.

### My 1.21 config keys are missing on 1.20.1

Expected. `capacityMode`, `showInLevelZScreen`, `skillIdOverride`, and `dynamicSkill.*` do
not exist on 1.20.1. They are ignored if present and have no effect.

## Related Documentation

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [LevelZ](https://modrinth.com/mod/levelz)
- [fzzy_config](https://modrinth.com/mod/fzzy-config)
