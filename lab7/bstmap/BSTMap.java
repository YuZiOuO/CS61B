package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable,V> implements Map61B{

    private static class BSTNode<K extends Comparable,V> {
        // left for the less
        BSTNode<K,V> left, right;
        K key;
        V value;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = right = null;
        }

        /* Find node by key recursively.*/
        public static <K extends Comparable,V> BSTNode<K,V> containsKey(BSTNode<K,V> node,Object key) {
            //Only requires that variable key implements compareTo method.
            if (node == null) {
                return null;
            }
            if (node.key.equals(key)) {
                return node;
            }
            if (node.key.compareTo(key) > 0) {
                return containsKey(node.left, key);
            } else {
                return containsKey(node.right, key);
            }
        }

        /* Insert a node recursively. Return the new root node that you specified.*/
        public static <K extends Comparable,V> BSTNode<K,V> insert(BSTNode<K,V> node, K key, V value) {
            //requires that key be of class K and value be of class V
            if (node == null) {
                return new BSTNode<>(key, value);
            }
            if(key.equals(node.key)) {
                node.value = value;
                return node;
            }
            if(key.compareTo(node.key) < 0) {
                node.left = insert(node.left, key, value);
            }else{
                node.right = insert(node.right, key, value);
            }
            return node;
        }

        /* Print all keys and values recursively */
        public static <K extends Comparable,V> void printRecursive(BSTNode<K,V> node) {
            if (node == null) {
                return;
            }
            printRecursive(node.left);
            System.out.print("["+node.key + "]"+node.value+"\t");
            printRecursive(node.right);
        }
    }
    private BSTNode<K,V> root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }
    public BSTMap(K key, V value){
        root = new BSTNode<>(key,value);
        size = 1;
    }

    /**
     * Removes all mappings from this map.
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * search if the map contains a special key.
     *
     * @param key the key to be searched
     * @return true if this map contains the key,otherwise false
     */
    @Override
    public boolean containsKey(Object key) {
        BSTNode<K,V> node = BSTNode.containsKey(root,key);
        return node != null;
    }

    /**
     * Get the value according to the given key.
     *
     * @param key the key that pairs the value.
     * @return null if the map doesn't contain the key,otherwise the corresponding value.
     */
    @Override
    public Object get(Object key) {
        BSTNode<K,V> node = BSTNode.containsKey(root,key);
        return node == null ? null : node.value;
    }

    /**
     * @return the size of the map.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Insert the pair of key and value to the map. If the key exists already,the corresponding value will be overwritten.
     *
     * @param key the key you want to insert.
     * @param value the value that corresponding the value.
     */
    @Override
    public void put(Object key, Object value) {
        // TODO: Type Checking?
        // Note: "root = " is important,otherwise you would not insert anything if the map is empty.
        root = BSTNode.insert(root,(K)key,(V)value);
        size++;
    }

    public void printInOrder(){
        BSTNode.printRecursive(root);
    }

    /**
     * @return
     */
    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
        //return Set.of();
    }

    /**
     * @param key
     * @return
     */
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
        //return null;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException();
        //return null;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
        //return null;
    }

}
