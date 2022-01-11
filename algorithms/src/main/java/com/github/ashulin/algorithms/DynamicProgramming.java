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

import com.github.ashulin.algorithms.BinaryTree.TreeNode;
import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;

public class DynamicProgramming {

    /**
     * 如果连续数字之间的差严格地在正数和负数之间交替，则数字序列称为 摆动序列 。第一个差（如果存在的话）可能是正数或负数。
     *
     * <p>仅有一个元素或者含两个不等元素的序列也视作摆动序列。
     *
     * <p>例如，[1, 7, 4, 9, 2, 5] 是一个 摆动序列 ，因为差值 (6, -3, 5, -7, 3)是正负交替出现的。
     *
     * <p>相反，[1, 4, 7, 2, 5]和[1, 7, 4, 5, 5] 不是摆动序列，第一个序列是因为它的前两个差值都是正数，第二个序列是因为它的最后一个差值为零。 子序列
     * 可以通过从原始序列中删除一些（也可以不删除）元素来获得，剩下的元素保持其原始顺序。
     *
     * <p>给你一个整数数组 nums ，返回 nums 中作为 摆动序列 的 最长子序列的长度 。
     */
    @Source(376)
    public int wiggleMaxLength(int[] nums) {
        if (nums.length < 2) {
            return nums.length;
        }

        int up = 1;
        int down = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > nums[i - 1]) {
                up = down + 1;
            }
            if (nums[i] < nums[i - 1]) {
                down = up + 1;
            }
        }
        return Math.max(up, down);
    }

    /**
     * 给你一个整数数组 nums ，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
     *
     * <p>子数组 是数组中的一个连续部分。
     */
    @Source(53)
    public int maxSubArray(int[] nums) {
        int ans = Integer.MIN_VALUE;
        int[] dp = new int[nums.length + 1];
        for (int i = 1; i <= nums.length; i++) {
            dp[i] = Math.max(dp[i - 1] + nums[i - 1], nums[i - 1]);
            ans = Math.max(dp[i], ans);
        }
        return ans;
    }

    /**
     * 斐波那契数，通常用 F(n) 表示，形成的序列称为 斐波那契数列 。该数列由 0 和 1 开始，后面的每一项数字都是前面两项数字的和。
     *
     * <p>其中：F(0) = 0，F(1) = 1
     */
    @Source(509)
    public int fib(int n) {
        if (n < 2) {
            return n;
        }
        int pre2 = 0;
        int pre1 = 1;
        int ans = 0;
        int num = 2;
        while (num++ <= n) {
            ans = pre1 + pre2;
            pre2 = pre1;
            pre1 = ans;
        }
        return ans;
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
        if (n < 3) {
            return n;
        }
        int pre2 = 1;
        int pre1 = 2;
        int fn = 0;
        int num = 3;
        while (num++ <= n) {
            // f(n) = f(n - 1) + f(n - 2), (n > 2)
            fn = pre1 + pre2;
            pre2 = pre1;
            pre1 = fn;
        }
        return fn;
    }

    /**
     * 给你一个整数数组 cost ，其中 cost[i] 是从楼梯第 i 个台阶向上爬需要支付的费用。一旦你支付此费用，即可选择向上爬一个或者两个台阶。
     *
     * <p>你可以选择从下标为 0 或下标为 1 的台阶开始爬楼梯。
     *
     * <p>请你计算并返回达到楼梯顶部的最低花费。
     *
     * <p>2 <= cost.length <= 1000 0 <= cost[i] <= 999
     */
    @Source(746)
    public int minCostClimbingStairs(int[] cost) {
        int preMin1 = cost[1];
        int preMin2 = cost[0];
        int fn = 0;
        for (int n = 2; n <= cost.length; n++) {
            // f(n) = min(f(n-1), f(n-2)) + cost(n)
            fn = Math.min(preMin1, preMin2) + (n == cost.length ? 0 : cost[n]);
            preMin2 = preMin1;
            preMin1 = fn;
        }
        return fn;
    }

    /**
     * 一个机器人位于一个 m x n网格的左上角 （起始点在下图中标记为 “Start” ）。
     *
     * <p>机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为 “Finish” ）。
     *
     * <p>问总共有多少条不同的路径？
     *
     * <p>1 <= m, n <= 100
     */
    @Source(62)
    @Complexity(time = "O(m*n)", space = "O(min(m,n))")
    public int uniquePaths(int m, int n) {
        if (m > n) {
            int temp = n;
            n = m;
            m = temp;
        }
        int[] dp = new int[m];
        dp[0] = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < m; j++) {
                dp[j] += dp[j - 1];
            }
        }
        return dp[m - 1];
    }

    /**
     * 一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为“Start” ）。
     *
     * <p>机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为“Finish”）。
     *
     * <p>现在考虑网格中有障碍物。那么从左上角到右下角将会有多少条不同的路径？
     *
     * <p>网格中的障碍物和空位置分别用 1 和 0 来表示。
     */
    @Source(63)
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid[0].length;
        int n = obstacleGrid.length;
        int[] dp = new int[m];
        dp[0] = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[j] = 0;
                } else if (j > 0) {
                    dp[j] += dp[j - 1];
                }
            }
        }
        return dp[m - 1];
    }

    /**
     * 给定一个正整数 n，将其拆分为至少两个正整数的和，并使这些整数的乘积最大化。 返回你可以获得的最大乘积。
     *
     * <p>你可以假设 2 <= n <= 58。
     */
    @Source(343)
    public int integerBreak(int n) {
        if (n <= 3) {
            return n - 1;
        }
        int ans = 1;
        // 经过简单测试可得：拆成最多的3时值最大；<=4时继续拆分值将变小
        while (n > 4) {
            ans *= 3;
            n -= 3;
        }
        return ans * n;
    }

    /**
     * 给你一个整数 n ，求恰由 n 个节点组成且节点值从 1 到 n 互不相同的 二叉搜索树 有多少种？返回满足题意的二叉搜索树的种数。
     *
     * <p>1 <= n <= 19
     */
    @Source(96)
    public int numTrees(int n) {
        // 初始化 dp 数组
        int[] dp = new int[n + 1];
        // 初始化0个节点和1个节点的情况
        dp[0] = 1;
        dp[1] = 1;
        // f(n) = sum(f(i-1)*f(n-i)), 其中i:[1,n)
        for (int i = 2; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                // 对于第i个节点，需要考虑1作为根节点直到i作为根节点的情况，所以需要累加
                // 一共i个节点，对于根节点j时,左子树的节点个数为j-1，右子树的节点个数为i-j
                dp[i] += dp[j - 1] * dp[i - j];
            }
        }
        return dp[n];
    }

    /**
     * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统:
     *
     * <p>如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
     *
     * <p>给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额。
     */
    @Source(198)
    public int rob(int[] nums) {
        int[][] dp = new int[2][nums.length + 1];
        for (int i = 1; i <= nums.length; i++) {
            // 不偷窃
            dp[0][i] = Math.max(dp[0][i - 1], dp[1][i - 1]);
            // 偷窃
            dp[1][i] = dp[0][i - 1] + nums[i - 1];
        }
        return Math.max(dp[0][nums.length], dp[1][nums.length]);
    }

    @Source(198)
    public int rob2(int[] nums) {
        int select = 0;
        int unselect = 0;
        for (int num : nums) {
            int temp = unselect;
            unselect = Math.max(select, unselect);
            select = temp + num;
        }
        return Math.max(select, unselect);
    }

    /**
     * 你是一个专业的小偷，计划偷窃沿街的房屋，每间房内都藏有一定的现金。这个地方所有的房屋都 围成一圈
     * ，这意味着第一个房屋和最后一个房屋是紧挨着的。同时，相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警 。
     *
     * <p>给定一个代表每个房屋存放金额的非负整数数组，计算你 在不触动警报装置的情况下 ，今晚能够偷窃到的最高金额。
     */
    @Source(213)
    public int rob3(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        int[] ans = rob(nums, true);
        if (ans[0] >= ans[1]) {
            // 不选择最后的房屋金额也是最大的
            return ans[0];
        }
        return Math.max(rob(nums, false)[0], ans[0]);
    }

    private int[] rob(int[] nums, boolean positive) {
        int[] ans = new int[2];
        for (int i = 0; i < nums.length; i++) {
            int temp = ans[0];
            ans[0] = Math.max(ans[0], ans[1]);
            ans[1] = temp + nums[positive ? i : nums.length - 1 - i];
        }
        return ans;
    }

    /**
     * 在上次打劫完一条街道之后和一圈房屋后，小偷又发现了一个新的可行窃的地区。这个地区只有一个入口，我们称之为“根”。
     *
     * <p>除了“根”之外，每栋房子有且只有一个“父“房子与之相连。一番侦察之后，聪明的小偷意识到“这个地方的所有房屋的排列类似于一棵二叉树”。
     *
     * <p>如果两个直接相连的房子在同一天晚上被打劫，房屋将自动报警。
     *
     * <p>计算在不触动警报的情况下，小偷一晚能够盗取的最高金额。
     */
    @Source(337)
    public int rob(TreeNode root) {
        int[] ans = robMoney(root);
        return Math.max(ans[0], ans[1]);
    }

    private int[] robMoney(TreeNode root) {
        int[] ans = new int[2];
        if (root == null) {
            return ans;
        }
        int[] left = robMoney(root.left);
        int[] right = robMoney(root.right);
        ans[0] = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        ans[1] = root.val + left[0] + right[0];
        return ans;
    }
}
