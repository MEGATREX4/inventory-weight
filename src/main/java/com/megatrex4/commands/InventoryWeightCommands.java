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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class InventoryWeightCommands {

    private InventoryWeightCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("inventoryweight")
                .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .then(Commands.literal("set")
                        .then(Commands.literal("base")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(1.0f))
                                        .executes(context -> {
                                            float value = FloatArgumentType.getFloat(context, "value");

                                            InventoryWeightConfig.getServer().maxWeight = value;

                                            InventoryWeightConfigEvents.applyServerConfigChange(
                                                    context.getSource().getServer(),
                                                    "command /inventoryweight set base"
                                            );

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable("command.inventoryweight.set.base", value),
                                                    true
                                            );

                                            return 1;
                                        })))
                        .then(Commands.literal("bonus")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    float value = FloatArgumentType.getFloat(context, "value");

                                                    setBonus(player, value);

                                                    context.getSource().sendSuccess(
                                                            () -> Component.translatable(
                                                                    "command.inventoryweight.set.bonus",
                                                                    player.getName().getString(),
                                                                    value
                                                            ),
                                                            true
                                                    );

                                                    return 1;
                                                })))
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            float value = FloatArgumentType.getFloat(context, "value");

                                            setBonus(player, value);

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable(
                                                            "command.inventoryweight.set.bonus",
                                                            player.getName().getString(),
                                                            value
                                                    ),
                                                    true
                                            );

                                            return 1;
                                        }))))
                .then(Commands.literal("get")
                        .then(Commands.literal("base")
                                .executes(context -> {
                                    float value = InventoryWeightConfig.getServer().maxWeight;

                                    context.getSource().sendSuccess(
                                            () -> Component.translatable("command.inventoryweight.get.base", value),
                                            false
                                    );

                                    return 1;
                                }))
                        .then(Commands.literal("bonus")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            float value = getBonus(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable(
                                                            "command.inventoryweight.get.bonus",
                                                            player.getName().getString(),
                                                            value
                                                    ),
                                                    false
                                            );

                                            return 1;
                                        }))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    float value = getBonus(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    "command.inventoryweight.get.bonus",
                                                    player.getName().getString(),
                                                    value
                                            ),
                                            false
                                    );

                                    return 1;
                                }))
                        .then(Commands.literal("combined")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            float value = InventoryWeightServices.capacityService().getMaxWeight(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable(
                                                            "command.inventoryweight.get.combined",
                                                            player.getName().getString(),
                                                            value
                                                    ),
                                                    false
                                            );

                                            return 1;
                                        }))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    float value = InventoryWeightServices.capacityService().getMaxWeight(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    "command.inventoryweight.get.combined",
                                                    player.getName().getString(),
                                                    value
                                            ),
                                            false
                                    );

                                    return 1;
                                }))
                        .then(Commands.literal("value")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            WeightResult value = InventoryWeightServices.playerWeightService()
                                                    .getInventoryWeight(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.translatable(
                                                            "command.inventoryweight.get.value",
                                                            player.getName().getString(),
                                                            value.weight()
                                                    ),
                                                    false
                                            );

                                            return 1;
                                        }))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    WeightResult value = InventoryWeightServices.playerWeightService()
                                            .getInventoryWeight(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    "command.inventoryweight.get.value",
                                                    player.getName().getString(),
                                                    value.weight()
                                            ),
                                            false
                                    );

                                    return 1;
                                }))));

        dispatcher.register(Commands.literal("debugweight")
                .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    ItemStack stack = player.getMainHandItem();
                    Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

                    WeightResult weight = InventoryWeightServices.weightService().getWeight(
                            stack,
                            new WeightContext(player.level(), player, 0)
                    );

                    context.getSource().sendSuccess(
                            () -> Component.translatable(
                                    "command.inventoryweight.debugweight",
                                    itemId.toString(),
                                    WeightMath.exact(weight.weight())
                            ),
                            false
                    );

                    return 1;
                })
        );

        dispatcher.register(Commands.literal("debugarmor")
                .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    boolean any = false;

                    for (EquipmentSlot slot : new EquipmentSlot[]{
                            EquipmentSlot.HEAD,
                            EquipmentSlot.CHEST,
                            EquipmentSlot.LEGS,
                            EquipmentSlot.FEET
                    }) {
                        ItemStack armor = player.getItemBySlot(slot);

                        if (ArmorAttributeHelper.isArmorStack(armor)) {
                            any = true;

                            int pockets = InventoryWeightServices.pocketService()
                                    .getPockets(armor, player)
                                    .orElse(0);

                            String item = BuiltInRegistries.ITEM.getKey(armor.getItem()).toString();

                            context.getSource().sendSuccess(
                                    () -> Component.translatable(
                                            "command.inventoryweight.debugarmor.piece",
                                            item,
                                            pockets
                                    ),
                                    false
                            );
                        }
                    }

                    if (!any) {
                        context.getSource().sendSuccess(
                                () -> Component.translatable("command.inventoryweight.debugarmor.empty"),
                                false
                        );
                    }

                    float combined = InventoryWeightServices.capacityService().getMaxWeight(player)
                            - InventoryWeightConfig.getServer().maxWeight
                            - getBonus(player);

                    context.getSource().sendSuccess(
                            () -> Component.translatable(
                                    "command.inventoryweight.debugarmor.total",
                                    combined
                            ),
                            false
                    );

                    return 1;
                })
        );
    }

    private static void setBonus(ServerPlayer player, float value) {
        PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player).ifPresent(component -> {
            component.setCapacityBonus(value);
            PlayerWeightComponentRegistry.PLAYER_WEIGHT.sync(player);
        });

        PlayerWeightController.updatePlayer(player);
    }

    private static float getBonus(ServerPlayer player) {
        return PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player)
                .map(component -> component.getCapacityBonus())
                .orElse(0.0f);
    }
}