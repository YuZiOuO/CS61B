package deque;

import java.util.Iterator;

import static java.lang.Math.abs;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node<>(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    @Override
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node<T> current = sentinel;
        int index = -1;
        while (true) {
            current = current.next;
            index++;
            if (current == sentinel) {
                break;
            }
            System.out.print("[" + index + "]" + current.item + "  ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        Node<T> node = sentinel.next;
        node.next.prev = sentinel;
        sentinel.next = node.next;
        if (size > 0) {
            size--;
        }
        return node.item;
    }

    @Override
    public T removeLast() {
        Node<T> node = sentinel.prev;
        node.prev.next = sentinel;
        sentinel.prev = node.prev;
        if (size > 0) {
            size--;
        }
        return node.item;
    }

    /* return ith item where i starts from 0 to size()-1 */
    @Override
    public T get(int index) {
        index = index + 1; //staring from 1;
        return getByIndex(index >= index / 2 + 1 ? index - size - 1 : index);
    }

    public T getRecursive(int index) {
        if (size == 0 || index < 0 || index >= size) return null;
        return getRecursiveNode(index + 1, sentinel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> that = (Deque<?>) o;
        if (size() != that.size()) {
            return false;
        }

        /* Traverse all the list */
        boolean allEqual = true;
        for (int i = 0; i < size; i++) {
            allEqual = allEqual && get(i).equals(that.get(i));
        }
        return allEqual;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> currentNode = sentinel;
            int index = -1;

            @Override
            public boolean hasNext() {
                return index < size - 1;
            }

            @Override
            public T next() {
                currentNode = currentNode.next;
                index++;
                return index >= size ? null : currentNode.item;
            }
        };
    }

    /** return ith item STARTING FROM 1
     * no matter whether the sign of i is positive
     * (i of sentinel is 0)
     */
    private T getByIndex(int index) {
        if (size == 0 || index == 0 || abs(index) > size) {
            return null;
        }
        Node<T> current = sentinel;
        int iter = 0;
        while (iter != index) {
            if (index > 0) {
                iter++;
                current = current.next;
            } else {
                iter--;
                current = current.prev;
            }
        }
        return current.item;
    }

    private T getRecursiveNode(int index, Node<T> current) {
        if (index == 0) {
            return current.item;
        } else {
            return getRecursiveNode(index - 1, current.next);
        }
    }

    private static class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;

        Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private int size;
    private final Node<T> sentinel;
}
