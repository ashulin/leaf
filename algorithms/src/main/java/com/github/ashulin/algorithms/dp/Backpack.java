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
import java.util.List;

@Tag(Type.DP)
public class Backpack {

    /** 给你一个 只包含正整数 的 非空 数组 nums 。请你判断是否可以将这个数组分割成两个子集，使得两个子集的元素和相等。 */
    @Source(416)
    public boolean canPartition(int[] nums) {
        if (nums == null || nums.length < 2) {
            return false;
        }
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        if (sum % 2 == 1) {
            return false;
        }
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : nums) {
            // 为true时，即前面的数可以组合为当前index的值
            for (int j = target; j >= num; j--) {
                // dp[j]为不选择该数，dp[j-num]为选择该数
                dp[j] = dp[j] | dp[j - num];
                if (j == target && dp[j]) {
                    return true;
                }
            }
        }
        return dp[target];
    }

    /**
     * 有一堆石头，用整数数组stones 表示。其中stones[i] 表示第 i 块石头的重量。
     *
     * <p>每一回合，从中选出任意两块石头，然后将它们一起粉碎。假设石头的重量分别为x 和y，且x <= y。那么粉碎的可能结果如下：
     *
     * <p>如果x == y，那么两块石头都会被完全粉碎； 如果x != y，那么重量为x的石头将会完全粉碎，而重量为y的石头新重量为y-x。 最后，最多只会剩下一块 石头。返回此石头
     * 最小的可能重量 。如果没有石头剩下，就返回 0。
     */
    @Source(1049)
    public int lastStoneWeightII(int[] stones) {
        int sum = 0;
        for (int stone : stones) {
            sum += stone;
        }

        int target = sum >> 1;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        int ans = 0;
        for (int stone : stones) {
            for (int i = target; i >= stone; i--) {
                // dp[i]为不选择该数，dp[i-num]为选择该数
                dp[i] = dp[i] | dp[i - stone];
                // 为true时，即前面的数可以组合为当前index的值
                if (dp[i]) {
                    ans = Math.max(ans, i);
                }
            }
        }
        return sum - 2 * ans;
    }

    /**
     * 给你一个整数数组 nums 和一个整数 target 。
     *
     * <p>向数组中的每个整数前添加'+' 或 '-' ，然后串联起所有整数，可以构造一个 表达式 ：
     *
     * <p>例如，nums = [2, 1] ，可以在 2 之前添加 '+' ，在 1 之前添加 '-' ，然后串联起来得到表达式 "+2-1" 。 返回可以通过上述方法构造的、运算结果等于
     * target 的不同 表达式 的数目。
     *
     * <p>1 <= nums.length <= 20 0 <= nums[i] <= 1000 0 <= sum(nums[i]) <= 1000 -1000 <= target <=
     * 1000
     */
    @Source(494)
    public int findTargetSumWays(int[] nums, int target) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }

        int min = Math.min(sum + target, sum - target);
        // 0 <= sum(nums[i]) <= 1000, -1000 <= target <= 1000
        if (min < 0) {
            return 0;
        }
        if (min % 2 != 0) {
            return 0;
        }

        int size = min >> 1;
        int[] dp = new int[size + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int i = size; i >= num; i--) {
                dp[i] += dp[i - num];
            }
        }
        return dp[size];
    }

    /**
     * 给你一个二进制字符串数组 strs 和两个整数 m 和 n 。
     *
     * <p>请你找出并返回 strs 的最大子集的长度，该子集中 最多 有 m 个 0 和 n 个 1 。
     *
     * <p>如果 x 的所有元素也是 y 的元素，集合 x 是集合 y 的 子集 。
     */
    @Source(474)
    public int findMaxForm(String[] strs, int m, int n) {
        int[][] dp = new int[m + 1][n + 1];
        for (String str : strs) {
            int zeroCounter = getZeroCounter(str);
            int oneCounter = str.length() - zeroCounter;
            for (int i = m; i >= zeroCounter; i--) {
                for (int j = n; j >= oneCounter; j--) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - zeroCounter][j - oneCounter] + 1);
                }
            }
        }
        return dp[m][n];
    }

    private static int getZeroCounter(String str) {
        int zeroCounter = 0;
        for (int i = 0; i < str.length(); i++) {
            if ('0' == str.charAt(i)) {
                zeroCounter++;
            }
        }
        return zeroCounter;
    }

    /**
     * 给你一个整数数组 coins 表示不同面额的硬币，另给一个整数 amount 表示总金额。
     *
     * <p>请你计算并返回可以凑成总金额的硬币组合数。如果任何硬币组合都无法凑出总金额，返回 0 。
     *
     * <p>假设每一种面额的硬币有无限个。
     *
     * <p>题目数据保证结果符合 32 位带符号整数。
     */
    @Source(518)
    @Source(377)
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }
        return dp[amount];
    }

    /**
     * 给你一个由 不同 整数组成的数组 nums ，和一个目标整数 target 。请你从 nums 中找出并返回总和为 target 的元素组合的个数。
     *
     * <p>题目数据保证答案符合 32 位整数范围。
     *
     * <p>顺序不同的序列被视作不同的组合。
     */
    @Source(377)
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1;
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (i >= num) {
                    dp[i] += dp[i - num];
                }
            }
        }
        return dp[target];
    }

    /**
     * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
     *
     * <p>每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
     *
     * <p>注意：给定 n 是一个正整数。
     */
    @Source(70)
    public int climbStairs(int n) {
        int[] dp = new int[n + 1];
        int[] steps = {1, 2};
        dp[0] = 1;

        // 排列时，先遍历背包
        for (int i = 0; i <= n; i++) {
            for (int step : steps) {
                if (i >= step) {
                    dp[i] += dp[i - step];
                }
            }
        }

        return dp[n];
    }

    /**
     * 给你一个整数数组 coins ，表示不同面额的硬币；以及一个整数 amount ，表示总金额。
     *
     * <p>计算并返回可以凑成总金额所需的 最少的硬币个数 。如果没有任何一种硬币组合能组成总金额，返回-1 。
     *
     * <p>你可以认为每种硬币的数量是无限的。
     *
     * <p>1 <= coins[i] <= 231 - 1 0 <= amount <= 104
     */
    @Source(322)
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (i >= coin) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    /**
     * 给定正整数n，找到若干个完全平方数（比如1, 4, 9, 16, ...）使得它们的和等于 n。你需要让组成和的完全平方数的个数最少。
     *
     * <p>给你一个整数 n ，返回和为 n 的完全平方数的 最少数量 。
     *
     * <p>完全平方数 是一个整数，其值等于另一个整数的平方；换句话说，其值等于一个整数自乘的积。例如，1、4、9 和 16 都是完全平方数，而 3 和 11 不是。
     */
    @Source(279)
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int minn = Integer.MAX_VALUE;
            for (int j = 1; j * j <= i; j++) {
                minn = Math.min(minn, dp[i - j * j]);
            }
            dp[i] = minn + 1;
        }
        return dp[n];
    }

    /**
     * 给你一个字符串 s 和一个字符串列表 wordDict 作为字典。请你判断是否可以利用字典中出现的单词拼接出 s 。
     *
     * <p>注意：不要求字典中出现的单词全部都使用，并且字典中的单词可以重复使用。
     */
    @Source(139)
    public boolean wordBreak(String s, List<String> wordDict) {
        final boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        int endIndex;
        for (int i = 1; i <= s.length(); i++) {
            if (!dp[i - 1]) {
                continue;
            }
            for (String word : wordDict) {
                endIndex = i - 1 + word.length();
                if (endIndex > s.length()) {
                    continue;
                }
                if (word.equals(s.substring(i - 1, endIndex))) {
                    dp[endIndex] = true;
                }
            }
        }
        return dp[s.length()];
    }
}
