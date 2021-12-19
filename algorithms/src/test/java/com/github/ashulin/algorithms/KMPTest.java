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

public class KMPTest {
    KMP solution = new KMP();

    @Test
    public void testStrStr() {
        Assertions.assertEquals(2, solution.strStr("hello", "ll"));
        Assertions.assertEquals(-1, solution.strStr("aaaaa", "bba"));
        Assertions.assertEquals(0, solution.strStr("", ""));
        Assertions.assertEquals(16, solution.strStr("aaaaaaaaabaaaaceaaaabaaaacd", "aaaabaaaacd"));
        Assertions.assertEquals(4, solution.strStr("aabaaabaaac", "aabaaac"));
    }

    @Test
    public void testRepeatedSubstringPattern() {
        Assertions.assertTrue(solution.repeatedSubstringPattern("abaababaab"));
        Assertions.assertTrue(solution.repeatedSubstringPattern("abab"));
        Assertions.assertFalse(solution.repeatedSubstringPattern("aba"));
        Assertions.assertFalse(solution.repeatedSubstringPattern("abac"));
        Assertions.assertTrue(solution.repeatedSubstringPattern("abcabcabc"));
        Assertions.assertTrue(solution.repeatedSubstringPattern("abcabcabcabcabc"));
        Assertions.assertFalse(solution.repeatedSubstringPattern("abcdabc"));
        Assertions.assertFalse(solution.repeatedSubstringPattern("abcdefghabcd"));
    }
}
