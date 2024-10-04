package deque;

import java.util.Comparator;
import java.util.Objects;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> defaultComparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        defaultComparator = c;
    }

    public T max(){
        return max(defaultComparator);
    }

    public T max(Comparator<T> c){
        if(size == 0) return null;
        T max = get(0);
        for(int i=0; i<size; i++){
            if(c.compare(max, get(i)) < 0) max = get(i);
        }
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaxArrayDeque)) return false;
        if (!super.equals(o)) return false;
        MaxArrayDeque<?> that = (MaxArrayDeque<?>) o;
        return Objects.equals(defaultComparator, that.defaultComparator);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(defaultComparator);
    }
}
