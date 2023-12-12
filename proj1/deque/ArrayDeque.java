package deque;

import java.util.Iterator;
//import afu.org.checkerframework.checker.oigj.qual.O;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int first;
    private int last;
    private int factor = 2;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        first = minusOne(0);
        last = 0;
    }
    public  ArrayDeque(ArrayDeque other) {
        items = (T[]) new Object[8];
        size = 0;
        first = minusOne(0);
        last = 0;
        for (int index = 0; index < other.size; index++) {
            addLast((T) other.get(index));
        }
    }

    @Override
    public void addLast(T item) {
        if (items.length == size) {
            resize(size * factor);
        }
        items[last] = item;
        last = plusOne(last);
        size += 1;
    }
    @Override
    public void addFirst(T item) {
        if (items.length == size) {
            resize(size * factor);
        }
        items[first] = item;
        first = minusOne(first);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    /*
    public boolean isEmpty() {
        return size() == 0;
    }*/

    @Override
    public void printDeque() {
        int index = 0;
        for (; index < size - 1; index++) {
            System.out.print(get(index) + " ");
        }
        System.out.println(get(index));
    }

    @Override
    public T get(int index) {
        return items[plusOne(first + index)];
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if ((items.length >= 16) && (4 * size < items.length)) {
            resize(size);
        }
        T item = items[plusOne(first)];
        items[plusOne(first)] = null;
        first = plusOne(first);
        size -= 1;
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if ((items.length >= 16) && (4 * size < items.length)) {
            resize(size);
        }
        T item = items[minusOne(last)];
        items[minusOne(last)] = null;
        last = minusOne(last);
        size -= 1;
        return item;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = get(i);
        }
        items = newItems;
        first = minusOne(0);
        last = plusOne(size - 1);
    }

    private int plusOne(int index) {
        int newIndex = index + 1;
        return newIndex % items.length;
    }

    private int minusOne(int index) {
        int newIndex = index - 1;
        if (newIndex >= 0) {
            return newIndex % items.length;
        } else {
            return newIndex % items.length + items.length;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            T returnItem = get(pos);
            pos += 1;
            return returnItem;
        }
    }
}
