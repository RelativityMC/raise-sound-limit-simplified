package com.ishland.fabric.rsls.common;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class HashSetList<E> extends ObjectLinkedOpenHashSet<E> implements List<E> {

    public HashSetList(int expected, float f) {
        super(expected, f);
    }

    public HashSetList(int expected) {
        super(expected);
    }

    public HashSetList() {
        super();
    }

    public HashSetList(Collection<? extends E> c, float f) {
        super(c, f);
    }

    public HashSetList(Collection<? extends E> c) {
        super(c);
    }

    public HashSetList(ObjectCollection<? extends E> c, float f) {
        super(c, f);
    }

    public HashSetList(ObjectCollection<? extends E> c) {
        super(c);
    }

    public HashSetList(Iterator<? extends E> i, float f) {
        super(i, f);
    }

    public HashSetList(Iterator<? extends E> i) {
        super(i);
    }

    public HashSetList(E[] a, int offset, int length, float f) {
        super(a, offset, length, f);
    }

    public HashSetList(E[] a, int offset, int length) {
        super(a, offset, length);
    }

    public HashSetList(E[] a, float f) {
        super(a, f);
    }

    public HashSetList(E[] a) {
        super(a);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        return this.addAll(c);
    }

    @Override
    public E get(int index) {
        return Iterators.get(this.iterator(), index);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return Iterators.indexOf(this.stream().iterator(), input -> input == o);
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}
