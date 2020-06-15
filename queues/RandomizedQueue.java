/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Implements a randomized queue with all standard operations.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private int n; // index of the most recent element
    private Item[] data; // contains all items

    // construct an empty randomized queue
    public RandomizedQueue() {
        n = 0;
        data = (Item[]) new Object[1]; // start with array of length 1
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Invalid argument!");
        // if the array reaches 100% capacity, double its size
        if (size() == data.length && size() >= 1) doubleSize();
        data[n++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new java.util.NoSuchElementException("Queue is empty!");
        // pick a random element, swap it with the last element, and return it
        int i = StdRandom.uniform(n);
        Item temp = data[i];
        data[i] = data[--n];
        data[n] = null; // no loitering
        // if the array reaches 25% capacity, halve its size
        if (size() == data.length / 4 && size() > 1) halveSize();
        return temp;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new java.util.NoSuchElementException("Queue is empty!");
        int i = StdRandom.uniform(n);
        return data[i]; // random index
    }

    // implements the iterator
    private class RQIterator implements Iterator<Item> {
        private int count = 0; // current node
        private final Item[] dataCopy; // copy of data

        // creates a copy of the data array (linear in number of items)
        public RQIterator() {
            int index = 0;
            // make a copy of the data array (iterators must be independent)
            dataCopy = (Item[]) new Object[size()];
            for (int i = 0; i < data.length; i++)
                // copy only non-null elements in the data array
                if (data[i] != null) dataCopy[index++] = data[i];
            StdRandom.shuffle(dataCopy);
        }

        public boolean hasNext() {
            return count < n;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported!");
        }

        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException("None left!");
            Item item = dataCopy[count++];
            return item;
        }
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RQIterator();
    }

    // doubles the size of the data array
    private void doubleSize() {
        Item[] bigger = (Item[]) new Object[data.length * 2];
        for (int i = 0; i < data.length; i++)
            bigger[i] = data[i];
        data = bigger;
    }

    // halves the size of the data array
    private void halveSize() {
        Item[] smaller = (Item[]) new Object[data.length / 2];
        for (int i = 0; i < data.length / 4; i++)
            smaller[i] = data[i];
        data = smaller;
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue1 = new RandomizedQueue<Integer>();

        StdOut.println("\nall methods test: (should print only true)");
        StdOut.println(queue1.isEmpty()); // true
        queue1.enqueue(10);
        StdOut.println(!queue1.isEmpty()); // true
        StdOut.println(queue1.size() == 1); // true
        StdOut.println(queue1.sample() == 10); // true
        StdOut.println(queue1.dequeue() == 10); // true
        StdOut.println(queue1.isEmpty()); // true
        StdOut.println(queue1.size() == 0); // true

        // the below code is copied from the assignment checklist
        int n = 5;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
            // }
        }
    }
}
