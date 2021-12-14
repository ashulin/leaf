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

package com.github.ashulin.algorithms.leetcode.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Li Zongwen
 * @since 2021/12/14
 */
public class DoublePointerTest {
    private final DoublePointer solution = new DoublePointer();

    @Test
    public void testRemoveDuplicates() {
        Assertions.assertEquals(2, solution.removeDuplicates(new int[] {1, 1, 2}));
        Assertions.assertEquals(
                5, solution.removeDuplicates(new int[] {0, 0, 1, 1, 1, 2, 2, 3, 4}));
        Assertions.assertEquals(
                1, solution.removeDuplicates(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4}));
    }

    @Test
    public void testRemoveElement() {
        Assertions.assertEquals(1, solution.removeElement(new int[] {1, 1, 2}, 1));
        Assertions.assertEquals(
                6, solution.removeElement(new int[] {0, 0, 1, 1, 1, 2, 2, 3, 4}, 1));
        Assertions.assertEquals(
                0, solution.removeElement(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4}, 4));
    }

    @Test
    public void testRemoveElement2() {
        Assertions.assertEquals(1, solution.removeElement2(new int[] {1, 1, 2}, 1));
        Assertions.assertEquals(
                6, solution.removeElement2(new int[] {0, 0, 1, 1, 1, 2, 2, 3, 4}, 1));
        Assertions.assertEquals(
                0, solution.removeElement2(new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4}, 4));
    }

    @Test
    public void testMoveZeroes() {
        int[] ints = new int[] {1, 1, 2};
        solution.moveZeroes(ints);
        Assertions.assertArrayEquals(new int[] {1, 1, 2}, ints);
        ints = new int[] {0, 1, 1, 1, 2, 2, 0, 3, 4};
        solution.moveZeroes(ints);
        Assertions.assertArrayEquals(new int[] {1, 1, 1, 2, 2, 3, 4, 0, 0}, ints);
    }

    @Test
    public void testBackspaceCompare() {
        Assertions.assertTrue(solution.backspaceCompare("ab#c", "ad#c"));
        Assertions.assertTrue(solution.backspaceCompare("##ab#c", "ad#c"));
        Assertions.assertTrue(solution.backspaceCompare("ab##", "c#d#"));
        Assertions.assertTrue(solution.backspaceCompare("a##c", "#a#c"));
        Assertions.assertFalse(solution.backspaceCompare("a#c", "b"));
    }

    @Test
    public void testSortedSquares() {
        Assertions.assertArrayEquals(
                new int[] {49, 49, 64, 81, 121},
                solution.sortedSquares(new int[] {7, 7, 8, 9, 11}));
        Assertions.assertArrayEquals(
                new int[] {49, 49, 49, 49}, solution.sortedSquares(new int[] {7, 7, 7, 7}));
        Assertions.assertArrayEquals(
                new int[] {25, 49, 64, 81, 121},
                solution.sortedSquares(new int[] {-11, -9, -8, -7, -5}));
        Assertions.assertArrayEquals(
                new int[] {0, 9, 49, 81, 144}, solution.sortedSquares(new int[] {-7, 0, 3, 9, 12}));
    }

    public static class SlidingWindowTest {
        private final DoublePointer.SlidingWindow solution = new DoublePointer.SlidingWindow();

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
}
