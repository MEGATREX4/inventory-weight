---
title: "Pockets"
description: "Learn how the pockets system works in the MT Inventory Weight mod and how to configure pockets for armor using datapacks."
---

# Pockets

In the **MT Inventory Weight** mod, the **pocket system** adds extra storage functionality to certain armor pieces. Pockets allow players to carry additional weight, enhancing inventory management while balancing the use of armor for both protection and utility.

## How Pockets Work

1. **Armor-Based Storage**:
   - Armor items in the game can be equipped with **pockets**. These pockets contribute to the total weight capacity of a player’s inventory.
   - The more pockets an armor piece has, the more weight it can carry.

2. **Pocket Assignment**:
   - Pockets can be assigned to armor either through default calculations or **customized using datapacks**.

3. **Default Pocket Calculation**:
   - If no specific number of pockets is defined for an armor item via a datapack, the mod uses a formula based on the **protection value** and **toughness** of the armor:
     ```java
     Math.max(1, 7 - (int)(protectionValue / 1.2) - toughnessValue);
     ```
   - Armor with higher protection and toughness values will generally have fewer pockets by default.

4. **Custom Pocket Configuration**:
   - You can **override the default pocket assignment** by defining custom values using datapacks.
   - For details on configuring pockets through datapacks, refer to the [Datapacks Guide](../datapacks.md).

## Customizing Pocket Weight

The weight each pocket can hold is determined by the **pocket weight** value in the mod’s configuration. 
* This allows you to set how much additional weight each pocket can carry, fine-tuning the balance between protection and storage.

## Pocket Calculation Example

The total weight a player can carry through armor pockets is calculated by multiplying the number of pockets each armor piece has by the pocket weight value. For example:

```java
totalArmorWeight += pockets * POCKET_WEIGHT;
```

If a player is wearing armor with 3 pockets and the pocket weight is set to 2.0, the total additional weight capacity from that armor would be:

```java
3 pockets * 2.0 = 6.0 additional weight capacity
```

This gives players an opportunity to balance their protection and storage needs by choosing different armor configurations.
