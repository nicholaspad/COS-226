/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Client for RandomizedQueue that prints permutations of length
 *  k from a given file of Strings.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();

        // load all Strings into the randomized queue (rq)
        while (!StdIn.isEmpty())
            rq.enqueue(StdIn.readString());

        // print k unique random Strings from the queue
        for (int i = 0; i < k; i++)
            StdOut.println(rq.dequeue());
    }
}
