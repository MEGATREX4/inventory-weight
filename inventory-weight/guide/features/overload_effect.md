---
title: "Overload Effect in MT Inventory Weight"
description: "Details on the Overload effect, its impact, and how it interacts with other effects like Strength and Haste in the MT Inventory Weight mod."
---

# **Overload Effect in MT Inventory Weight**

The **Overload** effect in the **MT Inventory Weight** mod is a status effect that penalizes players for exceeding their inventory weight limit. This guide explains how the Overload effect works, including its impact on player attributes and interactions with Strength and Haste effects.

## **Effect Overview**

The Overload effect decreases a player's movement speed, attack speed, and damage reduction based on their inventory weight. The intensity of these penalties increases with the level of the Overload effect, which is determined by how much the player exceeds their maximum weight limit.

### **Base Values**

- **Movement Speed Decrease:** Reduces movement speed by 10% at the base level. The decrease increases by 10% per level of the Overload effect, up to a maximum of 90% reduction.
- **Attack Speed Decrease:** Reduces attack speed by 10% at the base level. The decrease increases by 10% per level of the Overload effect, up to a maximum of 90% reduction.
- **Damage Reduction Decrease:** Reduces damage reduction by 10% at the base level. The reduction increases by 10% per level of the Overload effect, up to a maximum of 90% reduction.

### **Calculating Overload Level**

The level of the Overload effect is calculated based on how much the player's inventory weight exceeds their maximum weight:

- **Inventory Fullness Percentage:** The percentage of the inventory's weight capacity that is used.
- **Overload Level Calculation:** For every 25% above the maximum weight, the Overload level increases by 3 levels.
- **Maximum Overload Level:** Capped at 70.

### **Interactions with Strength and Haste Effects**

When a player has **Strength** or **Haste** effects, the Overload level can be adjusted:

- **Strength Effect:** Reduces the Overload level by an amount equal to twice the amplifier level (capped at 10). For example, a Strength II effect reduces the Overload level by 4.
- **Haste Effect:** Similarly reduces the Overload level by an amount equal to twice the amplifier level (capped at 10). For example, a Haste III effect reduces the Overload level by 6.

If a player has both Strength and Haste effects, the reduction is combined. However, the Overload level will never be reduced below 1.

### **Application and Removal**

- **Applying the Overload Effect:** The Overload effect is applied when the player's inventory weight exceeds the maximum weight limit. It is updated every 40 ticks (2 seconds).
- **Removing the Overload Effect:** If the player's inventory weight falls below the maximum limit, or if the player is in Creative Mode, the Overload effect is removed.

## **Example**

1. **Player Inventory Weight:** 150% of max weight.
2. **Overload Level Calculation:**
   - Percentage Full: 150%
   - Overload Levels: `(150 - 100) / 25 * 3 = 6`
   - Capped at 70 levels if exceeding.

3. **Strength II and Haste III Effects:**
   - Strength II: Reduces Overload level by 4.
   - Haste III: Reduces Overload level by 6.
   - Total Reduction: 10 (but cannot reduce below 1).

In this example, if the calculated Overload level is 6, applying the reductions from Strength II and Haste III will adjust the level to 1.

For more details on item weights and how they affect the Overload effect, refer to the [Inventory Weights Items](../options/inventory_weights_items) guide.