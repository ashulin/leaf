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

public class StackAndQueueTest {
    StackAndQueue solution = new StackAndQueue();

    @Test
    public void testEvalRPN() {
        Assertions.assertEquals(9, solution.evalRPN(new String[] {"2", "1", "+", "3", "*"}));
        Assertions.assertEquals(6, solution.evalRPN(new String[] {"4", "13", "5", "/", "+"}));
        Assertions.assertEquals(
                22,
                solution.evalRPN(
                        new String[] {
                            "10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"
                        }));
    }

    @Test
    public void testMaxSlidingWindow() {
        Assertions.assertArrayEquals(
                new int[] {3, 3, 5, 5, 6, 7},
                solution.maxSlidingWindow(new int[] {1, 3, -1, -3, 5, 3, 6, 7}, 3));
        Assertions.assertArrayEquals(
                new int[] {4}, solution.maxSlidingWindow(new int[] {4, -2}, 2));
    }

    @Test
    public void testTopKFrequent() {
        Assertions.assertArrayEquals(
                new int[] {1, 2}, solution.topKFrequent(new int[] {1, 1, 1, 2, 2, 3}, 2));
        Assertions.assertArrayEquals(new int[] {2}, solution.topKFrequent(new int[] {1, 2, 2}, 1));
    }

    @Test
    public void testSimplifyPath() {
        Assertions.assertEquals("/c", solution.simplifyPath("/a/./b/../../c/"));
        Assertions.assertEquals("/", solution.simplifyPath("/../"));
        Assertions.assertEquals("/...", solution.simplifyPath("/.../"));
        Assertions.assertEquals("/home", solution.simplifyPath("/home/"));
        Assertions.assertEquals("/home/foo", solution.simplifyPath("/home//foo/"));
        Assertions.assertEquals("/a/b/c", solution.simplifyPath("/a//b////c/d//././/.."));
        Assertions.assertEquals("/c", solution.simplifyPath("/a/../../b/../c//.//"));
        Assertions.assertEquals("/..hidden", solution.simplifyPath("/..hidden"));
        Assertions.assertEquals("/hello../world", solution.simplifyPath("/hello../world"));
        Assertions.assertEquals("/c", solution.simplifyPath2("/a/./b/../../c/"));
        Assertions.assertEquals("/", solution.simplifyPath2("/../"));
        Assertions.assertEquals("/...", solution.simplifyPath2("/.../"));
        Assertions.assertEquals("/home", solution.simplifyPath2("/home/"));
        Assertions.assertEquals("/home/foo", solution.simplifyPath2("/home//foo/"));
        Assertions.assertEquals("/a/b/c", solution.simplifyPath2("/a//b////c/d//././/.."));
        Assertions.assertEquals("/c", solution.simplifyPath2("/a/../../b/../c//.//"));
        Assertions.assertEquals("/..hidden", solution.simplifyPath2("/..hidden"));
        Assertions.assertEquals("/hello../world", solution.simplifyPath2("/hello../world"));
    }
}
