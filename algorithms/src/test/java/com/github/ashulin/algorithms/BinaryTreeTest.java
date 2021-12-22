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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BinaryTreeTest {
    private final BinaryTree solution = new BinaryTree();
    private BinaryTree.TreeNode root;
    private BinaryTree.TreeNode node;

    @BeforeEach
    public void before() {
        root =
                new BinaryTree.TreeNode(
                        1,
                        new BinaryTree.TreeNode(
                                2, new BinaryTree.TreeNode(4), new BinaryTree.TreeNode(5)),
                        new BinaryTree.TreeNode(
                                3, new BinaryTree.TreeNode(6), new BinaryTree.TreeNode(7)));
        node =
                new BinaryTree.TreeNode(
                        1,
                        new BinaryTree.TreeNode(2, null, new BinaryTree.TreeNode(5)),
                        new BinaryTree.TreeNode(3));
    }

    @Test
    public void testInorderTraversal() {
        Assertions.assertArrayEquals(
                new int[] {4, 2, 5, 1, 6, 3, 7},
                solution.inorderTraversal(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 2, 5, 1, 6, 3, 7},
                solution.inorderTraversal2(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 2, 5, 1, 6, 3, 7},
                solution.inorderTraversal3(root).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testPreorderTraversal() {
        Assertions.assertArrayEquals(
                new int[] {1, 2, 4, 5, 3, 6, 7},
                solution.preorderTraversal(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 2, 4, 5, 3, 6, 7},
                solution.preorderTraversal2(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 2, 4, 5, 3, 6, 7},
                solution.preorderTraversal3(root).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testPostorderTraversal() {
        Assertions.assertArrayEquals(
                new int[] {4, 5, 2, 6, 7, 3, 1},
                solution.postorderTraversal(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 5, 2, 6, 7, 3, 1},
                solution.postorderTraversal2(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 5, 2, 6, 7, 3, 1},
                solution.postorderTraversal3(root).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testLevelOrder() {
        List<List<Integer>> result = solution.levelOrder(root);
        Assertions.assertEquals(3, result.size());
        Assertions.assertArrayEquals(
                new int[] {1}, result.get(0).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {2, 3}, result.get(1).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 5, 6, 7},
                result.get(2).stream().mapToInt(Integer::intValue).toArray());
        result = solution.levelOrder2(root);
        Assertions.assertEquals(3, result.size());
        Assertions.assertArrayEquals(
                new int[] {1}, result.get(0).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {2, 3}, result.get(1).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 5, 6, 7},
                result.get(2).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testLevelOrderBottom() {
        List<List<Integer>> result = solution.levelOrderBottom(root);
        Assertions.assertEquals(3, result.size());
        Assertions.assertArrayEquals(
                new int[] {1}, result.get(2).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {2, 3}, result.get(1).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 5, 6, 7},
                result.get(0).stream().mapToInt(Integer::intValue).toArray());
        result = solution.levelOrderBottom2(root);
        Assertions.assertEquals(3, result.size());
        Assertions.assertArrayEquals(
                new int[] {1}, result.get(2).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {2, 3}, result.get(1).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {4, 5, 6, 7},
                result.get(0).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testRightSideView() {
        Assertions.assertArrayEquals(
                new int[] {1, 3, 7},
                solution.rightSideView(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 3, 5},
                solution.rightSideView(node).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 3, 7},
                solution.rightSideView2(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 3, 5},
                solution.rightSideView2(node).stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    public void testAverageOfLevels() {
        Assertions.assertArrayEquals(
                new double[] {1, 2.5, 5.5},
                solution.averageOfLevels(root).stream().mapToDouble(Double::doubleValue).toArray());
        Assertions.assertArrayEquals(
                new double[] {1, 2.5, 5},
                solution.averageOfLevels(node).stream().mapToDouble(Double::doubleValue).toArray());
    }

    @Test
    public void testLargestValues() {
        Assertions.assertArrayEquals(
                new int[] {1, 3, 7},
                solution.largestValues(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 3, 5},
                solution.largestValues(node).stream().mapToInt(Integer::intValue).toArray());

        Assertions.assertArrayEquals(
                new int[] {1, 3, 7},
                solution.largestValues2(root).stream().mapToInt(Integer::intValue).toArray());
        Assertions.assertArrayEquals(
                new int[] {1, 3, 5},
                solution.largestValues2(node).stream().mapToInt(Integer::intValue).toArray());
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
}
