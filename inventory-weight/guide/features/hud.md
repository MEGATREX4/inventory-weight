---
title: "Custom HUD Display in MT Inventory Weight"
description: "Detailed information on the custom HUD display for inventory weight, including visual indicators and settings."
---

# **Custom HUD Display in MT Inventory Weight**

The **MT Inventory Weight** mod enhances the Minecraft experience by adding a custom Heads-Up Display (HUD) that provides visual feedback about the player's inventory weight. This feature helps players monitor their load and manage their inventory more effectively.

## **Visual Display of Inventory Weight**

The HUD displays the player's current inventory weight relative to their maximum allowable weight using a series of visual indicators:

### **1. Fullness Indicator**

- **Empty Icon**: The background icon represents the player's weight capacity when empty. This is a static icon that shows the baseline for weight monitoring.
- **Filled Icons**: As the player accumulates weight, the HUD dynamically updates to show the current weight. The icon changes based on the proportion of weight to maximum capacity. Icons range from empty to full, with a total of 12 stages representing varying levels of weight. The more weight the player carries, the higher the filled icon displayed.
- **Overload Icon**: If the player's weight reaches or exceeds the maximum capacity, an overload icon is shown. This visual cue alerts the player that they are carrying too much and might need to offload items to avoid penalties or other effects.

### **2. Strength and Haste Effects**

- **STRENGTH_ICON**: When the player has active status effects such as Strength or Haste, an additional icon is displayed. This icon indicates that the player has these buffs, which might affect their gameplay in relation to inventory weight.

## **Client Settings**

The position of the HUD on the screen can be customized through the client settings. Players can choose from several predefined positions or set a custom location for the HUD:

``` md
- **TOP_LEFT**
- **TOP_RIGHT**
- **CENTER_LEFT**
- **CENTER_RIGHT**
- **BOTTOM_LEFT**
- **BOTTOM_RIGHT**
- **HOTBAR_LEFT**
- **HOTBAR_RIGHT**
- **CENTER_HOTBAR**
- **CUSTOM** (Allows precise positioning using x and y offsets)
```

These settings enable players to place the HUD where it is most convenient for their gameplay, ensuring that it does not obstruct important game visuals or information.
