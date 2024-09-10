package com.megatrex4.compat;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import com.megatrex4.config.ItemWeightsConfigClient;
import com.megatrex4.config.ItemWeightConfigItems;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.ItemWeights;
import com.megatrex4.InventoryWeightState;
import net.minecraft.util.Identifier;

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
        clientCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.client.someSetting"), ItemWeightsConfigClient.someSetting)
                .setDefaultValue(10.0f)
                .setSaveConsumer(newValue -> ItemWeightsConfigClient.someSetting = newValue)
                .build());

        // Server Config Category
        ConfigCategory serverCategory = builder.getOrCreateCategory(Text.translatable("category.inventoryweight.server"));

        // Add fields for server settings
        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.buckets"), ItemWeights.getItemWeight("buckets"))
                .setDefaultValue(810.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("buckets", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.bottles"), ItemWeights.getItemWeight("bottles"))
                .setDefaultValue(270.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("bottles", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.blocks"), ItemWeights.getItemWeight("blocks"))
                .setDefaultValue(810.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("blocks", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.ingots"), ItemWeights.getItemWeight("ingots"))
                .setDefaultValue(90.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("ingots", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.nuggets"), ItemWeights.getItemWeight("nuggets"))
                .setDefaultValue(10.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("nuggets", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.items"), ItemWeights.getItemWeight("items"))
                .setDefaultValue(20.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("items", newValue))
                .build());

        InventoryWeightState state = InventoryWeightState.fromNbt(null);
        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.maxWeight"), state.getMaxWeight())
                .setDefaultValue(8000.0f)
                .setSaveConsumer(state::setMaxWeight)
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
