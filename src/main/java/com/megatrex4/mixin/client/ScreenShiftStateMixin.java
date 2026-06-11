package com.megatrex4.mixin.client;

import com.megatrex4.client.ClientKeyState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenShiftStateMixin {

    private static final int GLFW_KEY_LEFT_SHIFT = 340;
    private static final int GLFW_KEY_RIGHT_SHIFT = 344;

    @Dynamic("Minecraft 26.1 GUI input uses KeyEvent; IntelliJ Mixin plugin may not resolve this target.")
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void inventoryweight$keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isShiftKey(event)) {
            ClientKeyState.pressShift();
        } else {
            ClientKeyState.setShiftDown(event.hasShiftDown());
        }
    }

    @Dynamic("Minecraft 26.1 GUI input uses KeyEvent; IntelliJ Mixin plugin may not resolve this target.")
    @Inject(method = "keyReleased", at = @At("HEAD"), require = 0)
    private void inventoryweight$keyReleased(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isShiftKey(event)) {
            ClientKeyState.releaseShift();
        } else {
            ClientKeyState.setShiftDown(event.hasShiftDown());
        }
    }

    @Dynamic("Minecraft 26.1 Screen#onClose exists in runtime/decompiled sources, but IDE Mixin plugin may not resolve it.")
    @Inject(method = "onClose", at = @At("HEAD"), require = 0)
    private void inventoryweight$onClose(CallbackInfo ci) {
        ClientKeyState.clear();
    }

    private static boolean isShiftKey(KeyEvent event) {
        return event.key() == GLFW_KEY_LEFT_SHIFT || event.key() == GLFW_KEY_RIGHT_SHIFT;
    }
}