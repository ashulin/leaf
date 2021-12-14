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
import org.junit.jupiter.api.Test;

public class SlidingWindowTest {
    private final SlidingWindow solution = new SlidingWindow();

    @Test
    public void testMinSubArrayLen() {
        Assertions.assertEquals(2, solution.minSubArrayLen(7, new int[] {2, 3, 1, 2, 4, 3}));
        Assertions.assertEquals(
                1, solution.minSubArrayLen(4, new int[] {0, 0, 1, 1, 1, 2, 2, 3, 4}));
        Assertions.assertEquals(
                0, solution.minSubArrayLen(100, new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4}));
    }

    @Test
    public void testTotalFruit() {
        Assertions.assertEquals(3, solution.totalFruit(new int[] {1, 2, 1}));
        Assertions.assertEquals(3, solution.totalFruit(new int[] {0, 1, 2, 2}));
        Assertions.assertEquals(4, solution.totalFruit(new int[] {1, 2, 3, 2, 2}));
        Assertions.assertEquals(3, solution.totalFruit(new int[] {1, 0, 3, 4, 3}));
        Assertions.assertEquals(
                5, solution.totalFruit(new int[] {3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4}));
    }

    @Test
    public void testTotalFruit2() {
        Assertions.assertEquals(3, solution.totalFruit2(new int[] {1, 2, 1}));
        Assertions.assertEquals(3, solution.totalFruit2(new int[] {0, 1, 2, 2}));
        Assertions.assertEquals(4, solution.totalFruit2(new int[] {1, 2, 3, 2, 2}));
        Assertions.assertEquals(3, solution.totalFruit2(new int[] {1, 0, 3, 4, 3}));
        Assertions.assertEquals(
                5, solution.totalFruit2(new int[] {3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4}));
    }

    @Test
    public void testMinWindow() {
        Assertions.assertEquals("", solution.minWindow("a", "aa"));
        Assertions.assertEquals("a", solution.minWindow("a", "a"));
        Assertions.assertEquals("BANC", solution.minWindow("ADOBECODEBANC", "ABC"));
    }

    @Test
    public void testMinWindow2() {
        Assertions.assertEquals("", solution.minWindow2("a", "aa"));
        Assertions.assertEquals("a", solution.minWindow2("a", "a"));
        Assertions.assertEquals("BANC", solution.minWindow2("ADOBECODEBANC", "ABC"));
    }
}
