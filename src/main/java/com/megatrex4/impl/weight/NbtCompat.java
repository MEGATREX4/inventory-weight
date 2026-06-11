package com.megatrex4.impl.weight;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

public final class NbtCompat {
    private NbtCompat() {}

    @Nullable
    public static CompoundTag compound(CompoundTag tag, String key) {
        return tag.getCompound(key).orElse(null);
    }

    public static CompoundTag compoundOrEmpty(CompoundTag tag, String key) {
        return tag.getCompound(key).orElseGet(CompoundTag::new);
    }

    public static ListTag list(CompoundTag tag, String key) {
        return tag.getList(key).orElseGet(ListTag::new);
    }

    @Nullable
    public static CompoundTag listCompound(ListTag list, int index) {
        return list.getCompound(index).orElse(null);
    }

    public static String string(CompoundTag tag, String key) {
        return tag.getString(key).orElse("");
    }

    public static String string(Tag element) {
        return element.asString().orElse("");
    }

    public static int intValue(CompoundTag tag, String key, int fallback) {
        return tag.getIntOr(key, fallback);
    }

    public static short shortValue(CompoundTag tag, String key, short fallback) {
        return tag.getShortOr(key, fallback);
    }
}