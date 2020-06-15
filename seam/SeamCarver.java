/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Immutable data type providing seam-carving functionality for
 *  images of dimensions greater than 1 pixel.
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SeamCarver {
    private int[][] picture; // stores the current picture as 32-bit RGB ints

    private int width; // current picture width
    private int height; // current picture height

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        checkPicture(picture);
        this.width = picture.width();
        this.height = picture.height();
        this.picture = new int[height][width];
        convertPicture(picture);
    }

    // current picture
    public Picture picture() {
        Picture p = new Picture(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                p.setRGB(x, y, picture[y][x]);
        }
        return p;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        checkXY(x, y);

        int west; // left pixel
        int east; // right pixel
        int north; // top pixel
        int south; // bottom pixel
        int FIRST_ROW_COL = 0;

        if (x != 0) west = picture[y][x - 1];
        else west = picture[y][width - 1]; // right edge

        if (x != width - 1) east = picture[y][x + 1];
        else east = picture[y][FIRST_ROW_COL]; // left edge

        if (y != 0) north = picture[y - 1][x];
        else north = picture[height - 1][x]; // bottom edge

        if (y != height - 1) south = picture[y + 1][x];
        else south = picture[FIRST_ROW_COL][x]; // top edge

        return Math.sqrt(gradSq(west, east) + gradSq(north, south));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = findVerticalSeam();
        transpose();
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // pixelTo[][] contains ints -1, 0, or 1 and is analagous to edgeTo[]
        // -1: above-left pixel; 0: above pixel; 1: above-right pixel
        int[][] pixelTo = new int[height][width];

        // totalEnergyTo[][] is analagous to distTo[]
        double[][] totalEnergyTo = new double[height][width];

        // populate pixelTo[][] and totalEnergyTo[][]
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0) totalEnergyTo[y][x] = energy(x, y);
                else {
                    int c = minEnergyAncestor(x, y, totalEnergyTo);
                    pixelTo[y][x] = c;
                    totalEnergyTo[y][x] = energy(x, y)
                            + totalEnergyTo[y - 1][x + c];
                }
            }
        }

        int leastTotalEnergyX = 0;
        double leastTotalEnergy = Double.POSITIVE_INFINITY;

        // find the x-coordinate of least total energy along the bottom edge
        for (int x = 0; x < width; x++) {
            double e = totalEnergyTo[height - 1][x];
            if (e < leastTotalEnergy) {
                leastTotalEnergy = e;
                leastTotalEnergyX = x;
            }
        }

        int[] seam = new int[height];

        // use pixelTo[] to retrace the lowest energy path
        for (int y = height - 1; y >= 0; y--) {
            seam[y] = leastTotalEnergyX;
            leastTotalEnergyX += pixelTo[y][leastTotalEnergyX];
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam);
        int[][] newPicture = new int[height][width - 1];

        // delete pixels and energy values along the seam
        for (int y = 0; y < height; y++) {
            int cut = seam[y];

            // copy pixels to the left of the seam
            for (int x = 0; x < cut; x++)
                newPicture[y][x] = picture[y][x];

            // shift pixels to the right of the seam left by one
            for (int x = cut + 1; x < width; x++)
                newPicture[y][x - 1] = picture[y][x];
        }

        width--;
        picture = newPicture;
    }

    // return the relative coordinate of the least total-energy ancestor pixel:
    // -1 for above-left, 0 for above, 1 for above-right
    private int minEnergyAncestor(int x, int y, double[][] totalEnergyTo) {
        // relative pixel coordinates
        int ABOVE = 0;
        int ABOVE_L = -1;
        int ABOVE_R = 1;

        int c = ABOVE; // total energy tie => default to the above pixel
        double above = totalEnergyTo[y - 1][x];

        if (width == 1) return ABOVE;

        if (x == 0) { // left edge => check above and above-right
            double aboveRight = totalEnergyTo[y - 1][x + 1];
            if (aboveRight < above) c = ABOVE_R;
        }
        else if (x == width - 1) { // right edge => check above and above-left
            double aboveLeft = totalEnergyTo[y - 1][x - 1];
            if (aboveLeft < above) c = ABOVE_L;
        }
        else { // non-edge => check above-left, above, and above-right
            double aboveLeft = totalEnergyTo[y - 1][x - 1];
            double aboveRight = totalEnergyTo[y - 1][x + 1];
            if (aboveLeft < above && aboveLeft <= aboveRight) c = ABOVE_L;
            else if (aboveRight < above && aboveRight <= aboveLeft) c = ABOVE_R;
        }

        return c;
    }

    // compute the squared gradient value given two RGB ints
    private double gradSq(int p, int q) {
        int pR = (p >> 16) & 0xFF;
        int pG = (p >> 8) & 0xFF;
        int pB = p & 0xFF;

        int qR = (q >> 16) & 0xFF;
        int qG = (q >> 8) & 0xFF;
        int qB = q & 0xFF;

        double dRxSq = Math.pow(pR - qR, 2);
        double dGxSq = Math.pow(pG - qG, 2);
        double dBxSq = Math.pow(pB - qB, 2);

        return dRxSq + dGxSq + dBxSq;
    }

    // convert the Picture object into a 2D array of 32-bit RGB ints
    private void convertPicture(Picture p) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                picture[y][x] = p.getRGB(x, y);
        }
    }

    // transpose the picture array
    private void transpose() {
        int[][] transposed = new int[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                transposed[x][y] = picture[y][x];
        }

        // update picture array and swap height and width values
        picture = transposed;
        int temp = height;
        height = width;
        width = temp;
    }

    // check validity of x and y coordinates
    private void checkXY(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            throw new IllegalArgumentException("invalid pixel coordinates");
    }

    // check validity of Picture object
    private void checkPicture(Picture p) {
        if (p == null) throw new IllegalArgumentException("null Picture");
    }

    // check validity of a seam array
    private void checkSeam(int[] s) {
        if (s == null) throw new IllegalArgumentException("null seam array");
        if (width == 1) throw new IllegalArgumentException("width is 1");
        if (s.length != height)
            throw new IllegalArgumentException("invalid seam array length");

        int adj = 0; // previous seam coordinate
        for (int i = 0; i < s.length; i++) {
            int cut = s[i];
            if (i != 0 && Math.abs(cut - adj) > 1)
                throw new IllegalArgumentException("invalid seam coordinates");
            adj = cut;
            if (cut < 0 || cut >= width)
                throw new IllegalArgumentException("invalid seam coordinate");
        }
    }

    //  unit testing
    public static void main(String[] args) {
        String filename = "3x4.png";
        SeamCarver sc = new SeamCarver(new Picture(filename));
        StdOut.println(sc.picture());
        StdOut.println(sc.width()); // 3
        StdOut.println(sc.height()); // 4
        StdOut.println(sc.energy(1, 0)); // 228.079
        int[] seamV = sc.findVerticalSeam();
        StdOut.println(Arrays.toString(seamV)); // 0 0 0 0
        sc.removeVerticalSeam(seamV);
        int[] seamH = sc.findHorizontalSeam();
        StdOut.println(Arrays.toString(seamH)); // 0 0
        sc.removeHorizontalSeam(seamH);
        StdOut.println(sc.picture());
    }
}
