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

public class StockTradingTest {
    private final StockTrading solution = new StockTrading();

    @Test
    public void testMaxProfit() {
        Assertions.assertEquals(5, solution.maxProfit(new int[] {7, 1, 5, 3, 6, 4}));
        Assertions.assertEquals(0, solution.maxProfit(new int[] {7, 6, 4, 3, 1}));
    }

    @Test
    public void testMaxProfitUnlimited() {
        Assertions.assertEquals(0, solution.maxProfitUnlimited(new int[] {7, 6, 4, 3, 1}));
        Assertions.assertEquals(7, solution.maxProfitUnlimited(new int[] {7, 1, 5, 3, 6, 4}));
        Assertions.assertEquals(4, solution.maxProfitUnlimited(new int[] {1, 2, 3, 4, 5}));
    }

    @Test
    public void testMaxProfitTwice() {
        Assertions.assertEquals(6, solution.maxProfitTwice(new int[] {3, 3, 5, 0, 0, 3, 1, 4}));
    }

    @Test
    public void testMaxProfitKth() {
        Assertions.assertEquals(2, solution.maxProfitKth(2, new int[] {2, 4, 1}));
        Assertions.assertEquals(7, solution.maxProfitKth(2, new int[] {3, 2, 6, 5, 0, 3}));
    }

    @Test
    public void testMaxProfitWithFee() {
        Assertions.assertEquals(8, solution.maxProfitWithFee(new int[] {1, 3, 2, 8, 4, 9}, 2));
        Assertions.assertEquals(6, solution.maxProfitWithFee(new int[] {1, 3, 7, 5, 10, 3}, 3));
    }

    @Test
    public void testMaxProfitWithFreeze() {
        Assertions.assertEquals(3, solution.maxProfitWithFreeze(new int[] {1, 2, 3, 0, 2}));
    }
}
