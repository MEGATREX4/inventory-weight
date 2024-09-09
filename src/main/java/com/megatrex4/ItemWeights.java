package com.megatrex4;

public class ItemWeights {
    public static final long BUCKET = 81000;
    public static final long BOTTLE = 27000;
    public static final long BLOCK = 81000;
    public static final long INGOT = 9000;
    public static final long NUGGET = 1000;
    public static final long MAX_WEIGHT = 5000; // Default maximum weight value

    // Method to convert weights to points
    public static long getItemWeight(String item) {
        switch (item) {
            case "bucket":
                return BUCKET / 1000;
            case "bottle":
                return BOTTLE / 1000;
            case "block":
                return BLOCK / 1000;
            case "ingot":
                return INGOT / 1000;
            case "nugget":
                return NUGGET / 1000;
            default:
                return 0;
        }
    }
}
