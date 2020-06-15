/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Implements the Autocomplete data type, which uses Term and
 *  BinarySearchDeluxe to provide autocomplete functionality.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class Autocomplete {

    private final Term[] sortedTerms; // stores the sorted terms array
    private int lo; // stores leftmost value of a prefix match
    private int hi; // stores rightmost value of a prefix match

    // Initializes the data structure from the given array of terms.
    public Autocomplete(Term[] terms) {
        if (terms == null)
            throw new IllegalArgumentException("terms is null!");

        // copies the Term objects from terms to ensure immutability
        sortedTerms = new Term[terms.length];
        for (int i = 0; i < terms.length; i++) {
            if (terms[i] == null)
                throw new IllegalArgumentException("a term is null!");

            sortedTerms[i] = terms[i];
        }

        // sort the copied Term array lexographically
        Arrays.sort(sortedTerms);
    }

    // Returns all terms that start with the given prefix, in descending order
    // of weight.
    public Term[] allMatches(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("prefix is null!");

        /**
         * Find the number of Terms that begin with a given prefix.
         * numberOfMatches() stores the leftmost and rightmost indices as
         * instance variables lo and hi, respectively, so that allMatches()
         * can retrieve the two values.
         */
        int n = numberOfMatches(prefix);

        if (n == 0) return new Term[0]; // no matches
        Term[] prefixMatches = new Term[n]; // positive number of matches

        // copy matches from sortedTerms for reverse weight sorting
        for (int i = lo; i <= hi; i++)
            prefixMatches[i - lo] = sortedTerms[i];

        // sort the matching prefix array by weight, reversed
        Comparator<Term> cmp = Term.byReverseWeightOrder();
        Arrays.sort(prefixMatches, cmp);
        return prefixMatches;
    }

    // Returns the number of terms that start with the given prefix.
    public int numberOfMatches(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("prefix is null!");

        int r = prefix.length();
        Term temp = new Term(prefix, 1); // dummy Term containing prefix

        Comparator<Term> cmp = Term.byPrefixOrder(r);

        // find the leftmost matching index in the Term array
        lo = BinarySearchDeluxe.firstIndexOf(sortedTerms, temp, cmp);

        // nothing found --> return 0 matches
        if (lo == -1) return 0;

        // find the rightmost matching index in the Term array
        hi = BinarySearchDeluxe.lastIndexOf(sortedTerms, temp, cmp);

        // return the number of matches
        return hi - lo + 1;
    }

    // unit testing (required)
    public static void main(String[] args) {
        // read in the terms from a file
        String filename = args[0];
        In in = new In(filename);
        int n = in.readInt();
        Term[] terms = new Term[n];
        for (int i = 0; i < n; i++) {
            long weight = in.readLong();           // read the next weight
            in.readChar();                         // scan past the tab
            String query = in.readLine();          // read the next query
            terms[i] = new Term(query, weight);    // construct the term
        }

        // read in queries from standard input and print the top k matching terms
        int k = Integer.parseInt(args[1]);
        Autocomplete autocomplete = new Autocomplete(terms);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            Term[] results = autocomplete.allMatches(prefix);
            StdOut.printf("%d matches\n", autocomplete.numberOfMatches(prefix));
            for (int i = 0; i < Math.min(k, results.length); i++)
                StdOut.println(results[i]);
        }
    }

}
