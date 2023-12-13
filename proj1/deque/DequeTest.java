package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class DequeTest {

    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size1 = B.size();
                assertEquals(size, size1);
                //System.out.println("size: " + size);
            } else if (operationNumber == 2) {
                // get
                if (L.isEmpty()) {
                    continue;
                }
                int randIndex = StdRandom.uniform(0, L.size());
                //System.out.println("get: " + randIndex);
                //System.out.println("in dll: " + L.get(randIndex));
                //System.out.println("in ad: " + B.get(randIndex));
                assertEquals(L.get(randIndex), B.get(randIndex));
            } else if (operationNumber == 3) {
                //removeLast
                if (L.isEmpty()) {
                    continue;
                }
                int removeVal = L.removeLast();
                int removeVal1 = B.removeLast();
                //System.out.println("removeLast: " + removeVal);
                //System.out.println("in dll: " + removeVal);
                //System.out.println("in ad: " + removeVal_1);
                assertEquals(removeVal, removeVal1);
            } else if (operationNumber == 4) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                B.addFirst(randVal);
                //System.out.println("addFirst(" + randVal + ")");
            } else if (operationNumber == 5) {
                //removeFirst
                if (L.isEmpty()) {
                    continue;
                }
                int removeVal = L.removeFirst();
                int removeVal1 = B.removeFirst();
                //System.out.println("removeFirst: " + removeVal);
                assertEquals(removeVal, removeVal1);
            } else if (operationNumber == 6) {
                if (L.isEmpty()) {
                    continue;
                }
                int randIndex = StdRandom.uniform(0, L.size());
                //System.out.println("getRecursive: " + randIndex);
                //System.out.println("in dll: " + L.get(randIndex));
                //System.out.println("in ad: " + B.get(randIndex));
                assertEquals(L.getRecursive(randIndex), B.get(randIndex));
            }
        }
        L.printDeque();
        B.printDeque();
    }

    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addFirst(10);
        L.addFirst(15);
        L.addFirst(20);
        for (int item : L) {
            System.out.println(item);
        }
    }
}
