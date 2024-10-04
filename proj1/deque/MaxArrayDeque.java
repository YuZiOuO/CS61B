package deque;

import java.util.Comparator;
import java.util.Objects;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> defaultComparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        defaultComparator = c;
    }

    public T max(){
        return max(defaultComparator);
    }

    public T max(Comparator<T> c){
        if(size() == 0) return null;
        T max = get(0);
        for(int i=0; i<size(); i++){
            if(c.compare(max, get(i)) < 0) max = get(i);
        }
        return max;
    }
}
