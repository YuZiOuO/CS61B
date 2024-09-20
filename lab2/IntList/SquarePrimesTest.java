package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesAllPrimes1() {
        IntList lst = IntList.of(2,3,5,7,11,13);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 9 -> 25 -> 49 -> 121 -> 169",lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesAllPrimes2() {
        IntList lst = IntList.of(101,103,107,109,113);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("10201 -> 10609 -> 11449 -> 11881 -> 12769",lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesNoPrimes1() {
        IntList lst = IntList.of(0,1,4,6,8,9,10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("0 -> 1 -> 4 -> 6 -> 8 -> 9 -> 10",lst.toString());
        assertFalse(changed);
    }

    @Test
    public void testSquarePrimesCross() {
        IntList lst = IntList.of(1,2,3,4,5,6,7,8,9,0);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 4 -> 9 -> 4 -> 25 -> 6 -> 49 -> 8 -> 9 -> 0",lst.toString());
        assertTrue(changed);
    }
}
