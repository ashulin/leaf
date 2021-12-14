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
 * @since 2021/12/13
 */
public class BinarySearchTest {
    private final BinarySearch solution = new BinarySearch();

    @Test
    public void testSearch() {
        Assertions.assertEquals(4, solution.search(new int[] {-1, 0, 3, 5, 9, 12}, 9));
        Assertions.assertEquals(-1, solution.search(new int[] {-1, 0, 3, 5, 9, 12}, 2));
        Assertions.assertEquals(4, solution.search(new int[] {-1, 0, 3, 5, 9}, 9));
        Assertions.assertEquals(3, solution.search(new int[] {-1, 0, 3, 9, 12}, 9));
        Assertions.assertEquals(0, solution.search(new int[] {9}, 9));
        Assertions.assertEquals(-1, solution.search(new int[] {9}, 10));
    }

    @Test
    public void testSearchInsert() {
        Assertions.assertEquals(4, solution.searchInsert(new int[] {-1, 0, 3, 5, 9, 12}, 9));
        Assertions.assertEquals(2, solution.searchInsert(new int[] {-1, 0, 3, 5, 9, 12}, 2));
        Assertions.assertEquals(4, solution.searchInsert(new int[] {-1, 0, 3, 5, 9}, 9));
        Assertions.assertEquals(3, solution.searchInsert(new int[] {-1, 0, 3, 9, 12}, 9));
        Assertions.assertEquals(0, solution.searchInsert(new int[] {9}, 9));
        Assertions.assertEquals(1, solution.searchInsert(new int[] {9}, 10));
        Assertions.assertEquals(1, solution.searchInsert(new int[] {1, 3}, 2));
        Assertions.assertEquals(2, solution.searchInsert(new int[] {1, 3}, 4));
    }

    @Test
    public void testSearchRange() {
        Assertions.assertArrayEquals(
                new int[] {0, 6}, solution.searchRange(new int[] {7, 7, 7, 7, 7, 7, 7}, 7));
        Assertions.assertArrayEquals(
                new int[] {0, 3}, solution.searchRange(new int[] {7, 7, 7, 7, 8, 9, 11}, 7));
        Assertions.assertArrayEquals(
                new int[] {3, 6}, solution.searchRange(new int[] {1, 2, 3, 7, 7, 7, 7}, 7));
        Assertions.assertArrayEquals(
                new int[] {0, 5}, solution.searchRange(new int[] {7, 7, 7, 7, 7, 7}, 7));
        Assertions.assertArrayEquals(
                new int[] {1, 5}, solution.searchRange(new int[] {6, 7, 7, 7, 7, 7, 8}, 7));
        Assertions.assertArrayEquals(
                new int[] {-1, -1}, solution.searchRange(new int[] {-1, 0, 3, 9, 12}, 2));
        Assertions.assertArrayEquals(
                new int[] {2, 2}, solution.searchRange(new int[] {-1, 0, 3, 9, 12}, 3));
        Assertions.assertArrayEquals(
                new int[] {0, 0}, solution.searchRange(new int[] {-1, 0, 3, 9, 12}, -1));
        Assertions.assertArrayEquals(
                new int[] {4, 4}, solution.searchRange(new int[] {-1, 0, 3, 9, 12}, 12));
    }

    @Test
    public void testSearchRange2() {
        Assertions.assertArrayEquals(
                new int[] {0, 6}, solution.searchRange2(new int[] {7, 7, 7, 7, 7, 7, 7}, 7));
        Assertions.assertArrayEquals(
                new int[] {0, 3}, solution.searchRange2(new int[] {7, 7, 7, 7, 8, 9, 11}, 7));
        Assertions.assertArrayEquals(
                new int[] {3, 6}, solution.searchRange2(new int[] {1, 2, 3, 7, 7, 7, 7}, 7));
        Assertions.assertArrayEquals(
                new int[] {0, 5}, solution.searchRange2(new int[] {7, 7, 7, 7, 7, 7}, 7));
        Assertions.assertArrayEquals(
                new int[] {1, 5}, solution.searchRange2(new int[] {6, 7, 7, 7, 7, 7, 8}, 7));
        Assertions.assertArrayEquals(
                new int[] {-1, -1}, solution.searchRange2(new int[] {-1, 0, 3, 9, 12}, 2));
        Assertions.assertArrayEquals(
                new int[] {2, 2}, solution.searchRange2(new int[] {-1, 0, 3, 9, 12}, 3));
        Assertions.assertArrayEquals(
                new int[] {0, 0}, solution.searchRange2(new int[] {-1, 0, 3, 9, 12}, -1));
        Assertions.assertArrayEquals(
                new int[] {4, 4}, solution.searchRange2(new int[] {-1, 0, 3, 9, 12}, 12));
    }

    @Test
    public void testMySqrt() {
        Assertions.assertEquals(1, solution.mySqrt(1));
        Assertions.assertEquals(0, solution.mySqrt(0));
        Assertions.assertEquals(1, solution.mySqrt(2));
        Assertions.assertEquals(1, solution.mySqrt(3));
        Assertions.assertEquals(2, solution.mySqrt(4));
        Assertions.assertEquals(2, solution.mySqrt(6));
        Assertions.assertEquals(2, solution.mySqrt(8));
        Assertions.assertEquals(3, solution.mySqrt(9));
        Assertions.assertEquals(46339, solution.mySqrt(2147395599));
        Assertions.assertEquals(46340, solution.mySqrt(2147395600));
        Assertions.assertEquals(46340, solution.mySqrt(2147395601));
        Assertions.assertEquals(46340, solution.mySqrt(2147483647));
    }

    @Test
    public void testIsPerfectSquare() {
        Assertions.assertTrue(solution.isPerfectSquare(4));
        Assertions.assertTrue(solution.isPerfectSquare(9));
        Assertions.assertTrue(solution.isPerfectSquare(2147395600));
        Assertions.assertFalse(solution.isPerfectSquare(8));
        Assertions.assertFalse(solution.isPerfectSquare(2147395599));
        Assertions.assertFalse(solution.isPerfectSquare(2147395601));
        Assertions.assertFalse(solution.isPerfectSquare(2147483647));
    }
}
