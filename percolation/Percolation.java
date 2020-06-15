/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Implements the Percolation datatype responsible for simulating
 *  percolation and for enabling the estimation of the percolation threshold p*.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[] system; // grid representing the open/blocked slots
    private final WeightedQuickUnionUF uf; // union-find library
    private final int n;  // number of rows and columns in the grid
    private int numOpenSites = 0; // keeps track of the number of open sites

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be a positive integer!");

        // uf has +2 indices for the virtual sites
        // location n * n is the water source
        // location n * n + 1 is the water terminus
        uf = new WeightedQuickUnionUF(n * n + 2);
        // n-by-n grid mapped/represented by a 1D array of n*n indices
        system = new boolean[n * n];
        this.n = n;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkValidLocation(row, col);
        int i = to1D(row, col);

        // checks if the site is already open
        if (isOpen(row, col)) return;

        // opens the site
        if (!system[i]) system[i] = true;

        // links to adjacent open sites
        if (i - n >= 0 && system[i - n]) uf.union(i, i - n); // up
        if (i + n < n * n && system[i + n]) uf.union(i, i + n); // down
        if (i % n != 0 && system[i - 1]) uf.union(i, i - 1); // left
        if ((i + 1) % n != 0 && system[i + 1]) uf.union(i, i + 1); // right

        // links to the water source/terminus sites if the cell is on the
        // top or bottom rows
        if (i - n < 0) uf.union(i, n * n); // link to source
        if (i + n >= n * n) uf.union(i, n * n + 1); // link to terminus

        numOpenSites++;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkValidLocation(row, col);
        int i = to1D(row, col);
        return system[i];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkValidLocation(row, col);
        int i = to1D(row, col);
        return uf.connected(i, n * n);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.connected(n * n, n * n + 1);
    }

    // converts a (row, col) location to a 1D index
    private int to1D(int row, int col) {
        return n * row + col;
    }

    // checks if a given (row, col) resides within the system grid
    private void checkValidLocation(int row, int col) {
        if (row < 0 || row >= n || col < 0 || col >= n)
            throw new IllegalArgumentException("Invalid (row, col) index");
    }

    // unit testing (required)
    public static void main(String[] args) {
        Percolation p = new Percolation(4);

        StdOut.println(p.to1D(2, 2)); // 10
        p.checkValidLocation(3, 3); // valid
        // p.checkValidLocation(3, 4); // invalid; throws exception
        StdOut.println(p.isOpen(2, 2)); // false
        p.open(2, 2);
        StdOut.println(p.isOpen(2, 2)); // true
        StdOut.println(p.uf.connected(10, 6)); // false
        p.open(1, 2);
        StdOut.println(p.uf.connected(10, 6)); // true
        StdOut.println(p.isFull(2, 2)); // false
        StdOut.println(p.percolates()); // false
        StdOut.println(p.numberOfOpenSites()); // 2
        p.open(0, 2);
        p.open(3, 2);
        StdOut.println(p.numberOfOpenSites()); // 4
        StdOut.println(p.isFull(2, 2)); // true
        StdOut.println(p.isFull(3, 2)); // true
        StdOut.println(p.percolates()); // true
    }
}
