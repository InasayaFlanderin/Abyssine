package org.inasayaflanderin.abyssine.miscellaneous;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@EqualsAndHashCode @ToString
public class BinarySearchTree<D> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4324646208402441958L;

    @EqualsAndHashCode @ToString
    class Node implements Serializable {
        @Serial
        private static final long serialVersionUID = 9004112549029617584L;

        D data;
        Node left, right;

        Node(D data) {
            this.data = data;
            this.left = this.right = null;
        }
    }

    Node root;
    Comparator<D> comparator;

    public BinarySearchTree(Comparator<D> comparator) {
        this.root = null;
        this.comparator = comparator;
    }

    public void insert(D data) {
        this.root = insert(this.root, data);
    }

    public void inorderTransverse(List<D> storage) {
        inorderTransverse(this.root, storage, new int[]{0});
    }

    public void inorderTransverse(D[] storage) {
        inorderTransverse(this.root, storage, new int[]{0});
    }

    private Node insert(Node root, D data) {
        if (root == null) {
            root = new Node(data);
            return root;
        }

        if (comparator.compare(data, root.data) < 0) {
            root.left = insert(root.left, data);
        } else {
            root.right = insert(root.right, data);
        }

        return root;
    }

    private void inorderTransverse(Node root, List<D> storage, int[] index) {
        if (root != null) {
            inorderTransverse(root.left, storage, index);
            storage.set(index[0]++, root.data);
            inorderTransverse(root.right, storage, index);
        }
    }

    private void inorderTransverse(Node root, D[] storage, int[] index) {
        if (root != null) {
            inorderTransverse(root.left, storage, index);
            storage[index[0]++] = root.data;
            inorderTransverse(root.right, storage, index);
        }
    }
}