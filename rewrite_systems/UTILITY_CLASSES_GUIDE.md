# Utility Classes - Quick Reference Guide

## ItemTypeChecker

Lightweight utility for checking item types without instanceof chains.

```java
import com.megatrex4.util.ItemTypeChecker;

// Check item type
if (ItemTypeChecker.isTool(item)) { }
if (ItemTypeChecker.isArmor(item)) { }
if (ItemTypeChecker.isFood(item)) { }

// Get item properties
int protection = ItemTypeChecker.getArmorProtection(item);
int maxStack = ItemTypeChecker.getMaxStackSize(item);
int durability = ItemTypeChecker.getMaxDurability(stack);

// Check conditions
if (ItemTypeChecker.isDurable(stack)) { }
if (ItemTypeChecker.canStack(item)) { }
if (ItemTypeChecker.isValidBlockStack(stack)) {
    Block block = ItemTypeChecker.getBlockFromStack(stack);
}
```

## WeightCalculationResult (Record)

Standardized result object for weight calculations.

```java
import com.megatrex4.util.WeightCalculationResult;

// Create result with both total and base weight
WeightCalculationResult result = WeightCalculationResult.of(1500, 1200);

// Or create with only base weight (no modifiers)
WeightCalculationResult result = WeightCalculationResult.ofBase(1200);

// Access values
float total = result.totalWeight;
float base = result.baseWeight;

// Get modifier information
float difference = result.getModifierDifference();      // 1500 - 1200 = 300
float multiplier = result.getModifierMultiplier();      // 1500 / 1200 = 1.25
boolean hasModifiers = result.hasModifiers();           // true
```

## WeightModifierCalculator

Centralized weight calculation formulas.

```java
import com.megatrex4.util.WeightModifierCalculator;

// Dynamic calculations
float stackMult = WeightModifierCalculator.calculateStackMultiplier(64);  // 1.15625
float rarityMult = WeightModifierCalculator.calculateRarityModifier(Rarity.RARE);  // 2.0
float foodWeight = WeightModifierCalculator.calculateFoodComponentWeight(foodComponent);

// Block weight calculations
float hardnessWeight = WeightModifierCalculator.calculateHardnessWeight(3.0f);  // 30.0
float resistanceWeight = WeightModifierCalculator.calculateBlastResistanceWeight(10.0f);  // 500.0
float transparencyMod = WeightModifierCalculator.calculateTransparencyModifier(false);  // 1.0

// Block type modifiers
float slabWeight = baseWeight * WeightModifierCalculator.calculateSlabModifier();  // * 0.5
float stairsWeight = baseWeight * WeightModifierCalculator.calculateStairsModifier();  // * 0.875

// Item modifiers
float durabilityWeight = WeightModifierCalculator.calculateToolDurabilityWeight(1500);  // 300.0
float armorWeight = WeightModifierCalculator.calculateArmorProtectionWeight(3);  // 30.0
float fireproofMult = WeightModifierCalculator.calculateFireproofMultiplier();  // 1.25
```

## WeightTooltipFormatter

Consistent weight formatting for tooltips and UI.

```java
import com.megatrex4.util.WeightTooltipFormatter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

// Format weights for display
String formatted = WeightTooltipFormatter.formatWeight(1500);  // "1.5k"
String formatted = WeightTooltipFormatter.formatWeight(1500000);  // "1.5M"
String numeric = WeightTooltipFormatter.formatNumericWeight(1500.5f);  // "1500"

// Create styled text components
Text text = WeightTooltipFormatter.createWeightText("Weight", 1500, Formatting.YELLOW);
// Output: "Weight: 1.5k" with yellow color

// Create comparison text
Text comparison = WeightTooltipFormatter.createWeightComparisonText(1500, 2000, Formatting.GREEN);
// Output: "1.5k/2.0k" with green color

// Create hint text
Text hint = WeightTooltipFormatter.createShiftHintText();
// Output: "[Hold Shift for more info]" in gray italic

// Percentage calculations
String percentage = WeightTooltipFormatter.formatPercentage(1500, 2000);  // "75.0%"

// Get color based on weight percentage
Formatting color = WeightTooltipFormatter.getWeightPercentageColor(1500, 2000);
// Returns: GREEN (< 50%), YELLOW (50-80%), GOLD (80-100%), RED (>= 100%)
```

