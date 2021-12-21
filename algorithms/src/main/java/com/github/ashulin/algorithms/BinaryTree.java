package com.github.ashulin.algorithms;

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

import javax.swing.tree.TreeNode;

import java.util.ArrayList;
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
    @Complexity(time = "O(2n)", space = "O(1)")
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
    @Complexity(time = "O(2n)", space = "O(1)")
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
}
