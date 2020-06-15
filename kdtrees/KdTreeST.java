/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Generalizes a BST to 2D keys, using the same API as in
 *  PointST. Implements efficient range and nearest-neighbor search.
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

public class KdTreeST<Value> {

    // internal class represents nodes in the data structure
    private class Node {
        private final Point2D p; // point
        private Value val; // value
        private Node lb; // left/bottom node
        private Node rt; // right/top node
        private final RectHV rect; // axis-aligned rectangle of this node
        private final boolean isVertical; // orientation of this node

        // creates a new Node given a point, value, and rectangle
        public Node(Point2D p, Value val, boolean isVertical, RectHV rect) {
            this.p = p;
            this.val = val;
            this.isVertical = isVertical;
            this.rect = rect;
        }

        // returns right or top subrectangle
        public RectHV rightTopRect() {
            if (!isVertical) // top
                return new RectHV(rect.xmin(), p.y(), rect.xmax(), rect.ymax());
            else // right
                return new RectHV(p.x(), rect.ymin(), rect.xmax(), rect.ymax());

        }

        // returns left or bottom subrectangle
        public RectHV leftBottomRect() {
            if (!isVertical) // bottom
                return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), p.y());
            else // left
                return new RectHV(rect.xmin(), rect.ymin(), p.x(), rect.ymax());
        }

        // is a given point above or to the right of this node's point?
        public boolean checkPointRightTop(Point2D p0) {
            if (p0.x() >= p.x() && isVertical) return true;
            if (p0.y() >= p.y() && !isVertical) return true;
            return false;
        }
    }

    // boolean constants to represent the orientation of a node
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private Node root; // the root node
    private int size; // number of nodes in the tree

    // construct an empty symbol table of points
    public KdTreeST() {
        size = 0;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points
    public int size() {
        return size;
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        validate(p);
        validate(val);
        size++;
        root = put(root, null, p, val, VERTICAL);
    }

    // helper method for put()
    private Node put(Node n, Node prev, Point2D p, Value val,
                     boolean isVertical) {
        // tree is empty
        if (root == null) {
            RectHV rect = new RectHV(Double.NEGATIVE_INFINITY,
                                     Double.NEGATIVE_INFINITY,
                                     Double.POSITIVE_INFINITY,
                                     Double.POSITIVE_INFINITY);
            return new Node(p, val, VERTICAL, rect);
        }

        // reached bottom of the tree
        if (n == null) {
            if (prev.checkPointRightTop(p))
                return new Node(p, val, isVertical, prev.rightTopRect());
            else
                return new Node(p, val, isVertical, prev.leftBottomRect());
        }

        // key already exists; update value
        if (n.p.equals(p)) {
            n.val = val;
            size--;
            return n;
        }

        if (isVertical) { // compare x-coordinates
            boolean xLessThan = p.x() < n.p.x();
            if (xLessThan) n.lb = put(n.lb, n, p, val, HORIZONTAL);
            else n.rt = put(n.rt, n, p, val, HORIZONTAL);
        }
        else { // compare y-coordinates
            boolean yLessThan = p.y() < n.p.y();
            if (yLessThan) n.lb = put(n.lb, n, p, val, VERTICAL);
            else n.rt = put(n.rt, n, p, val, VERTICAL);
        }

        return n;
    }

    // value associated with point p
    public Value get(Point2D p) {
        validate(p);
        return get(root, p, VERTICAL);
    }

    // helper method for get()
    private Value get(Node n, Point2D p, boolean isVertical) {
        if (n == null) return null;
        if (n.p.equals(p)) return n.val;

        if (isVertical) { // compare x-coordinates
            boolean xLessThan = p.x() < n.p.x();
            if (xLessThan) return get(n.lb, p, HORIZONTAL);
            else return get(n.rt, p, HORIZONTAL);
        }
        else { // compare y-coordinates
            boolean yLessThan = p.y() < n.p.y();
            if (yLessThan) return get(n.lb, p, VERTICAL);
            else return get(n.rt, p, VERTICAL);
        }
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        validate(p);
        return get(p) != null;
    }

    // all points in the symbol table, in level-order
    public Iterable<Point2D> points() {
        Queue<Point2D> points = new Queue<Point2D>();
        Queue<Node> nodes = new Queue<Node>();
        if (isEmpty()) return points;

        /*
         * Starting with the root, enqueue the node's point in the points queue,
         * then enqueue its left and right children (in that order) in the
         * nodes queue. The nodes queue ensures that the loop will visit nodes
         * from left to right, top to bottom, in Theta(n) time.
         */
        nodes.enqueue(root);
        while (!nodes.isEmpty()) {
            Node n = nodes.dequeue();
            if (n == null) continue;
            points.enqueue(n.p);
            nodes.enqueue(n.lb);
            nodes.enqueue(n.rt);
        }

        return points;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        Queue<Point2D> points = new Queue<Point2D>();
        range(root, rect, points);
        return points;
    }

    // helper method for range()
    private void range(Node n, RectHV rect, Queue<Point2D> points) {
        if (n == null) return;
        // prune if the enclosing rectangle does not intersect
        if (!n.rect.intersects(rect)) return;

        if (rect.contains(n.p)) points.enqueue(n.p);
        range(n.lb, rect, points);
        range(n.rt, rect, points);
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        validate(p);
        if (isEmpty()) return null;
        return nearest(root, p, root.p);
    }

    // helper method for nearest()
    private Point2D nearest(Node n, Point2D p, Point2D champion) {
        if (n == null) return champion;

        double championSqDistance = champion.distanceSquaredTo(p);
        double rectDistance = n.rect.distanceSquaredTo(p);

        // prune if the enclosing rectangle contains only points that are not
        // closer to the given point
        if (rectDistance < championSqDistance) {
            if (n.p.distanceSquaredTo(p) < championSqDistance)
                champion = n.p;

            // first check the rectangle in the direction of the given point
            if (n.checkPointRightTop(p)) {
                champion = nearest(n.rt, p, champion);
                champion = nearest(n.lb, p, champion);
            }
            else {
                champion = nearest(n.lb, p, champion);
                champion = nearest(n.rt, p, champion);
            }
        }

        return champion;
    }

    // check if a given Point2D is non-null
    private void validate(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null Point2D");
    }

    // check if a given Value is non-null
    private void validate(Value val) {
        if (val == null) throw new IllegalArgumentException("null Value");
    }

    // check if a given RectHV is non-null
    private void validate(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("null RectHV");
    }

    // unit testing
    public static void main(String[] args) {
        KdTreeST<Integer> kd = new KdTreeST<Integer>();
        Point2D p1 = new Point2D(2, 3);
        Point2D p2 = new Point2D(4, 2);
        Point2D p3 = new Point2D(4, 5);
        Point2D p4 = new Point2D(3, 3);
        Point2D p5 = new Point2D(1, 5);
        Point2D p6 = new Point2D(4, 4);
        Point2D p7 = new Point2D(4, 4);
        kd.put(p1, 1);
        kd.put(p2, 2);
        kd.put(p3, 3);
        kd.put(p4, 4);
        kd.put(p5, 5);
        kd.put(p6, 6);
        kd.put(p7, 7);
        StdOut.println(kd.points());
        StdOut.println(kd.size()); // 6
        StdOut.println(kd.contains(p4)); // true
        StdOut.println(kd.get(p7)); // 7
        StdOut.println(kd.size); // 6
        StdOut.println(kd.isEmpty()); // false
        StdOut.println(kd.range(new RectHV(1, 2, 3, 4))); // p1 and p4
        StdOut.println(kd.nearest(new Point2D(2.5, 5))); // p3
    }
}
