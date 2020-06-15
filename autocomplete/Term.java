/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Implements the Term data type and related Comparator
 *  functionalities (ReverseWeightOrder and PrefixOrder).
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class Term implements Comparable<Term> {

    private final String query; // stores the query string
    private final long weight; // store the query weight

    // Initializes a term with the given query string and weight.
    public Term(String query, long weight) {
        if (query == null) throw new IllegalArgumentException("query is null!");
        if (weight < 0) throw new IllegalArgumentException("weight is negative!");

        this.query = query;
        this.weight = weight;
    }

    // Compares the two terms in descending order by weight.
    public static Comparator<Term> byReverseWeightOrder() {
        return new ReverseWeightOrder();
    }

    // Implements the ReverseWeightOrder Comparator
    private static class ReverseWeightOrder implements Comparator<Term> {
        /**
         * If the first weight is bigger than second weight, return a negative
         * number. If the second weight is bigger than first weight, return a
         * positive number. This results in reverse ordering by weight.
         */
        public int compare(Term t1, Term t2) {
            return (int) (t2.weight - t1.weight);
        }
    }

    // Compares the two terms in lexicographic order,
    // but using only the first r characters of each query.
    public static Comparator<Term> byPrefixOrder(int r) {
        if (r < 0) throw new IllegalArgumentException("r is negative!");

        return new PrefixOrder(r);
    }

    // Implements the PrefixOrder Comparator
    private static class PrefixOrder implements Comparator<Term> {
        private final int r; // first r characters

        // initializes a PrefixOrder with a given r value, where r is the length
        // of the prefix
        public PrefixOrder(int r) {
            this.r = r;
        }

        // provide functionality to sort Terms by a given prefix
        public int compare(Term t1, Term t2) {
            String s1 = t1.query;
            String s2 = t2.query;

            // if r is longer than the string length, set r to the string length
            int prefixLength1 = Math.min(r, s1.length());
            int prefixLength2 = Math.min(r, s2.length());

            // compare char-by-char, return 1/-1 when chars are different
            for (int i = 0; i < Math.min(prefixLength1, prefixLength2); i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                if (c1 > c2) return 1;
                if (c1 < c2) return -1;
            }

            /**
             * first string is longer than the second string, and second string
             * is shorter than the prefix length --> second str < first str
             */
            if (s1.length() > s2.length() && s2.length() < r) return 1;
            /**
             * first string is shorter than the second string, and first string
             * is shorter than the prefix length --> first str < second str
             */
            if (s1.length() < s2.length() && s1.length() < r) return -1;

            /**
             * all conditions above are not met --> strings must be the same
             * when comparing using the given prefix
             */
            return 0;
        }
    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(Term that) {
        return query.compareTo(that.query);
    }

    // Returns a string representation of this Term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString() {
        return weight + "\t" + query;
    }

    // unit testing (required)
    public static void main(String[] args) {
        Term t1 = new Term("dogcatcher", 1500);
        Term t2 = new Term("do", 3000);
        Term t3 = new Term("car", 4500);
        StdOut.println(t1);
        StdOut.println(t2);
        StdOut.println(t3);
        StdOut.println(t1.compareTo(t2)); // 8
        StdOut.println(t2.compareTo(t3)); // 1
        Term[] test = { t1, t2, t3 };
        Arrays.sort(test, Term.byReverseWeightOrder());
        for (Term t : test)
            StdOut.print(t.weight + " "); // 4500 3000 1500
        StdOut.println();
        Term[] test1 = { t1, t2, t3 };
        Arrays.sort(test1, Term.byPrefixOrder(1));
        for (Term t : test1)
            StdOut.print(t.query + " "); // car do dogcatcher
        StdOut.println();
    }

}
