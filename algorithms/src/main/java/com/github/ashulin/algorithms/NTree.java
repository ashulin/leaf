package com.github.ashulin.algorithms;

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class NTree {
    public static class Node {
        public int val;
        public List<Node> children;

        public Node() {}

        public Node(int val) {
            this.val = val;
        }

        public Node(int val, List<Node> children) {
            this.val = val;
            this.children = children;
        }
    }

    @Source(429)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<List<Integer>> levelOrder(Node root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<List<Integer>> ans = new ArrayList<>();
        Deque<Node> deque = new LinkedList<>();
        deque.offer(root);
        while (!deque.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            for (int i = deque.size(); i > 0; --i) {
                Node node = deque.pop();
                level.add(node.val);
                if (node.children != null) {
                    deque.addAll(node.children);
                }
            }
            ans.add(level);
        }
        return ans;
    }

    @Source(429)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<List<Integer>> levelOrder2(Node root) {
        List<List<Integer>> ans = new ArrayList<>();
        bfs(root, 1, ans);
        return ans;
    }

    public void bfs(Node root, int deep, List<List<Integer>> result) {
        if (root == null) {
            return;
        }
        if (result.size() < deep) {
            result.add(new ArrayList<>());
        }
        result.get(deep - 1).add(root.val);
        deep++;
        if (root.children == null) {
            return;
        }
        for (Node child : root.children) {
            bfs(child, deep, result);
        }
    }
}
