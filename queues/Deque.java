/* *****************************************************************************
 *  Name:    Nick Padmanabhan
 *  NetID:   ntyp
 *  Precept: P07
 *
 *  Description:  Implements a double-ended queue with all standard operations.
 *  All methods except the iterator methods are constant time.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {

    private Node<Item> first; // first node
    private Node<Item> last; // last node
    private int size; // number of nodes

    private static class Node<Item> {
        private Node<Item> next; // next node
        private Node<Item> previous; // previous node
        private Item item; // node's item
    }

    // construct an empty deque
    public Deque() {
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Invalid argument!");
        if (size() == 0) {
            first = new Node<Item>();
            first.item = item;
            last = first; // last and first nodes now the same
        }
        else if (size() == 1) {
            last = first; // the old first node becomes the last node
            first = new Node<Item>();
            first.item = item;
            first.next = last;
            last.previous = first;
        }
        else {
            Node<Item> temp = first; // store the old first node
            first = new Node<Item>();
            first.item = item;
            first.next = temp;
            temp.previous = first;
        }
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Invalid argument!");
        if (size() == 0) {
            last = new Node<Item>();
            last.item = item;
            first = last; // last and first nodes now the same
        }
        else if (size() == 1) {
            first = last; // the old last node becomes the first node
            last = new Node<Item>();
            last.item = item;
            last.previous = first;
            first.next = last;
        }
        else {
            Node<Item> temp = last; // store the old last node
            last = new Node<Item>();
            last.item = item;
            last.previous = temp;
            temp.next = last;
        }
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (size() == 0) throw new java.util.NoSuchElementException("Queue empty!");
        if (size() == 1) {
            Item temp = first.item;
            first = null; // no loitering
            last = null; // no loitering
            size--;
            return temp;
        }
        else if (size() == 2) {
            Item temp = first.item;
            first = last; // last and first nodes now the same
            first.previous = null; // no loitering
            size--;
            return temp;
        }
        Item temp = first.item;
        first = first.next;
        first.previous = null; // no loitering
        size--;
        return temp;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (size() == 0) throw new java.util.NoSuchElementException("Queue empty!");
        if (size() == 1) {
            Item temp = last.item;
            last = null; // no loitering
            first = null; // no loitering
            size--;
            return temp;
        }
        else if (size() == 2) {
            Item temp = last.item;
            last = first; // last and first nodes now the same
            last.next = null; // no loitering
            size--;
            return temp;
        }
        Item temp = last.item;
        last = last.previous;
        last.next = null; // no loitering
        size--;
        return temp;
    }

    // implements the iterator
    private class DequeIterator implements Iterator<Item> {
        private Node<Item> current = first; // current node

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported!");
        }

        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException("None left!");
            // grab the current node's item and move to the next node
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> dq = new Deque<Integer>();

        StdOut.println("\nall methods test: (should print only true)");
        StdOut.println(dq.isEmpty());
        StdOut.println(dq.size() == 0);
        dq.addFirst(100);
        dq.addFirst(101);
        dq.addLast(99);
        StdOut.println(dq.removeFirst() == 101);
        dq.addFirst(102);
        dq.addLast(98);
        dq.addFirst(103);
        StdOut.println(dq.removeLast() == 98);
        StdOut.println(dq.removeFirst() == 103);
        StdOut.println(dq.removeFirst() == 102);
        StdOut.println(dq.removeFirst() == 100);
        StdOut.println(dq.removeFirst() == 99);
        StdOut.println(dq.size() == 0);
        StdOut.println(dq.isEmpty()); // true
        // StdOut.println(dq.removeFirst()); // exception

        StdOut.println("\niterator test:");
        for (int i : dq)
            StdOut.print(i + " "); // nothing
        dq.addFirst(100);
        for (int i : dq)
            StdOut.print(i + " "); // 100
        StdOut.println();
        dq.addFirst(101);
        dq.addLast(99);
        for (int i : dq)
            StdOut.print(i + " "); // 101 100 99
        StdOut.println();
        dq.removeFirst();
        dq.removeFirst();
        for (int i : dq)
            StdOut.print(i + " "); // 99
        dq.removeLast();
        for (int i : dq)
            StdOut.print(i + " "); // nothing

        StdOut.println();
    }

}
