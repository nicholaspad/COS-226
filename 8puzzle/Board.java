/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Immutable data type that models an n-by-n board with sliding
 *  tiles.
 **************************************************************************** */

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {
    private static final int BLANK = 0; // value of the blank tile

    private final int n; // board size
    private final int[] board; // array of length nxn representing the board
    private final int hammingDistance; // number of tiles out of place
    private final int manhattanDistance; // total Manhattan distance

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        board = copyBoard(tiles);
        hammingDistance = calculateHammingDistance();
        manhattanDistance = calculateManhattanDistance();
    }

    // OVERLOADED: create a board from a 1D array of tiles
    private Board(int[] tiles) {
        n = (int) Math.sqrt(tiles.length);
        board = copyBoard(tiles);
        hammingDistance = calculateHammingDistance();
        manhattanDistance = calculateManhattanDistance();
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(size() + "\n");
        for (int row = 0; row < size(); row++) {
            for (int col = 0; col < size(); col++)
                s.append(String.format("%2d ", tileAt(row, col)));
            s.append("\n");
        }
        return s.toString();
    }

    // tile at (row, col) or 0 if blank
    public int tileAt(int row, int col) {
        if (row >= size() || row < 0 || col >= size() || col < 0)
            throw new IllegalArgumentException("Invalid row/col value!");
        return board[to1D(row, col)];
    }

    // board size n
    public int size() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return manhattanDistance == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return Arrays.equals(board, that.board);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<Board>();

        int[] blankLocation = to2D(blankLocation());
        int row = blankLocation[0];
        int col = blankLocation[1];

        if (row > 0) // swap above
            neighbors.enqueue(new Board(exchangeTiles(row, col, row - 1, col)));
        if (col > 0) // swap left
            neighbors.enqueue(new Board(exchangeTiles(row, col, row, col - 1)));
        if (row < size() - 1) // swap below
            neighbors.enqueue(new Board(exchangeTiles(row, col, row + 1, col)));
        if (col < size() - 1) // swap right
            neighbors.enqueue(new Board(exchangeTiles(row, col, row, col + 1)));

        return neighbors;
    }

    // is this board solvable?
    public boolean isSolvable() {
        int numInversions = 0;

        // count all inverted pairs of tiles, excluding the blank tile
        for (int row0 = 0; row0 < size(); row0++) {
            for (int col0 = 0; col0 < size(); col0++) {
                for (int row1 = 0; row1 < size(); row1++) {
                    for (int col1 = 0; col1 < size(); col1++) {
                        int tile0 = tileAt(row0, col0);
                        int tile1 = tileAt(row1, col1);
                        if (tile0 != BLANK && tile1 != BLANK) {
                            // row-major order: ensure that the second board
                            // space is to the right of the first board space
                            if (row1 == row0 && col1 > col0 && tile0 > tile1)
                                numInversions++;
                            else if (row1 > row0 && tile0 > tile1)
                                numInversions++;
                        }
                    }
                }
            }
        }

        if (size() % 2 != 0) return numInversions % 2 == 0;
        else {
            int blankRow = to2D(blankLocation())[0];
            return (numInversions + blankRow) % 2 != 0;
        }
    }

    // finds the index of the blank tile
    private int blankLocation() {
        for (int row = 0; row < size(); row++) {
            for (int col = 0; col < size(); col++)
                if (tileAt(row, col) == BLANK) return to1D(row, col);
        }
        return -1;
    }

    // makes a 1D copy of a 2D board array
    private int[] copyBoard(int[][] old) {
        int[] copy = new int[size() * size()];
        for (int row = 0; row < size(); row++) {
            for (int col = 0; col < size(); col++)
                copy[to1D(row, col)] = old[row][col];
        }
        return copy;
    }

    // OVERLOADED: makes a 1D copy of a 1D board array
    private int[] copyBoard(int[] old) {
        int[] copy = new int[size() * size()];
        for (int i = 0; i < size() * size(); i++)
            copy[i] = old[i];
        return copy;
    }

    // makes a copy of the board with a pair of exchanged tiles
    private int[] exchangeTiles(int row0, int col0, int row1, int col1) {
        int[] copy = copyBoard(board);
        int temp = copy[to1D(row0, col0)];
        copy[to1D(row0, col0)] = copy[to1D(row1, col1)];
        copy[to1D(row1, col1)] = temp;
        return copy;
    }

    // determines the tile that sits at (row, col) in the goal puzzle
    private int getProperTileAt(int row, int col) {
        if (row == size() - 1 && col == size() - 1) return BLANK;
        return row * size() + col + 1;
    }

    // determines (row, col) for a given tile in the goal puzzle
    private int[] getProperLocationOf(int tile) {
        return new int[] { (tile - 1) / size(), (tile - 1) % size() };
    }

    // converts 2D coordinate to a 1D index
    private int to1D(int row, int col) {
        return size() * row + col;
    }

    // converts 1D index to 2D coordinate
    private int[] to2D(int index) {
        return new int[] { index / size(), index % size() };
    }

    // calculate the Hamming distance of the current board state
    private int calculateHammingDistance() {
        int d = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int tile = tileAt(row, col);
                if (tile != getProperTileAt(row, col) && tile != BLANK) d++;
            }
        }
        return d;
    }

    // calculate the Manhattan distance of the current board state
    private int calculateManhattanDistance() {
        int d = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int tile = tileAt(row, col);
                if (tile != getProperTileAt(row, col) && tile != BLANK) {
                    int[] properLocation = getProperLocationOf(tile);
                    int r0 = properLocation[0];
                    int c0 = properLocation[1];
                    // Manhattan distance for a given out-of-place tile
                    d += Math.abs(row - r0) + Math.abs(col - c0);
                }
            }
        }
        return d;
    }

    // test client
    public static void main(String[] args) {
        int[][] board = {
                { 8, 1, 3 },
                { 4, 0, 2 },
                { 7, 6, 5 }
        };

        Board b = new Board(board);
        StdOut.println(b);
        StdOut.println(b.hamming()); // 5
        StdOut.println(b.manhattan()); // 10
        StdOut.println(b.isGoal()); // false
        StdOut.println(b.tileAt(1, 2)); // 2

        Iterable<Board> q = b.neighbors();
        for (Board b0 : q) {
            StdOut.println(b0);
        }

        int[][] board1 = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 },
                { 13, 15, 14, 0 }
        };
        Board b1 = new Board(board1);
        StdOut.println(b1);
        StdOut.println(b1.size()); // 4
        StdOut.println(b1.isSolvable()); // false

        int[][] board2 = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 0 }
        };
        Board b2 = new Board(board2);
        StdOut.println(b2);
        StdOut.println(b2.isSolvable()); // true
        StdOut.println(b2.isGoal()); // true

        int[][] board3 = {
                { 8, 1, 3 },
                { 4, 0, 2 },
                { 7, 6, 5 }
        };
        Board b3 = new Board(board3);
        StdOut.println(b.equals(b3)); // true
    }
}
