/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ashulin.algorithms;

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;
import org.w3c.dom.Node;

import javax.swing.tree.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Tag(Type.BINARY_TREE)
public class BinaryTree {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {}

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {}

        public Node(int val) {
            this.val = val;
        }

        public Node(int val, Node left, Node right, Node next) {
            this.val = val;
            this.left = left;
            this.right = right;
            this.next = next;
        }
    }

    /** 给定一个二叉树的根节点 root ，返回它的中序遍历。 */
    @Source(94)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        inorderTraversal(root.left, result);
        result.add(root.val);
        inorderTraversal(root.right, result);
    }

    @Source(94)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> inorderTraversal2(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        List<Integer> result = new ArrayList<>();
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            result.add(root.val);
            root = root.right;
        }
        return result;
    }

    @Source(94)
    @Complexity(time = "O(n)", space = "O(1)")
    public List<Integer> inorderTraversal3(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                ans.add(cur.val);
                cur = cur.right;
            } else {
                // 查找cur节点左子树的最右节点
                TreeNode mr = cur.left;
                while (mr.right != null
                        &&
                        // 实际已遍历过
                        mr.right != cur) {
                    mr = mr.right;
                }

                if (mr.right == null) {
                    // 第一次遍历，将cur的左子树的最右节点的right指向cur，达到前序遍历的 左中右
                    mr.right = cur;
                    // 前序遍历，左优先
                    cur = cur.left;
                } else {
                    // 第二次遍历到cur的左子树的最右节点，即cur左子树已遍历完成
                    mr.right = null;
                    ans.add(cur.val);
                    // 左中遍历完成，遍历右
                    cur = cur.right;
                }
            }
        }
        return ans;
    }

    /** 给定一个二叉树的根节点 root ，返回它的前序遍历。 */
    @Source(144)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorderTraversal(root, result);
        return result;
    }

    private void preorderTraversal(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        result.add(root.val);
        preorderTraversal(root.left, result);
        preorderTraversal(root.right, result);
    }

    @Source(144)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> preorderTraversal2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                result.add(root.val);
                stack.push(root);
                root = root.left;
            }
            root = stack.pop().right;
        }
        return result;
    }

    @Source(144)
    @Complexity(time = "O(n)", space = "O(1)")
    public List<Integer> preorderTraversal3(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                ans.add(cur.val);
                cur = cur.right;
            } else {
                TreeNode pre = cur.left;
                while (pre.right != null && pre.right != cur) {
                    pre = pre.right;
                }

                if (pre.right == null) {
                    // 第一次遍历cur的左子树的最右节点
                    pre.right = cur;
                    ans.add(cur.val);
                    cur = cur.left;
                } else {
                    pre.right = null;
                    cur = cur.right;
                }
            }
        }
        return ans;
    }

    /** 给定一个二叉树的根节点 root ，返回它的后序遍历。 */
    @Source(145)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderTraversal(root, result);
        return result;
    }

    private void postorderTraversal(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        postorderTraversal(root.left, result);
        postorderTraversal(root.right, result);
        result.add(root.val);
    }

    @Source(145)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> postorderTraversal2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode pre = null;
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            if (root.right == null
                    // 右子树已处理
                    || root.right == pre) {
                result.add(root.val);
                // 标记已处理的右子树
                pre = root;
                root = null;
            } else {
                stack.push(root);
                root = root.right;
            }
        }
        return result;
    }

    @Source(145)
    @Complexity(time = "O(n)", space = "O(1)")
    public List<Integer> postorderTraversal3(TreeNode root) {
        List<Integer> res = new ArrayList<Integer>();
        if (root == null) {
            return res;
        }

        TreeNode p1 = root, p2 = null;

        while (p1 != null) {
            p2 = p1.left;
            if (p2 != null) {
                while (p2.right != null && p2.right != p1) {
                    p2 = p2.right;
                }
                if (p2.right == null) {
                    // 将中序遍历下的前驱节点指向中
                    p2.right = p1;
                    p1 = p1.left;
                    continue;
                } else {
                    // 断开前面添加的链接
                    p2.right = null;
                    // 倒序输出从当前节点的左子节点到该前驱节点这条路径上的所有节点
                    addPath(res, p1.left);
                }
            }
            // 到最右节点后跳出
            p1 = p1.right;
        }
        // 输出root的最右段
        addPath(res, root);
        return res;
    }

    public void addPath(List<Integer> res, TreeNode node) {
        int count = 0;
        while (node != null) {
            ++count;
            res.add(node.val);
            node = node.right;
        }
        int left = res.size() - count, right = res.size() - 1;
        while (left < right) {
            int temp = res.get(left);
            res.set(left, res.get(right));
            res.set(right, temp);
            left++;
            right--;
        }
    }

    /** 给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。 */
    @Source(102)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<List<Integer>> levelOrder(TreeNode root) {
        Deque<TreeNode> deque = new LinkedList<>();
        if (root == null) {
            return Collections.emptyList();
        }
        deque.offer(root);
        List<List<Integer>> ans = new ArrayList<>();
        while (!deque.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            for (int size = deque.size(); size > 0; size--) {
                TreeNode node = deque.pop();
                level.add(node.val);
                if (node.left != null) {
                    deque.offer(node.left);
                }
                if (node.right != null) {
                    deque.offer(node.right);
                }
            }
            ans.add(level);
        }
        return ans;
    }

    @Source(102)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<List<Integer>> levelOrder2(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        bfs(root, 1, ans);
        return ans;
    }

    public void bfs(TreeNode root, int deep, List<List<Integer>> result) {
        if (root == null) {
            return;
        }
        if (result.size() < deep) {
            List<Integer> level = new ArrayList<>();
            result.add(level);
        }
        result.get(deep - 1).add(root.val);
        deep++;
        bfs(root.left, deep, result);
        bfs(root.right, deep, result);
    }

    /** 给定一个二叉树，返回其节点值自底向上的层序遍历。 （即按从叶子节点所在层到根节点所在的层，逐层从左向右遍历）. */
    @Source(107)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        Deque<TreeNode> deque = new LinkedList<>();
        if (root == null) {
            return Collections.emptyList();
        }
        deque.offer(root);
        List<List<Integer>> ans = new ArrayList<>();
        while (!deque.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            for (int size = deque.size(); size > 0; size--) {
                TreeNode node = deque.pop();
                level.add(node.val);
                if (node.left != null) {
                    deque.offer(node.left);
                }
                if (node.right != null) {
                    deque.offer(node.right);
                }
            }
            ans.add(0, level);
        }
        return ans;
    }

    @Source(107)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<List<Integer>> levelOrderBottom2(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        bfs2(root, 1, ans);
        return ans;
    }

    public void bfs2(TreeNode root, int deep, List<List<Integer>> result) {
        if (root == null) {
            return;
        }
        if (result.size() < deep) {
            List<Integer> level = new ArrayList<>();
            result.add(0, level);
        }
        result.get(result.size() - deep).add(root.val);
        deep++;
        bfs2(root.left, deep, result);
        bfs2(root.right, deep, result);
    }

    /** 给定一个二叉树的 根节点 root，想象自己站在它的右侧，按照从顶部到底部的顺序，返回从右侧所能看到的节点值。 */
    @Source(199)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> rightSideView(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        Deque<TreeNode> deque = new LinkedList<>();
        deque.offer(root);
        List<Integer> ans = new ArrayList<>();
        while (!deque.isEmpty()) {
            int size = deque.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = deque.pop();
                if (i == 0) {
                    ans.add(node.val);
                }
                if (node.right != null) {
                    deque.offer(node.right);
                }
                if (node.left != null) {
                    deque.offer(node.left);
                }
            }
        }
        return ans;
    }

    @Source(199)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> rightSideView2(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        rightSideView(root, 1, list);
        return list;
    }

    public void rightSideView(TreeNode root, int deep, List<Integer> result) {
        if (root == null) {
            return;
        }
        if (result.size() < deep) {
            result.add(root.val);
        }
        deep++;
        rightSideView(root.right, deep, result);
        rightSideView(root.left, deep, result);
    }

    /** 给定一个非空二叉树, 返回一个由每层节点平均值组成的数组。 */
    @Source(637)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Double> averageOfLevels(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }

        Deque<TreeNode> deque = new LinkedList<>();
        List<Double> ans = new ArrayList<>();
        deque.offer(root);
        while (!deque.isEmpty()) {
            double sum = 0;
            int size = deque.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = deque.pop();
                sum += node.val;
                if (node.left != null) {
                    deque.offer(node.left);
                }
                if (node.right != null) {
                    deque.offer(node.right);
                }
            }
            ans.add(sum / size);
        }
        return ans;
    }

    @Source(515)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> largestValues(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> ans = new ArrayList<>();
        Deque<TreeNode> deque = new LinkedList<>();
        deque.offer(root);
        while (!deque.isEmpty()) {
            int max = Integer.MIN_VALUE;
            for (int i = deque.size(); i > 0; --i) {
                TreeNode node = deque.pop();
                max = Math.max(max, node.val);
                if (node.left != null) {
                    deque.offer(node.left);
                }
                if (node.right != null) {
                    deque.offer(node.right);
                }
            }
            ans.add(max);
        }
        return ans;
    }

    @Source(515)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public List<Integer> largestValues2(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        largestValues(root, 1, ans);
        return ans;
    }

    public void largestValues(TreeNode root, int deep, List<Integer> result) {
        if (root == null) {
            return;
        }
        if (result.size() < deep) {
            result.add(root.val);
        } else if (root.val > result.get(deep - 1)) {
            result.set(deep - 1, root.val);
        }
        deep++;
        largestValues(root.left, deep, result);
        largestValues(root.right, deep, result);
    }

    /**
     * 给定一个完美二叉树，其所有叶子节点都在同一层，每个父节点都有两个子节点。 填充它的每个 next 指针，让这个指针指向其下一个右侧节点。如果找不到下一个右侧节点，则将 next
     * 指针设置为 NULL。
     *
     * <p>初始状态下，所有next 指针都被设置为 NULL。
     */
    @Source(116)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public Node connect(Node root) {
        if (root == null) {
            return null;
        }
        Deque<Node> deque = new LinkedList<>();
        deque.offer(root);
        while (!deque.isEmpty()) {
            for (int i = deque.size(); i > 0; --i) {
                Node cur = deque.pop();
                if (i > 1) {
                    cur.next = deque.peek();
                }
                if (cur.left != null) {
                    deque.offer(cur.left);
                }
                if (cur.right != null) {
                    deque.offer(cur.right);
                }
            }
        }
        return root;
    }

    @Source(116)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(n)")
    public Node connect2(Node root) {
        connect(root, 1, 0, new Node[(1 << deep(root)) - 1]);
        return root;
    }

    private int deep(Node node) {
        int deep = 0;
        while (node != null) {
            deep++;
            node = node.left;
        }
        return deep;
    }

    /** 由于是完美二叉树可以使用数组做存储. */
    private void connect(Node cur, int deep, int index, Node[] nodes) {
        if (cur == null) {
            return;
        }
        int max = (1 << deep) - 1;
        nodes[index] = cur;
        if (index < max - 1) {
            cur.next = nodes[index + 1];
        }
        deep++;
        connect(cur.right, deep, 2 * index + 2, nodes);
        connect(cur.left, deep, 2 * index + 1, nodes);
    }

    @Source(116)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public Node connect3(Node root) {
        if (root == null || root.right == null) {
            return root;
        }

        root.left.next = root.right;
        if (root.next != null) {
            root.right.next = root.next.left;
        }
        connect3(root.right);
        connect3(root.left);
        return root;
    }

    @Source(116)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(1)")
    public Node connect4(Node root) {
        if (root == null) {
            return root;
        }
        Node left = root;
        // 只有一层或最后一层时不需要处理
        while (left.left != null) {
            Node cur = left;
            // 层级处理
            while (cur != null) {
                cur.left.next = cur.right;
                // == null时即已到最右
                if (cur.next != null) {
                    cur.right.next = cur.next.left;
                }
                // 当前层级向右处理
                cur = cur.next;
            }
            // 处理下一层级
            left = left.left;
        }
        return root;
    }

    /**
     * 给定一个普通二叉树，填充它的每个 next 指针，让这个指针指向其下一个右侧节点。如果找不到下一个右侧节点，则将 next 指针设置为 NULL。
     *
     * <p>初始状态下，所有next 指针都被设置为 NULL。
     *
     * <p>进阶：
     *
     * <p>你只能使用常量级额外空间。 使用递归解题也符合要求，本题中递归程序占用的栈空间不算做额外的空间复杂度。
     */
    @Source(117)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(1)")
    public Node connect5(Node root) {
        Node mostLeft = root;
        // 建立虚拟头部，不确定每层的最左节点（头节点）时方便处理，其next即为该层最左
        Node dummyHead = new Node(0);
        // 找不到新的层级时退出
        while (mostLeft != null) {
            // 每层由左至右处理
            Node cur = mostLeft;
            // 标记下一层中等待连接的节点
            Node pending = dummyHead;
            // 最右时开始下一层
            while (cur != null) {
                // 连接
                if (cur.left != null) {
                    pending.next = cur.left;
                    pending = cur.left;
                }
                if (cur.right != null) {
                    pending.next = cur.right;
                    pending = cur.right;
                }
                // 向右遍历
                cur = cur.next;
            }
            mostLeft = dummyHead.next;
            dummyHead.next = null;
        }
        return root;
    }

    /**
     * 给定一个二叉树，找出其最大深度。
     *
     * <p>二叉树的深度为根节点到最远叶子节点的最长路径上的节点数。
     */
    @Source(104)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(h)")
    public int maxDepth(TreeNode root) {
        return root == null ? 0 : Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    /**
     * 给定一个二叉树，找出其最小深度。
     *
     * <p>最小深度是从根节点到最近叶子节点的最短路径上的节点数量。
     */
    @Source(111)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(h)")
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = minDepth(root.left);
        int right = minDepth(root.right);
        return (left == 0 || right == 0)
                // 如果左子树或右子树的深度不为 0，即存在一个子树，那么当前子树的最小深度就是该子树的深度+1
                ? left + right + 1
                // 如果左子树和右子树的深度都不为 0，即左右子树都存在，那么当前子树的最小深度就是它们较小值+1
                : Math.min(left, right) + 1;
    }

    /** 翻转一棵二叉树。 */
    @Source(226)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(h)")
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return root;
        }
        TreeNode left = invertTree(root.left);
        TreeNode right = invertTree(root.right);
        root.right = left;
        root.left = right;
        return root;
    }

    /** 给定一个二叉树，检查它是否是镜像对称的。 */
    @Source(101)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(h)")
    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isSymmetric(root.left, root.right);
    }

    protected boolean isSymmetric(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left != null && right != null && left.val == right.val) {
            return isSymmetric(left.left, right.right) && isSymmetric(left.right, right.left);
        } else {
            return false;
        }
    }

    @Source(101)
    @Tag(Type.ITERATION)
    @Complexity(time = "O(n)", space = "O(n)")
    public boolean isSymmetric2(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isSymmetric2(root.left, root.right);
    }

    protected boolean isSymmetric2(TreeNode left, TreeNode right) {
        Deque<TreeNode> deque = new LinkedList<>();
        deque.offer(left);
        deque.offer(right);
        while (!deque.isEmpty()) {
            left = deque.pop();
            right = deque.pop();
            if (left == null && right == null) {
                continue;
            }
            if ((left == null || right == null) || left.val != right.val) {
                return false;
            }
            deque.offer(left.left);
            deque.offer(right.right);
            deque.offer(left.right);
            deque.offer(right.left);
        }
        return true;
    }

    @Source(222)
    @Tag(Type.ITERATION)
    @Complexity(time = "O((logn)^2)", space = "O(1)")
    public int countNodes(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = leftDepth(root.left);
        int right = rightDepth(root.right);
        // 完美二叉树
        if (left == right) {
            return (1 << (left + 1)) - 1;
        }
        int count = 0;
        while (root != null) {
            right = leftDepth(root.right);
            if (left == right) {
                // 右子树的最左深度与左子树相同，即最左叶子节点在右子树中，即左子树为完美二叉树
                // 左子树节点数：(1 << left) - 1 + 根节点
                count += (1 << left);
                root = root.right;
            } else {
                count += (1 << right);
                root = root.left;
            }
            left--;
        }
        return count;
    }

    private int leftDepth(TreeNode root) {
        int depth = 0;
        while (root != null) {
            depth++;
            root = root.left;
        }
        return depth;
    }

    private int rightDepth(TreeNode root) {
        int depth = 0;
        while (root != null) {
            depth++;
            root = root.right;
        }
        return depth;
    }

    /**
     * 给定一个二叉树，判断它是否是高度平衡的二叉树。
     *
     * <p>本题中，一棵高度平衡二叉树定义为：
     *
     * <p>一个二叉树每个节点 的左右两个子树的高度差的绝对值不超过 1 。
     */
    @Source(110)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public boolean isBalanced(TreeNode root) {
        return depth(root) != -1;
    }

    private int depth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftDepth = depth(root.left);
        if (leftDepth == -1) {
            return -1;
        }
        int rightDepth = depth(root.right);
        if (rightDepth == -1) {
            return -1;
        }
        return Math.abs(leftDepth - rightDepth) > 1 ? -1 : Math.max(leftDepth, rightDepth) + 1;
    }

    /**
     * 给你一个二叉树的根节点 root ，按 任意顺序 ，返回所有从根节点到叶子节点的路径。
     *
     * <p>叶子节点 是指没有子节点的节点。
     */
    @Source(257)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public List<String> binaryTreePaths(TreeNode root) {
        if (root == null) {
            return Collections.emptyList();
        }
        if (root.left == null && root.right == null) {
            return Collections.singletonList(String.valueOf(root.val));
        }
        List<String> paths = new ArrayList<>();
        if (root.left != null) {
            for (String path : binaryTreePaths(root.left)) {
                paths.add(root.val + "->" + path);
            }
        }
        if (root.right != null) {
            for (String path : binaryTreePaths(root.right)) {
                paths.add(root.val + "->" + path);
            }
        }
        return paths;
    }

    @Source(257)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public List<String> binaryTreePaths2(TreeNode root) {
        Deque<String> paths = new LinkedList<>();
        List<String> result = new ArrayList<>();
        dfs(root, paths, result);
        return result;
    }

    public void dfs(TreeNode root, Deque<String> paths, List<String> result) {
        if (root == null) {
            return;
        }

        paths.offer(String.valueOf(root.val));
        dfs(root.left, paths, result);
        dfs(root.right, paths, result);
        if (root.left == null && root.right == null) {
            result.add(String.join("->", paths));
        }
        // 回溯
        paths.pollLast();
    }

    /**
     * 给你两棵二叉树的根节点 p 和 q ，编写一个函数来检验这两棵树是否相同。
     *
     * <p>如果两个树在结构上相同，并且节点具有相同的值，则认为它们是相同的。
     */
    @Source(100)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if ((p == null || q == null) || p.val != q.val) {
            return false;
        }
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    /** 计算给定二叉树的所有左叶子之和。 */
    @Source(404)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public int sumOfLeftLeaves(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int ans = 0;
        if (root.left != null && root.left.left == null && root.left.right == null) {
            ans = root.left.val;
        }
        return ans + sumOfLeftLeaves(root.left) + sumOfLeftLeaves(root.right);
    }

    @Source(513)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(1)")
    public int findBottomLeftValue(TreeNode root) {
        int[] ans = new int[] {0, 0};
        findBottomLeftValue(root, ans, 1);
        return ans[1];
    }

    public void findBottomLeftValue(TreeNode root, int[] ans, int depth) {
        if (root == null) {
            return;
        }
        if (ans[0] < depth) {
            ans[0] = depth;
            ans[1] = root.val;
        }
        depth++;
        findBottomLeftValue(root.left, ans, depth);
        findBottomLeftValue(root.right, ans, depth);
    }

    /**
     * 给你二叉树的根节点root 和一个表示目标和的整数targetSum 。判断该树中是否存在 根节点到叶子节点 的路径，这条路径上所有节点值相加等于目标和targetSum
     * 。如果存在，返回 true ；否则，返回 false 。
     */
    @Source(112)
    @Tag(Type.RECURSIVE)
    @Complexity(time = "O(n)", space = "O(h)")
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }
        targetSum -= root.val;
        if (root.left == null && root.right == null) {
            return 0 == targetSum;
        }
        return hasPathSum(root.left, targetSum) || hasPathSum(root.right, targetSum);
    }

    /** 给定一棵树的前序遍历 preorder 与中序遍历 inorder。请构造二叉树并返回其根节点。 */
    @Source(105)
    @Tag(Type.SIM)
    @Tag(Type.RECURSIVE)
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> map = new HashMap<>();
        int i = 0;
        for (int in : inorder) {
            map.put(in, i++);
        }
        return buildTreeByPre(preorder, 0, 0, preorder.length, map);
    }

    private TreeNode buildTreeByPre(
            int[] preorder, int inStart, int preStart, final int len, Map<Integer, Integer> map) {
        if (len < 1) {
            return null;
        }
        int rootNum = preorder[preStart];
        TreeNode root = new TreeNode(rootNum);
        if (len == 1) {
            return root;
        }

        int index = map.get(rootNum);
        root.left = buildTreeByPre(preorder, inStart, preStart + 1, index - inStart, map);
        root.right =
                buildTreeByPre(
                        preorder,
                        index + 1,
                        preStart + 1 + index - inStart,
                        len - index + inStart - 1,
                        map);
        return root;
    }

    /**
     * 根据一棵树的中序遍历与后序遍历构造二叉树。
     *
     * <p>注意: 你可以假设树中没有重复的元素。
     */
    @Source(106)
    @Tag(Type.SIM)
    @Tag(Type.RECURSIVE)
    public TreeNode buildTree2(int[] inorder, int[] postorder) {
        Map<Integer, Integer> map = new HashMap<>();
        int i = 0;
        for (int in : inorder) {
            map.put(in, i++);
        }
        return buildTreeByPost(postorder, 0, 0, postorder.length, map);
    }

    private TreeNode buildTreeByPost(
            int[] postorder, int inStart, int postStart, final int len, Map<Integer, Integer> map) {
        if (len < 1) {
            return null;
        }
        int rootNum = postorder[postStart + len - 1];
        TreeNode root = new TreeNode(rootNum);
        if (len == 1) {
            return root;
        }
        int index = map.get(rootNum);

        root.left = buildTreeByPost(postorder, inStart, postStart, index - inStart, map);
        root.right =
                buildTreeByPost(
                        postorder,
                        index + 1,
                        postStart + index - inStart,
                        inStart + len - 1 - index,
                        map);

        return root;
    }

    /**
     * 给定一个不含重复元素的整数数组 nums 。
     *
     * <p>一个以此数组直接递归构建的 最大二叉树 定义如下：
     *
     * <p>二叉树的根是数组 nums 中的最大元素。 左子树是通过数组中 最大值左边部分 递归构造出的最大二叉树。 右子树是通过数组中 最大值右边部分 递归构造出的最大二叉树。
     * 返回有给定数组 nums 构建的 最大二叉树 。
     */
    @Source(654)
    @Tag(Type.SIM)
    @Tag(Type.RECURSIVE)
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return constructMaximumBinaryTree(nums, 0, nums.length - 1);
    }

    private TreeNode constructMaximumBinaryTree(int[] nums, int start, int end) {
        if (start > end) {
            return null;
        }
        int maxIndex = getMaxNumIndex(nums, start, end);
        TreeNode node = new TreeNode(nums[maxIndex]);
        node.left = constructMaximumBinaryTree(nums, start, maxIndex - 1);
        node.right = constructMaximumBinaryTree(nums, maxIndex + 1, end);
        return node;
    }

    private int getMaxNumIndex(int[] nums, int start, int end) {
        int maxNumIndex = start;
        for (int i = start; i <= end; i++) {
            if (nums[i] > nums[maxNumIndex]) {
                maxNumIndex = i;
            }
        }
        return maxNumIndex;
    }

    /**
     * 给定两个二叉树，想象当你将它们中的一个覆盖到另一个上时，两个二叉树的一些节点便会重叠。
     *
     * <p>你需要将他们合并为一个新的二叉树。合并的规则是如果两个节点重叠，那么将他们的值相加作为节点合并后的新值，否则不为 NULL 的节点将直接作为新二叉树的节点。
     */
    @Source(617)
    @Tag(Type.RECURSIVE)
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return root2;
        } else if (root2 == null) {
            return root1;
        }
        root1.val += root2.val;
        root1.left = mergeTrees(root1.left, root2.left);
        root1.right = mergeTrees(root1.right, root2.right);
        return root1;
    }

    /** 给定二叉搜索树（BST）的根节点和一个值。 你需要在BST中找到节点值等于给定值的节点。 返回以该节点为根的子树。 如果节点不存在，则返回 NULL。 */
    @Source(700)
    @Tag(Type.RECURSIVE)
    public TreeNode searchBST(TreeNode root, int val) {
        if (root == null || root.val == val) {
            return root;
        } else if (val < root.val) {
            return searchBST(root.left, val);
        } else {
            return searchBST(root.right, val);
        }
    }

    /**
     * 给你一个二叉树的根节点 root ，判断其是否是一个有效的二叉搜索树。
     *
     * <p>有效 二叉搜索树定义如下：
     *
     * <p>节点的左子树只包含 小于 当前节点的数。 节点的右子树只包含 大于 当前节点的数。 所有左子树和右子树自身必须也是二叉搜索树。
     *
     * <p>即在中序遍历中呈现为递增，即使用中序遍历解答
     */
    @Source(98)
    @Tag(Type.RECURSIVE)
    public boolean isValidBST(TreeNode root) {
        return inOrder(root, new AtomicLong(Long.MIN_VALUE));
    }

    public boolean inOrder(TreeNode root, AtomicLong pre) {
        if (root == null) {
            return true;
        }
        boolean l = inOrder(root.left, pre);
        if (pre.get() >= root.val) {
            return false;
        } else {
            pre.set(root.val);
        }
        boolean r = inOrder(root.right, pre);
        return l && r;
    }

    @Source(98)
    @Tag(Type.ITERATION)
    public boolean isValidBST2(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        long pre = Long.MIN_VALUE;
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            if (pre >= root.val) {
                return false;
            }
            pre = root.val;
            root = root.right;
        }
        return true;
    }

    /**
     * 给你一个二叉搜索树的根节点 root ，返回 树中任意两不同节点值之间的最小差值 。
     *
     * <p>差值是一个正数，其数值等于两值之差的绝对值。
     */
    @Source(530)
    @Tag(Type.ITERATION)
    public int getMinimumDifference(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        long min = Long.MAX_VALUE;
        int pre = Integer.MIN_VALUE;
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            min = Math.min(min, ((long) root.val) - pre);
            pre = root.val;
            root = root.right;
        }
        return (int) min;
    }

    @Source(530)
    @Tag(Type.RECURSIVE)
    public int getMinimumDifference2(TreeNode root) {
        AtomicReference<Integer> ans = new AtomicReference<>(Integer.MAX_VALUE);
        getMinimumDifference(root, new AtomicReference<>(), ans);
        return ans.get();
    }

    public void getMinimumDifference(
            TreeNode root, AtomicReference<Integer> pre, AtomicReference<Integer> ans) {
        if (root == null) {
            return;
        }
        getMinimumDifference(root.left, pre, ans);
        if (pre.get() != null) {
            ans.set(Math.min(ans.get(), root.val - pre.get()));
        }
        pre.set(root.val);
        getMinimumDifference(root.right, pre, ans);
    }

    /** 给定一个有相同值的二叉搜索树（BST），找出 BST 中的所有众数（出现频率最高的元素）。 */
    @Source(501)
    @Tag(Type.RECURSIVE)
    public int[] findMode(TreeNode root) {
        FindMode findMode = new FindMode();
        return findMode.findMode(root);
    }

    public static class FindMode {
        private List<Integer> result;
        private Integer maxCount;
        private Integer curCount;
        private TreeNode pre;

        public int[] findMode(TreeNode root) {
            result = new ArrayList<>();
            maxCount = 0;
            curCount = 0;
            pre = null;
            findModeInternal(root);
            int[] res = new int[result.size()];
            for (int i = 0; i < result.size(); i++) {
                res[i] = result.get(i);
            }
            return res;
        }

        private void findModeInternal(TreeNode root) {
            if (root == null) {
                return;
            }

            findModeInternal(root.left);
            if (pre == null || pre.val != root.val) {
                curCount = 1;
            } else {
                curCount++;
            }
            if (curCount > maxCount) {
                maxCount = curCount;
                result.clear();
                result.add(root.val);
            } else if (curCount.equals(maxCount)) {
                result.add(root.val);
            }
            pre = root;
            findModeInternal(root.right);
        }
    }

    /**
     * 给定一个二叉搜索树 BST, 找到该树中两个指定节点的最近公共祖先。
     *
     * <p>所有节点的值都是唯一的。 p、q 为不同节点且均存在于给定的二叉搜索树中。
     */
    @Source(235)
    @Tag(Type.ITERATION)
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        while (root != null) {
            if (root.val > p.val && root.val > q.val) {
                root = root.left;
            } else if (root.val < p.val && root.val < q.val) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }

    /** 给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。 */
    @Source(236)
    @Tag(Type.RECURSIVE)
    public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }
        // 后序遍历
        TreeNode left = lowestCommonAncestor2(root.left, p, q);
        TreeNode right = lowestCommonAncestor2(root.right, p, q);
        if (left == null && right == null) {
            return null;
        } else if (left != null && right == null) {
            return left;
        } else if (left == null) {
            return right;
        } else {
            return root;
        }
    }

    /** 给定二叉搜索树（BST）的根节点和要插入树中的值，将值插入二叉搜索树。 返回插入后二叉搜索树的根节点。 输入数据 保证 ，新值和原始二叉搜索树中的任意节点值都不同。 */
    @Source(236)
    @Tag(Type.RECURSIVE)
    public TreeNode insertIntoBST(TreeNode root, int val) {
        TreeNode node = new TreeNode(val);
        if (root == null) {
            return node;
        }
        TreeNode cur = root;
        while (true) {
            if (cur.val > val) {
                if (cur.left == null) {
                    cur.left = node;
                    break;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = node;
                    break;
                }
                cur = cur.right;
            }
        }
        return root;
    }

    /** 给定一个二叉搜索树的根节点 root 和一个值 key，删除二叉搜索树中的 key 对应的节点，并保证二叉搜索树的性质不变。返回二叉搜索树（有可能被更新）的根节点的引用。 */
    @Source(450)
    @Tag(Type.RECURSIVE)
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }

        if (root.val > key) {
            root.left = deleteNode(root.left, key);
        } else if (root.val < key) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            // 左右孩子节点都不为空，则将删除节点的左子树放到删除节点的右子树的最左面节点的左孩子的位置
            TreeNode mostLeft = root.right;
            while (mostLeft.left != null) {
                mostLeft = mostLeft.left;
            }
            mostLeft.left = root.left;
            root.left = null;
            root.val = mostLeft.val;
            return root.right;
        }
        return root;
    }

    /**
     * 给你二叉搜索树的根节点 root ，同时给定最小边界low 和最大边界 high。通过修剪二叉搜索树，使得所有节点的值在[low,
     * high]中。修剪树不应该改变保留在树中的元素的相对结构（即，如果没有被移除，原有的父代子代关系都应当保留）。 可以证明，存在唯一的答案。
     *
     * <p>所以结果应当返回修剪好的二叉搜索树的新的根节点。注意，根节点可能会根据给定的边界发生改变。
     */
    @Source(669)
    @Tag(Type.RECURSIVE)
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null) {
            return null;
        }
        if (root.val > high) {
            return trimBST(root.left, low, high);
        } else if (root.val < low) {
            return trimBST(root.right, low, high);
        }
        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);
        return root;
    }

    /**
     * 给你一个整数数组 nums ，其中元素已经按 升序 排列，请你将其转换为一棵 高度平衡 二叉搜索树。
     *
     * <p>高度平衡 二叉树是一棵满足「每个节点的左右两个子树的高度差的绝对值不超过 1 」的二叉树。
     */
    @Source(108)
    @Tag(Type.RECURSIVE)
    public TreeNode sortedArrayToBST(int[] nums) {
        return sortedArrayToBST(nums, 0, nums.length - 1);
    }

    private TreeNode sortedArrayToBST(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = sortedArrayToBST(nums, left, mid - 1);
        root.right = sortedArrayToBST(nums, mid + 1, right);
        return root;
    }

    /**
     * 给出二叉 搜索 树的根节点，该树的节点值各不相同，请你将其转换为累加树（Greater Sum Tree），使每个节点 node的新值等于原树中大于或等于node.val的值之和。
     */
    @Source(538)
    @Source(1038)
    @Tag(Type.RECURSIVE)
    public TreeNode convertBST(TreeNode root) {
        TreeNode ans = root;
        // 右中左，即反序中序遍历累加即可
        Stack<TreeNode> stack = new Stack<>();
        int sum = 0;
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.right;
            }
            root = stack.pop();
            sum += root.val;
            root.val = sum;
            root = root.left;
        }
        return ans;
    }

    /**
     * 给定一个二叉树，我们在树的节点上安装摄像头。
     *
     * <p>节点上的每个摄影头都可以监视其父对象、自身及其直接子对象。
     *
     * <p>计算监控树的所有节点所需的最小摄像头数量。
     */
    @Source(968)
    public int minCameraCover(TreeNode root) {
        AtomicInteger ans = new AtomicInteger(0);
        // 主节点未被监控覆盖，添加监控
        if (minCameraCover(root, ans) == 0) {
            ans.incrementAndGet();
        }
        return ans.get();
    }

    /** 0: 未被监控覆盖； 1: 该节点已安装监控，2: 该节点被监控覆盖。 */
    private int minCameraCover(TreeNode root, AtomicInteger ans) {
        if (root == null) {
            // 空节点无意义
            return -1;
        }
        int left = minCameraCover(root.left, ans);
        int right = minCameraCover(root.right, ans);

        // 子节点存在未被监控的，在该节点安装监控
        if (left == 0 || right == 0) {
            ans.incrementAndGet();
            return 1;
        }

        // 子节点存在监控，表示该节点已被监控
        if (left == 1 || right == 1) {
            return 2;
        }
        // 子节点不存在监控，且未在该节点安装监控，表示该节点未被监控
        return 0;
    }

    /**
     * 路径 被定义为一条从树中任意节点出发，沿父节点-子节点连接，达到任意节点的序列。同一个节点在一条路径序列中 至多出现一次 。该路径 至少包含一个 节点，且不一定经过根节点。
     *
     * <p>路径和 是路径中各节点值的总和。
     *
     * <p>给你一个二叉树的根节点 root ，返回其 最大路径和 。
     */
    @Source(124)
    public int maxPathSum(TreeNode root) {
        return maxPathSumDfs(root)[1];
    }

    private Integer[] maxPathSumDfs(TreeNode root) {
        Integer[] leftSum = null;
        if (root.left != null) {
            leftSum = maxPathSumDfs(root.left);
        }
        Integer[] rightSum = null;
        if (root.right != null) {
            rightSum = maxPathSumDfs(root.right);
        }
        Integer[] ans = new Integer[2];
        if (leftSum != null & rightSum != null) {
            int max = Math.max(leftSum[0], rightSum[0]);
            // 单边
            ans[0] = root.val + Math.max(max, 0);
            // 目前的最大路径和
            ans[1] =
                    Math.max(
                            rightSum[1],
                            Math.max(
                                    leftSum[1],
                                    Math.max(
                                            max,
                                            Math.max(
                                                    root.val,
                                                    Math.max(
                                                            ans[0],
                                                            leftSum[0]
                                                                    + rightSum[0]
                                                                    + root.val)))));
        } else if (leftSum == null && rightSum == null) {
            ans[0] = root.val;
            ans[1] = root.val;
        } else {
            if (leftSum != null) {
                rightSum = leftSum;
            }
            ans[0] = root.val + Math.max(rightSum[0], 0);
            ans[1] = Math.max(rightSum[1], Math.max(rightSum[0], Math.max(root.val, ans[0])));
        }

        return ans;
    }
}
