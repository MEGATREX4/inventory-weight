---
title: "Sophisticated Backpacks"
description: "Sophisticated Backpacks Inventory Weight provides accurate MT Inventory Weight support for Sophisticated Backpacks (Unofficial Fabric port) by reading real contents from BackpackStorage instead of relying on item NBT."
---

Sophisticated Backpacks Inventory Weight is an add-on that connects **Sophisticated Backpacks** with **MT Inventory Weight**.

It allows accurate weight calculation for Sophisticated Backpacks by reading the actual contents stored in the world's `BackpackStorage` (via UUID) rather than the limited NBT data on the item stack.

Generic container NBT readers often under-count or mis-count the weight of Sophisticated Backpacks. This add-on fixes that by using the Sophisticated Backpacks storage API.

It registers a high-priority **Inventory Weight item weight provider** so Sophisticated Backpacks items are handled accurately while all other items continue to use default behavior.

## Required Mods

Install these mods:

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [Sophisticated Backpacks (Unofficial Fabric port)](https://modrinth.com/mod/sophisticated-backpacks-(unoffical-fabric-port))
- [Fabric API](https://modrinth.com/mod/fabric-api)

## How It Works

MT Inventory Weight supports custom item weight providers via the `Inventory Weight` entrypoint.

Sophisticated Backpacks Inventory Weight registers its provider at priority 8500 (higher than the built-in generic backpacks provider at 8000).

When MT Inventory Weight asks for the weight of an item:

1. The provider checks if the item belongs to Sophisticated Backpacks.
2. For real backpacks, it reads the actual contents using the Fabric Transfer API (which resolves the UUID-backed `BackpackStorage`).
3. It recurses into the contents using `context.nested()` so nested containers, datapack weights, and other add-ons are respected.
4. For other Sophisticated Backpacks items (upgrades, etc.), it returns a small fixed weight.

If Sophisticated Backpacks is not present, the provider stays inactive and MT Inventory Weight falls back to its normal behavior.

No configuration file is needed. All behavior is automatic once the required mods are installed.

## Weight Model

The add-on matches the built-in backpack calculation used by MT Inventory Weight:

```
effectiveWeight = emptyBackpackWeight + (contentsWeight / 2)
```

- `contentsWeight` is the summed weight of everything inside the backpack (calculated recursively).
- The returned `WeightResult` carries both the effective (halved) weight and the full `contentsWeight` for display purposes.
- The empty backpack base weight is currently a constant (1.0 by default).

## Client Tooltip Behavior

A client mixin ensures that only actual backpacks from Sophisticated Backpacks are treated as weight containers in tooltips.

Non-backpack Sophisticated Backpacks items (such as upgrades) will not incorrectly show container weight information.

## Configuration

The empty backpack weight is currently hardcoded to 1.0.

Future versions may expose this value through a config system (such as fzzy_config).

## Examples

### Default Behavior

A Sophisticated Backpack with 10 kg of contents (after all inner weights are calculated):

```
effectiveWeight = 1.0 + (10 / 2) = 6.0
```

The backpack contributes 6.0 to carried weight, while the full 10.0 contents weight is recorded for display and overload calculations.

### Nested Backpacks

Nested backpacks are handled safely because the provider uses `context.nested()` when recursing, preventing infinite loops.

## Troubleshooting

### Sophisticated Backpacks items show incorrect weight

Make sure both Sophisticated Backpacks (Unofficial Fabric port) and this add-on are installed on the same side (server or client) as MT Inventory Weight.

### The add-on does nothing

Check the server/client log for messages from `sb-Inventory Weight`.

If Sophisticated Backpacks is missing, the add-on will log that it is inactive and will not register its provider.

### Tooltips still treat non-backpack SB items as containers

Ensure the client-side add-on is installed. The mixin that filters Sophisticated Backpacks items only runs on the client.

### Performance concerns with large backpacks

The add-on only reads contents when MT Inventory Weight requests a weight. It uses efficient Transfer API iteration and respects the `nested()` depth guard provided by MT Inventory Weight.
