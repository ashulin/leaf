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

public class MatricesTest {
    private final Matrix solution = new Matrix();

    @Test
    public void testGenerateMatrix() {
        int[][] ints = solution.generateMatrix(3);
        Assertions.assertArrayEquals(new int[] {1, 2, 3}, ints[0]);
        Assertions.assertArrayEquals(new int[] {8, 9, 4}, ints[1]);
        Assertions.assertArrayEquals(new int[] {7, 6, 5}, ints[2]);
        ints = solution.generateMatrix(4);
        Assertions.assertArrayEquals(new int[] {1, 2, 3, 4}, ints[0]);
        Assertions.assertArrayEquals(new int[] {12, 13, 14, 5}, ints[1]);
        Assertions.assertArrayEquals(new int[] {11, 16, 15, 6}, ints[2]);
        Assertions.assertArrayEquals(new int[] {10, 9, 8, 7}, ints[3]);
    }

    @Test
    public void testSpiralOrder() {
        Assertions.assertArrayEquals(
                new Integer[] {1, 2, 3, 6, 9, 8, 7, 4, 5},
                solution.spiralOrder(new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})
                        .toArray(new Integer[0]));
        Assertions.assertArrayEquals(
                new Integer[] {1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7},
                solution.spiralOrder(new int[][] {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}})
                        .toArray(new Integer[0]));
        Assertions.assertArrayEquals(
                new Integer[] {6, 9, 7},
                solution.spiralOrder(new int[][] {{6, 9, 7}}).toArray(new Integer[0]));
    }
}
