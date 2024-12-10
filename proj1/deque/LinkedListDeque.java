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
        Node<T> node = sentinel.next;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node == null ? null : node.item;
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
