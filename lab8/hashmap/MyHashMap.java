package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author CYZ
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int DEFAULT_INITIAL_SIZE = 16;


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
    private int load = 0; //Nodes contained by this map
    private int size = DEFAULT_INITIAL_SIZE; //buckets number
    private double loadFactor=DEFAULT_LOAD_FACTOR;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(size);
    }

    public MyHashMap(int initialSize) {
        size = initialSize;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
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
        // TODO: optimization
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        int bucketIndex = Math.floorMod(key.hashCode(),size);
        Collection<Node> bucket = buckets[bucketIndex];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        int bucketIndex = Math.floorMod(key.hashCode(),size);
        if(!buckets[bucketIndex].removeIf(node -> node.key.equals(key))){
            load++;
        }
        buckets[bucketIndex].add(createNode(key, value));
        resize();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(K key) {
        int bucketIndex = Math.floorMod(key.hashCode(),size);
        for (Node node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     *
     * @return null if the map does not contain the key,otherwise the corresponding value.
     */
    @Override
    public V remove(K key) {
        int bucketIndex = Math.floorMod(key.hashCode(),size);
        for(Node node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                V value = node.value;
                buckets[bucketIndex].remove(node);
                load--;
                return value;
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     *
     * @return null if the map does not contain the key,or the corresponding value stored
     * does not match,otherwise the value.
     */
    @Override
    public V remove(K key, V value) {
        V _value = get(key);
        if(_value.equals(value)) {
            remove(key);
            load--;
            return _value;
        }
        return null;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return new Iterator<>() {
            int currentBucketIndex = 0;
            int currentNodeIndex = 0;
            Iterator<Node> iterator = buckets[currentBucketIndex].iterator();

            /**
             * Returns {@code true} if the iteration has more elements.
             * (In other words, returns {@code true} if {@link #next} would
             * return an element rather than throwing an exception.)
             *
             * @return {@code true} if the iteration has more elements
             */
            @Override
            public boolean hasNext() {
                /* invoke iterator.hasNext() here
                because a flaw in helper method resulting in the ignorance of the last element. */
                return iterator.hasNext() || hasNext(currentBucketIndex, currentNodeIndex);
            }

            // Recursive Helper Method for hasNext()
            private boolean hasNext(int bucketIndex, int nodeIndex) {
                if (buckets[bucketIndex].size() > nodeIndex) {
                    return true;
                }
                if (bucketIndex == size - 1) {
                    return false;
                }
                return hasNext(bucketIndex + 1, 0);
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public K next() {
                if (hasNext()) {
                    while(!iterator.hasNext()) {
                        currentBucketIndex++;
                        iterator = buckets[currentBucketIndex].iterator();
                    }
                    currentNodeIndex++;
                    return iterator.next().key;
                }
                throw new NoSuchElementException();
            }
        };
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return load;
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for(K key:this){
            keySet.add(key);
        }
        return keySet;
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        buckets = createTable(size);
        load = 0;
    }

    private void resize(){
        if((double)load/(double)size>loadFactor){
            int newSize = size*2;
            Collection<Node>[] newBuckets = createTable(newSize);
            for(K key:this){
                int bucketIndex = Math.floorMod(key.hashCode(),newSize);
                newBuckets[bucketIndex].add(createNode(key,this.get(key)));
                // this.get() crashes if size has doubled previously
            }
            size = newSize;
            buckets = newBuckets;
        }
    }
}
