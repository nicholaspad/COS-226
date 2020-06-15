/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Partner Name:    Byron Zhang
 *  Partner NetID:   zishuoz
 *  Partner Precept: P05
 *
 *  Description:  Provides Burrows-Wheeler transform functionality in
 *  preparation for move-to-front encoding.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int ASCII_E = 256; // length of extended ASCII alphabet

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String text = BinaryStdIn.readString();
        CircularSuffixArray ar = new CircularSuffixArray(text);
        int len = ar.length();

        // find and write the "first" index
        for (int i = 0; i < len; i++) {
            if (ar.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }

        // write the BW transform (last column of the sorted circular suffixes)
        for (int i = 0; i < len; i++)
            BinaryStdOut.write(text.charAt((ar.index(i) + len - 1) % len));

        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String text = BinaryStdIn.readString();

        /* @citation Adapted from: https://algs4.cs.princeton.edu/51radix/
           LSD.java.html. Accessed 04/24/2020. */

        int[] count = new int[ASCII_E + 1]; // stores cumulate char counts
        int n = text.length();
        int[] next = new int[n]; // stores pointers to the next char

        // construct next[] via key-indexed counting
        for (int i = 0; i < n; i++) count[text.charAt(i) + 1]++; // frequencies
        for (int i = 0; i < ASCII_E; i++) count[i + 1] += count[i]; // cumulates
        for (int i = 0; i < n; i++) next[count[text.charAt(i)]++] = i;

        /* @end-citation */

        // reconstruct text using next[]
        for (int i = 0; i < n; i++) {
            first = next[first];
            BinaryStdOut.write(text.charAt(first));
        }

        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
    }
}
