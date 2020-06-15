/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Performs a series of experiments to estimate p*.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private final double CONFIDENCE_95 = 1.96;
    private final int trials; // number of trials
    private final double[] data; // stores the proportion data

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException("n and T must be > 0");

        this.trials = trials;
        data = new double[trials];

        Percolation p;
        for (int i = 0; i < trials; i++) {
            p = new Percolation(n);
            int randCell = StdRandom.uniform(n * n); // random cell

            while (true) {
                int row = randCell / n;
                int col = randCell % n;

                if (!p.isOpen(row, col)) {
                    p.open(row, col); // opens a cell if it's not open
                    if (p.percolates()) break; // breaks if the system percolates
                }

                randCell = StdRandom.uniform(n * n); // new random cell if the cell is already open
            }

            data[i] = (double) p.numberOfOpenSites() / (n * n); // stores the open proportion
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(data);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        if (trials == 1) return Double.NaN;
        return StdStats.stddev(data);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    // test client
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        Stopwatch s = new Stopwatch();
        PercolationStats ps = new PercolationStats(n, t);
        double runtime = s.elapsedTime();

        StdOut.printf("mean()\t\t\t= %.6f\n", ps.mean());
        StdOut.printf("stddev()\t\t= %.6f\n", ps.stddev());
        StdOut.printf("confidenceLow()\t\t= %.6f\n", ps.confidenceLow());
        StdOut.printf("confidenceHigh()\t= %.6f\n", ps.confidenceHigh());
        StdOut.println("elapsed time\t\t= " + runtime);
    }
}
