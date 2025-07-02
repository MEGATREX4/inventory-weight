# Creating Inventory Weight Datapacks

Inventory Weight reads custom configuration from datapacks. Datapacks can change
item weights and define how many pockets each armour piece has.

## Folder structure

```
<data pack root>
└─ data
   └─ inventoryweight
      ├─ armor/               # armour pocket files
      ├─ item_weights/        # legacy weight files
      └─ config.json          # optional global options
   └─ inventory
      └─ items/               # alternative folder for item weight files
```

Files inside `armor/` or `inventory/items/` can have any name but must end with
`.json`. You may keep everything in a single file or split it across multiple
files and folders.

## Item weights

Item weight overrides can be placed inside `data/<namespace>/inventoryweight/items/`. Each `.json` file is a single object where keys are item ids. Values may be a simple number or an object with a weight and optional NBT requirement.

```json
{
  "minecraft:stone": 200,
  "minecraft:diamond_sword": {"weight": 500},
  "minecraft:carrot_on_a_stick": {
    "weight": 100,
    "requirements": {
      "nbt": {"tag": "CustomModelData", "value": 1}
    }
  }
}
```

## Armor pockets

Armor pocket values are defined under `data/<namespace>/inventoryweight/armor/`. Files follow the same structure as item weights, mapping armor ids to a pocket count. NBT conditions work here as well.

```json
{
  "minecraft:iron_chestplate": 4,
  "minecraft:leather_helmet": {
    "pockets": 1,
    "requirements": {
      "nbt": {"tag": "display", "value": {"color": 123456}}
    }
  }
}
```

The number of pockets on a piece of armor increases the player's maximum carry weight. All files from these folders are loaded on world start or when datapacks reload.