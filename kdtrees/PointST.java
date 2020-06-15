/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Uses a red-black BST to represent a symbol table whose keys are
 *  Point2D objects.Implements brute-force range and nearest-neighbor search.
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

public class PointST<Value> {

    private final RedBlackBST<Point2D, Value> st; // represents symbol table

    // construct an empty symbol table of points
    public PointST() {
        st = new RedBlackBST<Point2D, Value>();
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return st.isEmpty();
    }

    // number of points
    public int size() {
        return st.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null)
            throw new IllegalArgumentException("null input");
        st.put(p, val);
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null input");
        return st.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null input");
        return st.contains(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return st.keys();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("null input");
        Queue<Point2D> inside = new Queue<Point2D>();
        for (Point2D p : points())
            if (rect.contains(p)) inside.enqueue(p);
        return inside;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null input");
        if (size() == 0) return null;
        Point2D nearest = null; // will never be null because size() > 0
        double leastSquaredDistance = Double.POSITIVE_INFINITY;
        for (Point2D p0 : points()) {
            double d = p0.distanceSquaredTo(p);
            if (d < leastSquaredDistance) {
                nearest = p0;
                leastSquaredDistance = d;
            }
        }
        return nearest;
    }

    // unit testing (required)
    public static void main(String[] args) {
        PointST<Integer> st = new PointST<Integer>();
        StdOut.println(st.isEmpty()); // true
        for (Point2D p : st.points())
            StdOut.println(p); // nothing
        Point2D p1 = new Point2D(1, 1);
        Point2D p2 = new Point2D(2, 2);
        st.put(p1, 1);
        st.put(p2, 2);
        StdOut.println(st.size()); // 2
        StdOut.println(st.nearest(new Point2D(3, 3))); // p2
        StdOut.println(st.nearest(new Point2D(1, 1))); // p1
        StdOut.println(st.contains(new Point2D(1, 1))); // true
        for (Point2D p : st.points())
            StdOut.println(p); // p1, p2
        StdOut.println(st.get(p2)); // 2
        RectHV rect1 = new RectHV(1.5, 1.5, 3, 3);
        StdOut.println(st.range(rect1)); // p2
        RectHV rect2 = new RectHV(5, 5, 7, 7);
        StdOut.println(st.range(rect2)); // nothing
    }
}
