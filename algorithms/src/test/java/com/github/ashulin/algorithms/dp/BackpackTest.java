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

package com.github.ashulin.algorithms.dp;

import com.github.ashulin.algorithms.Assertions;
import org.junit.jupiter.api.Test;

public class BackpackTest {
    private final Backpack solution = new Backpack();

    @Test
    public void testCanPartition() {
        Assertions.assertTrue(solution.canPartition(new int[] {1, 5, 11, 5}));
        Assertions.assertFalse(solution.canPartition(new int[] {1, 2, 3, 5}));
    }

    @Test
    public void testLastStoneWeightII() {
        Assertions.assertEquals(1, solution.lastStoneWeightII(new int[] {2, 7, 4, 1, 8, 1}));
        Assertions.assertEquals(5, solution.lastStoneWeightII(new int[] {31, 26, 33, 21, 40}));
        Assertions.assertEquals(1, solution.lastStoneWeightII(new int[] {1, 2}));
    }

    @Test
    public void testFindTargetSumWays() {
        Assertions.assertEquals(5, solution.findTargetSumWays(new int[] {1, 1, 1, 1, 1}, 3));
        Assertions.assertEquals(1, solution.findTargetSumWays(new int[] {1}, 1));
        Assertions.assertEquals(1, solution.findTargetSumWays(new int[] {1, 0}, 1));
    }

    @Test
    public void testFindMaxForm() {
        Assertions.assertEquals(
                4, solution.findMaxForm(new String[] {"10", "0001", "111001", "1", "0"}, 5, 3));
        Assertions.assertEquals(2, solution.findMaxForm(new String[] {"10", "0", "1"}, 1, 1));
    }

    @Test
    public void testChange() {
        Assertions.assertEquals(4, solution.change(5, new int[] {1, 2, 5}));
        Assertions.assertEquals(0, solution.change(3, new int[] {2}));
        Assertions.assertEquals(1, solution.change(5, new int[] {5}));
    }

    @Test
    public void testCombinationSum4() {
        Assertions.assertEquals(7, solution.combinationSum4(new int[] {1, 2, 3}, 4));
        Assertions.assertEquals(0, solution.combinationSum4(new int[] {9}, 3));
    }
}
