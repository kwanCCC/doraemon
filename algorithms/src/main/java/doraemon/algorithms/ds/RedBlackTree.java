package doraemon.algorithms.ds;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RedBlackTree<K extends Comparable, V> {

    private RBNode root;

    public void put(K key, V value) {
        if (Objects.isNull(root)) {
            root = new RBNode(key, value);
        } else {
            putRecursive(root, key, value);
        }
    }

    public RBNode putRecursive(RBNode x, K key, V value) {
        if (Objects.isNull(root)) {
            return new RBNode(key, value);
        }
        if (x.key.compareTo(key) > 0) {
            x.left = putRecursive(root.left, key, value);
        } else if (x.key.compareTo(key) < 0) {
            x.right = putRecursive(root.right, key, value);
        } else {
            x.value = value;
        }
        return x;
    }

    @Override
    public String toString() {
        return "RedBlackTree{" +
               "root=" + (Objects.nonNull(root) ? root.toString() : "") +
               '}';
    }

    @Data
    class RBNode {
        K      key;
        V      value;
        RBNode parent, left, right;
        boolean isBlack;

        public RBNode(K key, V value) {
            this.key = key;
            this.value = value;
            isBlack = true;
        }

        public RBNode(K key, V value, boolean isBlack) {
            this.key = key;
            this.value = value;
            this.isBlack = isBlack;
        }

        @Override
        public String toString() {
            return "RBNode{" + "color=" + (isBlack ? "black" : "red") +
                   "key=" + key +
                   ", value=" + value +
                   ", left={\n\t" + left.toString() +
                   "\n\t}, right={\n\t" + right.toString() +
                   "\n\t}}";
        }
    }

    public static void main(String[] args) {
        RedBlackTree<Integer, Integer> tree = new RedBlackTree<>();
        ThreadLocalRandom current = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            int key = current.nextInt(100);
            tree.put(key, key);
        }
        System.out.println(tree.toString());
    }
}

