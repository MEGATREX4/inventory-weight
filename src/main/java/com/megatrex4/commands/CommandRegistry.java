package com.megatrex4.commands;

import com.megatrex4.InventoryWeightArmor;
import com.megatrex4.InventoryWeightHandler;
import com.megatrex4.config.ItemWeightsConfigServer;
import com.megatrex4.util.ItemWeights;
import com.megatrex4.data.PlayerDataHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ArmorItem;
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
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("base")
                                .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            ItemWeightsConfigServer.setMaxWeight(value);
                                            source.sendFeedback(() -> Text.translatable("command.inventoryweight.set.base", value), true);
                                            return 1;
                                        })
                                )
                        )
                        .then(CommandManager.literal("multiplier")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                                    float value = FloatArgumentType.getFloat(context, "value");
                                                    PlayerDataHandler.setPlayerMultiplier(targetPlayer, value);
                                                    source.sendFeedback(() -> Text.translatable("command.inventoryweight.set.multiplier.other", targetPlayer.getName().getString(), value), true);
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
                                            source.sendFeedback(() -> Text.translatable("command.inventoryweight.set.multiplier", value), true);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("get")
                        .then(CommandManager.literal("base")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float baseWeight = PlayerDataHandler.getPlayerMaxWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.base.other", targetPlayer.getName().getString(), baseWeight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float baseWeight = PlayerDataHandler.getPlayerMaxWeight(player);
                                    source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.base", baseWeight), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("multiplier")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float multiplier = PlayerDataHandler.getPlayerMultiplier(targetPlayer);
                                            source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.multiplier.other", targetPlayer.getName().getString(), multiplier), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float multiplier = PlayerDataHandler.getPlayerMultiplier(player);
                                    source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.multiplier", multiplier), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("combined")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float combinedValue = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(targetPlayer);
                                            source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.combined.other", targetPlayer.getName().getString(), combinedValue), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float combinedValue = PlayerDataHandler.getPlayerMaxWeightWithMultiplier(player);
                                    source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.combined", combinedValue), false);
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("value")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            float currentWeight = InventoryWeightHandler.calculateInventoryWeight(targetPlayer);
                                            source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.value.other", targetPlayer.getName().getString(), currentWeight), false);
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity player = source.getPlayer();
                                    float currentWeight = InventoryWeightHandler.calculateInventoryWeight(player);
                                    source.sendFeedback(() -> Text.translatable("command.inventoryweight.get.value", currentWeight), false);
                                    return 1;
                                })
                        )
                )
        );

        dispatcher.register(CommandManager.literal("debugweight")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();

                    if (player == null) {
                        source.sendError(Text.translatable("command.error.not_player"));
                        return 1;
                    }

                    ItemStack itemStack = player.getMainHandStack();
                    Identifier itemId = Registries.ITEM.getId(itemStack.getItem());
                    String itemIdString = (itemId != null) ? itemId.toString() : "unknown";

                    Float weight = ItemWeights.getCustomItemWeight(itemIdString);
                    if (weight != null) {
                        source.sendFeedback(() -> Text.translatable("command.debugweight", itemIdString, weight), false);
                    } else {
                        PlayerDataHandler.ItemCategoryInfo categoryInfo = PlayerDataHandler.getItemCategoryInfo(itemStack);
                        String itemCategory = categoryInfo.getCategory();
                        float fallbackWeight = ItemWeights.getItemWeight(categoryInfo.getStack());

                        source.sendFeedback(() -> Text.translatable("command.debugweight.fallback", itemCategory, fallbackWeight), false);
                    }

                    return 1;
                })
        );


        dispatcher.register(CommandManager.literal("debugarmor")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();

                    if (player == null) {
                        source.sendError(Text.translatable("command.error.not_player"));
                        return 1;
                    }

                    float totalArmorWeight = InventoryWeightArmor.calculateArmorWeight(player);
                    StringBuilder pocketsInfo = new StringBuilder();

                    boolean hasArmorWithPockets = false; // Flag to check if any armor has pockets

                    for (ItemStack armorPiece : player.getInventory().armor) {
                        if (armorPiece.getItem() instanceof ArmorItem armorItem) {
                            int pockets = InventoryWeightArmor.getPocketsBasedOnProtection(armorItem); // Use the new method
                            pocketsInfo.append(Registries.ITEM.getId(armorPiece.getItem()).toString())
                                    .append(": ").append(pockets)
                                    .append(" pockets ");
                            hasArmorWithPockets = true;
                        }
                    }

                    if (!hasArmorWithPockets) {
                        source.sendFeedback(() -> Text.translatable("command.debugarmor.no_pockets"), false);
                    } else {
                        source.sendFeedback(() -> Text.translatable("command.debugarmor.pockets", pocketsInfo.toString()), false);
                        source.sendFeedback(() -> Text.translatable("command.debugarmor.total_weight", totalArmorWeight), false);
                    }

                    return 1;
                })
        );
    }
}
