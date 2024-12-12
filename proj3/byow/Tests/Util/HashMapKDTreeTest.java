package byow.Tests.Util;

import byow.Util.HashMapKDTree;
import byow.Util.KDTree;
import org.junit.Test;
import static org.junit.Assert.*;

public class HashMapKDTreeTest {
    @Test
    public void testBasic() {
        KDTree<String> t = new HashMapKDTree<>(2);
        t.put("A",new double[]{2, 3});
        t.put("B",new double[]{4,2});
        t.put("C",new double[]{4,5});
        t.put("D",new double[]{3,3});
        t.put("E",new double[]{1,5});
        t.put("F",new double[]{4,4});

        assertArrayEquals(t.nearest("A").getValue(),new double[]{3,3},Double.MIN_VALUE);
        assertArrayEquals(t.nearest("C").getValue(),new double[]{4,4},Double.MIN_VALUE);
        assertArrayEquals(t.nearest(new double[]{0,7}).getValue(),new double[]{1,5},Double.MIN_VALUE);
    }
}
