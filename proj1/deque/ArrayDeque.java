package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int first;
    private int last;
    private int factor = 2;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        first = -1;
        last = 0;
    }
    public  ArrayDeque(ArrayDeque other) {
        items = (T[]) new Object[8];
        size = 0;
        first = -1;
        last = 0;
        for (int index = 0; index < other.size; index++) {
            addLast((T)other.get(index));
        }
    }

    public void addLast(T item) {
        if (items.length == size) {
            resize(size*factor);
        }
        items[last] = item;
        last = (last+1) % items.length;
        size += 1;
    }
    public void addFirst(T item) {
        if (items.length == size) {
            resize(size*factor);
        }
        items[first] = item;
        first = (first-1) % items.length;
        size += 1;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public void printDeque() {
        int index = (first+1) % items.length;
        int stop = (last-1) % items.length;
        for (; index < stop; index++) {
            System.out.print(get(index)+" ");
        }
        System.out.print(get(index));
        System.out.println();
    }
    public T get(int index) {
        return items[(first+1+index)%items.length];
    }
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if ((items.length >= 16) && (4*size < items.length)) {
            resize(size);
        }
        T item = items[(first+1) % items.length];
        items[(first+1) % items.length] = null;
        first = (first+1) % items.length;
        size -= 1;
        return item;
    }
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if ((items.length >= 16) && (4*size < items.length)) {
            resize(size);
        }
        T item = items[(last-1) % items.length];
        items[(last-1) % items.length] = null;
        last = (last-1) % items.length;
        size -= 1;
        return item;
    }

    private void resize(int capacity) {
        T[] new_items = (T[]) new Object[capacity];
        for (int i=0; i<size; i++) {
            new_items[i] = get((i+first+1)%items.length);
        }
        items = new_items;
        first = -1;
        last = size;
    }
}
