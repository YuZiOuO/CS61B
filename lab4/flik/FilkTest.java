package flik;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilkTest {
    static int MAX_TRIES = 1000000;

    @Test
    public void randomTest() {
        for (int i = 0; i < MAX_TRIES; i++) {
            int a = StdRandom.uniform(0, MAX_TRIES);
            int b = StdRandom.uniform(0, MAX_TRIES);
            String s = a + " " + b;
            assertEquals(s, a - b == 0 ? true : false, Flik.isSameNumber(a, b));
        }
    }
}
