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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class BinaryTreeTest {
    private final BinaryTree solution = new BinaryTree();
    private BinaryTree.TreeNode root;
    private BinaryTree.TreeNode node;

    /** 参考leet-code的数组输入用例转换为二叉树的实现方式。 */
    private BinaryTree.TreeNode buildTreeNode(Integer[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }

        Deque<BinaryTree.TreeNode> deque = new LinkedList<>();
        BinaryTree.TreeNode ans = new BinaryTree.TreeNode(nums[0]);
        deque.add(ans);
        int index = 1;
        while (!deque.isEmpty()) {
            for (int i = deque.size(); i > 0; i--) {
                BinaryTree.TreeNode cur = deque.pop();
                if (index == nums.length) {
                    break;
                }
                Integer num = nums[index++];
                if (num != null) {
                    cur.left = new BinaryTree.TreeNode(num);
                    deque.offer(cur.left);
                }
                if (index == nums.length) {
                    break;
                }
                num = nums[index++];
                if (num != null) {
                    cur.right = new BinaryTree.TreeNode(num);
                    deque.offer(cur.right);
                }
            }
        }
        return ans;
    }

    private BinaryTree.TreeNode buildTreeNode(Integer[] nums, int index) {
        if (nums == null || index >= nums.length || nums[index] == null) {
            return null;
        }
        BinaryTree.TreeNode node = new BinaryTree.TreeNode(nums[index]);
        node.left = buildTreeNode(nums, 2 * index + 1);
        node.right = buildTreeNode(nums, 2 * index + 2);
        return node;
    }

    private BinaryTree.TreeNode buildTreeNode(int len) {
        Integer[] integers = new Integer[len];
        for (int i = 0; i < len; i++) {
            integers[i] = i + 1;
        }
        return buildTreeNode(integers, 0);
    }

    @BeforeEach
    public void before() {
        root = buildTreeNode(new Integer[] {1, 2, 3, 4, 5, 6, 7});
        node = buildTreeNode(new Integer[] {1, 2, 3, null, 5});
    }

    @Test
    public void testInorderTraversal() {
        int[] expected = {4, 2, 5, 1, 6, 3, 7};
        Assertions.assertArrayEquals(expected, solution.inorderTraversal(root));
        Assertions.assertArrayEquals(expected, solution.inorderTraversal2(root));
        Assertions.assertArrayEquals(expected, solution.inorderTraversal3(root));
    }

    @Test
    public void testPreorderTraversal() {
        int[] expected = {1, 2, 4, 5, 3, 6, 7};
        Assertions.assertArrayEquals(expected, solution.preorderTraversal(root));
        Assertions.assertArrayEquals(expected, solution.preorderTraversal2(root));
        Assertions.assertArrayEquals(expected, solution.preorderTraversal3(root));
    }

    @Test
    public void testPostorderTraversal() {
        int[] expected = {4, 5, 2, 6, 7, 3, 1};
        Assertions.assertArrayEquals(expected, solution.postorderTraversal(root));
        Assertions.assertArrayEquals(expected, solution.postorderTraversal2(root));
        Assertions.assertArrayEquals(expected, solution.postorderTraversal3(root));
    }

    @Test
    public void testLevelOrder() {
        int[][] expected = {{1}, {2, 3}, {4, 5, 6, 7}};
        Assertions.assertArrayEquals(expected, solution.levelOrder(root));
        Assertions.assertArrayEquals(expected, solution.levelOrder2(root));
    }

    @Test
    public void testLevelOrderBottom() {
        int[][] expected = {{4, 5, 6, 7}, {2, 3}, {1}};
        Assertions.assertArrayEquals(expected, solution.levelOrderBottom(root));
        Assertions.assertArrayEquals(expected, solution.levelOrderBottom2(root));
    }

    @Test
    public void testRightSideView() {
        int[] expected = {1, 3, 7};
        Assertions.assertArrayEquals(expected, solution.rightSideView(root));
        Assertions.assertArrayEquals(expected, solution.rightSideView2(root));
        expected = new int[] {1, 3, 5};
        Assertions.assertArrayEquals(expected, solution.rightSideView(node));
        Assertions.assertArrayEquals(expected, solution.rightSideView2(node));
    }

    @Test
    public void testAverageOfLevels() {
        Assertions.assertArrayEquals(new double[] {1, 2.5, 5.5}, solution.averageOfLevels(root));
        Assertions.assertArrayEquals(new double[] {1, 2.5, 5}, solution.averageOfLevels(node));
    }

    @Test
    public void testLargestValues() {
        int[] expected = {1, 3, 7};
        Assertions.assertArrayEquals(expected, solution.largestValues(root));
        Assertions.assertArrayEquals(expected, solution.largestValues2(root));
        expected = new int[] {1, 3, 5};
        Assertions.assertArrayEquals(expected, solution.largestValues(node));
        Assertions.assertArrayEquals(expected, solution.largestValues2(node));
    }

    @Test
    public void testConnect() {
        BinaryTree.Node node1 = new BinaryTree.Node(1);
        BinaryTree.Node node2 = new BinaryTree.Node(2);
        BinaryTree.Node node3 = new BinaryTree.Node(3);
        BinaryTree.Node node4 = new BinaryTree.Node(4);
        BinaryTree.Node node5 = new BinaryTree.Node(5);
        BinaryTree.Node node6 = new BinaryTree.Node(6);
        BinaryTree.Node node7 = new BinaryTree.Node(7);
        node1.left = node2;
        node1.right = node3;
        node2.left = node4;
        node2.right = node5;
        node3.left = node6;
        node3.right = node7;
        solution.connect(node1);
        Assertions.assertNull(node1.next);
        Assertions.assertEquals(node2.next, node3);
        Assertions.assertNull(node3.next);
        Assertions.assertEquals(node4.next, node5);
        Assertions.assertEquals(node5.next, node6);
        Assertions.assertEquals(node6.next, node7);
        Assertions.assertNull(node7.next);
    }

    @Test
    public void testConnect2() {
        BinaryTree.Node node1 = new BinaryTree.Node(1);
        BinaryTree.Node node2 = new BinaryTree.Node(2);
        BinaryTree.Node node3 = new BinaryTree.Node(3);
        BinaryTree.Node node4 = new BinaryTree.Node(4);
        BinaryTree.Node node5 = new BinaryTree.Node(5);
        BinaryTree.Node node6 = new BinaryTree.Node(6);
        BinaryTree.Node node7 = new BinaryTree.Node(7);
        node1.left = node2;
        node1.right = node3;
        node2.left = node4;
        node2.right = node5;
        node3.left = node6;
        node3.right = node7;
        solution.connect2(node1);
        Assertions.assertNull(node1.next);
        Assertions.assertEquals(node2.next, node3);
        Assertions.assertNull(node3.next);
        Assertions.assertEquals(node4.next, node5);
        Assertions.assertEquals(node5.next, node6);
        Assertions.assertEquals(node6.next, node7);
        Assertions.assertNull(node7.next);
    }

    @Test
    public void testConnect3() {
        BinaryTree.Node node1 = new BinaryTree.Node(1);
        BinaryTree.Node node2 = new BinaryTree.Node(2);
        BinaryTree.Node node3 = new BinaryTree.Node(3);
        BinaryTree.Node node4 = new BinaryTree.Node(4);
        BinaryTree.Node node5 = new BinaryTree.Node(5);
        BinaryTree.Node node6 = new BinaryTree.Node(6);
        BinaryTree.Node node7 = new BinaryTree.Node(7);
        node1.left = node2;
        node1.right = node3;
        node2.left = node4;
        node2.right = node5;
        node3.left = node6;
        node3.right = node7;
        solution.connect3(node1);
        Assertions.assertNull(node1.next);
        Assertions.assertEquals(node2.next, node3);
        Assertions.assertNull(node3.next);
        Assertions.assertEquals(node4.next, node5);
        Assertions.assertEquals(node5.next, node6);
        Assertions.assertEquals(node6.next, node7);
        Assertions.assertNull(node7.next);
    }

    @Test
    public void testConnect4() {
        BinaryTree.Node node1 = new BinaryTree.Node(1);
        BinaryTree.Node node2 = new BinaryTree.Node(2);
        BinaryTree.Node node3 = new BinaryTree.Node(3);
        BinaryTree.Node node4 = new BinaryTree.Node(4);
        BinaryTree.Node node5 = new BinaryTree.Node(5);
        BinaryTree.Node node6 = new BinaryTree.Node(6);
        BinaryTree.Node node7 = new BinaryTree.Node(7);
        node1.left = node2;
        node1.right = node3;
        node2.left = node4;
        node2.right = node5;
        node3.left = node6;
        node3.right = node7;
        solution.connect4(node1);
        Assertions.assertNull(node1.next);
        Assertions.assertEquals(node2.next, node3);
        Assertions.assertNull(node3.next);
        Assertions.assertEquals(node4.next, node5);
        Assertions.assertEquals(node5.next, node6);
        Assertions.assertEquals(node6.next, node7);
        Assertions.assertNull(node7.next);
    }

    @Test
    public void testConnect5() {
        BinaryTree.Node node1 = new BinaryTree.Node(1);
        BinaryTree.Node node2 = new BinaryTree.Node(2);
        BinaryTree.Node node3 = new BinaryTree.Node(3);
        BinaryTree.Node node4 = new BinaryTree.Node(4);
        BinaryTree.Node node5 = new BinaryTree.Node(5);
        BinaryTree.Node node6 = new BinaryTree.Node(6);
        BinaryTree.Node node7 = new BinaryTree.Node(7);
        BinaryTree.Node node8 = new BinaryTree.Node(8);
        BinaryTree.Node node9 = new BinaryTree.Node(9);
        BinaryTree.Node node10 = new BinaryTree.Node(10);
        BinaryTree.Node node11 = new BinaryTree.Node(11);
        node1.left = node2;
        node1.right = node3;
        node2.right = node4;
        node3.left = node5;
        node3.right = node6;
        node5.left = node7;
        node6.right = node8;
        node8.left = node9;
        node8.right = node10;
        node10.right = node11;
        solution.connect5(node1);
        Assertions.assertNull(node1.next);
        Assertions.assertNull(node3.next);
        Assertions.assertNull(node6.next);
        Assertions.assertNull(node8.next);
        Assertions.assertNull(node11.next);
        Assertions.assertEquals(node2.next, node3);
        Assertions.assertEquals(node4.next, node5);
        Assertions.assertEquals(node5.next, node6);
        Assertions.assertEquals(node7.next, node8);
        Assertions.assertEquals(node9.next, node10);
    }

    @Test
    public void testMaxDepth() {
        Assertions.assertEquals(3, solution.maxDepth(node));
    }

    @Test
    public void testMinDepth() {
        Assertions.assertEquals(
                4, solution.minDepth(buildTreeNode(new Integer[] {1, 2, null, 4, null, 9})));
    }

    @Test
    public void testIsSymmetric() {
        BinaryTree.TreeNode node1 =
                buildTreeNode(new Integer[] {1, 2, 2, null, 3, 3, null, 4, 5, 5, 4});
        BinaryTree.TreeNode node2 =
                buildTreeNode(new Integer[] {1, 2, 2, null, 3, 3, null, 4, 5, 5, 5});
        Assertions.assertTrue(solution.isSymmetric(node1));
        Assertions.assertFalse(solution.isSymmetric(node2));
        Assertions.assertTrue(solution.isSymmetric2(node1));
        Assertions.assertFalse(solution.isSymmetric2(node2));
    }

    @Test
    public void testCountNodes() {
        Assertions.assertEquals(7, solution.countNodes(root));
        Assertions.assertEquals(13, solution.countNodes(buildTreeNode(13)));
    }

    @Test
    public void testIsBalanced() {
        Assertions.assertTrue(solution.isBalanced(root));
        Assertions.assertTrue(solution.isBalanced(buildTreeNode(13)));
        Assertions.assertTrue(solution.isBalanced(node));
        Assertions.assertFalse(
                solution.isBalanced(
                        buildTreeNode(new Integer[] {1, 2, 3, 4, 5, null, null, 8, 9})));
    }

    @Test
    public void testBinaryTreePaths() {
        Assertions.assertEquals(
                Arrays.asList("1->2->4", "1->2->5", "1->3->6", "1->3->7"),
                solution.binaryTreePaths(root));
        Assertions.assertEquals(Arrays.asList("1->2->5", "1->3"), solution.binaryTreePaths(node));
        Assertions.assertEquals(
                Arrays.asList("1->2->4", "1->2->5", "1->3->6", "1->3->7"),
                solution.binaryTreePaths2(root));
        Assertions.assertEquals(Arrays.asList("1->2->5", "1->3"), solution.binaryTreePaths2(node));
    }

    @Test
    public void testIsSameTree() {
        BinaryTree.TreeNode node1 =
                buildTreeNode(new Integer[] {1, 2, 6, null, 3, 3, null, 4, 5, 5, 4});
        BinaryTree.TreeNode node2 =
                buildTreeNode(new Integer[] {1, 2, 6, null, 3, 3, null, 4, 5, 5, 4});
        BinaryTree.TreeNode node3 =
                buildTreeNode(new Integer[] {1, 2, 6, null, 3, 3, null, 4, 5, 5, 7});
        Assertions.assertTrue(solution.isSameTree(node1, node2));
        Assertions.assertFalse(solution.isSameTree(node1, node3));
    }

    @Test
    public void testSumOfLeftLeaves() {
        Assertions.assertEquals(10, solution.sumOfLeftLeaves(root));
        Assertions.assertEquals(0, solution.sumOfLeftLeaves(node));
    }

    @Test
    public void testFindBottomLeftValue() {
        Assertions.assertEquals(4, solution.findBottomLeftValue(root));
        Assertions.assertEquals(5, solution.findBottomLeftValue(node));
        Assertions.assertEquals(
                13,
                solution.findBottomLeftValue(
                        buildTreeNode(new Integer[] {1, 2, 3, null, null, 6, 7, 13})));
        Assertions.assertEquals(
                7,
                solution.findBottomLeftValue(
                        buildTreeNode(new Integer[] {1, 2, 3, 4, null, 5, 6, null, null, 7})));
    }

    @Test
    public void testHasPathSum() {
        Assertions.assertTrue(
                solution.hasPathSum(
                        buildTreeNode(
                                new Integer[] {
                                    5, 4, 8, 11, null, 13, 4, 7, 2, null, null, null, 1
                                }),
                        22));
        Assertions.assertFalse(solution.hasPathSum(buildTreeNode(new Integer[] {1, 2, 3}), 5));
    }

    @Test
    public void testBuildTree() {
        Assertions.assertTrue(
                solution.isSameTree(
                        buildTreeNode(new Integer[] {3, 9, 20, null, null, 15, 7}),
                        solution.buildTree(
                                new int[] {3, 9, 20, 15, 7}, new int[] {9, 3, 15, 20, 7})));
        Assertions.assertTrue(
                solution.isSameTree(
                        buildTreeNode(new Integer[] {3, 9, 20, null, null, 15, 7}),
                        solution.buildTree2(
                                new int[] {9, 3, 15, 20, 7}, new int[] {9, 15, 7, 20, 3})));
    }

    @Test
    public void testConstructMaximumBinaryTree() {
        Assertions.assertTrue(
                solution.isSameTree(
                        buildTreeNode(new Integer[] {6, 3, 5, null, 2, 0, null, null, 1}),
                        solution.constructMaximumBinaryTree(new int[] {3, 2, 1, 6, 0, 5})));
        Assertions.assertTrue(
                solution.isSameTree(
                        buildTreeNode(new Integer[] {3, null, 2, null, 1}),
                        solution.constructMaximumBinaryTree(new int[] {3, 2, 1})));
    }

    @Test
    public void testMergeTrees() {
        Assertions.assertTrue(
                solution.isSameTree(
                        buildTreeNode(new Integer[] {3, 4, 5, 5, 4, null, 7}),
                        solution.mergeTrees(
                                buildTreeNode(new Integer[] {1, 3, 2, 5}),
                                buildTreeNode(new Integer[] {2, 1, 3, null, 4, null, 7}))));
    }

    @Test
    public void testSearchBST() {
        Assertions.assertTrue(
                solution.isSameTree(
                        buildTreeNode(new Integer[] {2, 1, 3}),
                        solution.searchBST(buildTreeNode(new Integer[] {4, 2, 7, 1, 3}), 2)));
        Assertions.assertTrue(
                solution.isSameTree(
                        null, solution.searchBST(buildTreeNode(new Integer[] {4, 2, 7, 1, 3}), 5)));
    }

    @Test
    public void testIsValidBST() {
        Assertions.assertTrue(solution.isValidBST(buildTreeNode(new Integer[] {2, 1, 3})));
        Assertions.assertFalse(
                solution.isValidBST(buildTreeNode(new Integer[] {5, 1, 4, null, null, 3, 6})));
        Assertions.assertTrue(solution.isValidBST2(buildTreeNode(new Integer[] {2, 1, 3})));
        Assertions.assertFalse(
                solution.isValidBST2(buildTreeNode(new Integer[] {5, 1, 4, null, null, 3, 6})));
    }
}
