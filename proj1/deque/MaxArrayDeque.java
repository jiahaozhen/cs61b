package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int index = 1; index < size(); index++) {
            if (comparator.compare(get(index), maxItem) > 0) {
                maxItem = get(index);
            }
        }
        return maxItem;
    }
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int index = 1; index < size(); index++) {
            if (c.compare(get(index), maxItem) > 0) {
                maxItem = get(index);
            }
        }
        return maxItem;
    }
}
