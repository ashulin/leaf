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

    @Test
    public void testMaxProfit() {
        Assertions.assertEquals(7, solution.maxProfit(new int[] {7, 1, 5, 3, 6, 4}));
        Assertions.assertEquals(4, solution.maxProfit(new int[] {1, 2, 3, 4, 5}));
        Assertions.assertEquals(0, solution.maxProfit(new int[] {7, 6, 4, 3, 1}));
        Assertions.assertEquals(8, solution.maxProfit(new int[] {1, 3, 2, 8, 4, 9}, 2));
        Assertions.assertEquals(6, solution.maxProfit(new int[] {1, 3, 7, 5, 10, 3}, 3));
    }

    @Test
    public void testCanJump() {
        Assertions.assertTrue(solution.canJump(new int[] {2, 3, 1, 1, 4}));
        Assertions.assertTrue(solution.canJump(new int[] {1}));
        Assertions.assertFalse(solution.canJump(new int[] {3, 2, 1, 0, 4}));
        Assertions.assertFalse(solution.canJump(new int[] {0, 1}));
    }

    @Test
    public void testJump() {
        Assertions.assertEquals(2, solution.jump(new int[] {2, 3, 1, 1, 4}));
        Assertions.assertEquals(2, solution.jump(new int[] {3, 5, 7, 1, 1, 1, 1, 2}));
        Assertions.assertEquals(2, solution.jump(new int[] {2, 3, 0, 1, 4}));
    }

    @Test
    public void testLargestSumAfterKNegations() {
        Assertions.assertEquals(5, solution.largestSumAfterKNegations(new int[] {4, 2, 3}, 1));
        Assertions.assertEquals(6, solution.largestSumAfterKNegations(new int[] {3, -1, 0, 2}, 3));
        Assertions.assertEquals(5, solution.largestSumAfterKNegations(new int[] {3, -1, 1, 2}, 4));
        Assertions.assertEquals(6, solution.largestSumAfterKNegations(new int[] {3, -1, 2, 2}, 4));
        Assertions.assertEquals(
                13, solution.largestSumAfterKNegations(new int[] {2, -3, -1, 5, -4}, 2));
    }

    @Test
    public void testCanCompleteCircuit() {
        Assertions.assertEquals(
                3,
                solution.canCompleteCircuit(new int[] {1, 2, 3, 4, 5}, new int[] {3, 4, 5, 1, 2}));
        Assertions.assertEquals(
                -1, solution.canCompleteCircuit(new int[] {2, 3, 4}, new int[] {3, 4, 3}));
    }

    @Test
    public void testCandy() {
        Assertions.assertEquals(5, solution.candy(new int[] {1, 0, 2}));
        Assertions.assertEquals(4, solution.candy(new int[] {1, 2, 2}));
    }

    @Test
    public void testLemonadeChange() {
        Assertions.assertTrue(solution.lemonadeChange(new int[] {5, 5, 5, 10, 20}));
        Assertions.assertTrue(solution.lemonadeChange(new int[] {5, 5, 10}));
        Assertions.assertFalse(solution.lemonadeChange(new int[] {5, 5, 10, 10, 20}));
    }

    @Test
    public void testReconstructQueue() {
        Assertions.assertArrayEquals(
                new int[][] {{5, 0}, {7, 0}, {5, 2}, {6, 1}, {4, 4}, {7, 1}},
                solution.reconstructQueue(
                        new int[][] {{7, 0}, {4, 4}, {7, 1}, {5, 0}, {6, 1}, {5, 2}}));
        Assertions.assertArrayEquals(
                new int[][] {{4, 0}, {5, 0}, {2, 2}, {3, 2}, {1, 4}, {6, 0}},
                solution.reconstructQueue(
                        new int[][] {{6, 0}, {5, 0}, {4, 0}, {3, 2}, {2, 2}, {1, 4}}));
    }

    @Test
    public void testFindMinArrowShots() {
        Assertions.assertEquals(
                2, solution.findMinArrowShots(new int[][] {{10, 16}, {2, 8}, {1, 6}, {7, 12}}));
        Assertions.assertEquals(
                4, solution.findMinArrowShots(new int[][] {{1, 2}, {3, 4}, {5, 6}, {7, 8}}));
        Assertions.assertEquals(
                2, solution.findMinArrowShots(new int[][] {{1, 2}, {2, 3}, {3, 4}, {4, 5}}));
        Assertions.assertEquals(1, solution.findMinArrowShots(new int[][] {{1, 2}}));
        Assertions.assertEquals(1, solution.findMinArrowShots(new int[][] {{2, 3}, {2, 3}}));
    }

    @Test
    public void testEraseOverlapIntervals() {
        Assertions.assertEquals(
                1, solution.eraseOverlapIntervals(new int[][] {{1, 2}, {2, 3}, {3, 4}, {1, 3}}));
        Assertions.assertEquals(
                2, solution.eraseOverlapIntervals(new int[][] {{1, 2}, {1, 2}, {1, 2}}));
        Assertions.assertEquals(
                1, solution.eraseOverlapIntervals(new int[][] {{1, 3}, {1, 2}, {2, 3}}));
        Assertions.assertEquals(
                3,
                solution.eraseOverlapIntervals(
                        new int[][] {{0, 5}, {1, 2}, {-1, 2}, {-2, 2}, {2, 5}}));
        Assertions.assertEquals(0, solution.eraseOverlapIntervals(new int[][] {{1, 2}, {2, 3}}));
        Assertions.assertEquals(
                7,
                solution.eraseOverlapIntervals(
                        new int[][] {
                            {-52, 31}, {-73, -26}, {82, 97}, {-65, -11}, {-62, -49}, {95, 99},
                            {58, 95}, {-31, 49}, {66, 98}, {-63, 2}, {30, 47}, {-40, -26}
                        }));
    }

    @Test
    public void testPartitionLabels() {
        Assertions.assertArrayEquals(
                new int[] {9, 7, 8}, solution.partitionLabels("ababcbacadefegdehijhklij"));
    }

    @Test
    public void testMerge() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 6}, {8, 10}, {15, 18}},
                solution.merge(new int[][] {{1, 3}, {2, 6}, {8, 10}, {15, 18}}));
        Assertions.assertArrayEquals(
                new int[][] {{1, 5}}, solution.merge(new int[][] {{1, 4}, {4, 5}}));
    }

    @Test
    public void testMonotoneIncreasingDigits() {
        Assertions.assertEquals(299, solution.monotoneIncreasingDigits(332));
        Assertions.assertEquals(1234, solution.monotoneIncreasingDigits(1234));
        Assertions.assertEquals(99, solution.monotoneIncreasingDigits(110));
        Assertions.assertEquals(99, solution.monotoneIncreasingDigits(110));
    }
}
