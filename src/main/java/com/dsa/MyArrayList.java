package com.dsa;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayList<E> implements List<E> {

    //初始化10个元素
    private Object[] table = new Object[10];
    //有效元素size个
    private int size;

    @Override
    public void add(Object element) {
        if (size == table.length) {
            resize(table);
        }
        table[size] = element;
    }

    private void resize(Object[] table) {
        Object[] newTable = new Object[table.length * 2];
        System.arraycopy(table, 0, newTable, 0, table.length);
        this.table = newTable;
    }

    @Override
    public void add(Object element, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == table.length) {
            resize(table);
        }
        System.arraycopy(table, index, table, index + 1, size - index);
        table[index] = element;
        size++;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E removeElement = (E) table[index];
        System.arraycopy(table, index + 1, table, index, size - index - 1);
        table[size] = null;
        size--;
        return removeElement;
    }

    @Override
    public boolean remove(Object element) {
        for (int i = 0; i < size; i++) {
            if (element == table[i]) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public E set(int index, Object element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E oldValue = (E) table[index];
        table[index] = element;
        return oldValue;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (E) table[index];
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayListIterator();
    }

    class ArrayListIterator implements Iterator<E> {

        int cursor;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public E next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            E element = (E) table[cursor];
            cursor++;
            return element;
        }
    }

}
