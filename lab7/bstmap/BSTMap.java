package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private static class BSTNode<K extends Comparable<K>, V> {
        // left for the less
        BSTNode<K, V> left, right;
        K key;
        V value;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = right = null;
        }

        /* Find node by key recursively.*/
        public static <K extends Comparable<K>, V> BSTNode<K, V> containsKey(BSTNode<K, V> node, K key) {
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
        public static <K extends Comparable<K>, V> BSTNode<K, V> insert(BSTNode<K, V> node, K key, V value) {
            //requires that key be of class K and value be of class V
            if (node == null) {
                return new BSTNode<>(key, value);
            }
            if (key.equals(node.key)) {
                node.value = value;
                return node;
            }
            if (key.compareTo(node.key) < 0) {
                node.left = insert(node.left, key, value);
            } else {
                node.right = insert(node.right, key, value);
            }
            return node;
        }

        /* Print all keys and values recursively. */
        public static <K extends Comparable<K>, V> void printRecursive(BSTNode<K, V> node) {
            if (node == null) {
                return;
            }
            printRecursive(node.left);
            System.out.print("[" + node.key + "]" + node.value + "\t");
            printRecursive(node.right);
        }

        /* Generate the set of keys contained under the node recursively.Must pass an empty set. */
        public static <K extends Comparable<K>, V> void generateKeySet(BSTNode<K, V> node, Set<K> set) {
            if (node == null) {
                return;
            }
            generateKeySet(node.left, set);
            set.add(node.key);
            generateKeySet(node.right, set);
        }

        /* Get the parent of the node.Return null if corresponding node doesn't exist or has no parent */
        public static <K extends Comparable<K>, V> BSTNode<K, V> getParent(BSTNode<K, V> node, K key) {
            // If node has no parent,the function will return null by iterating towards leaf.
            if (node == null) {
                return null;
            }

            BSTNode<K, V> left = node.left;
            BSTNode<K, V> right = node.right;
            if ((left != null && key.equals(left.key)) || (right != null && key.equals(right.key))) {
                return node;
            }

            if (node.key.compareTo(key) > 0) {
                return getParent(node.left, key);
            } else {
                return getParent(node.right, key);
            }
        }

        /* Find the node that has the longest distance to the given node at the (pos) side, where true represent left. */
        public static <K extends Comparable<K>, V> BSTNode<K, V> findNearestRecursive(BSTNode<K, V> node, boolean pos) {
            if (pos ? node.left == null : node.right == null) {
                return node;
            }
            return findNearestRecursive(pos ? node.left : node.right, pos);
        }

        /* Find the node whose key is nearest to that of the given node. Must ensure that node has children.*/
        public static <K extends Comparable<K>, V> BSTNode<K, V> findNearest(BSTNode<K, V> node) {
            return node.left == null ? findNearestRecursive(node.right, true) : findNearestRecursive(node.left, false);
        }
    }

    private BSTNode<K, V> root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
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
    public boolean containsKey(K key) {
        BSTNode<K, V> node = BSTNode.containsKey(root, key);
        return node != null;
    }

    /**
     * Get the value according to the given key.
     *
     * @param key the key that pairs the value.
     * @return null if the map doesn't contain the key,otherwise the corresponding value.
     */
    @Override
    public V get(K key) {
        BSTNode<K, V> node = BSTNode.containsKey(root, key);
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
     * @param key   the key you want to insert.
     * @param value the value that corresponding the value.
     */
    @Override
    public void put(K key, V value) {
        // Note: "root = " is important,otherwise you would not insert anything if the map is empty.
        root = BSTNode.insert(root, key, value);
        size++;
    }

    /**
     * Print all keys and values in order.
     */
    public void printInOrder() {
        BSTNode.printRecursive(root);
    }

    /**
     * @return A set containing all keys stored in the map.
     */
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        BSTNode.generateKeySet(root, set);
        return set;
    }

    /**
     * Remove the node corresponding to the given key.If not contained,return null,otherwise return its value;
     *
     * @param key the key to the node being removed.
     * @return the value of the node.Return null if such node doesn't exist.
     */
    @Override
    public V remove(K key) {
        BSTNode<K, V> node = BSTNode.containsKey(root, key);
        if (node == null) {
            return null;
        }
        V returnValue = node.value;
        size--;
        if (!(node.left != null && node.right != null)) {
            // case that the node has 0 or 1 children
            BSTNode<K, V> parent = BSTNode.getParent(root, key);
            BSTNode<K, V> child = (node.left == null) ? node.right : node.left;
            if (parent == null) {
                // TODO: how to fix?
                // Special case:delete root when it has only 1 child.
                root = child;
                return node.value;
            }
            if (parent.left == node) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        } else {
            // case that the node has 2 children
            BSTNode<K, V> nearest = BSTNode.findNearest(node);
            K _key = nearest.key;
            V _value = nearest.value;
            remove(nearest.key);
            node.key = _key;
            node.value = _value;
        }
        return returnValue;
    }

    /**
     * Remove the node corresponding to the given key if its value matches,otherwise do nothing.
     *
     * @param key   the given key to the node.
     * @param value the given value.
     * @return value of the removed node.Null if such node doesn't exist or the value doesn't match.
     */
    @Override
    public V remove(K key, V value) {
        BSTNode<K, V> node = BSTNode.containsKey(root, key);
        if (node == null || !node.key.equals(key)) {
            return null;
        }
        return remove(key);
    }

    /**
     * Returns an iterator over elements of type {@code K}.
     * Each of the methods has the time O(logN);
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        // TODO: Broken
        return new Iterator<>() {
            private K current = BSTNode.findNearestRecursive(root, true).key;

            @Override
            public boolean hasNext() {
                return current != BSTNode.findNearestRecursive(root, false).key;
            }

            @Override
            public K next() {
                setNext(root, current);
                return current;
            }

            /**
             * Set current to the key that is greater than previous current and less than other keys.
             * Return the corresponding node of the given key(In order to maintain recursive).
             */
            private BSTNode<K, V> setNext(BSTNode<K, V> node, K key) {
                // Special case
                if (key == root.key) {
                    current = BSTNode.findNearestRecursive(node.right, true).key;
                    return node;
                }

                if (node.key.equals(key)) {
                    return node;
                }
                if (node.key.compareTo(key) > 0) {
                    current = node.key;
                    return setNext(node.left, key);
                } else {
                    return setNext(node.right, key);
                }
            }
        };
    }

}
