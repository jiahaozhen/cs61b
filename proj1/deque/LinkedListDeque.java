package deque;

import java.util.Iterator;
import java.util.ListIterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class ListNode {
        public  ListNode pre;
        public T item;
        public ListNode next;

        public ListNode(ListNode p, T i, ListNode n) {
            item = i;
            pre = p;
            next = n;
        }
        public ListNode(ListNode p, ListNode n) {
            pre = p;
            next = n;
        }
    }
    private ListNode front_sentinel;
    private ListNode last_sentinel;
    private int size;
    public LinkedListDeque() {
        size = 0;
        front_sentinel = new ListNode(null, null);
        last_sentinel = new ListNode(front_sentinel, null);
        front_sentinel.next = last_sentinel;
    }
    public LinkedListDeque(LinkedListDeque other) {
        front_sentinel = new ListNode(null,null);
        last_sentinel = new ListNode(front_sentinel, null);
        front_sentinel.next = last_sentinel;
        size = 0;
        for (int i = 0; i < other.size(); i++) {
            addLast((T)other.get(i));
        }
    }

    @Override
    public void addFirst(T item) {
        ListNode new_node = new ListNode(front_sentinel, item, front_sentinel.next);
        front_sentinel.next = new_node;
        new_node.next.pre = new_node;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        ListNode new_node = new ListNode(last_sentinel.pre, item, last_sentinel);
        last_sentinel.pre = new_node;
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
        ListNode p = front_sentinel.next;
        while (p != last_sentinel.pre) {
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
        ListNode first = front_sentinel.next;
        first.next.pre = front_sentinel;
        front_sentinel.next = first.next;
        size -= 1;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        ListNode last = last_sentinel.pre;
        last.pre.next = last_sentinel;
        last_sentinel.pre = last.pre;
        size -= 1;
        return last.item;
    }

    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        ListNode p = front_sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index > size - 1) {
            return null;
        }
        return getRecursive_helper(index, front_sentinel.next);
    }

    private T getRecursive_helper(int index, ListNode p){
        if (index == 0){
            return p.item;
        } else {
            return getRecursive_helper(index-1, p.next);
        }
    }

    /** return an iterator*/
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int pos;

        public LinkedListDequeIterator() {
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
