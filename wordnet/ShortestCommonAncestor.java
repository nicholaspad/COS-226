/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description: Immutable data type that contains algorithms related to
 *  determining shortest common ancestors.
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.StdOut;

public class ShortestCommonAncestor {

    // the digraph of interest
    private final Digraph G;

    // stores the shortest ancestral distance and the closest common ancestor
    // for pairs of vertices
    private final LinearProbingHashST<int[], int[]> recentVals;

    // constructor takes a rooted DAG as argument
    public ShortestCommonAncestor(Digraph G) {
        validateDAG(G); // checks that G is acyclic and rooted
        this.G = new Digraph(G);
        recentVals = new LinearProbingHashST<int[], int[]>();
    }

    // length of shortest ancestral path between v and w
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        // if the shortest ancestral path has already been found, return it
        int[] vals = recentVals.get(orderedVertices(v, w));
        if (vals != null) return vals[0];
        return closest(v, w)[0];
    }

    // a closest common ancestor of vertices v and w
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        // if the closest common ancestor has already been found, return it
        int[] vals = recentVals.get(orderedVertices(v, w));
        if (vals != null) return vals[1];
        return closest(v, w)[1];
    }

    // length of shortest ancestral path of vertex subsets A and B
    public int lengthSubset(Iterable<Integer> a, Iterable<Integer> b) {
        validateSet(a);
        validateSet(b);
        return closest(a, b)[0];
    }

    // a closest common ancestor of vertex subsets A and B
    public int ancestorSubset(Iterable<Integer> a, Iterable<Integer> b) {
        validateSet(a);
        validateSet(b);
        return closest(a, b)[1];
    }

    // determines the closest common ancestor of v and w
    private int[] closest(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(G, w);
        int championDistance = Integer.MAX_VALUE;
        int closestAncestor = -1; // will never be -1 because G is a DAG

        // iterate through all vertices
        for (int u = 0; u < G.V(); u++) {
            if (vBFS.hasPathTo(u) && wBFS.hasPathTo(u)) {
                int d = vBFS.distTo(u) + wBFS.distTo(u);
                if (d < championDistance) {
                    championDistance = d;
                    closestAncestor = u;
                }
            }
        }

        int[] vertices = orderedVertices(v, w);
        int[] result = { championDistance, closestAncestor };
        recentVals.put(vertices, result);
        return result;
    }


    // determines shortest distance and closest common ancestor of sets a and b
    private int[] closest(Iterable<Integer> a, Iterable<Integer> b) {
        validateSet(a);
        validateSet(b);
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(G, a);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(G, b);
        int championDistance = Integer.MAX_VALUE;
        int closestAncestor = -1; // will never be -1 because G is a DAG

        // iterate through all vertices
        for (int u = 0; u < G.V(); u++) {
            if (vBFS.hasPathTo(u) && wBFS.hasPathTo(u)) {
                int d = vBFS.distTo(u) + wBFS.distTo(u);
                if (d < championDistance) {
                    championDistance = d;
                    closestAncestor = u;
                }
            }
        }

        int[] result = { championDistance, closestAncestor };
        return result;
    }

    // returns an ordered int[] for a pair of vertices
    private int[] orderedVertices(int v, int w) {
        int[] vertices = new int[2];
        if (v < w) {
            vertices[0] = v;
            vertices[1] = w;
        }
        else {
            vertices[0] = w;
            vertices[1] = v;
        }
        return vertices;
    }

    // check if vertex is within range
    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException("invalid vertex");
    }

    // check if argument is non-null
    private void validate(Object obj) {
        if (obj == null) throw new IllegalArgumentException("null argument");
    }

    // check if set has zero vertices
    private void validateSet(Iterable<Integer> set) {
        validate(set);
        if (!set.iterator().hasNext())
            throw new IllegalArgumentException("empty set");
        for (Integer s : set) {
            if (s == null || s < 0 || s >= G.V())
                throw new IllegalArgumentException("invalid vertex");
        }
    }

    // check if digraph is a rooted DAG
    private void validateDAG(Digraph d) {
        validate(d);
        DirectedCycle dc = new DirectedCycle(d);

        // DAG must not have a cycle:
        if (dc.hasCycle())
            throw new IllegalArgumentException("invalid DAG: has a cycle");

        // DAG must have exactly one vertex of outdegree 0:
        int n = 0;

        for (int v = 0; v < d.V(); v++) {
            if (d.outdegree(v) == 0) n++;
            if (n > 1)
                throw new IllegalArgumentException("invalid DAG: unrooted");
        }

        if (n == 0) throw new IllegalArgumentException("invalid DAG: unrooted");
    }

    // unit testing of all methods
    public static void main(String[] args) {
        String file = "digraph1.txt";
        In in = new In(file);
        Digraph G = new Digraph(in);
        ShortestCommonAncestor sca = new ShortestCommonAncestor(G);
        int v = 3;
        int w = 10;
        StdOut.println(sca.length(v, w)); // 4
        StdOut.println(sca.ancestor(v, w)); // 1

        String file1 = "digraph25.txt";
        In in1 = new In(file1);
        Digraph G1 = new Digraph(in1);
        ShortestCommonAncestor sca1 = new ShortestCommonAncestor(G1);
        Bag<Integer> a = new Bag<Integer>();
        Bag<Integer> b = new Bag<Integer>();
        a.add(13);
        a.add(23);
        a.add(24);
        b.add(6);
        b.add(16);
        b.add(17);
        StdOut.println(sca1.lengthSubset(a, b)); // 4
        StdOut.println(sca1.ancestorSubset(a, b)); // 3
    }
}