## AttributeModifierManager

Simplified attribute modifier management.

```java
import com.megatrex4.util.AttributeModifierManager;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;

// Remove all modifiers at once
AttributeModifierManager.removeAllWeightModifiers(
    player, 
    speedModId, 
    attackSpeedModId, 
    damageModId
);

// Remove specific modifiers
AttributeModifierManager.removeSpeedModifier(player, speedModId);
AttributeModifierManager.removeAttackSpeedModifier(player, attackSpeedModId);
AttributeModifierManager.removeDamageModifier(player, damageModId);

// Apply modifiers only if not already present
AttributeModifierManager.applySpeedModifierIfAbsent(
    player, 
    speedModId, 
    -0.3,  // 30% slower
    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
);

AttributeModifierManager.applyAttackSpeedModifierIfAbsent(
    player, 
    attackSpeedModId, 
    -0.25,  // 25% slower attacks
    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
);

// Check if modifier is already applied
EntityAttributeInstance attribute = player.getAttributes()
    .getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
if (AttributeModifierManager.hasModifier(attribute, speedModId)) {
    // Modifier already applied
}
```

## NbtWeightDefinition (Record)

Type-safe NBT weight definitions.

```java
import com.megatrex4.util.NbtWeightDefinition;
import java.util.Map;

// Create a new NBT weight definition
Map<String, Float> weights = Map.of(
    "9mm", 0.1f,
    "20mm", 0.25f
);

NbtWeightDefinition ammoWeight = new NbtWeightDefinition(
    "modded_item:ammo",
    "AmmoType",
    weights
);

// Access the definition
String itemId = ammoWeight.itemId;
String nbtKey = ammoWeight.nbtKey;
Map<String, Float> values = ammoWeight.nbtValueWeights;

// Query specific values
Float weight9mm = ammoWeight.getWeightForValue("9mm");  // 0.1f
Float weight20mm = ammoWeight.getWeightForValue("20mm");  // 0.25f

// Check if value exists
if (ammoWeight.hasWeight("9mm")) {
    // Weight is defined for 9mm ammunition
}

// Get all defined values
Set<String> allTypes = ammoWeight.getDefinedValues();  // {9mm, 20mm}
```

## Integration Examples

### Calculating Item Weight with Modifiers

```java
float weight = InventoryWeightUtil.ITEMS;

// Apply stack multiplier
if (ItemTypeChecker.canStack(item)) {
    weight *= WeightModifierCalculator.calculateStackMultiplier(
        ItemTypeChecker.getMaxStackSize(item)
    );
}

// Apply rarity modifier
weight *= WeightModifierCalculator.calculateRarityModifier(stack.getRarity());

// Store result
WeightCalculationResult result = WeightCalculationResult.ofBase(weight);
```

### Creating Weight Tooltips

```java
float currentWeight = 1500;
float maxWeight = 2000;

// Format text
Formatting color = WeightTooltipFormatter.getWeightPercentageColor(currentWeight, maxWeight);
Text weightText = WeightTooltipFormatter.createWeightComparisonText(currentWeight, maxWeight, color);

// Add to tooltip
tooltip.add(weightText);
tooltip.add(WeightTooltipFormatter.createShiftHintText());
```

### Managing Player Attributes

```java
// Remove old modifiers
AttributeModifierManager.removeAllWeightModifiers(player, speedId, attackSpeedId, damageId);

// Apply new modifiers based on weight
float weightPercent = (currentWeight / maxWeight);
double speedPenalty = -(weightPercent * 0.5);  // 50% max penalty

AttributeModifierManager.applySpeedModifierIfAbsent(
    player,
    speedId,
    speedPenalty,
    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
);
```
