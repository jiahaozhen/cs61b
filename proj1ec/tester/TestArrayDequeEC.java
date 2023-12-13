package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> B = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> L = new ArrayDequeSolution<>();
        String message = "";

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                message = message + "addLast(" + randVal + ")\n";
                L.addLast(randVal);
                B.addLast(randVal);
                assertEquals(message, L.get(L.size()-1), B.get(B.size()-1));
            } else if (operationNumber == 1) {
                //removeLast
                if (L.isEmpty()) {
                    continue;
                }
                message = message + "removeLast()\n";
                assertEquals(message, L.removeLast(), B.removeLast());
            } else if (operationNumber == 2) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                message = message + "addFirst(" + randVal + ")\n";
                L.addFirst(randVal);
                B.addFirst(randVal);
                assertEquals(message, L.get(0), B.get(0));
            } else if (operationNumber == 3) {
                //removeFirst
                if (L.isEmpty()) {
                    continue;
                }
                message = message + "removeFirst()\n";
                assertEquals(message, L.removeFirst(), B.removeFirst());
            } else if (operationNumber == 4) {
                //size
                message = message + "size()\n";
                assertEquals(message, L.size(), B.size());
            } else if (operationNumber == 5) {
                //isEmpty
                message = message + "isEmpty()\n";
                assertEquals(message, L.isEmpty(), B.isEmpty());
            } else if (operationNumber == 6) {
                //get
                if (L.isEmpty()) {
                    continue;
                }
                int randIndex = StdRandom.uniform(0, L.size());
                message = message + "get(" + randIndex + ")\n";
                assertEquals(message, L.get(randIndex), B.get(randIndex));
            }
        }
    }
}
