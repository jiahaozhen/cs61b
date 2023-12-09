package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T max_item = get(0);
        for (int index = 1; index < size(); index++) {
            if (comparator.compare(get(index), max_item) > 0) {
                max_item = get(index);
            }
        }
        return max_item;
    }
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T max_item = get(0);
        for (int index = 1; index < size(); index++) {
            if (c.compare(get(index), max_item) > 0) {
                max_item = get(index);
            }
        }
        return max_item;
    }
}
