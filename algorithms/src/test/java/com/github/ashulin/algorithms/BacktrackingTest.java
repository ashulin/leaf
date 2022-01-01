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

import java.util.Arrays;
import java.util.Collections;

public class BacktrackingTest {
    Backtracking solution = new Backtracking();

    @Test
    public void testCombine() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4}},
                solution.combine(4, 2));
        Assertions.assertArrayEquals(new int[][] {{1}}, solution.combine(1, 1));
    }

    @Test
    public void testCombinationSum() {
        Assertions.assertArrayEquals(
                new int[][] {{2, 2, 3}, {7}}, solution.combinationSum(new int[] {2, 3, 6, 7}, 7));
        Assertions.assertArrayEquals(
                new int[][] {{2, 2, 2, 2}, {2, 3, 3}, {3, 5}},
                solution.combinationSum(new int[] {2, 3, 5}, 8));
        Assertions.assertArrayEquals(new int[][] {}, solution.combinationSum(new int[] {2}, 1));
        Assertions.assertArrayEquals(new int[][] {{1}}, solution.combinationSum(new int[] {1}, 1));
        Assertions.assertArrayEquals(
                new int[][] {{1, 1}}, solution.combinationSum(new int[] {1}, 2));
    }

    @Test
    public void testCombinationSum2() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 1, 6}, {1, 2, 5}, {1, 7}, {2, 6}},
                solution.combinationSum2(new int[] {10, 1, 2, 7, 6, 1, 5}, 8));
        Assertions.assertArrayEquals(
                new int[][] {{1, 2, 2}, {5}},
                solution.combinationSum2(new int[] {2, 5, 2, 1, 2}, 5));
    }

    @Test
    public void testCombinationSum3() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 2, 6}, {1, 3, 5}, {2, 3, 4}}, solution.combinationSum3(3, 9));
        Assertions.assertArrayEquals(new int[][] {{1, 2, 4}}, solution.combinationSum3(3, 7));
    }

    @Test
    public void testLetterCombinations() {
        Assertions.assertEquals(
                Arrays.asList("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"),
                solution.letterCombinations("23"));
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), solution.letterCombinations("2"));
        Assertions.assertEquals(
                Arrays.asList("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"),
                solution.letterCombinations2("23"));
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), solution.letterCombinations2("2"));
    }

    @Test
    public void testPartition() {
        Assertions.assertArrayEquals(
                new String[][] {{"a", "a", "b"}, {"aa", "b"}}, solution.partition("aab"));
        Assertions.assertArrayEquals(
                new String[][] {{"a", "b", "a"}, {"aba"}}, solution.partition("aba"));
        Assertions.assertArrayEquals(
                new String[][] {{"a", "a", "b"}, {"aa", "b"}}, solution.partition2("aab"));
        Assertions.assertArrayEquals(
                new String[][] {{"a", "b", "a"}, {"aba"}}, solution.partition2("aba"));
    }

    @Test
    public void testRestoreIpAddresses() {
        Assertions.assertEquals(Collections.EMPTY_LIST, solution.restoreIpAddresses("000"));
        Assertions.assertEquals(
                Collections.EMPTY_LIST, solution.restoreIpAddresses("2552552552555"));
        Assertions.assertEquals(
                Arrays.asList("255.255.11.135", "255.255.111.35"),
                solution.restoreIpAddresses("25525511135"));
        Assertions.assertEquals(Arrays.asList("0.0.0.0"), solution.restoreIpAddresses("0000"));
        Assertions.assertEquals(
                Arrays.asList("255.255.255.255"), solution.restoreIpAddresses("255255255255"));
        Assertions.assertEquals(Arrays.asList("1.1.1.1"), solution.restoreIpAddresses("1111"));
        Assertions.assertEquals(
                Arrays.asList("0.10.0.10", "0.100.1.0"), solution.restoreIpAddresses("010010"));
        Assertions.assertEquals(
                Arrays.asList("1.0.10.23", "1.0.102.3", "10.1.0.23", "10.10.2.3", "101.0.2.3"),
                solution.restoreIpAddresses("101023"));
    }

    @Test
    public void testSubsets() {
        Assertions.assertArrayEquals(
                new int[][] {{}, {1}, {1, 2}, {1, 2, 3}, {1, 3}, {2}, {2, 3}, {3}},
                solution.subsets(new int[] {1, 2, 3}));
    }

    @Test
    public void testSubsetsWithDup() {
        Assertions.assertArrayEquals(
                new int[][] {{}, {1}, {1, 2}, {1, 2, 3}, {1, 3}, {2}, {2, 3}, {3}},
                solution.subsetsWithDup(new int[] {1, 2, 3}));
        Assertions.assertArrayEquals(
                new int[][] {{}, {1}, {1, 2}, {1, 2, 2}, {2}, {2, 2}},
                solution.subsetsWithDup(new int[] {1, 2, 2}));
    }

    @Test
    public void testFindSubsequences() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 2}, {1, 2, 3}, {1, 3}, {2, 3}},
                solution.findSubsequences(new int[] {1, 2, 3}));
        Assertions.assertArrayEquals(
                new int[][] {{1, 2}, {1, 2, 2}, {2, 2}},
                solution.findSubsequences(new int[] {1, 2, 2}));
        Assertions.assertArrayEquals(
                new int[][] {{4, 4}}, solution.findSubsequences(new int[] {4, 4, 3, 2, 1}));
        Assertions.assertArrayEquals(
                new int[][] {
                    {4, 6}, {4, 6, 7}, {4, 6, 7, 7}, {4, 7}, {4, 7, 7}, {6, 7}, {6, 7, 7}, {7, 7}
                },
                solution.findSubsequences(new int[] {4, 6, 7, 7}));
        Assertions.assertArrayEquals(
                new int[][] {{1, 4}, {1, 1}, {1, 1, 1}},
                solution.findSubsequences(new int[] {1, 4, 1, 1}));
    }

    @Test
    public void testPermute() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}},
                solution.permute(new int[] {1, 2, 3}));
    }

    @Test
    public void testPermuteUnique() {
        Assertions.assertArrayEquals(
                new int[][] {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}},
                solution.permuteUnique(new int[] {1, 2, 3}));
        Assertions.assertArrayEquals(
                new int[][] {{1, 1, 2}, {1, 2, 1}, {2, 1, 1}},
                solution.permuteUnique(new int[] {1, 1, 2}));
        Assertions.assertArrayEquals(
                new int[][] {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}},
                solution.permuteUnique2(new int[] {1, 2, 3}));
        Assertions.assertArrayEquals(
                new int[][] {{1, 1, 2}, {1, 2, 1}, {2, 1, 1}},
                solution.permuteUnique2(new int[] {1, 1, 2}));
    }
}
