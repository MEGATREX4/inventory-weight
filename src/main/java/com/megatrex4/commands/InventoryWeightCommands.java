package com.megatrex4.commands;

import com.megatrex4.api.v1.WeightContext;
import com.megatrex4.api.v1.WeightResult;
import com.megatrex4.component.PlayerWeightComponentRegistry;
import com.megatrex4.config.InventoryWeightConfig;
import com.megatrex4.impl.InventoryWeightServices;
import com.megatrex4.impl.config.InventoryWeightConfigEvents;
import com.megatrex4.impl.player.PlayerWeightController;
import com.megatrex4.impl.weight.ArmorAttributeHelper;
import com.megatrex4.impl.weight.WeightMath;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class InventoryWeightCommands {
    private InventoryWeightCommands() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("inventoryweight")
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("base")
                                .then(CommandManager.argument("value", FloatArgumentType.floatArg(1.0f))
                                        .executes(context -> {
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            InventoryWeightConfig.getServer().maxWeight = value;
                                            InventoryWeightConfigEvents.applyServerConfigChange(context.getSource().getServer(), "command /inventoryweight set base");
                                            context.getSource().sendFeedback(() -> Text.translatable("command.inventoryweight.set.base", value), true);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("bonus")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                    float value = FloatArgumentType.getFloat(context, "value");
                                                    setBonus(player, value);
                                                    context.getSource().sendFeedback(
                                                            () -> Text.translatable("command.inventoryweight.set.bonus", player.getName().getString(), value),
                                                            true
                                                    );
                                                    return 1;
                                                })))
                                .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            setBonus(player, value);
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("command.inventoryweight.set.bonus", player.getName().getString(), value),
                                                    true
                                            );
                                            return 1;
                                        }))))
                .then(CommandManager.literal("get")
                        .then(CommandManager.literal("base")
                                .executes(context -> {
                                    float value = InventoryWeightConfig.getServer().maxWeight;
                                    context.getSource().sendFeedback(() -> Text.translatable("command.inventoryweight.get.base", value), false);
                                    return 1;
                                }))
                        .then(CommandManager.literal("bonus")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            float value = getBonus(player);
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("command.inventoryweight.get.bonus", player.getName().getString(), value),
                                                    false
                                            );
                                            return 1;
                                        }))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    float value = getBonus(player);
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("command.inventoryweight.get.bonus", player.getName().getString(), value),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(CommandManager.literal("combined")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            float value = InventoryWeightServices.capacityService().getMaxWeight(player);
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("command.inventoryweight.get.combined", player.getName().getString(), value),
                                                    false
                                            );
                                            return 1;
                                        }))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    float value = InventoryWeightServices.capacityService().getMaxWeight(player);
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("command.inventoryweight.get.combined", player.getName().getString(), value),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(CommandManager.literal("value")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(context -> {
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            WeightResult value = InventoryWeightServices.playerWeightService().getInventoryWeight(player);
                                            context.getSource().sendFeedback(
                                                    () -> Text.translatable("command.inventoryweight.get.value", player.getName().getString(), value.weight()),
                                                    false
                                            );
                                            return 1;
                                        }))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                    WeightResult value = InventoryWeightServices.playerWeightService().getInventoryWeight(player);
                                    context.getSource().sendFeedback(
                                            () -> Text.translatable("command.inventoryweight.get.value", player.getName().getString(), value.weight()),
                                            false
                                    );
                                    return 1;
                                }))));

        dispatcher.register(CommandManager.literal("debugweight")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    ItemStack stack = player.getMainHandStack();
                    Identifier itemId = Registries.ITEM.getId(stack.getItem());
                    WeightResult weight = InventoryWeightServices.weightService().getWeight(
                            stack,
                            new WeightContext(player.getWorld(), player, 0)
                    );
                    context.getSource().sendFeedback(
                            () -> Text.translatable("command.inventoryweight.debugweight", itemId.toString(), WeightMath.exact(weight.weight())),
                            false
                    );
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("debugarmor")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    boolean any = false;
                    for (EquipmentSlot slot : new EquipmentSlot[]{
                            EquipmentSlot.HEAD,
                            EquipmentSlot.CHEST,
                            EquipmentSlot.LEGS,
                            EquipmentSlot.FEET
                    }) {
                        ItemStack armor = player.getEquippedStack(slot);

                        if (ArmorAttributeHelper.isArmorStack(armor)) {
                            any = true;

                            int pockets = InventoryWeightServices.pocketService()
                                    .getPockets(armor, player)
                                    .orElse(0);

                            String item = Registries.ITEM.getId(armor.getItem()).toString();

                            context.getSource().sendFeedback(
                                    () -> Text.translatable("command.inventoryweight.debugarmor.piece", item, pockets),
                                    false
                            );
                        }
                    }

                    if (!any) {
                        context.getSource().sendFeedback(() -> Text.translatable("command.inventoryweight.debugarmor.empty"), false);
                    }

                    float combined = InventoryWeightServices.capacityService().getMaxWeight(player) - InventoryWeightConfig.getServer().maxWeight - getBonus(player);
                    context.getSource().sendFeedback(
                            () -> Text.translatable("command.inventoryweight.debugarmor.total", combined),
                            false
                    );
                    return 1;
                })
        );
    }

    private static void setBonus(ServerPlayerEntity player, float value) {
        PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player).ifPresent(component -> {
            component.setCapacityBonus(value);
            PlayerWeightComponentRegistry.PLAYER_WEIGHT.sync(player);
        });
        PlayerWeightController.updatePlayer(player);
    }

    private static float getBonus(ServerPlayerEntity player) {
        return PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player)
                .map(component -> component.getCapacityBonus())
                .orElse(0.0f);
    }
}
