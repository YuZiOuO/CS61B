package byow.Util;

import java.util.HashMap;
import java.util.Map;

public class HashMapKDTree<K> implements KDTree<K> {
    private final HashMap<K, double[]> elem;
    private final int d;

    public HashMapKDTree(int dimensional) {
        elem = new HashMap<>();
        d = dimensional;
    }

    @Override
    public double[] put(K key, double[] value) {
        return elem.put(key, value);
    }

    @Override
    public Map.Entry<K, double[]> nearest(K key) {
        double[] value = elem.get(key);
        return value == null ? null : nearest(value, true);
    }

    @Override
    public Map.Entry<K, double[]> nearest(double[] value) {
        return nearest(value, false);
    }

    private Map.Entry<K, double[]> nearest(double[] value, boolean ignoreSelf) {
        double minDist = Double.MAX_VALUE;
        Map.Entry<K, double[]> nearest = null;
        for (Map.Entry<K, double[]> e : elem.entrySet()) {
            double currentDist = compareTo(value, e.getValue());
            boolean self = currentDist == 0;
            if (currentDist < minDist && !(self && ignoreSelf)) {
                nearest = e;
                minDist = currentDist;
            }
        }
        return nearest;
    }

    private double compareTo(double[] value1, double[] value2) {
        double diff = 0;
        for (int i = 0; i < d; i++) {
            diff += Math.pow(value1[i] - value2[i], 2);
        }
        return Math.sqrt(diff);
    }
}
