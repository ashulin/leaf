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
}
