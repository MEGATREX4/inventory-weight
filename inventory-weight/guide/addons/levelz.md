---
title: "LevelZ"
description: "Detailed documentation for the LevelZ Inventory Weight add-on, including dependencies, configuration, capacity modes, the Carrying skill, dynamic skill creation, GUI hiding, and troubleshooting."
order: 10
---


# LevelZ Inventory Weight Add-on


**LevelZ Inventory Weight** is an add-on that integrates **MT Inventory Weight** with **LevelZ**.


You can download the add-on from Modrinth:


- [LevelZ Inventory Weight on Modrinth](++[https://modrinth.com/mod/levelz-inventory-weight](https://modrinth.com/mod/levelz-inventory-weight)++)


It allows the player's **LevelZ overall character level** and/or a configured **LevelZ skill** to increase the player's maximum inventory weight capacity.


This add-on is **not standalone**. It requires both **LevelZ** and **MT Inventory Weight**.


## Required Dependencies


You must install:


- [LevelZ](++[https://modrinth.com/mod/levelz](https://modrinth.com/mod/levelz)++)
- [MT Inventory Weight](++[https://modrinth.com/mod/inventory-weight](https://modrinth.com/mod/inventory-weight)++)
- [Fabric API](++[https://modrinth.com/mod/fabric-api](https://modrinth.com/mod/fabric-api)++)


Recommended versions:


| Dependency | Recommended Version |
| --- | --- |
| Minecraft | `1.21.1` |
| LevelZ | `2.0.10+1.21.1` or newer |
| MT Inventory Weight | `2.0.1-1.21` or newer |


::: warning
LevelZ Inventory Weight depends on both **LevelZ** and **MT Inventory Weight**. If either mod is missing, the add-on cannot work.
:::


## Mod IDs


The important mod IDs are:


```text
LevelZ:              levelz
MT Inventory Weight: inventoryweight
LevelZ add-on:       levelz_inventoryweight
```


::: info
The MT Inventory Weight Modrinth slug is usually `inventory-weight`, but its Fabric mod id is `inventoryweight`.

Use `inventoryweight` in `fabric.mod.json` dependencies and for the Inventory Weight attribute namespace.
:::


The Inventory Weight max-weight attribute is:


```text
inventoryweight:generic.max_weight
```


## What This Add-on Does


LevelZ Inventory Weight can increase max inventory weight from:


1. The player's total LevelZ character level.
2. One configured LevelZ skill, such as `carrying`.
3. A dynamically created LevelZ skill, if enabled and missing.
4. A LevelZ skill attribute using `inventoryweight:generic.max_weight`, if `LEVELZ_ATTRIBUTE` mode is selected.


The recommended setup is to use a custom LevelZ skill called:


```text
carrying
```


This skill can be shown in the LevelZ skill screen or hidden through the add-on config.


## Capacity Formula


When using the recommended `EVENT_MODIFIER` mode, the add-on modifies max weight with this formula:


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
Carrying skill level: 10

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


| Option | Default | Description |
| --- | ---: | --- |
| `enabled` | `true` | Master switch for the integration. |
| `minimumMaxWeight` | `1.0` | Safety minimum for the final max weight. |


### Overall Level Formula


The `overallLevel` section controls how the player's total LevelZ character level affects max inventory weight.


| Option | Default | Description |
| --- | ---: | --- |
| `overallLevel.enabled` | `true` | Enables overall-level scaling. |
| `overallLevel.additivePerLevel` | `0.0` | Flat max-weight bonus per total LevelZ level. |
| `overallLevel.multiplierPerLevel` | `0.01` | Multiplier bonus per total LevelZ level. `0.01` means +1% per level. |


Example:


```text
overallLevel.multiplierPerLevel = 0.01
overall level = 20

multiplier = 1.20
```


### Skill Formula


The `skill` section controls how one configured LevelZ skill affects max inventory weight.


| Option | Default | Description |
| --- | ---: | --- |
| `skill.enabled` | `true` | Enables skill-based scaling. |
| `skill.showInLevelZScreen` | `true` | Shows or hides the configured skill in the LevelZ screen. |
| `skill.capacityMode` | `EVENT_MODIFIER` | Determines how the skill affects capacity. |
| `skill.skillKey` | `carrying` | LevelZ skill key to use. |
| `skill.skillIdOverride` | `-1` | Optional numeric skill id override. `-1` means resolve by skill key. |
| `skill.additivePerLevel` | `0.0` | Flat max-weight bonus per configured skill level. |
| `skill.multiplierPerLevel` | `0.05` | Multiplier bonus per configured skill level. `0.05` means +5% per level. |


::: tip
Use `skillKey` instead of hardcoding the skill id when possible. LevelZ skills can be changed by datapacks, so resolving by key is safer.
:::


## Capacity Modes


### EVENT_MODIFIER


`EVENT_MODIFIER` is the recommended mode.


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


## Skill Translation and Icon


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


The add-on can dynamically create the configured LevelZ skill if it does not already exist.


This is controlled by:


```text
dynamicSkill.createIfMissing
```


If enabled, the add-on checks whether LevelZ already has a skill with the configured `skill.skillKey`. If not, it creates one.


Dynamic skill config options:


| Option | Default | Description |
| --- | ---: | --- |
| `dynamicSkill.createIfMissing` | `true` | Creates the configured skill if LevelZ does not already have it. |
| `dynamicSkill.skillId` | `12` | Skill id for the dynamically created skill. `-1` uses the next available id. |
| `dynamicSkill.maxLevel` | `20` | Max level of the dynamic skill. |
| `dynamicSkill.attributeDisplayId` | `-1` | Attribute display id for LevelZ's attribute panel. `-1` hides it from that panel. |
| `dynamicSkill.attributeBaseValue` | `-10000.0` | Base value for `LEVELZ_ATTRIBUTE` mode. `-10000.0` keeps the normal base. |
| `dynamicSkill.attributeValuePerLevel` | `10.0` | Attribute value per skill level in `LEVELZ_ATTRIBUTE` mode. |
| `dynamicSkill.attributeOperation` | `ADD_VALUE` | Attribute modifier operation in `LEVELZ_ATTRIBUTE` mode. |


::: warning
If you already include `data/levelz/skill/inventory_weight.json`, then LevelZ loads the `carrying` skill from the datapack file. In that case, dynamic creation normally does nothing because the skill already exists.
:::


## Hiding the Carrying Skill from LevelZ Screen


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


## Multiplayer Support


LevelZ Inventory Weight supports multiplayer.


The server is authoritative for:


- whether the integration is enabled
- overall level formula
- skill formula
- capacity mode
- dynamic skill creation
- final max weight calculation


Clients use synced config data for GUI-related behavior, such as hiding the configured LevelZ skill from the LevelZ screen.


## Troubleshooting


### The Carrying skill does not appear


Check:


- `skill.enabled` is `true`
- `skill.showInLevelZScreen` is `true`
- the skill exists through datapack JSON or dynamic creation
- the skill id does not conflict with another LevelZ skill
- the LevelZ datapack was loaded successfully


### The Carrying skill appears even when disabled


If you include:


```text
data/levelz/skill/inventory_weight.json
```


LevelZ will still load the skill. The add-on uses a client-side mixin to hide it from the LevelZ screen when disabled.


Make sure the add-on mixin file is included in `fabric.mod.json`.


### Attribute mode gives no multiplier effect


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
- `skill.skillKey` matches the LevelZ skill key
- `skill.capacityMode` is correct
- `skill.multiplierPerLevel` or `skill.additivePerLevel` is above `0`
- MT Inventory Weight is installed and working


### Dynamic skill creation does nothing


This usually means the skill already exists.


If this file exists:


```text
data/levelz/skill/inventory_weight.json
```


then LevelZ creates the `carrying` skill before dynamic creation runs, so the add-on skips dynamic creation.


## Related Documentation


- [MT Inventory Weight](++[https://modrinth.com/mod/inventory-weight](https://modrinth.com/mod/inventory-weight)++)
- [LevelZ](++[https://modrinth.com/mod/levelz](https://modrinth.com/mod/levelz)++)
- [fzzy_config](++[https://modrinth.com/mod/fzzy-config](https://modrinth.com/mod/fzzy-config)++)


