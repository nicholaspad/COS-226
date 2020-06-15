/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Implements the A* search algorithm to solve n-by-n slider
 *  puzzles, represented as Board objects.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private SearchNode bestMove; // stores the SearchNode with lowest priority

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board; // the current Board state
        private int numMoves = 0; // number of moves since the first Board
        private SearchNode prev; // the previous SearchNode
        private int priority; // priority metric of this SearchNode

        // creates a new SearchNode given a Board and a previous SearchNode
        public SearchNode(Board board, SearchNode prev) {
            this.board = board;
            this.numMoves = prev.numMoves + 1;
            this.prev = prev;
            this.priority = board.manhattan() + numMoves;
        }

        // overloaded constructor for the first SearchNode created
        public SearchNode(Board board) {
            this.board = board;
        }

        // compares SearchNodes using Manhattan distances
        public int compareTo(SearchNode that) {
            return priority - that.priority;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Board is null!");
        if (!initial.isSolvable())
            throw new IllegalArgumentException("Board is unsolvable!");

        final MinPQ<SearchNode> pq = new MinPQ<SearchNode>();

        // add the initial SearchNode, containing the initial Board, to pq
        SearchNode initialNode = new SearchNode(initial);
        pq.insert(initialNode);

        // A* search algorithm
        while (true) {
            // delete the SearchNode of least priority (closest to goal Board)
            bestMove = pq.delMin();
            if (bestMove.board.isGoal()) break;

            Iterable<Board> neighbors = bestMove.board.neighbors();

            // add all unvisited neighbors to the PQ
            for (Board neighbor : neighbors) {
                SearchNode possibleMove = new SearchNode(neighbor, bestMove);
                if (bestMove.numMoves >= 1 &&
                        !neighbor.equals(bestMove.prev.board))
                    pq.insert(possibleMove);
                else if (bestMove.numMoves == 0)
                    pq.insert(possibleMove);
            }
        }
    }

    // min number of moves to solve initial board
    public int moves() {
        return bestMove.numMoves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        Stack<Board> solution = new Stack<Board>();
        SearchNode current = bestMove;
        while (current != null) {
            solution.push(current.board);
            current = current.prev;
        }
        return solution;
    }

    // test client
    public static void main(String[] args) {
        final String filename = args[0];
        final In in = new In(filename);
        final int n = in.readInt();

        int[][] board = new int[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++)
                board[row][col] = in.readInt();
        }

        Board b = new Board(board);

        if (!b.isSolvable())
            throw new IllegalArgumentException("Board is not solvable!");

        Solver s = new Solver(b);

        for (Board b3 : s.solution())
            StdOut.println(b3);

        StdOut.println(s.moves());
    }
}
