---
title: "Maximum Weight in MT Inventory Weight"
description: "A guide on how the maximum weight is determined in the MT Inventory Weight mod, including how multipliers, armor, and admin commands affect it."
---

# **Maximum Weight in MT Inventory Weight**

In the **MT Inventory Weight** mod, a player's **Maximum Weight** is the upper limit of how much they can carry in their inventory before penalties like the Overload effect kick in. This maximum weight is influenced by several factors: the player's base maximum weight, additional armor-based weight from pockets, multipliers set by admins, and more. Let's dive into how this system works.

## **Base Maximum Weight**

The **base maximum weight** is the default amount of weight a player can carry before experiencing penalties. This value can be configured by server administrators, providing flexibility for different gameplay scenarios.

- **Base Weight** is the standard value every player starts with.
- **Admin Control**: Admins can modify this base value using in-game commands to adjust how much weight each player can carry before being affected by Overload.

For example, an admin can increase the base weight for players who need to carry more items in specific roles or gameplay challenges.

## **Weight Multipliers**

While referred to as "multipliers," these are actually additional weight values that are **added** to the player's base maximum weight. These multipliers allow players to increase their overall carrying capacity, often applied through in-game rewards, achievements, or via admin commands.

- **Weight Multiplier**: This is a bonus value added on top of the base weight. It doesn't scale the weight but instead increases the maximum weight directly.
- **Admin Control**: Admins can add multipliers to a player's maximum weight using commands, giving them more flexibility in determining a player's inventory capacity.

For example, if a player has a base maximum weight of 50 units and an admin grants a multiplier of 10, the player's new maximum weight becomes 60 units.

## **Armor Pockets and Extra Carrying Capacity**

Armor equipped with **pockets** provides an additional carrying capacity for the player, adding to their overall maximum weight. The more pockets a player has in their armor, the higher their maximum carrying weight.

- **Armor Max Weight**: This is the weight provided by armor pieces with pockets. Players can increase their maximum weight by equipping armor with more pockets.
- **Pockets System**: For more detailed information on how armor pockets work and how they affect carrying capacity, visit the [Pockets Guide](./pockets.md).

By strategically using armor with pockets, players can increase their maximum weight, enabling them to carry more items without hitting the Overload limit.

## **Final Maximum Weight Calculation**

The player's **final maximum weight** is the sum of their base maximum weight, any multipliers applied by admins, and the additional weight provided by armor pockets. This total represents how much a player can carry before being penalized with the Overload effect.

### **Final Maximum Weight Formula**

1. **Base Maximum Weight**: The starting weight each player has.
2. **Weight Multiplier**: Additional weight added through in-game bonuses or admin commands.
3. **Armor Max Weight**: Extra weight from armor pockets.

The total maximum weight is calculated by simply **adding** these three factors together. For example:

- **Base Weight**: 50 units
- **Weight Multiplier**: 10 units
- **Armor Max Weight**: 20 units (from armor pockets)

**Final Maximum Weight**:  
\[ 50 + 10 + 20 = 80 \, \text{units} \]

In this case, the player can carry 80 units of weight before experiencing the Overload effect.

## **Admin Commands and Maximum Weight Control**

Admins have full control over a player's base maximum weight and can apply additional multipliers or reset values as needed. This flexibility is key to balancing inventory weight across different gameplay scenarios, making sure no player is unfairly penalized.

### **Admin Commands**

- **Set Base Maximum Weight**: Admins can adjust the base maximum weight for a player using commands. This changes the core value a player can carry before hitting the limit.
- **Set Weight Multiplier**: Admins can add extra weight to a player's maximum carrying capacity. This does not multiply the weight but adds a flat value to the base weight.

These commands are essential for managing different player roles, balancing team inventory, or creating unique gameplay challenges that rely on weight management.



