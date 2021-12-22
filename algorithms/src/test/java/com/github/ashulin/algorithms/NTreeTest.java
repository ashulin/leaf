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

public class NTreeTest {
    private final NTree solution = new NTree();

    private NTree.Node node;

    @BeforeEach
    public void before() {
        node =
                new NTree.Node(
                        1,
                        Arrays.asList(
                                new NTree.Node(
                                        3, Arrays.asList(new NTree.Node(5), new NTree.Node(6))),
                                new NTree.Node(2),
                                new NTree.Node(4)));
    }

    @Test
    public void testLevelOrder() {
        int[][] expected = new int[][] {{1}, {3, 2, 4}, {5, 6}};
        Assertions.assertArrayEquals(expected, solution.levelOrder(node));
        Assertions.assertArrayEquals(expected, solution.levelOrder2(node));
    }
}
