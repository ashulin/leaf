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

import java.util.ArrayList;
import java.util.List;

public class NTreeTest {
    private final NTree solution = new NTree();

    private NTree.Node node;

    private NTree.Node buildNode(Integer[] nums, int n) {
        return buildNode(nums, n, 0, 0);
    }

    private NTree.Node buildNode(Integer[] nums, int n, int deep, int index) {
        if (nums == null || index >= nums.length || nums[index] == null) {
            return null;
        }
        NTree.Node node = new NTree.Node(nums[index]);
        int levelHead = size(n, deep);
        deep++;
        int levelTail = size(n, deep);
        int startIndex = levelTail + (index - levelHead) * n;
        List<NTree.Node> children = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            NTree.Node child = buildNode(nums, n, deep, startIndex++);
            if (child != null) {
                children.add(child);
            }
        }
        if (children.size() > 0) {
            node.children = children;
        }
        return node;
    }

    private int size(int n, int deep) {
        return ((int) Math.pow(n, deep) - 1) / (n - 1);
    }

    @BeforeEach
    public void before() {
        node = buildNode(new Integer[] {1, 3, 2, 4, 5, 6}, 3);
    }

    @Test
    public void testLevelOrder() {
        int[][] expected = new int[][] {{1}, {3, 2, 4}, {5, 6}};
        Assertions.assertArrayEquals(expected, solution.levelOrder(node));
        Assertions.assertArrayEquals(expected, solution.levelOrder2(node));
    }

    @Test
    public void testMaxDepth() {
        Assertions.assertEquals(3, solution.maxDepth(node));
    }
}
