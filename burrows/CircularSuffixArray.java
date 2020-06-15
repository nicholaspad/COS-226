/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Partner Name:    Byron Zhang
 *  Partner NetID:   zishuoz
 *  Partner Precept: P05
 *
 *  Description:  Immutable data type that describes the abstraction of a sorted
 *  array of n circular suffixes of a String of length n.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    private static final int CUTOFF = 6; // cutoff to insertion sort

    private final int len; // length of the original String
    private int[] ind; // original indices of each circular suffix

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("null s");
        len = s.length();
        ind = new int[len];
        for (int i = 0; i < len; i++) ind[i] = i;
        sort(s, 0, len - 1, 0); // by suffixes
    }

    // length of s
    public int length() {
        return len;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= len) throw new IllegalArgumentException("invalid i");
        return ind[i];
    }

    /* @citation Adapted from: https://algs4.cs.princeton.edu/code/edu/
       princeton/cs/algs4/Quick3string.java.html. Accessed 04/17/2020. */

    // sorts the inidices array based on its corresponding circular suffixes
    private void sort(String s, int lo, int hi, int d) {
        // base case for unary strings
        if (lo + d >= 2 * len || hi + d >= 2 * len) return;

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(s, lo, hi, d);
            return;
        }

        int lt = lo;
        int gt = hi;
        int v = s.charAt((ind[lo] + d) % len);
        int i = lo + 1;

        while (i <= gt) {
            // retrieve dth char, treating s as a circular string
            int t = s.charAt((ind[i] + d) % len);
            if (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else i++;
        }

        sort(s, lo, lt - 1, d);
        sort(s, lt, gt, d + 1);
        sort(s, gt + 1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private void insertion(String s, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(s, ind[j], ind[j - 1], d); j--)
                exch(j, j - 1);
    }

    // exchange ind[i] and ind[j]
    private void exch(int i, int j) {
        int temp = ind[i];
        ind[i] = ind[j];
        ind[j] = temp;
    }

    // is v less than w, starting at character d
    private boolean less(String s, int v, int w, int d) {
        for (int i = d; i < len; i++) {
            if (s.charAt((v + i) % len) < s.charAt((w + i) % len)) return true;
            if (s.charAt((v + i) % len) > s.charAt((w + i) % len)) return false;
        }
        return false;
    }

    /* @end-citation */

    // unit testing
    public static void main(String[] args) {
        CircularSuffixArray c = new CircularSuffixArray("ABRACADABRA!");
        StdOut.println(c.length()); // 12
        for (int i = 0; i < c.length(); i++)
            StdOut.println(c.index(i)); // 11 10 7 0 3 5 8 1 4 6 9 2
    }
}
