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

public class BinaryTreeTest {
    private final BinaryTree solution = new BinaryTree();
    private BinaryTree.TreeNode root;
    private BinaryTree.TreeNode node;

    private BinaryTree.TreeNode buildTreeNode(Integer[] nums) {
        return buildTreeNode(nums, 0);
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

    private BinaryTree.TreeNode buildTreeNode(int[] nums, int len) {
        Integer[] integers = new Integer[len];
        for (int num : nums) {
            integers[num - 1] = num;
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
        Assertions.assertEquals(4, solution.minDepth(buildTreeNode(new int[] {1, 2, 4, 9}, 15)));
    }
}