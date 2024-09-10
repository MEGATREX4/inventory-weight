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
                .setDefaultValue(1.0f)
                .setSaveConsumer(newValue -> ItemWeightsConfigClient.someSetting = newValue)
                .build());

        // Server Config Category
        ConfigCategory serverCategory = builder.getOrCreateCategory(Text.translatable("category.inventoryweight.server"));

        // Add fields for server settings
        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.buckets"), ItemWeights.getItemWeight("buckets"))
                .setDefaultValue(81.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("buckets", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.bottles"), ItemWeights.getItemWeight("bottles"))
                .setDefaultValue(27.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("bottles", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.blocks"), ItemWeights.getItemWeight("blocks"))
                .setDefaultValue(81.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("blocks", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.ingots"), ItemWeights.getItemWeight("ingots"))
                .setDefaultValue(9.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("ingots", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.nuggets"), ItemWeights.getItemWeight("nuggets"))
                .setDefaultValue(1.0f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("nuggets", newValue))
                .build());

        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.items"), ItemWeights.getItemWeight("items"))
                .setDefaultValue(0.5f)
                .setSaveConsumer(newValue -> ItemWeights.setItemWeight("items", newValue))
                .build());

        InventoryWeightState state = InventoryWeightState.fromNbt(null); // Assuming the state is loaded somewhere else
        serverCategory.addEntry(entryBuilder.startFloatField(Text.translatable("option.inventoryweight.server.maxWeight"), state.getMaxWeight())
                .setDefaultValue(10000.0f)
                .setSaveConsumer(state::setMaxWeight)
                .build());

        // Items Config Category
        ConfigCategory itemsCategory = builder.getOrCreateCategory(Text.translatable("category.inventoryweight.items"));
        // Add items-related settings here, similar to how we did for client and server

        return builder.build();
    }
}
