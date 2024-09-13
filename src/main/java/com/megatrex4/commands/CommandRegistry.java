package com.megatrex4.commands;

import com.megatrex4.InventoryWeightHandler;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.data.PlayerDataHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CommandRegistry {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("inventoryweight")
                .then(CommandManager.literal("set")
                        // Set Max Base Weight for a specific player or self
                        .then(CommandManager.literal("base")
                                .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity player = source.getPlayer();
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            PlayerDataHandler.setPlayerMaxWeight(player, value);
                                            source.sendFeedback(() -> Text.literal("Max base weight set to: " + value), false);
                                            return 1;
                                        })
                                )
                        )
                        // Set Max Multiplier for a specific player or self
                        .then(CommandManager.literal("multiplier")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                                    float value = FloatArgumentType.getFloat(context, "value");
                                                    PlayerDataHandler.setPlayerMultiplier(targetPlayer, value);
                                                    source.sendFeedback(() -> Text.literal("Max weight multiplier for " + targetPlayer.getName().getString() + " set to: " + value), false);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity player = source.getPlayer();
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            PlayerDataHandler.setPlayerMultiplier(player, value);
                                            source.sendFeedback(() -> Text.literal("Max weight multiplier set to: " + value), false);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("get")
                        // Get Max Base Weight
                        .then(CommandManager.literal("base")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float baseWeight = PlayerDataHandler.getPlayerMaxWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Max base weight for " + targetPlayer.getName().getString() + ": " + baseWeight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float baseWeight = PlayerDataHandler.getPlayerMaxWeight(player);
                                    source.sendFeedback(() -> Text.literal("Max base weight: " + baseWeight), false);
                                    return 1;
                                })
                        )
                        // Get Max Multiplier
                        .then(CommandManager.literal("multiplier")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float multiplier = PlayerDataHandler.getPlayerMultiplier(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Max weight multiplier for " + targetPlayer.getName().getString() + ": " + multiplier), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float multiplier = PlayerDataHandler.getPlayerMultiplier(player);
                                    source.sendFeedback(() -> Text.literal("Max weight multiplier: " + multiplier), false);
                                    return 1;
                                })
                        )
                        // get base + multiplier together
                        .then(CommandManager.literal("combined")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float combinedValue = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Combined base weight and multiplier for " + targetPlayer.getName().getString() + ": " + combinedValue), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float combinedValue = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
                                    source.sendFeedback(() -> Text.literal("Combined base weight and multiplier: " + combinedValue), false);
                                    return 1;
                                })
                        )

                        // Get Current Inventory Weight
                        .then(CommandManager.literal("value")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float currentWeight = InventoryWeightHandler.calculateInventoryWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Current weight for " + targetPlayer.getName().getString() + ": " + currentWeight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float currentWeight = InventoryWeightHandler.calculateInventoryWeight(player);
                                    source.sendFeedback(() -> Text.literal("Current weight: " + currentWeight), false);
                                    return 1;
                                })
                        )
                )
        );

        // Debug command to get the weight of the item in hand
        dispatcher.register(CommandManager.literal("debugweight")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();

                    if (player == null) {
                        source.sendError(Text.literal("You must be a player to run this command."));
                        return 1;
                    }

                    ItemStack itemStack = player.getMainHandStack();
                    Identifier itemId = Registries.ITEM.getId(itemStack.getItem());
                    String itemIdString = (itemId != null) ? itemId.toString() : "unknown";

                    Float weight = ItemWeights.getCustomItemWeight(itemIdString);
                    if (weight != null) {
                        source.sendFeedback(() -> Text.literal("Weight of item in hand (" + itemIdString + "): " + weight), false);
                    } else {
                        String itemCategory = PlayerDataHandler.getItemCategory(itemStack);
                        final String finalItemCategory = itemCategory;
                        float fallbackWeight = ItemWeights.getItemWeight(itemCategory);

                        source.sendFeedback(() -> Text.literal("Weight of item in hand (" + finalItemCategory + "): " + fallbackWeight), false);
                    }

                    return 1;
                })
        );
    }
}
