/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Immutable data type that represents a WordNet.
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {

    // stores synsets for a given noun id
    private final LinearProbingHashST<Integer, String> synsetST;
    // stores nouns and a set (Bag) of their related synsets
    private final LinearProbingHashST<String, Bag<Integer>> relatedNounsST;
    // number of synsets
    private int numSynsets = 0;
    // represents the hypernym DAG
    private final Digraph hypernymDAG;
    // ShortestCommonAncestor object used in sca() and distance()
    private final ShortestCommonAncestor sca;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        validate(synsets);
        validate(hypernyms);
        synsetST = new LinearProbingHashST<Integer, String>();
        relatedNounsST = new LinearProbingHashST<String, Bag<Integer>>();
        buildSynsetST(synsets);
        hypernymDAG = new Digraph(numSynsets);
        buildHypernymDAG(hypernyms);
        sca = new ShortestCommonAncestor(hypernymDAG);
    }

    // all WordNet nouns
    public Iterable<String> nouns() {
        return relatedNounsST.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        validate(word);
        return relatedNounsST.contains(word);
    }

    // a synset (second field of synsets.txt) that is a shortest common ancestor
    // of noun1 and noun2
    public String sca(String noun1, String noun2) {
        validateNoun(noun1);
        validateNoun(noun2);
        return synsetST.get(sca.ancestorSubset(relatedNounsST.get(noun1),
                                               relatedNounsST.get(noun2)));
    }

    // distance between noun1 and noun2
    public int distance(String noun1, String noun2) {
        validateNoun(noun1);
        validateNoun(noun2);
        return sca.lengthSubset(relatedNounsST.get(noun1),
                                relatedNounsST.get(noun2));
    }

    // build the synset data structure
    private void buildSynsetST(String synsets) {
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] components = in.readLine().split(",");
            int id = Integer.parseInt(components[0]);
            synsetST.put(id, components[1]); // put noun's synset in synsetST
            String[] nouns = components[1].split(" "); // parse noun's synonyms

            // add noun's id to related synsets in relatedNounsST
            for (String noun : nouns) {
                Bag<Integer> ids = relatedNounsST.get(noun);
                if (ids == null) {
                    Bag<Integer> bag = new Bag<Integer>();
                    bag.add(id);
                    relatedNounsST.put(noun, bag);
                }
                else ids.add(id);
            }

            numSynsets++;
        }
    }

    // build the hypernyms DAG
    private void buildHypernymDAG(String hypernyms) {
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] components = in.readLine().split(",");
            int v = Integer.parseInt(components[0]);

            for (int i = 1; i < components.length; i++)
                hypernymDAG.addEdge(v, Integer.parseInt(components[i]));
        }
    }

    // check if word argument is non-null
    private void validate(String s) {
        if (s == null) throw new IllegalArgumentException("null String");
    }

    // check if word inputs are WordNet nouns
    private void validateNoun(String s) {
        validate(s);
        if (!isNoun(s))
            throw new IllegalArgumentException("not a WordNet noun");
    }

    // unit testing of all methods
    public static void main(String[] args) {
        WordNet net = new WordNet("synsets6.txt", "hypernyms6TwoAncestors.txt");
        StdOut.println(net.isNoun("a")); // true
        for (String noun : net.nouns())
            StdOut.print(noun + " "); // a b c d e f g
        StdOut.println();
        StdOut.println(net.distance("b", "e")); // 3
        StdOut.println(net.sca("b", "e")); // a
        StdOut.println(net.sca("c", "f")); // f
    }
}
