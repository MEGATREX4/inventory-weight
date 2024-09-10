package com.megatrex4;

import com.megatrex4.ItemWeights;
import com.megatrex4.PlayerDataHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    int value = IntegerArgumentType.getInteger(context, "value");
                                    PlayerDataHandler.setPlayerMaxWeight(player, value);
                                    source.sendFeedback(() -> Text.literal("Max weight set to: " + value), false);
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("get")
                        .then(CommandManager.literal("max")
                                .then(CommandManager.argument("player", EntityArgumentType.player()) // Autocomplete player names
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

                                            float weight = PlayerDataHandler.getPlayerMaxWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Max weight for " + targetPlayer.getName().getString() + ": " + weight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float weight = PlayerDataHandler.getPlayerMaxWeight(player);
                                    source.sendFeedback(() -> Text.literal("Max weight: " + weight), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("value")
                                .then(CommandManager.argument("player", EntityArgumentType.player()) // Autocomplete player names
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

                                            float weight = PlayerDataHandler.getPlayerCurrentWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Current weight for " + targetPlayer.getName().getString() + ": " + weight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float weight = PlayerDataHandler.getPlayerCurrentWeight(player);
                                    source.sendFeedback(() -> Text.literal("Current weight: " + weight), false);
                                    return 1;
                                })
                        )
                )
        );

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
