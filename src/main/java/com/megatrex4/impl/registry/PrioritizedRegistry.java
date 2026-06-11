package com.megatrex4.impl.registry;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PrioritizedRegistry<T> {
    private final List<Entry<T>> entries = new ArrayList<>();

    public synchronized void register(Identifier id, int priority, T value) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(value, "value");

        entries.removeIf(entry -> entry.id().equals(id));
        entries.add(new Entry<>(id, priority, value));
        entries.sort(Comparator.<Entry<T>>comparingInt(Entry::priority).reversed());
    }

    public synchronized List<Entry<T>> entries() {
        return List.copyOf(entries);
    }

    public record Entry<T>(Identifier id, int priority, T value) {}
}
