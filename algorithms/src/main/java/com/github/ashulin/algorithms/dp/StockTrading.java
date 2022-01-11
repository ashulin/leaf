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

import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

import java.util.Arrays;

@Tag(Type.DP)
public class StockTrading {

    /**
     * 给定一个数组 prices ，它的第i 个元素prices[i] 表示一支给定股票第 i 天的价格。
     *
     * <p>你只能选择 某一天 买入这只股票，并选择在 未来的某一个不同的日子 卖出该股票。设计一个算法来计算你所能获取的最大利润。
     *
     * <p>返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 0 。
     */
    @Source(121)
    public int maxProfit(int[] prices) {
        int hold = Integer.MAX_VALUE;
        int sale = 0;
        for (int price : prices) {
            hold = Math.min(hold, price);
            sale = Math.max(sale, price - hold);
        }
        return sale;
    }

    /**
     * 给定一个数组 prices ，其中prices[i] 是一支给定股票第 i 天的价格。
     *
     * <p>设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
     *
     * <p>注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）
     */
    @Source(122)
    public int maxProfitUnlimited(int[] prices) {
        return maxProfitGreedy(prices);
    }

    private int maxProfitGreedy(int[] prices) {
        int sale = 0;
        int hold = -prices[0];
        for (int i = 1; i < prices.length; i++) {
            hold = Math.max(hold, sale - prices[i]);
            sale = Math.max(sale, hold + prices[i]);
        }
        return sale;
    }

    /**
     * 给定一个数组，它的第 i 个元素是一支给定的股票在第 i 天的价格。
     *
     * <p>设计一个算法来计算你所能获取的最大利润。你最多可以完成两笔交易。
     *
     * <p>注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
     */
    @Source(123)
    public int maxProfitTwice(int[] prices) {
        int buy1 = -prices[0];
        int sale1 = 0;
        int buy2 = -prices[0];
        int sale2 = 0;
        for (int i = 1; i < prices.length; i++) {
            buy1 = Math.max(buy1, -prices[i]);
            sale1 = Math.max(sale1, buy1 + prices[i]);
            buy2 = Math.max(buy2, sale1 - prices[i]);
            sale2 = Math.max(sale2, buy2 + prices[i]);
        }
        return sale2;
    }

    /**
     * 给定一个整数数组prices ，它的第 i 个元素prices[i] 是一支给定的股票在第 i 天的价格。
     *
     * <p>设计一个算法来计算你所能获取的最大利润。你最多可以完成 k 笔交易。
     *
     * <p>注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
     */
    @Source(188)
    public int maxProfitKth(int k, int[] prices) {
        if (prices == null || prices.length == 0 || k == 0) {
            return 0;
        }
        // 特例优化
        if (k >= prices.length / 2) {
            return maxProfitGreedy(prices);
        }
        int[][] dp = new int[2][k];
        // 买入状态
        Arrays.fill(dp[0], -prices[0]);
        for (int i = 1; i < prices.length; i++) {
            for (int j = 0; j < k; j++) {
                dp[0][j] = Math.max(dp[0][j], (j == 0 ? 0 : dp[1][j - 1]) - prices[i]);
                dp[1][j] = Math.max(dp[1][j], dp[0][j] + prices[i]);
            }
        }
        return dp[1][k - 1];
    }

    /**
     * 给定一个整数数组，其中第i个元素代表了第i天的股票价格 。
     *
     * <p>设计一个算法计算出最大利润。在满足以下约束条件下，你可以尽可能地完成更多的交易（多次买卖一支股票）:
     *
     * <p>你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。 卖出股票后，你无法在第二天买入股票 (即冷冻期为 1 天)。
     */
    @Source(309)
    public int maxProfitWithFreeze(int[] prices) {
        int[] dp = new int[4];
        dp[0] = -prices[0];
        for (int i = 1; i <= prices.length; i++) {
            // 不持有股票，且今天不做购买操作
            int freeze = dp[1];
            // 持有股票 或 今天买进
            int buy = Math.max(dp[0], dp[2] - prices[i - 1]);
            // 不持有股票 或 今天卖出
            int sale = Math.max(dp[1], dp[0] + prices[i - 1]);
            dp[0] = buy;
            dp[1] = sale;
            dp[2] = freeze;
        }
        return dp[1];
    }

    /**
     * 给定一个整数数组prices，其中第i个元素代表了第i天的股票价格 ；整数fee 代表了交易股票的手续费用。
     *
     * <p>你可以无限次地完成交易，但是你每笔交易都需要付手续费。如果你已经购买了一个股票，在卖出它之前你就不能再继续购买股票了。
     *
     * <p>返回获得利润的最大值。
     *
     * <p>注意：这里的一笔交易指买入持有并卖出股票的整个过程，每笔交易你只需要为支付一次手续费。
     */
    @Source(714)
    public int maxProfitWithFee(int[] prices, int fee) {
        // 持股票, 表示当天持有股票的最大收益
        int holdStock = -prices[0];
        // 卖出股票, 表示当天不持有股票的最大收益
        int saleStock = 0;
        for (int i = 1; i < prices.length; i++) {
            holdStock = Math.max(holdStock, saleStock - prices[i]);
            saleStock = Math.max(saleStock, holdStock + prices[i] - fee);
        }
        return saleStock;
    }
}
