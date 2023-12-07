package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAndThreeRemove() {
        AListNoResizing<Integer> ALNR = new AListNoResizing<>();
        BuggyAList<Integer> BAL = new BuggyAList<>();
        ALNR.addLast(4); BAL.addLast(4);
        ALNR.addLast(5); BAL.addLast(5);
        ALNR.addLast(6); BAL.addLast(6);
        assertEquals(ALNR.removeLast(), BAL.removeLast());
        assertEquals(ALNR.removeLast(), BAL.removeLast());
        assertEquals(ALNR.removeLast(), BAL.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size_1 = B.size();
                assertEquals(size, size_1);
                //System.out.println("size: " + size);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() == 0) {continue;}
               // System.out.println("getLast: " + L.getLast());
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 3) {
                //removeLast
                if (L.size() == 0) {continue;}
                int removeVal = L.removeLast();
                int removeVal_1 = B.removeLast();
                //System.out.println("removeLast: " + removeVal);
                assertEquals(removeVal, removeVal_1);
            }
        }
    }
}
