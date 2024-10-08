package deque;

import java.util.Iterator;

/**
 * Note:use Math.floorMod(int,int) to represent modulus operation instead of %
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    public ArrayDeque() {
        arr = (T[]) new Object[8];
        size = 0;
        arrLen = 8;
        nextFirst = arrLen / 2 - 1;
        nextLast = arrLen / 2;
    }

    @Override
    public void addFirst(T item) {
        resize();
        arr[nextFirst] = item;
        nextFirst = Math.floorMod(nextFirst - 1, arrLen);
        size++;
    }

    @Override
    public void addLast(T item) {
        resize();
        arr[nextLast] = item;
        nextLast = Math.floorMod(nextLast + 1, arrLen);
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print("[" + i + "]" + arr[(nextFirst + 1 + i) % arrLen] + "  ");
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        resize();
        nextFirst = Math.floorMod(nextFirst + 1, arrLen);
        size--;
        return arr[nextFirst];
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        resize();
        nextLast = Math.floorMod(nextLast - 1, arrLen);
        size--;
        return arr[nextLast];
    }

    /* staring from 1 */
    @Override
    public T get(int index) {
        if (index < 0 || index > size() - 1) {
            return null;
        }
        return arr[(nextFirst + 1 + index) % arrLen];
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
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                return currentIndex < size ? get(currentIndex++) : null;
            }
        };
    }

    private void resize() {
        if (nextFirst == nextLast) {
            /* increase capacity to 2 times of the original */
            T[] arrNew = (T[]) new Object[arrLen * 2];
            int lenNew = arrLen * 2;
            int lenOfSencondHalf = arrLen - nextFirst;
            int nextFirstNew = lenNew - lenOfSencondHalf;
            System.arraycopy(arr, nextFirst, arrNew, nextFirstNew, lenOfSencondHalf);
            System.arraycopy(arr, 0, arrNew, 0, nextLast);
            arr = arrNew;
            nextFirst = nextFirstNew;
            arrLen = lenNew;
        }
        if (arrLen > 8 && size <= arrLen / 4) {
            /* reduce capacity to one half of the original if size equal arrLen/4 */
            T[] arrNew = (T[]) new Object[arrLen / 2];
            if (nextFirst < nextLast) {
                System.arraycopy(arr, nextFirst, arrNew, 0, size + 1);
            } else {
                System.arraycopy(arr, nextFirst, arrNew, 0, arrLen - nextFirst);
                System.arraycopy(arr, 0, arrNew, arrLen - nextFirst, nextLast);
            }
            nextFirst = 0;
            nextLast = size + 1;
            arrLen = arrLen / 2;
            arr = arrNew;
        }
    }

    private T[] arr;
    private int size;
    private int arrLen; //should be 8*(4^n) where n is positive integer
    private int nextFirst;
    private int nextLast;

}
