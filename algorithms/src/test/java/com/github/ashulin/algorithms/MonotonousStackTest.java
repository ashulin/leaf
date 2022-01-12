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

public class MonotonousStackTest {
    private final MonotonousStack solution = new MonotonousStack();

    @Test
    public void testDailyTemperatures() {
        Assertions.assertArrayEquals(
                new int[] {1, 1, 4, 2, 1, 1, 0, 0},
                solution.dailyTemperatures(new int[] {73, 74, 75, 71, 69, 72, 76, 73}));
        Assertions.assertArrayEquals(
                new int[] {1, 1, 1, 0}, solution.dailyTemperatures(new int[] {30, 40, 50, 60}));
        Assertions.assertArrayEquals(
                new int[] {1, 1, 0}, solution.dailyTemperatures(new int[] {30, 60, 90}));
    }

    @Test
    public void testNextGreaterElement() {
        Assertions.assertArrayEquals(
                new int[] {-1, 3, -1},
                solution.nextGreaterElement(new int[] {4, 1, 2}, new int[] {1, 3, 4, 2}));
        Assertions.assertArrayEquals(
                new int[] {3, -1},
                solution.nextGreaterElement(new int[] {2, 4}, new int[] {1, 2, 3, 4}));
    }
}
