package com.megatrex4.compat;

import com.megatrex4.InventoryWeight;
import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.util.HudPosition;
import com.megatrex4.util.InventoryWeightUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import com.megatrex4.config.ItemWeightsConfigClient;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.InventoryWeightState;

import java.util.Map;

public class InventoryWeightConfigScreen {

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.inventoryweight.config"));

        // Save action when user clicks "Save"
        builder.setSavingRunnable(() -> {
            ItemWeightsConfigClient.saveConfig();
            ItemWeightConfigItems.saveConfig();
            ItemWeightsConfigServer.saveConfig();
        });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Client Config Category
        ConfigCategory clientCategory = builder.getOrCreateCategory(Text.translatable("category.inventoryweight.client"));

        // Add the input field with description
        clientCategory.addEntry(entryBuilder.startStrField(
                                Text.translatable("option.inventoryweight.client.hudPosition"), // Input field title
                                ItemWeightsConfigClient.hudPosition // Default value as the current HudPosition name
                        )
                        .setDefaultValue(HudPosition.BOTTOM_RIGHT.getDisplayName())
                        .setSaveConsumer(newValue -> {
                            HudPosition newPosition = HudPosition.fromString(newValue);
                            if (newPosition != null) {
                                ItemWeightsConfigClient.hudPosition = newValue;
                            }
                        })
                        .setTooltip(Text.translatable("option.inventoryweight.client.hudPosition.tooltip")) // Description as tooltip
                        .build()
        );

        // X Offset for CUSTOM position
        clientCategory.addEntry(entryBuilder
                .startFloatField(Text.translatable("option.inventoryweight.client.xOffset"), ItemWeightsConfigClient.xOffset)
                .setDefaultValue(0.5f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setTooltip(Text.translatable("option.inventoryweight.client.Offset.tooltip"))
                .setSaveConsumer(newValue -> ItemWeightsConfigClient.xOffset = newValue)
                .build()
        );

        // Y Offset for CUSTOM position
        clientCategory.addEntry(entryBuilder
                .startFloatField(Text.translatable("option.inventoryweight.client.yOffset"), ItemWeightsConfigClient.yOffset)
                .setDefaultValue(0.5f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setTooltip(Text.translatable("option.inventoryweight.client.Offset.tooltip"))
                .setSaveConsumer(newValue -> ItemWeightsConfigClient.yOffset = newValue)
                .build()
        );



        // Server Config Category
        ConfigCategory serverCategory = builder.getOrCreateCategory(Text.translatable("category.inventoryweight.server"));

        // Add fields for server settings
        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.buckets"), ItemWeights.getItemWeight("buckets"))
                .setDefaultValue(ItemWeights.BUCKETS)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("buckets", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.creative"), ItemWeights.getItemWeight("creative"))
                .setDefaultValue(ItemWeights.CREATIVE)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("creative", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.bottles"), ItemWeights.getItemWeight("bottles"))
                .setDefaultValue(ItemWeights.BOTTLES)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("bottles", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.blocks"), ItemWeights.getItemWeight("blocks"))
                .setDefaultValue(ItemWeights.BLOCKS)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("blocks", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.ingots"), ItemWeights.getItemWeight("ingots"))
                .setDefaultValue(ItemWeights.INGOTS)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("ingots", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.nuggets"), ItemWeights.getItemWeight("nuggets"))
                .setDefaultValue(ItemWeights.NUGGETS)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("nuggets", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.items"), ItemWeights.getItemWeight("items"))
                .setDefaultValue(ItemWeights.ITEMS)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("items", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(
                        Text.translatable("option.inventoryweight.server.maxWeight"),
                        ItemWeightsConfigServer.loadMaxWeight())
                .setDefaultValue(InventoryWeightUtil.MAXWEIGHT)
                .setTooltip(Text.translatable("option.inventoryweight.server.maxWeight.tooltip"))
                .setSaveConsumer(newValue -> {
                    ItemWeightsConfigServer.setMaxWeight(newValue); // Update config file
                    // Here, you should adjust the InventoryWeightState for client-side use
                    InventoryWeightState.setClientMaxWeight(newValue); // Update client-side state
                })
                .build());



        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.pocketWeight"), InventoryWeightArmor.getPocketWeight())
                .setDefaultValue(InventoryWeightUtil.POCKET_WEIGHT)
                .setTooltip(Text.translatable("option.inventoryweight.server.pocketWeight.tooltip"))
                .setSaveConsumer(newValue -> InventoryWeightArmor.setPocketWeight("pocketWeight", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.inventoryweight.server.realistic_mode"), InventoryWeightUtil.REALISTIC_MODE)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> InventoryWeightUtil.REALISTIC_MODE = newValue)
                .build());

        // Items Config Category
        ConfigCategory itemsCategory = builder.getOrCreateCategory(Text.translatable("category.inventoryweight.items"));

        // Load item weights from JSON and add configuration entries
        ItemWeightConfigItems.loadConfig(); // Ensure this loads the items into ItemWeights

        // Ensure that ItemWeights.getCustomItemWeights() returns only the weights from inventory_weights_items.json
        for (Map.Entry<String, Float> entry : ItemWeights.getCustomItemWeights().entrySet()) {
            String itemName = entry.getKey();
            float weight = entry.getValue();

            // Create item display name and hint text
            Text itemDisplayName = Text.literal(itemName);

            // Add entry with hint and image
            itemsCategory.addEntry(entryBuilder.startFloatField(itemDisplayName, weight)
                    .setDefaultValue(weight)
                    .setSaveConsumer(newValue -> ItemWeights.setItemWeight(itemName, newValue))
                    .build());
        }

        return builder.build();
    }
}
