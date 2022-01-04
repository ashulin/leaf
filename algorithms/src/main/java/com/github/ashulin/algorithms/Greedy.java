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

import java.util.Arrays;

public class Greedy {

    /**
     * 假设你是一位很棒的家长，想要给你的孩子们一些小饼干。但是，每个孩子最多只能给一块饼干。
     *
     * <p>对每个孩子 i，都有一个胃口值g[i]，这是能让孩子们满足胃口的饼干的最小尺寸；并且每块饼干 j，都有一个尺寸 s[j]。如果 s[j]>= g[i]，我们可以将这个饼干 j
     * 分配给孩子 i ，这个孩子会得到满足。你的目标是尽可能满足越多数量的孩子，并输出这个最大数值。
     */
    @Source(455)
    public int findContentChildren(int[] children, int[] biscuits) {
        Arrays.sort(children);
        Arrays.sort(biscuits);
        int ans = 0;
        int i = 0;
        for (int biscuit : biscuits) {
            if (i < children.length && biscuit >= children[i]) {
                i++;
                ans++;
            }
        }
        return ans;
    }

    /**
     * 给你一个整数数组 nums ，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
     *
     * <p>子数组 是数组中的一个连续部分。
     */
    @Source(53)
    public int maxSubArray(int[] nums) {
        int ans = Integer.MIN_VALUE;
        int sum = 0;
        for (int num : nums) {
            sum += num;
            ans = Math.max(ans, sum);
            if (sum < 0) {
                sum = 0;
            }
        }
        return ans;
    }
}
