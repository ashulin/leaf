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

import java.util.List;

public class Assertions extends org.junit.jupiter.api.Assertions {
    public static void assertArrayEquals(int[] expected, List<Integer> actual) {
        org.junit.jupiter.api.Assertions.assertArrayEquals(
                expected, actual.stream().mapToInt(Integer::intValue).toArray());
    }

    public static void assertArrayEquals(int[][] expecteds, List<List<Integer>> actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expecteds.length, actual.size());
        int i = 0;
        for (List<Integer> list : actual) {
            assertArrayEquals(expecteds[i], list);
            i++;
        }
    }

    public static void assertArrayEquals(double[] expected, List<Double> actual) {
        org.junit.jupiter.api.Assertions.assertArrayEquals(
                expected, actual.stream().mapToDouble(Double::doubleValue).toArray());
    }
}
