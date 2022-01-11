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

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

/**
 * @author Li Zongwen
 * @since 2021/12/14
 */
@Complexity(time = "O(n)", space = "O(1)")
@Tag(Type.TWO_POINTERS)
public class TwoPointers {
    /**
     * 给你一个有序数组 nums ，请你 原地 删除重复出现的元素，使每个元素 只出现一次 ，返回删除后数组的新长度。 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1)
     * 额外空间的条件下完成。
     *
     * @since 2021/12/14
     */
    @Source(26)
    @Tag(Type.ARRAY)
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int slow = 1;
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[fast - 1]) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        return slow;
    }

    /**
     * 给你一个数组 nums和一个值 val，你需要 原地 移除所有数值等于val的元素，并返回移除后数组的新长度。 不要使用额外的数组空间，你必须仅使用 O(1) 额外空间并 原地
     * 修改输入数组。
     *
     * <p>元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。
     *
     * @since 2021/12/14
     */
    @Source(27)
    @Tag(Type.ARRAY)
    public int removeElement(int[] nums, int val) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        return slow;
    }

    @Source(27)
    @Tag(Type.ARRAY)
    public int removeElement2(int[] nums, int val) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            if (nums[left] == val) {
                nums[left] = nums[right];
                right--;
            } else {
                left++;
            }
        }
        return left;
    }

    /**
     * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
     *
     * @since 2021/12/14
     */
    @Source(283)
    @Tag(Type.ARRAY)
    public void moveZeroes(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }

        for (int fast = 0, slow = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                int temp = nums[slow];
                nums[slow] = nums[fast];
                nums[fast] = temp;
                slow++;
            }
        }
    }

    /**
     * 给定 s 和 t 两个字符串，当它们分别被输入到空白的文本编辑器后，请你判断二者是否相等。# 代表退格字符。
     *
     * <p>如果相等，返回 true ；否则，返回 false 。
     *
     * <p>注意：如果对空文本输入退格字符，文本继续为空。
     *
     * @since 2021/12/14
     */
    @Source(844)
    @Tag(Type.STRING)
    @Tag(Type.SIM)
    public boolean backspaceCompare(String s, String t) {
        // 由右至左判断，即判断最新的有效输入是否一致；
        int indexS = s.length() - 1;
        int indexT = t.length() - 1;
        int skipS = 0;
        int skipT = 0;
        while (indexS >= 0 || indexT >= 0) {
            // 获取最近的有效输入
            while (indexS >= 0) {
                if (s.charAt(indexS) == '#') {
                    skipS++;
                    indexS--;
                } else if (skipS > 0) {
                    skipS--;
                    indexS--;
                } else {
                    break;
                }
            }
            while (indexT >= 0) {
                if (t.charAt(indexT) == '#') {
                    skipT++;
                    indexT--;
                } else if (skipT > 0) {
                    skipT--;
                    indexT--;
                } else {
                    break;
                }
            }
            // 判断两个字符串的最近有效输入是否相等
            if (indexS >= 0 && indexT >= 0) {
                if (s.charAt(indexS) != t.charAt(indexT)) {
                    return false;
                }
            } else {
                // 某一个输入的字符更多
                if (indexS >= 0 || indexT >= 0) {
                    return false;
                }
            }
            indexS--;
            indexT--;
        }
        return true;
    }

    /**
     * 给你一个按 非递减顺序 排序的整数数组 nums，返回 每个数字的平方 组成的新数组，要求也按 非递减顺序 排序。
     *
     * @since 2021/12/14
     */
    @Source(977)
    @Tag(Type.ARRAY)
    public int[] sortedSquares(int[] nums) {
        int left = 0;
        int right = nums.length - 1;
        int index = nums.length - 1;
        int[] result = new int[nums.length];
        while (left <= right) {
            int leftSquare = nums[left] * nums[left];
            int rightSquare = nums[right] * nums[right];
            if (leftSquare > rightSquare) {
                left++;
                result[index--] = leftSquare;
            } else {
                right--;
                result[index--] = rightSquare;
            }
        }
        return result;
    }

    /**
     * 给定字符串 s 和 t ，判断 s 是否为 t 的子序列。
     *
     * <p>字符串的一个子序列是原始字符串删除一些（也可以不删除）字符而不改变剩余字符相对位置形成的新字符串。（例如，"ace"是"abcde"的一个子序列，而"aec"不是）。
     *
     * <p>进阶：
     *
     * <p>如果有大量输入的 S，称作 S1, S2, ... , Sk 其中 k >= 10亿，你需要依次检查它们是否为 T 的子序列。在这种情况下，你会怎样改变代码？
     */
    @Source(392)
    public boolean isSubsequence(String s, String t) {
        int i = 0;
        for (int j = 0; i < s.length() && j < t.length(); j++) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
        }
        return i == s.length();
    }
}
