package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{

    private int size;
    private BSTNode root;
    private class BSTNode {
        public K key;
        public V value;
        public BSTNode leftTree;
        public BSTNode rightTree;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            leftTree = null;
            rightTree = null;
        }
    }

    public BSTMap() {
        size = 0;
        root = null;
    }
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKeyRecursion(key, root);
    }

    private boolean containsKeyRecursion(K key, BSTNode root) {
        if (root == null) {
            return false;
        } else if (root.key.compareTo(key) > 0) {
            return containsKeyRecursion(key, root.leftTree);
        } else if (root.key.compareTo(key) == 0) {
            return true;
        } else if (root.key.compareTo(key) < 0) {
            return containsKeyRecursion(key, root.rightTree);
        }
        return false;
    }

    @Override
    public V get(K key) {
        return getRecursion(key, root);
    }

    private V getRecursion(K key, BSTNode root) {
        if (root == null) {
            return null;
        } else if (root.key.compareTo(key) > 0) {
            return getRecursion(key, root.leftTree);
        } else if (root.key.compareTo(key) == 0) {
            return root.value;
        } else if (root.key.compareTo(key) < 0) {
            return getRecursion(key, root.rightTree);
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = putRecursion(key, value, root);
    }

    private BSTNode putRecursion(K key, V value, BSTNode root) {
        if (root == null) {
            root = new BSTNode(key, value);
            size += 1;
        } else if (root.key.compareTo(key) > 0) {
            root.leftTree = putRecursion(key, value, root.leftTree);
        } else if (root.key.compareTo(key) < 0) {
            root.rightTree = putRecursion(key, value, root.rightTree);
        }
        return root;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("this function is not supported");
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("this function is not supported");
        //return null;
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("this function is not supported");
        //return null;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("this function is not supported");
    }

    private class BSTMapIter implements Iterator<K> {

        private int index;
        public BSTMapIter() {

        }
        @Override
        public boolean hasNext() {

        }

        @Override
        public K next() {
            return null;
        }
    }

    public void printInOrder() {
        printInOrderRecursion(root);
    }

    private void printInOrderRecursion(BSTNode root) {
        if (root == null) {
            return;
        }
        printInOrderRecursion(root.leftTree);
        System.out.println(root.key);
        printInOrderRecursion(root.rightTree);
    }

}
