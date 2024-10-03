package deque;

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
        Node<T> newNode = new Node<>(item,sentinel.prev,sentinel);
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
        int index = 0;
        while(!travelled){
            current = current.next;
            index++;
            if(current==sentinel){
                travelled=true;
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

    /* return ith item */
    @Override
    public T get(int index) {
        return _get(index>=index/2+1 ? index-size-1 : index);
    }

    /* return ith item no matter whether the sign of i is positive */
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
}