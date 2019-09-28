package com.bwee.springboot.gae.model.pojo;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AtomicLong {

    private final Supplier<Long> get;
    private final Consumer<Long> set;
    private final Function<Long, Long> getAndIncrement;
    private final Consumer<String> setGroup;

    public AtomicLong(final Supplier<Long> get,
                      final Consumer<Long> set,
                      final Function<Long, Long> getAndIncrement,
                      final Consumer<String> setGroup) {
        this.get = get;
        this.set = set;
        this.getAndIncrement = getAndIncrement;
        this.setGroup = setGroup;
    }

    public long get() {
        return get.get();
    }

    public void set(final long value) {
        set.accept(value);
    }

    public long getAndIncrement() {
        return getAndIncrement(1);
    }

    public long getAndIncrement(final long value) {
        return getAndIncrement.apply(value);
    }

    public AtomicLong setGroup(final String group) {
        setGroup.accept(group);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomicLong that = (AtomicLong) o;
        return Objects.equals(get, that.get) &&
                Objects.equals(set, that.set) &&
                Objects.equals(getAndIncrement, that.getAndIncrement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(get, set, getAndIncrement);
    }

    @Override
    public String toString() {
        return "AtomicLong{" +
                "get=" + get +
                ", set=" + set +
                ", getAndIncrement=" + getAndIncrement +
                '}';
    }
}
