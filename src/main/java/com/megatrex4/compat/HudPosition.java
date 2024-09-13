package com.megatrex4.compat;

public enum HudPosition {
    TOP_LEFT("TOP_LEFT"),
    TOP_RIGHT("TOP_RIGHT"),
    CENTER_LEFT("CENTER_LEFT"),
    CENTER_RIGHT("CENTER_RIGHT"),
    BOTTOM_LEFT("BOTTOM_LEFT"),
    BOTTOM_RIGHT("BOTTOM_RIGHT"),
    HOTBAR_LEFT("HOTBAR_LEFT"),
    HOTBAR_RIGHT("HOTBAR_RIGHT"),
    CENTER_HOTBAR("CENTER_HOTBAR"),
    CUSTOM("CUSTOM");

    private final String displayName;

    HudPosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static HudPosition fromString(String text) {
        for (HudPosition position : HudPosition.values()) {
            if (position.displayName.equalsIgnoreCase(text)) {
                return position;
            }
        }
        return HudPosition.BOTTOM_RIGHT; // Default value if no match is found
    }

}

