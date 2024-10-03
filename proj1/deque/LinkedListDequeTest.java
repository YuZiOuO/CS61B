package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    static int MAX_TRIES = 2500;

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
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
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
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
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {
        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        assertNull("Should return null when removeFirst is called on an empty Deque,", lld1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,", lld1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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

    @Test
    public void randomTest(){
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        int size = 0;
        for(int i = 0; i< MAX_TRIES; i++){
            int operationType = StdRandom.uniform(0,4);
            if (operationType==0){
                /* addFirst */
                int item = StdRandom.uniform(0,100);
                lld.addFirst(item);
                size++;
                assertEquals((int) lld.get(1), item);
                assertFalse(lld.isEmpty());
                assertEquals(lld.size(), size);
            }else if (operationType==1){
                /* addLast */
                int item = StdRandom.uniform(0,100);
                lld.addLast(item);
                size++;
                assertEquals("operationType==0 testing get()",(int) lld.get(lld.size()), item);
                assertFalse("operationType==0 testing isEmpty()",lld.isEmpty());
                assertEquals("operationType==0 testing size()",lld.size(), size);
            }else if (operationType==1){
                /* removeFirst */
                if(lld.isEmpty()){
                    assertNull("operationType==1 testing isEmpty()",lld.removeFirst());
                }else{
                    size--;
                    assertEquals("operationType==1 testing get()",lld.get(1), lld.removeFirst());
                }
            }else if(operationType==2){
                /* removeLast */
                if(lld.isEmpty()){
                    assertNull("operationType==2 testing isEmpty()",lld.removeLast());
                }else{
                    size--;
                    assertEquals("operationType==2 testing get()",lld.get(lld.size()), lld.removeLast());
                }
            }else{
                assertEquals("operationType==3 testing size()",lld.size(),size);
            }
        }
    }

    @Test
    public void randomGet(){
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        for(int i = 1; i<= MAX_TRIES; i++){
            lld.addLast(i);
            for(int j=1;j<=i;j++){
                assertEquals((int)lld.get(j),j);
            }
            for(int j=1;j<=i;j++){
                assertEquals((int)lld.get(j),(int)lld.get(j-i-1));
            }
        }
    }
}
