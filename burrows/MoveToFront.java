/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Partner Name:    Byron Zhang
 *  Partner NetID:   zishuoz
 *  Partner Precept: P05
 *
 *  Description:  Provides move-to-front encode and decode functionality in
 *  preparation for Huffman encoding.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int ASCII_E = 256; // length of extended ASCII alphabet

    // apply move-to-front encoding, reading from stdin and writing to stdout
    public static void encode() {
        int[] alpha = new int[ASCII_E];
        for (int i = 0; i < ASCII_E; i++) alpha[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            for (int i = 0; i < ASCII_E; i++) {
                if (alpha[i] == c) {
                    BinaryStdOut.write((char) i);
                    // move current char to alpha[] head
                    for (int j = i; j >= 1; j--) {
                        int temp = alpha[j - 1];
                        alpha[j - 1] = alpha[j];
                        alpha[j] = temp;
                    }
                    break;
                }
            }
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from stdin and writing to stdout
    public static void decode() {
        int[] alpha = new int[ASCII_E];
        for (int i = 0; i < ASCII_E; i++) alpha[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            int r = BinaryStdIn.readChar();
            char c = (char) alpha[r];
            BinaryStdOut.write(c);
            // move current char to alpha[] head
            for (int j = r; j >= 1; j--) {
                int temp = alpha[j - 1];
                alpha[j - 1] = alpha[j];
                alpha[j] = temp;
            }
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}
