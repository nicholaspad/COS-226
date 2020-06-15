/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Implements the BinarySearchDeluxe data type, which provides
 *  functionality for finding the leftmost/rightmost indices of matching keys.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class BinarySearchDeluxe {

    // Returns the index of the first key in the sorted array a[]
    // that is equal to the search key, or -1 if no such key.
    public static <Key> int firstIndexOf(Key[] a, Key key,
                                         Comparator<Key> comparator) {
        if (a == null || key == null || comparator == null)
            throw new IllegalArgumentException("Arguments must be non-null!");

        int index = -1; // keeps track of the leftmost key index
        int lo = 0;
        int hi = a.length - 1;

        // iterate until lo and hi converge
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // calculate midpoint
            int compare = comparator.compare(key, a[mid]);

            if (compare < 0) hi = mid - 1; // key < current; search left
            else if (compare > 0) lo = mid + 1; // key > current; search right
            else {
                /**
                 * In this case, a match has been located; set hi to mid - 1 to
                 * search left, since an equal key could exist to the left.
                 */
                index = mid;
                hi = mid - 1;
            }
        }

        return index;
    }

    // Returns the index of the last key in the sorted array a[]
    // that is equal to the search key, or -1 if no such key.
    public static <Key> int lastIndexOf(Key[] a, Key key, Comparator<Key> comparator) {
        if (a == null || key == null || comparator == null)
            throw new IllegalArgumentException("Arguments must be non-null!");

        int index = -1; // keeps track of the rightmost key index
        int lo = 0;
        int hi = a.length - 1;

        // iterate until lo and hi converge
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // calculate midpoint
            int compare = comparator.compare(key, a[mid]);

            if (compare < 0) hi = mid - 1; // key < current; search left
            else if (compare > 0) lo = mid + 1; // key > current; search right
            else {
                /**
                 * In this case, a match has been located; set lo to mid + 1 to
                 * search right, since an equal key could exist to the right.
                 */
                index = mid;
                lo = mid + 1;
            }
        }

        return index;
    }

    // unit testing (required)
    public static void main(String[] args) {
        String[] a = { "A", "A", "C", "G", "G", "T" };
        int index = BinarySearchDeluxe.lastIndexOf(a, "G",
                                                   String.CASE_INSENSITIVE_ORDER);
        StdOut.println(index); // 4
        int index1 = BinarySearchDeluxe.firstIndexOf(a, "G",
                                                     String.CASE_INSENSITIVE_ORDER);
        StdOut.println(index1); // 3
    }
}

