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

public class BinaryTreeTest {
    BinaryTree solution = new BinaryTree();
    private BinaryTree.TreeNode root;

    @BeforeEach
    public void before() {
        root =
                new BinaryTree.TreeNode(
                        1,
                        new BinaryTree.TreeNode(
                                2, new BinaryTree.TreeNode(4), new BinaryTree.TreeNode(5)),
                        new BinaryTree.TreeNode(
                                3, new BinaryTree.TreeNode(6), new BinaryTree.TreeNode(7)));
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
}
