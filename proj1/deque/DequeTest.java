package deque;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DequeTest {
    @Test
    public void dequeEqualsTest() {
        Deque<Integer> d1 = new ArrayDeque<>();
        Deque<Integer> d2 = new LinkedListDeque<>();

        d1.addFirst(1);
        d1.addFirst(2);
        d1.addFirst(3);

        d2.addFirst(1);
        d2.addFirst(2);
        d2.addFirst(3);

        assertEquals(d1, d2);
    }
}
