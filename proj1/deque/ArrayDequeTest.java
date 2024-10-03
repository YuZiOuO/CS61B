package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/** Performs some basic array list tests. */
public class ArrayDequeTest {
    static int MAX_TRIES = 4;

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized ADeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    };

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        assertNull("Should return null when removeFirst is called on an empty Deque,", lld1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,", lld1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigADequeTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    /* build a string in the format of ArrayDeque.printDeque() */
    private String printIntArrayList(ArrayList<Integer> alist){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < alist.size(); i++){
            sb.append("["+i+"] "+alist.get(i)+"  ");
        }
        return sb.toString();
    }
    /* build a string in the format of ArrayDeque.printDeque() */
    private String printIntArrayDeque(ArrayDeque<Integer> ad){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ad.size(); i++){
            sb.append("["+i+"] "+ad.get(i)+"  ");
        }
        return sb.toString();
    }

    @Test
    public void randomAddRemoveLastCompareTest() {
        ArrayList<Integer> alist = new ArrayList<>();
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for(int i = 0; i < MAX_TRIES; i++) {
            int item = StdRandom.uniform(0,1000);
            alist.add(item);
            ad.addLast(item);
            assertEquals(printIntArrayList(alist), printIntArrayDeque(ad));
        }
        for(int i = 0; i < MAX_TRIES; i++) {
            alist.remove(alist.size()-1);
            ad.removeLast();
            assertEquals(printIntArrayList(alist), printIntArrayDeque(ad));
        }
    }

    @Test
    public void randomAddRemoveFirstCompareTest() {
        ArrayList<Integer> alist = new ArrayList<>();
        int[] arr = new int[MAX_TRIES];
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for(int i = 0; i < MAX_TRIES; i++) {
            int item = StdRandom.uniform(0,1000);
            alist.add(0,item);
            ad.addFirst(item);
            assertEquals(printIntArrayList(alist), printIntArrayDeque(ad));
        }
        for(int i = 0; i < MAX_TRIES; i++) {
            alist.remove(0);
            ad.removeFirst();
            assertEquals(printIntArrayList(alist), printIntArrayDeque(ad));
        }
    }
}
