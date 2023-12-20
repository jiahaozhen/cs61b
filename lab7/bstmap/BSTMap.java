package bstmap;

import java.util.HashSet;
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
        Set<K> keyset = new HashSet<>();
        keySetRecursion(keyset, root);
        return keyset;
    }

    private void keySetRecursion(Set<K> keyset, BSTNode root) {
        if (root == null) {
            return;
        }
        keySetRecursion(keyset, root.leftTree);
        keyset.add(root.key);
        keySetRecursion(keyset, root.rightTree);
    }

    @Override
    public V remove(K key) {
        V value;
        if (!containsKey(key)) {
            return null;
        } else {
            value = get(key);
        }
        root = removeRecursion(key, root);
        size -= 1;
        return value;
    }

    private BSTNode removeRecursion(K key, BSTNode root) {
        if (root == null) {
            return null;
        } else if (root.key.compareTo(key) > 0) {
            root.leftTree = removeRecursion(key, root.leftTree);
        } else if (root.key.compareTo(key) < 0) {
            root.rightTree = removeRecursion(key, root.rightTree);
        } else {
            if (root.leftTree == null) {
                return root.rightTree;
            }
            if (root.rightTree == null) {
                return root.leftTree;
            }
            BSTNode originNode = root;
            root = getMinChild(root.rightTree);
            root.leftTree = originNode.leftTree;
            root.rightTree = removeRecursion(root.key, root.rightTree);
        }
        return root;
    }

    private BSTNode getMinChild(BSTNode root) {
        BSTNode p = root;
        while (p.leftTree != null) {
            p = p.leftTree;
        }
        return p;
    }

    @Override
    public V remove(K key, V value) {
        if (get(key) != value) {
            return null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
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
