package deque;
import java.util.Objects;

/**
 * Note:use Math.floorMod(int,int) to represent modulus operation instead of %
 */
public class ArrayDeque<T> implements Deque<T> {
    T[] arr;
    int size;
    int arrLen;//should be 8*(4^n) where n is positive integer
    int nextFirst;
    int nextLast;

    public ArrayDeque() {
        arr = (T[]) new Object[8];
        size = 0;
        arrLen = 8;
        nextFirst = arrLen/2-1;
        nextLast = arrLen/2;
    }

    @Override
    public void addFirst(T item) {
        resize();
        arr[nextFirst] = item;
        nextFirst = Math.floorMod(nextFirst-1, arrLen);
        size++;
    }

    @Override
    public void addLast(T item) {
        resize();
        arr[nextLast] = item;
        nextLast = Math.floorMod(nextLast+1, arrLen);
        size++;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for(int i = 0;i<size;i++) {
            System.out.print("["+i+"]"+arr[(nextFirst+1+i)%arrLen] + "  ");
        }
    }

    @Override
    public T removeFirst() {
        if(size == 0) {
            return null;
        }
        resize();
        nextFirst = Math.floorMod(nextFirst+1, arrLen);
        size--;
        return arr[nextFirst];
    }

    @Override
    public T removeLast() {
        if(size == 0) {
            return null;
        }
        resize();
        nextLast = Math.floorMod(nextLast-1, arrLen);
        size--;
        return arr[nextLast];
    }

    /* staring from 1 */
    @Override
    public T get(int index) {
        if(index<0 || index>size()-1){
            return null;
        }
        return arr[(nextFirst+1+index)%arrLen];
    }

    private void resize(){
        if(nextFirst == nextLast) {
            /* increase capacity to 2 times of the original */
            T[] _arr = (T[])new Object[arrLen*2];
            System.arraycopy(arr, nextFirst, _arr, arrLen*2-(arrLen-nextFirst), arrLen-nextFirst);
            System.arraycopy(arr, 0, _arr, 0, nextLast);
            arr = _arr;
            nextFirst = arrLen*2-(arrLen-nextFirst);
            arrLen = arrLen*2;
        }
        if(arrLen > 8 && size <= arrLen/4) {
            /* reduce capacity to one half of the original if size equal arrLen/4 */
            T[] _arr = (T[])new Object[arrLen/2];
            if(nextFirst<nextLast){
                System.arraycopy(arr, nextFirst, _arr, 0, size+1);
            }else{
                System.arraycopy(arr, nextFirst, _arr, 0, arrLen-nextFirst);
                System.arraycopy(arr, 0, _arr, arrLen-nextFirst, nextLast);
            }
            nextFirst = 0;
            nextLast = size+1;
            arrLen = arrLen/2;
            arr = _arr;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayDeque)) return false;
        ArrayDeque<?> that = (ArrayDeque<?>) o;
        if (size != that.size) return false;

        /* Traverse all the list */
        boolean allEqual = true;
        for (int i=0;i<size;i++){
            allEqual = allEqual && get(i).equals(that.get(i));
        }
        return allEqual;
    }

    @Override
    public int hashCode(){
        return Objects.hash(size,get(0),get(size-1));
    }
}
