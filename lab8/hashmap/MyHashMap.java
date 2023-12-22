package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Jia Haozhen
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private double maxLoad;
    private double MAXLOAD = 0.75;
    private int INITIALSIZE = 16;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(INITIALSIZE);
        this.maxLoad = MAXLOAD;
        size = 0;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        this.maxLoad = MAXLOAD;
        size = 0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
        size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table =  new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // Your code won't compile until you do so!

    @Override
    public void clear() {
        size = 0;
        buckets = createTable(INITIALSIZE);
    }

    @Override
    public boolean containsKey(K key) {
       return getNode(key) != null;
    }

    @Override
    public V get(K key) {
        if (getNode(key) == null) {
            return null;
        } else {
            return getNode(key).value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (size > buckets.length * maxLoad) {
            resize(buckets.length * 2);
        }
        Node newNode = createNode(key, value);
        int index = Math.floorMod(key.hashCode(), buckets.length);
        if (containsKey(key)) {
            buckets[index].remove(getNode(key));
            size -= 1;
        }
        buckets[index].add(newNode);
        size += 1;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        for (K k : this) {
            set.add(k);
        }
        return set;
    }

    @Override
    public V remove(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                V value = node.value;
                bucket.remove(node);
                size -= 1;
                return value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (get(key) != value) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                bucket.remove(node);
                size -= 1;
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    private void resize(int capacity) {
        Collection<Node>[] newTable = createTable(capacity);
        Iterator<Node> iterator = new HashMapNodeIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            int index = Math.floorMod(node.key.hashCode(), capacity);
            newTable[index].add(node);
        }
        buckets = newTable;
    }

    private class HashMapNodeIterator implements Iterator<Node> {
        private int bucketIndex;
        private Iterator<Node> NodeIterator;

        public HashMapNodeIterator() {
            bucketIndex = 0;
            NodeIterator = buckets[bucketIndex].iterator();
        }
        @Override
        public boolean hasNext() {
            return NodeIterator.hasNext();
        }

        @Override
        public Node next() {
            Node next = NodeIterator.next();
            if (!NodeIterator.hasNext() && bucketIndex < buckets.length - 1){
                bucketIndex += 1;
                NodeIterator = buckets[bucketIndex].iterator();
            }
            return next;
        }
    }

    private class HashMapIterator implements Iterator<K> {

        private int bucketIndex;
        private Iterator<Node> NodeIterator;

        public HashMapIterator() {
            bucketIndex = 0;
            NodeIterator = buckets[bucketIndex].iterator();
        }
        @Override
        public boolean hasNext() {
            return NodeIterator.hasNext();
        }

        @Override
        public K next() {
            Node next = NodeIterator.next();
            if (!NodeIterator.hasNext() && bucketIndex < buckets.length - 1){
                bucketIndex += 1;
                NodeIterator = buckets[bucketIndex].iterator();
            }
            return next.key;
        }
    }

    private Node getNode(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> bucket = buckets[index];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }
}
