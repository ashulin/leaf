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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
}
