package deque;

import java.util.Objects;

import static java.lang.Math.abs;

public class LinkedListDeque<T> implements Deque<T>{
    static class Node<T>{
        T item;
        Node<T> prev;
        Node<T> next;
        public Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    int size;
    private final Node<T> sentinel;

    public LinkedListDeque(){
        size=0;
        sentinel = new Node<>(null,null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public void addLast(T item){
        Node<T> newNode = new Node<>(item,sentinel.prev,sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    @Override
    public void addFirst(T item){
        Node<T> newNode = new Node<>(item,sentinel,sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;
    }

    @Override
    public boolean isEmpty(){
        return size==0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node<T> current = sentinel;
        boolean travelled = false;
        int index = -1;
        while(true){
            current = current.next;
            index++;
            if(current==sentinel){
                break;
            }
            System.out.print("["+index+"]"+current.item+"  ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        Node<T> node = sentinel.next;
        node.next.prev = sentinel;
        sentinel.next = node.next;
        if(size>0){
            size--;
        }
        return node.item;
    }

    @Override
    public T removeLast() {
        Node<T> node = sentinel.prev;
        node.prev.next = sentinel;
        sentinel.prev = node.prev;
        if(size>0){
            size--;
        }
        return node.item;
    }

    /* return ith item where i starts from 0 to size()-1 */
    @Override
    public T get(int index) {
        index = index+1;//staring from 1;
        return _get(index>=index/2+1 ? index-size-1 : index);
    }

    /* return ith item **starting from 1** no matter whether the sign of i is positive (i of sentinel is 0)*/
    private T _get(int index){
        if(size == 0||index == 0||abs(index) > size){
            return null;
        }
        Node<T> current = sentinel;
        int iter = 0;
        while(iter != index){
            if(index > 0){
                iter++;
                current = current.next;
            }else{
                iter--;
                current = current.prev;
            }
        }
        return current.item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedListDeque)) return false;
        LinkedListDeque<?> that = (LinkedListDeque<?>) o;
        if (size != that.size) return false;

        /* Traverse all the list */
        boolean allEqual = true;
        for (int i=0;i<size;i++){
            allEqual = allEqual && get(i).equals(that.get(i));
        }
        return allEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size,sentinel.next.item,sentinel.prev.item);
    }
}