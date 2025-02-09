package org.inasayaflanderin.abyssine.miscellaneous;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CartesianTree<D> implements Serializable {
    @Serial
    private static final long serialVersionUID = 790584683554621166L;

    class Node implements Serializable {
        @Serial
        private static final long serialVersionUID = 6088533905243685543L;

        D key;
        Node left, right;

        Node(D key) {
            this.key = key;
            this.left = this.right = null;
        }
    }

    class Pair implements Serializable, Comparable<Pair> {
        @Serial
        private static final long serialVersionUID = -4273213932133105939L;

        D first;
        Node second;

        Pair(D first, Node second) {
            this.first = first;
            this.second = second;
        }

        public int compareTo(Pair o) {
            return comparator.compare(first, o.first);
        }
    }

    private Node root;
    private Comparator<D> comparator;

    @SuppressWarnings("unchecked")
    public CartesianTree(List<D> data, Comparator<D> comparator) {
        this.comparator = comparator;

        int[] rootArray =  new int[data.size()];
        int[] leftArray = new int[data.size()];
        int[] rightArray = new int[data.size()];

        Arrays.fill(rootArray, -1);
        Arrays.fill(leftArray, -1);
        Arrays.fill(rightArray, -1);

        var rootIndex = 0;
        int last;

        for(int i = 1; i < data.size(); i++) {
            last = i - 1;
            rightArray[i] = -1;

            while(this.comparator.compare(data.get(last), data.get(i)) >= 0 && last != rootIndex) last = rootArray[last];

            if(this.comparator.compare(data.get(last), data.get(i)) >= 0) {
                rootArray[rootIndex] = i;
                leftArray[i] = rootIndex;
                rootIndex = i;
            } else if(rightArray[last] == -1) {
                rightArray[last] = i;
                rootArray[i] = last;
                leftArray[i] = -1;
            } else {
                rootArray[rightArray[last]] = i;
                leftArray[i] = rightArray[last];
                rightArray[last] = i;
                rootArray[i] = last;
            }
        }

        rootArray[rootIndex] = -1;

        this.root = buildTree(data, leftArray, rightArray, rootIndex);
    }

    public void pQBasedTraversal(List<D> data) {
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(this.root.key, this.root));
        var i = 0;

        while(!pq.isEmpty()) {
            Pair p = pq.poll();
            data.set(i++, p.first);

            if(p.second.left != null) pq.add(new Pair(p.second.left.key, p.second.left));
            if(p.second.right != null) pq.add(new Pair(p.second.right.key, p.second.right));
        }
    }

    private Node buildTree(List<D> data, int[] leftArray, int[] rightArray, int rootIndex) {
        if(rootIndex == -1) return null;

        Node node = new Node(data.get(rootIndex));
        node.left = buildTree(data, leftArray, rightArray, leftArray[rootIndex]);
        node.right = buildTree(data, leftArray, rightArray, rightArray[rootIndex]);

        return node;
    }
}
