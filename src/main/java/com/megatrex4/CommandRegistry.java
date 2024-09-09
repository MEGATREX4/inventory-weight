package com.megatrex4;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

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
                                .then(CommandManager.argument("player", StringArgumentType.string())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            String playerName = StringArgumentType.getString(context, "player");
                                            ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(playerName);

                                            if (targetPlayer == null) {
                                                source.sendError(Text.literal("Player not found."));
                                                return 1;
                                            }

                                            long weight = PlayerDataHandler.getPlayerMaxWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.literal("Max weight for " + playerName + ": " + weight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    long weight = PlayerDataHandler.getPlayerMaxWeight(player);
                                    source.sendFeedback(() -> Text.literal("Max weight: " + weight), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("value")
                                .then(CommandManager.argument("player", StringArgumentType.string())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            String playerName = StringArgumentType.getString(context, "player");
                                            ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(playerName);

                                            if (targetPlayer == null) {
                                                source.sendError(Text.literal("Player not found."));
                                                return 1;
                                            }

                                            long weight = PlayerDataHandler.getPlayerCurrentWeight(targetPlayer); // Implement this method if needed
                                            source.sendFeedback(() -> Text.literal("Current weight for " + playerName + ": " + weight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    long weight = PlayerDataHandler.getPlayerCurrentWeight(player); // Implement this method if needed
                                    source.sendFeedback(() -> Text.literal("Current weight: " + weight), false);
                                    return 1;
                                })
                        )
                )
        );
    }
}
