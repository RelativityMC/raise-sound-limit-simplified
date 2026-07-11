package com.ishland.fabric.rsls.common;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ListFromSortedSet<T> implements List<T> {

    private final SortedSet<T> delegate;

    public ListFromSortedSet(SortedSet<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public @NotNull Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public @NotNull <T1> T1[] toArray(@NotNull T1[] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.delegate.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return this.delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public T get(int index) {
        return Iterators.get(this.delegate.iterator(), index);
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return Iterators.indexOf(this.delegate.iterator(), input -> input == o);
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(@NotNull UnaryOperator<T> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Spliterator<T> spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public <T1> T1[] toArray(@NotNull IntFunction<T1[]> generator) {
        return this.delegate.toArray(generator);
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super T> filter) {
        return this.delegate.removeIf(filter);
    }

    @Override
    public @NotNull Stream<T> stream() {
        return this.delegate.stream();
    }

    @Override
    public @NotNull Stream<T> parallelStream() {
        return this.delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.delegate.forEach(action);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "ListFromSortedSet[" + this.delegate.toString() + "]";
    }
}
