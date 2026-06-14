---
title: "FTB Ranks"
description: "FTB Ranks InventoryWeight connects FTB Ranks permission node values with MT Inventory Weight, allowing server admins to grant flat, percent, or multiplier-based carry capacity bonuses through FTB Ranks rank permissions."
---

FTB Ranks InventoryWeight is a server-side add-on that connects **FTB Ranks** permission values with **MT Inventory Weight** max carry capacity.

It allows server owners to give extra carry weight to players based on rank, condition-gated ranks, inherited ranks, or any other FTB Ranks setup that results in permission values being available on the player.

## Required Mods

Install these mods on the server:

- [MT Inventory Weight](https://modrinth.com/mod/inventory-weight)
- [FTB Ranks](https://www.curseforge.com/minecraft/mc-mods/ftb-ranks-fabric)
- [Architectury API](https://modrinth.com/mod/architectury-api) (not needed for 26.1+)
- [Fabric API](https://modrinth.com/mod/fabric-api)

::: info
FTB Ranks requires **Architectury API**. Make sure it is installed too, or FTB Ranks will not load.
:::

## Environment

This add-on is intended for the server.

It does not need to run on the client because it only changes server-side capacity calculation. FTB Ranks itself is also server-side.

FTB Ranks Inventory Weight can work for both:

- **Integrated server** — single player worlds and "Open to LAN" worlds, where your own client runs the internal server. The add-on (plus FTB Ranks and Architectury API) must be present in your client's mods folder for it to apply there.
- **Dedicated server** — a standalone multiplayer server. The add-on (plus FTB Ranks and Architectury API) must be present in the server's mods folder.

In both cases the logic runs on the server side; what differs is only *where* that server lives.

::: tip
On a **dedicated server**, players do not need this add-on installed on their clients. On an **integrated server** (single player / LAN host), the host's client is the server, so it does need to be installed there.
:::

## How It Works

MT Inventory Weight exposes a max-weight modification event:

```text
InventoryWeightEvents.MODIFY_MAX_WEIGHT
```

FTB Ranks InventoryWeight listens to that event. When MT Inventory Weight asks for a player's max carry capacity, the add-on:

1. Checks that FTB Ranks is loaded and its API has started.
2. Reads configured FTB Ranks permission nodes for the player.
3. Parses those values as floats.
4. Applies flat, percent, and multiplier bonuses to the current max weight.
5. Returns the modified value back to MT Inventory Weight.

No config file is required because all balancing is controlled through FTB Ranks.

## Supported Permission Nodes

The add-on reads exactly these FTB Ranks permission nodes:

```text
inventoryweight.maxweight.add
inventoryweight.maxweight.percent
inventoryweight.maxweight.multiplier
```

### `inventoryweight.maxweight.add`

Adds a flat amount to the player's max carry weight.

Example:

```text
/ftbranks node add vip inventoryweight.maxweight.add 50
```

Effect:

```text
+50 max weight
```

### `inventoryweight.maxweight.percent`

Adds a percentage bonus to the player's max carry weight.

Example:

```text
/ftbranks node add vip inventoryweight.maxweight.percent 25
```

Effect:

```text
+25% max weight
```

### `inventoryweight.maxweight.multiplier`

Applies a direct multiplier.

Example:

```text
/ftbranks node add vip inventoryweight.maxweight.multiplier 1.5
```

Effect:

```text
1.5x max weight
```

## Setting Nodes in `ranks.snbt`

FTB Ranks stores ranks in:

```text
config/ftbranks/ranks.snbt
```

You can set the nodes directly there instead of using commands:

```snbt
vip {
    permissions: {
        "inventoryweight.maxweight.add": 50
        "inventoryweight.maxweight.percent": 25
        "inventoryweight.maxweight.multiplier": 1.5
    }
}
```

::: tip
FTB Ranks permission values can be numeric. Set the node to a plain number (for example `50` or `1.5`) — do not append the value to the node name.
:::

## Formula

The add-on uses this formula:

```text
final max weight = (current max weight + add) * (1 + percent / 100) * multiplier
```

Where:

- `current max weight` is the value already calculated by MT Inventory Weight.
- `add` comes from `inventoryweight.maxweight.add`.
- `percent` comes from `inventoryweight.maxweight.percent`.
- `multiplier` comes from `inventoryweight.maxweight.multiplier`.

If a node is missing, the default values are:

```text
add = 0
percent = 0
multiplier = 1
```

## Example Calculation

Given:

```text
Current max weight: 100
inventoryweight.maxweight.add = 50
inventoryweight.maxweight.percent = 25
inventoryweight.maxweight.multiplier = 1.5
```

Calculation:

```text
final = (100 + 50) * (1 + 25 / 100) * 1.5
final = 150 * 1.25 * 1.5
final = 281.25
```

So the player's max weight becomes:

```text
281.25
```

## Rank Examples

### Default Rank

```text
/ftbranks node add default inventoryweight.maxweight.add 0
```

### VIP Rank

```text
/ftbranks node add vip inventoryweight.maxweight.add 50
/ftbranks node add vip inventoryweight.maxweight.percent 10
```

## Condition-based and Inherited Ranks

FTB Ranks supports rank conditions (for example permission-, time-, or team-based) and rank inheritance. Because the add-on reads the player's effective permission value, any node granted through an active or inherited rank is applied automatically.

If a player has multiple ranks that set the same node, FTB Ranks resolves which value applies (by rank power/priority), and the add-on uses that resolved value.

## Permissions, Not Meta

This add-on uses **FTB Ranks permission nodes** with numeric values.

Use:

```text
/ftbranks node add vip inventoryweight.maxweight.add 50
```

Do not encode the number into the node name:

```text
/ftbranks node add vip inventoryweight.maxweight.add.50 true
```

The node must hold the number as its value so the add-on can parse it.

## FTB Ranks Commands Reference

These are the general FTB Ranks commands used to manage ranks, players, and nodes. They are provided by FTB Ranks itself (not by this add-on). All commands start with `/ftbranks` and require operator (OP) permission.

| Command | Description |
| --- | --- |
| `/ftbranks reload` | Reloads the `ranks.snbt` and `players.snbt` files. Check your server log for errors. |
| `/ftbranks refresh_readme` | Regenerates `serverconfig/ftbranks/README.txt`, which lists all known command nodes registered on this server. Run it after adding/removing mods. |
| `/ftbranks list_all_ranks` | Lists all ranks defined in `ranks.snbt`. Ranks shown in **cyan** have no condition attached and must be added to a player manually with `/ftbranks add`. |
| `/ftbranks create <name> <power>` | Creates a rank named `<name>` with the given `<power>` level. It starts with no condition and no custom nodes. |
| `/ftbranks delete <rank>` | Deletes the named rank. This cannot be undone. |
| `/ftbranks add <players> <rank>` | Adds the given `<players>` to the rank. Entity selectors can target multiple players. |
| `/ftbranks remove <players> <rank>` | Removes the given `<players>` from the rank. Entity selectors can target multiple players. |
| `/ftbranks list_ranks_of <player>` | Lists all ranks the given player currently has. |
| `/ftbranks list_players_with <rank>` | Lists all players who currently have the given rank. |
| `/ftbranks node <add\|remove\|list> <rank> <node> <value>` | Adds, removes, or lists nodes on a rank. This is how you set the Inventory Weight nodes. |
| `/ftbranks condition <rank> <value>` | Sets the rank's condition. Simple conditions take a name; advanced conditions take a code block. Pass an empty string `""` to remove the condition. |
| `/ftbranks show_rank <rank>` | Dumps the full configuration for the given rank. |

### Setting the Inventory Weight nodes

The Inventory Weight bonuses are added with the `node add` command:

```text
/ftbranks node add vip inventoryweight.maxweight.add 50
/ftbranks node add vip inventoryweight.maxweight.percent 25
/ftbranks node add vip inventoryweight.maxweight.multiplier 1.5
```

List or remove them with:

```text
/ftbranks node list vip
/ftbranks node remove vip inventoryweight.maxweight.add
```

### Player and rank management examples

Create a rank and assign a player to it:

```text
/ftbranks create VIP 100
/ftbranks add PlayerName VIP
```

Inspect a rank or a player:

```text
/ftbranks show_rank VIP
/ftbranks list_ranks_of PlayerName
/ftbranks list_players_with VIP
```

After editing `ranks.snbt` by hand, reload it:

```text
/ftbranks reload
```

### Adding conditions

Conditions control when a rank is automatically active. (Inventory Weight nodes apply only while the rank is active for the player.)

Set a simple condition on the `Near Spawn` rank:

```text
/ftbranks condition "Near Spawn" spawn
```

Set an advanced condition on the `Veteran` rank (player has walked at least 500000 cm and has played for at least 3 weeks):

```text
/ftbranks condition Veteran { type: "and", conditions: [ { type: "stat", stat: "minecraft:walk_one_cm", value: 500000, value_check: ">=" }, { type: "playtime", time: 3, time_unit: "weeks" } ] }
```

Clear the condition from the `VIP` rank (after this, players only have `VIP` by direct assignment via `/ftbranks add`):

```text
/ftbranks condition VIP ""
```

## Troubleshooting

### FTB Ranks does not appear in the mod list

If the log does not contain `ftbranks`, then FTB Ranks is not installed or not being loaded.

Make sure the FTB Ranks Fabric jar (and Architectury API) is in the server mods folder.

### The add-on still logs `Hello Fabric world!`

That means the template initializer is still being used.

Replace the initializer with the real FTB Ranks InventoryWeight implementation.

### Nodes do nothing

Check:

- FTB Ranks is installed and loaded (and Architectury API is present).
- MT Inventory Weight is installed and loaded.
- FTB Ranks InventoryWeight is installed on the server.
- The node is spelled exactly correctly.
- The node value is a valid number.
- The rank is actually active for the player (conditions met, correct rank assigned).

Correct examples:

```text
inventoryweight.maxweight.add
inventoryweight.maxweight.percent
inventoryweight.maxweight.multiplier
```

### Invalid numbers are ignored

The add-on parses node values as floats.

Valid examples:

```text
50
25
1.5
2.0
```

Invalid examples:

```text
fifty
+25%
one point five
```

### Negative values

Negative values are clamped to neutral defaults to avoid breaking capacity:

- `add` and `percent` are floored at `0`.
- `multiplier` is floored at `0`.

### Nothing changes right after server start

FTB Ranks initializes its API as the server starts. If the API is not ready yet (or FTB Ranks is missing), the add-on returns the unmodified max weight instead of erroring. Once the server is fully started, the nodes apply normally.

### Client does not have the add-on

That is expected. This add-on is server-side only.

Clients still need whatever MT Inventory Weight requires for client-side HUD/tooltips, depending on your server/modpack setup.
