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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    /**
     * 给定一个数组 prices ，其中prices[i] 是一支给定股票第 i 天的价格。
     *
     * <p>设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
     *
     * <p>注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
     */
    @Source(122)
    public int maxProfit(int[] prices) {
        int ans = 0;
        for (int i = 1; i < prices.length; i++) {
            ans += Math.max(0, prices[i] - prices[i - 1]);
        }
        return ans;
    }

    /**
     * 给定一个非负整数数组 nums ，你最初位于数组的 第一个下标 。
     *
     * <p>数组中的每个元素代表你在该位置可以跳跃的最大长度。
     *
     * <p>判断你是否能够到达最后一个下标。
     */
    @Source(55)
    public boolean canJump(int[] nums) {
        if (nums.length == 1) {
            return true;
        }
        int jump = 0;
        for (int i = 0; i <= jump; i++) {
            jump = Math.max(i + nums[i], jump);
            if (jump >= nums.length - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 给你一个非负整数数组nums ，你最初位于数组的第一个位置。
     *
     * <p>数组中的每个元素代表你在该位置可以跳跃的最大长度。
     *
     * <p>你的目标是使用最少的跳跃次数到达数组的最后一个位置。
     *
     * <p>假设你总是可以到达数组的最后一个位置。
     */
    @Source(45)
    public int jump(int[] nums) {
        if (nums.length == 1) {
            return 0;
        }
        int nextMax = 0;
        int ans = 0;
        int curMax = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            nextMax = Math.max(nextMax, i + nums[i]);
            if (i == curMax) {
                ans++;
                curMax = nextMax;
            }
            if (curMax >= nums.length - 1) {
                break;
            }
        }
        return ans;
    }

    /**
     * 给你一个整数数组 nums 和一个整数 k ，按以下方法修改该数组：
     *
     * <p>选择某个下标 i并将 nums[i] 替换为 -nums[i] 。 重复这个过程恰好 k 次。可以多次选择同一个下标 i 。
     *
     * <p>以这种方式修改数组后，返回数组 可能的最大和 。
     */
    @Source(1005)
    public int largestSumAfterKNegations(int[] nums, int k) {
        int min = Integer.MAX_VALUE;
        Arrays.sort(nums);
        int sum = 0;
        for (int num : nums) {
            if (num < 0) {
                min = Math.min(-num, min);
            } else {
                min = Math.min(num, min);
            }
            if (k > 0 && num < 0) {
                num = -num;
                k--;
            }
            sum += num;
        }
        if (k % 2 == 0) {
            return sum;
        } else {
            return sum - 2 * min;
        }
    }

    /**
     * 在一条环路上有N个加油站，其中第i个加油站有汽油gas[i]升。
     *
     * <p>你有一辆油箱容量无限的的汽车，从第 i 个加油站开往第 i+1个加油站需要消耗汽油cost[i]升。你从其中的一个加油站出发，开始时油箱为空。
     *
     * <p>如果你可以绕环路行驶一周，则返回出发时加油站的编号，否则返回 -1。
     *
     * <p>说明:
     *
     * <p>如果题目有解，该答案即为唯一答案。 输入数组均为非空数组，且长度相同。 输入数组中的元素均为非负数。
     */
    @Source(134)
    public int canCompleteCircuit(int[] gas, int[] cost) {
        // 记录当前油量
        int cur = 0;
        int ans = 0;
        // 记录总缺量
        int lack = 0;
        for (int i = 0; i < gas.length; i++) {
            cur += gas[i] - cost[i];
            // 不可到达下一个点，以下一个点作为新的起始点
            if (cur < 0) {
                lack -= cur;
                cur = 0;
                ans = i + 1;
            }
        }
        // 总量 > 0 即可达
        return cur >= lack ? ans : -1;
    }

    /**
     * n 个孩子站成一排。给你一个整数数组 ratings 表示每个孩子的评分。
     *
     * <p>你需要按照以下要求，给这些孩子分发糖果：
     *
     * <p>每个孩子至少分配到 1 个糖果。 相邻两个孩子评分更高的孩子会获得更多的糖果。 请你给每个孩子分发糖果，计算并返回需要准备的 最少糖果数目 。
     */
    @Source(135)
    public int candy(int[] ratings) {
        int[] ans = new int[ratings.length];
        ans[0] = 1;
        for (int i = 1; i < ratings.length; i++) {
            if (ratings[i] > ratings[i - 1]) {
                ans[i] = ans[i - 1] + 1;
            } else {
                ans[i] = 1;
            }
        }

        for (int i = ratings.length - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                ans[i] = Math.max(ans[i], ans[i + 1] + 1);
            }
        }
        return Arrays.stream(ans).sum();
    }

    /**
     * 在柠檬水摊上，每一杯柠檬水的售价为5美元。顾客排队购买你的产品，（按账单 bills 支付的顺序）一次购买一杯。
     *
     * <p>每位顾客只买一杯柠檬水，然后向你付 5 美元、10 美元或 20 美元。你必须给每个顾客正确找零，也就是说净交易是每位顾客向你支付 5 美元。
     *
     * <p>注意，一开始你手头没有任何零钱。
     *
     * <p>给你一个整数数组 bills ，其中 bills[i] 是第 i 位顾客付的账。如果你能给每位顾客正确找零，返回true，否则返回 false。
     */
    @Source(860)
    public boolean lemonadeChange(int[] bills) {
        int five = 0;
        int ten = 0;
        for (int bill : bills) {
            if (bill == 5) {
                five++;
            } else if (bill == 10) {
                five--;
                ten++;
            } else if (ten > 0) {
                ten--;
                five--;
            } else {
                five -= 3;
            }
            if (five < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 假设有打乱顺序的一群人站成一个队列，数组 people 表示队列中一些人的属性（不一定按顺序）。每个 people[i] = [hi, ki] 表示第 i 个人的身高为 hi ，前面
     * 正好 有 ki 个身高大于或等于 hi 的人。
     *
     * <p>请你重新构造并返回输入数组people 所表示的队列。返回的队列应该格式化为数组 queue ，其中 queue[j] = [hj, kj] 是队列中第 j
     * 个人的属性（queue[0] 是排在队列前面的人）。
     */
    @Source(406)
    public int[][] reconstructQueue(int[][] people) {
        // 身高从大到小排（身高相同k小的站前面）
        Arrays.sort(people, (a, b) -> a[0] == b[0] ? a[1] - b[1] : b[0] - a[0]);

        List<int[]> que = new ArrayList<>();
        for (int[] p : people) {
            que.add(p[1], p);
        }

        return que.toArray(new int[people.length][]);
    }

    /**
     * 在二维空间中有许多球形的气球。对于每个气球，提供的输入是水平方向上，气球直径的开始和结束坐标。由于它是水平的，所以纵坐标并不重要，因此只要知道开始和结束的横坐标就足够了。开始坐标总是小于结束坐标。
     *
     * <p>一支弓箭可以沿着 x 轴从不同点完全垂直地射出。在坐标 x 处射出一支箭，若有一个气球的直径的开始和结束坐标为 xstart，xend， 且满足 xstart≤ x ≤
     * xend，则该气球会被引爆。可以射出的弓箭的数量没有限制。 弓箭一旦被射出之后，可以无限地前进。我们想找到使得所有气球全部被引爆，所需的弓箭的最小数量。
     *
     * <p>给你一个数组 points ，其中 points [i] = [xstart,xend] ，返回引爆所有气球所必须射出的最小弓箭数。
     */
    @Source(452)
    public int findMinArrowShots(int[][] points) {
        if (points.length == 0) {
            return 0;
        }
        Arrays.sort(points, Comparator.comparingInt(point -> point[0]));
        int ans = 1;
        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > points[i - 1][1]) {
                ans++;
            } else {
                points[i][1] = Math.min(points[i][1], points[i - 1][1]);
            }
        }
        return ans;
    }

    /**
     * 给定一个区间的集合，找到需要移除区间的最小数量，使剩余区间互不重叠。
     *
     * <p>注意:
     *
     * <p>可以认为区间的终点总是大于它的起点。 区间 [1,2] 和 [2,3] 的边界相互“接触”，但没有相互重叠。
     */
    @Source(435)
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length < 2) {
            return 0;
        }
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));
        int end = intervals[0][1];
        int ans = 0;
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < end) {
                ans++;
            } else {
                end = intervals[i][1];
            }
        }
        return ans;
    }

    /** 字符串 S 由小写字母组成。我们要把这个字符串划分为尽可能多的片段，同一字母最多出现在一个片段中。返回一个表示每个字符串片段的长度的列表。 */
    @Source(763)
    public List<Integer> partitionLabels(String s) {
        int[] hash = new int[26];
        for (int i = 0; i < s.length(); i++) {
            hash[s.charAt(i) - 'a'] = i;
        }
        List<Integer> ans = new ArrayList<>();
        int left = -1;
        int right = 0;
        for (int i = 0; i < s.length(); i++) {
            right = Math.max(right, hash[s.charAt(i) - 'a']);
            if (i == right) {
                ans.add(right - left);
                left = right;
            }
        }
        return ans;
    }

    /**
     * 以数组 intervals 表示若干个区间的集合，其中单个区间为 intervals[i] = [starti, endi]
     * 。请你合并所有重叠的区间，并返回一个不重叠的区间数组，该数组需恰好覆盖输入中的所有区间。
     */
    @Source(56)
    public int[][] merge(int[][] intervals) {
        if (intervals.length < 2) {
            return intervals;
        }

        List<int[]> ans = new ArrayList<>();
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        ans.add(intervals[0]);
        int[] last;
        for (int i = 1; i < intervals.length; i++) {
            last = ans.get(ans.size() - 1);
            if (intervals[i][0] <= last[1]) {
                last[1] = Math.max(last[1], intervals[i][1]);
            } else {
                ans.add(intervals[i]);
            }
        }
        return ans.toArray(new int[ans.size()][]);
    }

    /**
     * 给定一个非负整数N，找出小于或等于N的最大的整数，同时这个整数需要满足其各个位数上的数字是单调递增。
     *
     * <p>（当且仅当每个相邻位数上的数字x和y满足x <= y时，我们称这个整数是单调递增的。）
     */
    @Source(738)
    public int monotoneIncreasingDigits(int n) {
        boolean inc = true;
        char[] nums = Integer.toString(n).toCharArray();
        int maxIndex = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < nums[i - 1]) {
                inc = false;
                break;
            }
            if (nums[i] > nums[i - 1]) {
                maxIndex = i;
            }
        }
        if (inc) {
            return n;
        }
        int digit = (int) (Math.pow(10, nums.length - maxIndex - 1));
        return n / digit * digit - 1;
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
    public int maxProfit(int[] prices, int fee) {
        int buy = prices[0] + fee;
        int ans = 0;
        for (int i = 1; i < prices.length; i++) {
            // 买入价格
            if (prices[i] + fee < buy) {
                buy = prices[i] + fee;
            } else if (prices[i] > buy) {
                // 计算利润，可能有多次计算利润，最后一次计算利润才是真正意义的卖出
                ans += prices[i] - buy;
                // 持有股票
                buy = prices[i];
            }
        }
        return ans;
    }
}
