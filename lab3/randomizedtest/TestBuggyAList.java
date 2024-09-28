package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing listPrev = new AListNoResizing();
        BuggyAList listCurrent = new BuggyAList();
        listPrev.addLast(4);
        listCurrent.addLast(4);
        listPrev.addLast(5);
        listCurrent.addLast(5);
        listPrev.addLast(6);
        listCurrent.addLast(6);
        assertEquals(listCurrent.size(), listPrev.size());

        for (int i = 0; i < 2; i++) {
            assertEquals(listPrev.removeLast(), listCurrent.removeLast());
        }
    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> LToBeTest = new BuggyAList<>();
        int N = 100000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                LToBeTest.addLast(randVal);
                assertEquals(L.getLast(), LToBeTest.getLast());
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), LToBeTest.size());
            } else if (operationNumber == 2) {
                // getLast
                if(L.size() == 0 || LToBeTest.size() == 0){
                    continue;
                }
                assertEquals(L.getLast(), LToBeTest.getLast());
            } else if (operationNumber == 3) {
                // removeLast
                if(L.size() == 0 || LToBeTest.size() == 0){
                    continue;
                }
                assertEquals(L.removeLast(), LToBeTest.removeLast());
            }
        }
    }
}
