package com.megatrex4.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ClientKeyState {

    private static final long HIDE_DELAY_TICKS = 10L; // 0.5 sec at 20 TPS

    private static final int GLFW_KEY_LEFT_SHIFT = 340;
    private static final int GLFW_KEY_RIGHT_SHIFT = 344;

    private static boolean physicalShiftDown;
    private static boolean visibleShiftDown;
    private static long clientTicks;
    private static long releaseTick = Long.MIN_VALUE;

    private ClientKeyState() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientTicks++;

            if (client.screen == null) {
                clear();
                return;
            }

            boolean currentlyDown = client.hasShiftDown();

            if (currentlyDown) {
                pressShift();
                return;
            }

            if (physicalShiftDown) {
                releaseShift();
            }

            if (visibleShiftDown
                    && releaseTick != Long.MIN_VALUE
                    && clientTicks - releaseTick >= HIDE_DELAY_TICKS) {
                visibleShiftDown = false;
            }
        });
    }

    public static void pressShift() {
        physicalShiftDown = true;
        visibleShiftDown = true;
        releaseTick = Long.MIN_VALUE;
    }

    public static void releaseShift() {
        if (!physicalShiftDown && releaseTick != Long.MIN_VALUE) {
            return;
        }

        physicalShiftDown = false;
        releaseTick = clientTicks;
    }

    public static void setShiftDown(boolean value) {
        if (value) {
            pressShift();
        } else {
            releaseShift();
        }
    }

    public static void clear() {
        physicalShiftDown = false;
        visibleShiftDown = false;
        releaseTick = Long.MIN_VALUE;
    }

    public static boolean isShiftDown() {
        return visibleShiftDown;
    }
}