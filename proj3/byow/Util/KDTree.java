package byow.Util;

import java.util.Map.Entry;

public interface KDTree<K> {
    double[] put(K key, double[] value);

    Entry<K, double[]> nearest(K key);

    Entry<K, double[]> nearest(double[] value);
}
