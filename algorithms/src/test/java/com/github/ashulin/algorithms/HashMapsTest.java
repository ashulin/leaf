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

import java.util.List;

public class HashMapsTest {
    private final HashMaps solution = new HashMaps();

    @Test
    public void testIsAnagram() {
        Assertions.assertTrue(solution.isAnagram("anagram", "nagaram"));
        Assertions.assertFalse(solution.isAnagram("anagram", "nagarah"));
        Assertions.assertFalse(solution.isAnagram("anagram", "nagarama"));
        Assertions.assertFalse(solution.isAnagram("ab", "a"));
    }

    @Test
    public void testCanConstruct() {
        Assertions.assertTrue(solution.canConstruct("anagram", "nagaram"));
        Assertions.assertFalse(solution.canConstruct("anagram", "nagarah"));
        Assertions.assertTrue(solution.canConstruct("anagram", "nagarama"));
        Assertions.assertFalse(solution.canConstruct("ab", "a"));
    }

    @Test
    public void testGroupAnagrams() {
        List<List<String>> result =
                solution.groupAnagrams(new String[] {"eat", "tea", "tan", "ate", "nat", "bat"});
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testFindAnagrams() {
        Assertions.assertArrayEquals(new int[] {0, 6}, solution.findAnagrams("cbaebabacd", "abc"));
        Assertions.assertArrayEquals(new int[] {0, 1, 2}, solution.findAnagrams("abab", "ab"));
    }

    @Test
    public void testIntersection() {
        Assertions.assertArrayEquals(
                new int[] {2}, solution.intersection(new int[] {1, 2, 2, 1}, new int[] {2, 2}));
        Assertions.assertArrayEquals(
                new int[] {9, 4},
                solution.intersection(new int[] {4, 9, 5}, new int[] {9, 4, 9, 8, 4}));
    }

    @Test
    public void testIntersect() {
        Assertions.assertArrayEquals(
                new int[] {2, 2}, solution.intersect(new int[] {1, 2, 2, 1}, new int[] {2, 2}));
        Assertions.assertArrayEquals(
                new int[] {9, 4},
                solution.intersect(new int[] {4, 9, 5}, new int[] {9, 4, 9, 8, 4}));
    }

    @Test
    public void testIsHappy() {
        Assertions.assertTrue(solution.isHappy(19));
        Assertions.assertFalse(solution.isHappy(2));
    }

    @Test
    public void testTwoSum() {
        Assertions.assertArrayEquals(
                new int[] {0, 1}, solution.twoSum(new int[] {2, 7, 11, 15}, 9));
        Assertions.assertArrayEquals(new int[] {1, 2}, solution.twoSum(new int[] {3, 2, 4}, 6));
        Assertions.assertArrayEquals(new int[] {0, 1}, solution.twoSum(new int[] {3, 3}, 6));
    }

    @Test
    public void testFourSumCount() {
        Assertions.assertEquals(
                2,
                solution.fourSumCount(
                        new int[] {1, 2}, new int[] {-2, -1}, new int[] {-1, 2}, new int[] {2, 0}));
        Assertions.assertEquals(
                1,
                solution.fourSumCount(new int[] {0}, new int[] {0}, new int[] {0}, new int[] {0}));
    }

    @Test
    public void testThreeSum() {
        Assertions.assertArrayEquals(
                new int[][] {{-1, -1, 2}, {-1, 0, 1}},
                solution.threeSum(new int[] {-1, 0, 1, 2, -1, -4}));
        Assertions.assertArrayEquals(
                new int[][] {{0, 0, 0}}, solution.threeSum(new int[] {0, 0, 0, 0, 0, 0}));
    }

    @Test
    public void testFourSum() {
        Assertions.assertArrayEquals(
                new int[][] {{-2, -1, 1, 2}, {-2, 0, 0, 2}, {-1, 0, 0, 1}},
                solution.fourSum(new int[] {1, 0, -1, 0, -2, 2}, 0));
        Assertions.assertArrayEquals(
                new int[][] {{2, 2, 2, 2}}, solution.fourSum(new int[] {2, 2, 2, 2, 2, 2}, 8));
    }
}
