---
title: "PlayerEX"
description: "Detailed documentation for the PlayerEX Inventory Weight add-on, including dependencies, configuration, Carrying attribute setup, PlayerEX integration, capacity modes, multiplayer behavior, and troubleshooting."
order: 2
---

# PlayerEX Inventory Weight Add-on

PlayerEX Inventory Weight is an add-on that integrates MT Inventory Weight with PlayerEX: Directors Cut.

You can download the add-on from Modrinth:

- [PlayerEX Inventory Weight on Modrinth](https://modrinth.com/project/playerex-inventoryweight)

It allows the player's PlayerEX level and/or a configured PlayerEX attribute to increase maximum inventory weight capacity.

This add-on is not standalone. It requires both PlayerEX and MT Inventory Weight.

## Required Dependencies

You must install:

- [PlayerEX: Directors Cut](https://modrinth.com/mod/playerex-directors-cut)
- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
- [fzzy_config](https://modrinth.com/mod/fzzy-config)

PlayerEX also requires its own dependencies, such as Data Attributes: Directors Cut and owo-lib. If you install PlayerEX from Modrinth through a launcher, these dependencies should normally be installed automatically.

Recommended versions:

| Dependency | Recommended Version |
| --- | --- |
| Minecraft | `1.20.1` |
| PlayerEX: Directors Cut | `4.0.5+1.20.1` or newer |
| MT Inventory Weight | `2.0.1-1.20.1` or newer |

::: warning
PlayerEX Inventory Weight depends on both PlayerEX and MT Inventory Weight. If either mod is missing, the add-on cannot work.
:::

## Mod IDs

The MT Inventory Weight max-weight attribute is:

```text
inventoryweight:generic.max_weight
```

The custom Carrying attribute added by this add-on is:

```text
playerex_inventoryweight:carrying
```

::: info
The MT Inventory Weight Modrinth slug is usually `inventory-weight`, but its Fabric mod id is `inventoryweight`.

Use `inventoryweight` in `fabric.mod.json` dependencies and for the Inventory Weight attribute namespace.
:::

## What This Add-on Does

PlayerEX Inventory Weight can increase max inventory weight from:

1. The player's PlayerEX level attribute.
2. One configured PlayerEX attribute or skill.
3. The custom Carrying attribute added by this add-on.
4. A direct modifier applied to `inventoryweight:generic.max_weight`, if `INVENTORY_WEIGHT_ATTRIBUTE` mode is selected.

The recommended setup is to use the custom attribute:

```text
playerex_inventoryweight:carrying
```

When enabled, Carrying is added directly to PlayerEX's existing primary attribute list. It appears in the normal PlayerEX attribute screen together with:

```text
Constitution
Strength
Dexterity
Intelligence
Luckiness
Focus
Carrying
```

This means the add-on does not need a separate PlayerEX page for inventory weight. Players spend PlayerEX skill points on Carrying from the normal PlayerEX UI.

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
PlayerEX level: 10
Carrying level: 10

PlayerEX level multiplier per level: 0.01
Carrying multiplier per level: 0.05

Level multiplier:    1 + 10 * 0.01 = 1.10
Carrying multiplier: 1 + 10 * 0.05 = 1.50

Final max weight: 100 * 1.10 * 1.50 = 165
```

## Configuration File

The add-on uses a synced server config through fzzy_config.

The config id is:

```text
playerex_inventoryweight:server-config
```

The config is registered as a synced server/client config, so server gameplay settings can be synchronized to clients.

::: info
Config values are server-authoritative. In multiplayer, the server decides how much capacity PlayerEX level and attributes give.
:::

## Main Config Options

### General

| Option | Default | Description |
| --- | ---: | --- |
| `enabled` | `true` | Master switch for the integration. |
| `minimumMaxWeight` | `1.0` | Safety minimum for the final max weight. |

### PlayerEX Level Formula

The `level` section controls how the player's PlayerEX level affects max inventory weight.

| Option | Default | Description |
| --- | ---: | --- |
| `level.enabled` | `true` | Enables PlayerEX level scaling. |
| `level.levelAttributeId` | `playerex:level` | Attribute id used as the PlayerEX level. |
| `level.additivePerLevel` | `0.0` | Flat max-weight bonus per PlayerEX level. |
| `level.multiplierPerLevel` | `0.01` | Multiplier bonus per PlayerEX level. `0.01` means +1% per level. |

Example:

```text
level.multiplierPerLevel = 0.01
PlayerEX level = 20

multiplier = 1.20
```

### Skill or Attribute Formula

The `skill` section controls how one configured PlayerEX attribute affects max inventory weight.

| Option | Default | Description |
| --- | ---: | --- |
| `skill.enabled` | `true` | Enables configured attribute scaling. |
| `skill.capacityMode` | `EVENT_MODIFIER` | Determines how the configured attribute affects capacity. |
| `skill.attributeId` | `playerex_inventoryweight:carrying` | PlayerEX attribute id to use for capacity scaling. |
| `skill.additivePerLevel` | `0.0` | Flat max-weight bonus per configured attribute level. |
| `skill.multiplierPerLevel` | `0.05` | Multiplier bonus per configured attribute level. `0.05` means +5% per level. |
| `skill.attributeValuePerLevel` | `10.0` | Attribute value per level in `INVENTORY_WEIGHT_ATTRIBUTE` mode. |
| `skill.attributeOperation` | `ADDITION` | Attribute operation used in `INVENTORY_WEIGHT_ATTRIBUTE` mode. |

::: tip
Use the full attribute id, including namespace. For example, use `playerex_inventoryweight:carrying`, not just `carrying`.
:::

### Carrying Attribute

The `carryingAttribute` section controls the add-on's custom Carrying attribute.

| Option | Default | Description |
| --- | ---: | --- |
| `carryingAttribute.enabled` | `true` | Enables use of the custom Carrying attribute. |
| `carryingAttribute.addToPrimaryAttributeList` | `true` | Adds Carrying directly to PlayerEX's existing primary attribute list. |
| `carryingAttribute.maxValue` | `100.0` | Max value for the Carrying attribute. |

::: info
`carryingAttribute.addToPrimaryAttributeList = true` is what makes Carrying appear directly in the normal PlayerEX attributes menu.
:::

## Capacity Modes

### EVENT_MODIFIER

`EVENT_MODIFIER` is the recommended mode.

In this mode, PlayerEX Inventory Weight reads the configured PlayerEX attribute value and modifies MT Inventory Weight's max capacity through the MT Inventory Weight event system.

This mode supports:

- flat bonuses
- multiplier bonuses
- PlayerEX level scaling
- PlayerEX attribute scaling

Example:

```text
skill.capacityMode = EVENT_MODIFIER
skill.attributeId = playerex_inventoryweight:carrying
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

So the configured attribute gives a 2x max-weight multiplier.

### INVENTORY_WEIGHT_ATTRIBUTE

`INVENTORY_WEIGHT_ATTRIBUTE` applies a modifier directly to the MT Inventory Weight max-weight attribute:

```text
inventoryweight:generic.max_weight
```

This is best for flat attribute bonuses.

Example:

```text
skill.capacityMode = INVENTORY_WEIGHT_ATTRIBUTE
skill.attributeValuePerLevel = 10.0
skill.attributeOperation = ADDITION
```

This means:

```text
+10 max inventory weight per Carrying level
```

At Carrying level 20:

```text
+200 max inventory weight
```

::: warning
For `inventoryweight:generic.max_weight`, `ADDITION` is recommended.

Multiplied attribute operations are usually not useful for this attribute because the Inventory Weight attribute base value is normally `0`.
:::

## Carrying Attribute Setup

The add-on registers this attribute:

```text
playerex_inventoryweight:carrying
```

The translation key is:

```text
attribute.name.playerex_inventoryweight.carrying
```

Example language file:

```text
assets/playerex_inventoryweight/lang/en_us.json
```

```json
{
  "attribute.name.playerex_inventoryweight.carrying": "Carrying"
}
```

## Data Attributes Files

Registering the attribute in Java is not enough. PlayerEX uses Data Attributes to attach attributes to players.

The add-on includes a Data Attributes entity-type file:

```text
data/playerex_inventoryweight/data_attributes/entity_types/player.json
```

```json
{
  "entries": {
    "minecraft:player": {
      "playerex_inventoryweight:carrying": {
        "value": 0.0
      }
    }
  }
}
```

The add-on also includes a Data Attributes override file:

```text
data/playerex_inventoryweight/data_attributes/overrides/carrying.json
```

```json
{
  "entries": {
    "playerex_inventoryweight:carrying": {
      "enabled": true,
      "min": 0.0,
      "max": 100.0,
      "smoothness": 1.0,
      "formula": "Flat"
    }
  }
}
```

::: tip
If Carrying does not appear or cannot be leveled, check that these files are inside the built jar.
:::

## Direct PlayerEX Menu Integration

PlayerEX's default attribute menu is built from:

```text
PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS
```

By default, PlayerEX includes:

```text
playerex:constitution
playerex:strength
playerex:dexterity
playerex:intelligence
playerex:luckiness
playerex:focus
```

PlayerEX Inventory Weight adds:

```text
playerex_inventoryweight:carrying
```

to that same primary attribute list.

This makes Carrying appear as a normal PlayerEX attribute instead of creating a separate Inventory Weight page.

::: warning
This integration depends on PlayerEX's current primary attribute list behavior. If a future PlayerEX version changes how the attribute screen is built, this add-on may need an update.
:::

## Refund Support

PlayerEX's default refund logic only counts its built-in primary attributes.

PlayerEX Inventory Weight registers an extra refund condition for:

```text
playerex_inventoryweight:carrying
```

This means players can refund Carrying points like other PlayerEX primary attributes, assuming they have refundable points available.

## Using an Existing PlayerEX Attribute Instead

You do not have to use Carrying. You can configure the add-on to use an existing PlayerEX attribute.

Examples:

```text
skill.attributeId = playerex:constitution
skill.attributeId = playerex:strength
skill.attributeId = playerex:dexterity
skill.attributeId = playerex:intelligence
skill.attributeId = playerex:luckiness
skill.attributeId = playerex:focus
skill.attributeId = playerex:mining
skill.attributeId = playerex:smithing
skill.attributeId = playerex:farming
```

This is useful if you do not want to add a new attribute to PlayerEX.

## Recommended Setup

For most modpacks, use:

```text
level.enabled = true
level.levelAttributeId = playerex:level
level.additivePerLevel = 0.0
level.multiplierPerLevel = 0.01

skill.enabled = true
skill.capacityMode = EVENT_MODIFIER
skill.attributeId = playerex_inventoryweight:carrying
skill.additivePerLevel = 0.0
skill.multiplierPerLevel = 0.05

carryingAttribute.enabled = true
carryingAttribute.addToPrimaryAttributeList = true
```

This gives:

```text
+1% max weight per PlayerEX level
+5% max weight per Carrying level
```

## Multiplayer Support

PlayerEX Inventory Weight supports multiplayer.

The server is authoritative for:

- whether the integration is enabled
- PlayerEX level formula
- configured attribute formula
- capacity mode
- final max weight calculation

Clients receive synced config values through fzzy_config where applicable.

Both the server and client should have:

- PlayerEX
- MT Inventory Weight
- PlayerEX Inventory Weight
- required dependencies

## Troubleshooting

### Carrying does not appear in the PlayerEX menu

Check:

- `carryingAttribute.enabled` is `true`
- `carryingAttribute.addToPrimaryAttributeList` is `true`
- the add-on jar contains the Data Attributes files
- the add-on jar contains the lang file
- PlayerEX Inventory Weight is installed on the client
- PlayerEX version is compatible

### Carrying appears but cannot be leveled

Check:

- the player has PlayerEX skill points
- `data/playerex_inventoryweight/data_attributes/entity_types/player.json` is loaded
- `data/playerex_inventoryweight/data_attributes/overrides/carrying.json` is loaded
- the attribute max is high enough

### Carrying level changes but max weight does not change

Check:

- `enabled = true`
- `skill.enabled = true`
- `skill.attributeId = playerex_inventoryweight:carrying`
- `skill.capacityMode = EVENT_MODIFIER`
- `skill.multiplierPerLevel` or `skill.additivePerLevel` is above `0`
- MT Inventory Weight is installed and working

### Data Attributes says the same number of entries as before

If Data Attributes logs do not show an increased number of entries after installing the add-on, the data files may not be included in the jar or may be in the wrong path.

Expected paths inside the jar:

```text
data/playerex_inventoryweight/data_attributes/entity_types/player.json
data/playerex_inventoryweight/data_attributes/overrides/carrying.json
```

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
INVENTORY_WEIGHT_ATTRIBUTE + ADDITION
```

for flat per-level bonuses.

### I want to remove the custom Carrying skill

Set:

```text
carryingAttribute.addToPrimaryAttributeList = false
```

Then set `skill.attributeId` to an existing PlayerEX attribute, for example:

```text
playerex:strength
```

## Related Documentation

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [PlayerEX: Directors Cut](https://modrinth.com/mod/playerex-directors-cut)
- [fzzy_config](https://modrinth.com/mod/fzzy-config)
