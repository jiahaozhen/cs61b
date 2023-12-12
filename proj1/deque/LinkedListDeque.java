package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class ListNode {
        private   ListNode pre;
        private T item;
        private ListNode next;

        ListNode(ListNode p, T i, ListNode n) {
            item = i;
            pre = p;
            next = n;
        }
        ListNode(ListNode p, ListNode n) {
            pre = p;
            next = n;
        }
    }
    private ListNode frontSentinel;
    private ListNode lastSentinel;
    private int size;
    public LinkedListDeque() {
        size = 0;
        frontSentinel = new ListNode(null, null);
        lastSentinel = new ListNode(frontSentinel, null);
        frontSentinel.next = lastSentinel;
    }
    public LinkedListDeque(LinkedListDeque other) {
        frontSentinel = new ListNode(null, null);
        lastSentinel = new ListNode(frontSentinel, null);
        frontSentinel.next = lastSentinel;
        size = 0;
        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }
    }

    @Override
    public void addFirst(T item) {
        ListNode newNode = new ListNode(frontSentinel, item, frontSentinel.next);
        frontSentinel.next = newNode;
        newNode.next.pre = newNode;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        ListNode new_node = new ListNode(lastSentinel.pre, item, lastSentinel);
        lastSentinel.pre = new_node;
        new_node.pre.next = new_node;
        size += 1;
    }
    /*public boolean isEmpty() {
        return size == 0;
    }*/

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        ListNode p = frontSentinel.next;
        while (p != lastSentinel.pre) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println(p.item);
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        ListNode first = frontSentinel.next;
        first.next.pre = frontSentinel;
        frontSentinel.next = first.next;
        size -= 1;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        ListNode last = lastSentinel.pre;
        last.pre.next = lastSentinel;
        lastSentinel.pre = last.pre;
        size -= 1;
        return last.item;
    }

    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        ListNode p = frontSentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index > size - 1) {
            return null;
        }
        return getRecursiveHelper(index, frontSentinel.next);
    }

    private T getRecursiveHelper(int index, ListNode p) {
        if (index == 0) {
            return p.item;
        } else {
            return getRecursiveHelper(index-1, p.next);
        }
    }

    /** return an iterator*/
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int pos;

        LinkedListDequeIterator() {
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
