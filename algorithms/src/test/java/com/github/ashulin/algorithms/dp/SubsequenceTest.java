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

public class SubsequenceTest {
    private final Subsequence solution = new Subsequence();

    @Test
    public void testLengthOfLIS() {
        Assertions.assertEquals(4, solution.lengthOfLIS(new int[] {10, 9, 2, 5, 3, 7, 101, 18}));
        Assertions.assertEquals(4, solution.lengthOfLIS(new int[] {0, 1, 0, 3, 2, 3}));
        Assertions.assertEquals(1, solution.lengthOfLIS(new int[] {7, 7, 7, 7, 7, 7, 7}));
    }

    @Test
    public void testFindLengthOfLCIS() {
        Assertions.assertEquals(3, solution.findLengthOfLCIS(new int[] {1, 3, 5, 4, 7}));
        Assertions.assertEquals(1, solution.findLengthOfLCIS(new int[] {7, 7, 7, 7, 7, 7, 7}));
    }

    @Test
    public void testFindLength() {
        Assertions.assertEquals(
                3, solution.findLength(new int[] {1, 2, 3, 2, 1}, new int[] {3, 2, 1, 4, 7}));
    }

    @Test
    public void testLongestCommonSubsequence() {
        Assertions.assertEquals(3, solution.longestCommonSubsequence("abcde", "ace"));
        Assertions.assertEquals(3, solution.longestCommonSubsequence("abcde", "abc"));
        Assertions.assertEquals(0, solution.longestCommonSubsequence("abc", "edf"));
    }

    @Test
    public void testMaxUncrossedLines() {
        Assertions.assertEquals(
                2, solution.maxUncrossedLines(new int[] {1, 4, 2}, new int[] {1, 2, 4}));
        Assertions.assertEquals(
                3,
                solution.maxUncrossedLines(
                        new int[] {2, 5, 1, 2, 5}, new int[] {10, 5, 2, 1, 5, 2}));
        Assertions.assertEquals(
                2,
                solution.maxUncrossedLines(
                        new int[] {1, 3, 7, 1, 7, 5}, new int[] {1, 9, 2, 5, 1}));
    }

    @Test
    public void testIsSubsequence() {
        Assertions.assertTrue(solution.isSubsequence("abc", "ahbgdc"));
        Assertions.assertFalse(solution.isSubsequence("axc", "ahbgdc"));
    }

    @Test
    public void testNumDistinct() {
        Assertions.assertEquals(3, solution.numDistinct("rabbbit", "rabbit"));
        Assertions.assertEquals(5, solution.numDistinct("babgbag", "bag"));
    }

    @Test
    public void testMinDistance() {
        Assertions.assertEquals(2, solution.minDistance("abcde", "ace"));
        Assertions.assertEquals(2, solution.minDistance("abcde", "abc"));
        Assertions.assertEquals(6, solution.minDistance("abc", "edf"));
        Assertions.assertEquals(2, solution.minDistance("sea", "eat"));
    }

    @Test
    public void testMinDistanceWithReplace() {
        Assertions.assertEquals(3, solution.minDistanceWithReplace("horse", "ros"));
        Assertions.assertEquals(5, solution.minDistanceWithReplace("intention", "execution"));
    }

    @Test
    public void testCountSubstrings() {
        Assertions.assertEquals(3, solution.countSubstrings("abc"));
        Assertions.assertEquals(6, solution.countSubstrings("aaa"));
    }

    @Test
    public void testLongestPalindromeSubseq() {
        Assertions.assertEquals(4, solution.longestPalindromeSubseq("bbbab"));
        Assertions.assertEquals(2, solution.longestPalindromeSubseq("cbbd"));
    }
}
