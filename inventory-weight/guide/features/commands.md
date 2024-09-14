---
title: "Commands in MT Inventory Weight"
description: "A guide on available admin commands in MT Inventory Weight mod, including setting and retrieving player inventory weights."
---

# **MT Inventory Weight Mod Commands**

In the **MT Inventory Weight** mod, a set of admin commands allows for full control over a player's inventory weight, including setting base values, adding multipliers, and retrieving current weight information. These commands are crucial for managing gameplay balance, adjusting weight limits, or debugging inventory and armor weight in the game.

---

## **Available Commands**

### **/inventoryweight**

The primary command used to manage inventory weight for players. It has several subcommands, allowing admins to set and get different weight values for themselves or other players.

#### **/inventoryweight set**

This command is used to set specific weight-related values for players. It has the following subcommands:

- **/inventoryweight set base \<value\>**
  - Sets the **base maximum weight** for the player issuing the command.
  - Example: `/inventoryweight set base 50`
  - This will set the player's base maximum weight to 50 units.

- **/inventoryweight set multiplier \<value\>**
  - Sets an additional **multiplier** (flat value) to the player's maximum weight.
  - Example: `/inventoryweight set multiplier 10`
  - This adds 10 units to the player's max weight.

- **/inventoryweight set multiplier \<player\> \<value\>**
  - Sets the multiplier for another player.
  - Example: `/inventoryweight set multiplier player123 15`
  - This will give player `player123` a 15-unit weight increase.

#### **/inventoryweight get**

This set of commands allows admins to retrieve information about the weight-related stats of players.

- **/inventoryweight get base**
  - Returns the **base maximum weight** of the player issuing the command.
  - Example: `/inventoryweight get base`

- **/inventoryweight get base \<player\>**
  - Returns the base maximum weight of another player.
  - Example: `/inventoryweight get base player123`

- **/inventoryweight get multiplier**
  - Returns the player's **current multiplier**.
  - Example: `/inventoryweight get multiplier`

- **/inventoryweight get multiplier \<player\>**
  - Retrieves the multiplier of another player.
  - Example: `/inventoryweight get multiplier player123`

- **/inventoryweight get combined**
  - Retrieves the player's **final maximum weight**, which includes the base weight, multiplier, and armor weight from pockets.
  - Example: `/inventoryweight get combined`

- **/inventoryweight get combined \<player\>**
  - Retrieves another player's final maximum weight.
  - Example: `/inventoryweight get combined player123`

- **/inventoryweight get value**
  - Returns the **current total weight** of the items in the player's inventory.
  - Example: `/inventoryweight get value`

- **/inventoryweight get value \<player\>**
  - Retrieves the current total weight of another player's inventory.
  - Example: `/inventoryweight get value player123`

---

### **/debugweight**

The `debugweight` command is useful for debugging individual item weights in the game. It can give information about the weight of the item currently held in the player's main hand.

- **/debugweight**
  - Displays the weight of the item the player is currently holding.
  - If a custom weight is assigned to the item, the command will show that. If not, it will display the default weight based on the item category.
  - Example: `/debugweight`
  
  Output could be something like:
  - "The item 'minecraft:diamond_sword' weighs 2.5 units."
  - If no custom weight exists, it will show the fallback weight: "The item 'minecraft:wood' weighs 1.0 units."

---

### **/debugarmor**

This command provides detailed information about the player's armor and the pockets it has.

- **/debugarmor**
  - Displays the total **armor weight** and how many **pockets** each armor piece has.
  - Example: `/debugarmor`
  
  Output example:
  - "Chestplate: 2 pockets, Leggings: 1 pocket."
  - "Total armor weight: 5 units."
  
  If no armor pieces have pockets, it will inform the player: "No pockets detected on equipped armor."

---

## **Usage Scenarios**

1. **Set Player Limits**: 
   - Use `/inventoryweight set base` and `/inventoryweight set multiplier` to adjust how much weight a player can carry before being affected by penalties.
   
2. **Get Player Info**:
   - Use `/inventoryweight get base`, `/inventoryweight get multiplier`, and `/inventoryweight get combined` to monitor how much weight a player can carry and adjust balance accordingly.

3. **Debugging Items and Armor**:
   - Use `/debugweight` to check how much weight an item adds to the player's inventory, especially useful for modded items or debugging custom weights.
   - Use `/debugarmor` to inspect how many pockets armor pieces have and their overall weight contribution.

---

## **Permissions**

All the above commands require the player issuing them to have a permission level of 4 or higher (typically admin-level permissions).

This ensures that only server administrators or players with appropriate permissions can adjust inventory weight settings or retrieve sensitive player weight information.

---

## **Summary**

The MT Inventory Weight mod provides a robust set of commands for managing and debugging inventory weight mechanics. Admins can easily modify a player's weight capacity, monitor their inventory stats, and debug items and armor to maintain balance in gameplay.
