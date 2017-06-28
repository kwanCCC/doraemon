package doraemon.algorithms.ds;

import lombok.Data;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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
        String s = "";
        s = Arrays.stream(s.split("\\s+")).collect(Collectors.joining(" "));
        return x;
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


    }
}

