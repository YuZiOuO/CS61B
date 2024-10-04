package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    static int MAX_TRIES = 30000;

    @Test
    public void SimpleIntergerTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntegerComparator());
        mad.addLast(3);
        mad.addLast(7);
        mad.addLast(2);
        mad.addLast(4);
        assertEquals((int) mad.max(), 7);
    }

    @Test
    public void RandomIntegerTest(){
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntegerComparator());
        int item = 0;
        for(int i = 0; i < MAX_TRIES; i++){
            int _item = StdRandom.uniform(0,3724);
            item = Math.max(_item, item);
            mad.addLast(item);
        }
        assertEquals(item,(int)mad.max());
    }

    @Test
    public void RandomDoubleTest(){
        MaxArrayDeque<Double> mad = new MaxArrayDeque<>(new DoubleComparator());
        double item = 0;
        for(int i = 0; i < MAX_TRIES; i++){
            double _item = StdRandom.uniform(0,61.0);
            item = Math.max(_item, item);
            mad.addLast(item);
        }
        assertEquals(item,(double)mad.max(),0.0);
    }
}

class IntegerComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }
}
class DoubleComparator implements Comparator<Double> {
    @Override
    public int compare(Double o1, Double o2) {
        return o1.compareTo(o2);
    }
}