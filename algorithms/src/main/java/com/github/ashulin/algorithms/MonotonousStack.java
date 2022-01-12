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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 单调栈，栈内顺序要么从大到小 要么从小到大.
 *
 * <p>入站元素要和当前栈内栈首元素进行比较
 *
 * <p>若大于栈首则 则与元素下标做差
 *
 * <p>若大于等于则放入
 */
public class MonotonousStack {

    /** 请根据每日 气温 列表 temperatures ，请计算在每一天需要等几天才会有更高的温度。如果气温在这之后都不会升高，请在该位置用 0 来代替。 */
    @Source(739)
    public int[] dailyTemperatures(int[] temperatures) {
        Stack<Integer> stack = new Stack<>();
        int[] ans = new int[temperatures.length];
        for (int i = 0; i < temperatures.length; i++) {
            // 当气温比前面更高时，弹出前面代表的天数，并比较相隔几天
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int preIndex = stack.pop();
                ans[preIndex] = i - preIndex;
            }
            // 小于等于时放入下标
            stack.push(i);
        }
        return ans;
    }

    /**
     * nums1中数字x的 下一个更大元素 是指x在nums2 中对应位置 右侧 的 第一个 比x大的元素。
     *
     * <p>给你两个 没有重复元素 的数组nums1 和nums2 ，下标从 0 开始计数，其中nums1是nums2的子集。
     *
     * <p>对于每个 0 <= i < nums1.length ，找出满足 nums1[i] == nums2[j] 的下标 j ，并且在 nums2 确定 nums2[j] 的
     * 下一个更大元素 。如果不存在下一个更大元素，那么本次查询的答案是 -1 。
     *
     * <p>返回一个长度为nums1.length 的数组 ans 作为答案，满足 ans[i] 是如上所述的 下一个更大元素 。
     */
    @Source(496)
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        final int[] ans = new int[nums1.length];
        Arrays.fill(ans, -1);
        final Map<Integer, Integer> map = new HashMap<>(nums1.length);
        for (int i = 0; i < nums1.length; i++) {
            map.put(nums1[i], i);
        }
        final Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < nums2.length; i++) {
            while (!stack.isEmpty() && nums2[i] > nums2[stack.peek()]) {
                int preIndex = stack.pop();
                if (map.containsKey(nums2[preIndex])) {
                    ans[map.get(nums2[preIndex])] = nums2[i];
                }
            }
            stack.push(i);
        }
        return ans;
    }
}
