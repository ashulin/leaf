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

public class StringsTest {
    private final Strings solution = new Strings();

    @Test
    public void testReverseString() {
        char[] chars = {'z', 'e', 'b', 'a'};
        solution.reverseString(chars);
        Assertions.assertArrayEquals(new char[] {'a', 'b', 'e', 'z'}, chars);
    }

    @Test
    public void testReverseStr() {
        Assertions.assertEquals("dcbaefghkji", solution.reverseStr("abcdefghijk", 4));
    }

    @Test
    public void testReplaceSpace() {
        Assertions.assertEquals("abc%20defghi%20jk", solution.replaceSpace("abc defghi jk"));
    }

    @Test
    public void testReverseWords() {
        Assertions.assertEquals("blue is sky the", solution.reverseWords("the sky is blue"));
        Assertions.assertEquals("world hello", solution.reverseWords("   hello world  "));
        Assertions.assertEquals("example good a", solution.reverseWords("a good   example"));
        Assertions.assertEquals(
                "blue is sky the", solution.reverseWords("the sky is blue".toCharArray()));
        Assertions.assertEquals(
                "world hello", solution.reverseWords("   hello world  ".toCharArray()));
        Assertions.assertEquals(
                "example good a", solution.reverseWords("a good   example".toCharArray()));
    }

    @Test
    public void testReverseLeftWords() {
        Assertions.assertEquals("cdefgab", solution.reverseLeftWords("abcdefg", 2));
        Assertions.assertEquals("umghlrlose", solution.reverseLeftWords("lrloseumgh", 6));
        Assertions.assertEquals("cdefgab", solution.reverseLeftWords("abcdefg".toCharArray(), 2));
        Assertions.assertEquals(
                "umghlrlose", solution.reverseLeftWords("lrloseumgh".toCharArray(), 6));
    }
}
