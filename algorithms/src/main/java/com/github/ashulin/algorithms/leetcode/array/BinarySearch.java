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

package com.github.ashulin.algorithms.leetcode.array;

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.SolutionFor;

/**
 * @author Li Zongwen
 * @since 2021/12/13
 */
@Complexity(time = "O(log n)", space = "O(1)")
public class BinarySearch {

    /**
     * 给定一个n个元素有序的（升序）整型数组nums 和一个目标值target，写一个函数搜索nums中的 target，如果目标值存在返回下标，否则返回 -1。
     *
     * @since 2021/12/13
     */
    @SolutionFor("LC-704")
    public int search(int[] nums, int target) {
        if (nums == null
                || nums.length == 0
                || target > nums[nums.length - 1]
                || target < nums[0]) {
            return -1;
        }

        // 定义target在左闭右闭的区间里，[left, right]
        int left = 0;
        int right = nums.length - 1;
        int current;

        // 当left==right，区间[left, right]依然有效，所以用 <=
        while (left <= right) {
            current = (left + right) >> 1;
            if (nums[current] == target) {
                return current;
            } else if (nums[current] > target) {
                // target 在左区间，所以[left, current - 1]
                right = current - 1;
            } else {
                // target 在右区间，所以[current + 1, right]
                left = current + 1;
            }
        }
        return -1;
    }

    /**
     * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。 请必须使用时间复杂度为O(log n)的算法。
     *
     * @since 2021/12/13
     */
    @SolutionFor("LC-35")
    public int searchInsert(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (target > nums[nums.length - 1]) {
            return nums.length;
        }
        if (target < nums[0]) {
            return 0;
        }
        // 定义target在左闭右闭的区间里，[left, right]
        int left = 0;
        int right = nums.length - 1;
        int current;

        // 当left==right，区间[left, right]依然有效，所以用 <=
        while (left <= right) {
            current = (left + right) >> 1;
            if (nums[current] == target) {
                return current;
            } else if (nums[current] > target) {
                // target 在左区间，所以[left, current - 1]
                right = current - 1;
            } else {
                // target 在右区间，所以[current + 1, right]
                left = current + 1;
            }
        }
        return left;
    }

    /**
     * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。 如果数组中不存在目标值 target，返回[-1, -1]。
     *
     * <p>进阶： 你可以设计并实现时间复杂度为O(log n)的算法解决此问题吗？
     *
     * @since 2021/12/13
     */
    @SolutionFor("LC-34")
    public int[] searchRange(int[] nums, int target) {
        int[] result = new int[] {-1, -1};
        if (nums == null
                || nums.length == 0
                || target > nums[nums.length - 1]
                || target < nums[0]) {
            return result;
        }
        result[0] = binarySearch(nums, target, true);
        result[1] = binarySearch(nums, target, false);
        return result;
    }

    private int binarySearch(int[] nums, int target, boolean isLeft) {
        int left = 0;
        int right = nums.length - 1;
        int current;
        int result = -1;
        while (left <= right) {
            current = (left + right) >> 1;
            if (nums[current] == target) {
                result = current;
                if (isLeft) {
                    right = current - 1;
                } else {
                    left = current + 1;
                }
            } else if (nums[current] > target) {
                right = current - 1;
            } else {
                left = current + 1;
            }
        }
        return result;
    }

    @SolutionFor("LC-34")
    public int[] searchRange2(int[] nums, int target) {
        if (nums == null
                || nums.length == 0
                || target > nums[nums.length - 1]
                || target < nums[0]) {
            return new int[] {-1, -1};
        }
        // 查找该值最左的index
        int left = searchLeftIndex(nums, target);
        // target + 1的插入index - 1即为target的最右index
        int right = searchLeftIndex(nums, target + 1) - 1;
        // 判断边界值
        if (left == nums.length || nums[left] != target) {
            return new int[] {-1, -1};
        } else {
            return new int[] {left, right};
        }
    }

    /**
     * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其最左索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
     *
     * @since 2021/12/13
     */
    private int searchLeftIndex(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        int current;

        while (left <= right) {
            current = (left + right) >> 1;
            if (nums[current] >= target) {
                right = current - 1;
            } else {
                left = current + 1;
            }
        }
        return left;
    }

    /**
     * 给你一个非负整数 x ，计算并返回x的 算术平方根 。
     *
     * <p>由于返回类型是整数，结果只保留 整数部分 ，小数部分将被 舍去 。
     *
     * <p>注意：不允许使用任何内置指数函数和算符，例如 pow(x, 0.5) 或者 x ** 0.5 。
     *
     * @since 2021/12/13
     */
    @SolutionFor("LC-69")
    public int mySqrt(int x) {
        if (x < 2) {
            return x;
        }
        int left = 0;
        int right = x;
        int result = 0;
        while (left <= right) {
            int middle = (left + right) >> 1;
            if (middle <= x / middle) {
                result = middle;
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }
        return result;
    }

    /**
     * 给定一个 正整数 num ，编写一个函数，如果 num 是一个完全平方数，则返回 true ，否则返回 false 。
     *
     * <p>进阶：不要使用任何内置的库函数，如sqrt 。
     *
     * @since 2021/12/13
     */
    @SolutionFor("LC-367")
    public boolean isPerfectSquare(int num) {
        int left = 0;
        int right = num;
        while (left <= right) {
            int middle = (left + right) >> 1;
            long square = (long) middle * middle;
            if (square < num) {
                left = middle + 1;
            } else if (square > num) {
                right = middle - 1;
            } else {
                return true;
            }
        }
        return false;
    }
}
