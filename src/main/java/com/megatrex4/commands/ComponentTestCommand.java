package com.megatrex4.commands;

import com.megatrex4.component.PlayerWeightComponentRegistry;
import com.megatrex4.config.InventoryWeightConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ComponentTestCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("ccatest")
                .executes(context -> {
                    var player = context.getSource().getPlayer();
                    if (player == null) {
                        context.getSource().sendError(Text.literal("No player found!"));
                        return 0;
                    }
                    
                    // Get the component
                    var component = PlayerWeightComponentRegistry.PLAYER_WEIGHT.maybeGet(player);
                    
                    if (component.isEmpty()) {
                        context.getSource().sendError(Text.literal("✗ CCA Component Not Found!"));
                        return 0;
                    }
                    
                    context.getSource().sendFeedback(
                        () -> Text.literal("✓ CCA Component Found!"),
                        false
                    );
                    
                    context.getSource().sendFeedback(
                        () -> Text.literal("  Global Max Weight: " + InventoryWeightConfig.getServer().maxWeight),
                        false
                    );
                    
                    context.getSource().sendFeedback(
                        () -> Text.literal("  Current Weight: " + component.get().getCurrentInventoryWeight()),
                        false
                    );
                    
                    context.getSource().sendFeedback(
                        () -> Text.literal("  Weight Multiplier: " + component.get().getWeightMultiplier()),
                        false
                    );
                    
                    context.getSource().sendFeedback(
                        () -> Text.literal("  Is Overloaded: " + component.get().isOverloaded()),
                        false
                    );
                    
                    return 1;
                }));
    }
}
