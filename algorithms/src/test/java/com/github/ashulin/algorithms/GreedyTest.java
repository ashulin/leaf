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

import org.junit.jupiter.api.Test;

public class GreedyTest {

    Greedy solution = new Greedy();

    @Test
    public void testFindContentChildren() {
        Assertions.assertEquals(
                1, solution.findContentChildren(new int[] {1, 2, 3}, new int[] {1, 1}));
        Assertions.assertEquals(
                2, solution.findContentChildren(new int[] {1, 2, 3}, new int[] {1, 3}));
        Assertions.assertEquals(
                2, solution.findContentChildren(new int[] {1, 2, 3}, new int[] {1, 2, 2, 2}));
    }

    @Test
    public void testMaxSubArray() {
        Assertions.assertEquals(6, solution.maxSubArray(new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4}));
        Assertions.assertEquals(1, solution.maxSubArray(new int[] {1}));
        Assertions.assertEquals(23, solution.maxSubArray(new int[] {5, 4, -1, 7, 8}));
        Assertions.assertEquals(-1, solution.maxSubArray(new int[] {-5, -4, -1, -7, -8}));
    }
}
