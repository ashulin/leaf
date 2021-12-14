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

/**
 * @author Li Zongwen
 * @since 2021/12/14
 */
public class TwoPointersTest {
    private final TwoPointers solution = new TwoPointers();

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
}
