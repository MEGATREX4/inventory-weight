package com.megatrex4.impl.weight;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

public final class NbtCompat {
    private NbtCompat() {}

    @Nullable
    public static NbtCompound compound(NbtCompound tag, String key) {
        return tag.getCompound(key).orElse(null);
    }

    public static NbtCompound compoundOrEmpty(NbtCompound tag, String key) {
        return tag.getCompound(key).orElseGet(NbtCompound::new);
    }

    public static NbtList list(NbtCompound tag, String key) {
        return tag.getList(key).orElseGet(NbtList::new);
    }

    @Nullable
    public static NbtCompound listCompound(NbtList list, int index) {
        return list.getCompound(index).orElse(null);
    }

    public static String string(NbtCompound tag, String key) {
        return tag.getString(key).orElse("");
    }

    public static String string(NbtElement element) {
        return element.asString().orElse("");
    }

    public static int intValue(NbtCompound tag, String key, int fallback) {
        return tag.getInt(key, fallback);
    }

    public static short shortValue(NbtCompound tag, String key, short fallback) {
        return tag.getShort(key, fallback);
    }
}