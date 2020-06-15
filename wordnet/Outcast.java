/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Immutable data type that determines an outcast (least related
 *  noun) in a list of WordNet nouns.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet net; // the WordNet

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        net = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int greatestDistance = Integer.MIN_VALUE;
        String outcast = nouns[0];

        // calculate the sum of distances from each noun to all other nouns
        for (String noun : nouns) {
            int sum = 0;

            for (String otherNoun : nouns)
                sum += net.distance(noun, otherNoun);

            if (sum > greatestDistance) {
                greatestDistance = sum;
                outcast = noun;
            }
        }

        return outcast;
    }

    // test client (see below)
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
