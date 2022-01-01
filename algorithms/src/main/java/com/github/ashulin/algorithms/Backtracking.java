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
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Backtracking {

    /**
     * 给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
     *
     * <p>你可以按 任何顺序 返回答案。
     */
    @Source(77)
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        combine(n, k, 1, new ArrayDeque<>(k), result);
        return result;
    }

    private void combine(int n, int k, int index, Deque<Integer> path, List<List<Integer>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = index; i <= n - (k - path.size()) + 1; i++) {
            path.add(i);
            combine(n, k, i + 1, path, result);
            // 回溯
            path.removeLast();
        }
    }

    /**
     * 找出所有相加之和为n 的k个数的组合。组合中只允许含有 1 - 9 的正整数，并且每种组合中不存在重复的数字。
     *
     * <p>说明：
     *
     * <p>所有数字都是正整数。 解集不能包含重复的组合。
     */
    @Source(216)
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        combinationSum3(k, n, 1, new ArrayDeque<>(k), result);
        return result;
    }

    private void combinationSum3(
            int k, int target, int index, Deque<Integer> path, List<List<Integer>> result) {
        if (path.size() == k) {
            if (target == 0) {
                result.add(new ArrayList<>(path));
            }
            return;
        }
        int n = k - path.size() - 1;
        for (int i = Math.max(target - (9 * n + (1 - n) * n / 2), index);
                i <= 10 - k + path.size();
                i++) {
            int expectValue = target - i;
            int minSum = i * n + (n + 1) * n / 2;
            if (minSum > expectValue) {
                break;
            }
            path.add(i);
            combinationSum3(k, expectValue, i + 1, path, result);
            // 回溯
            path.removeLast();
        }
    }

    /** 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。 */
    @Source(17)
    @Tag(Type.ITERATION)
    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.length() == 0) {
            return new ArrayList<>();
        }
        String[] letters = {"abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        LinkedList<String> result = new LinkedList<>();
        result.add("");
        String s = "";
        for (int i = 0; i < digits.length(); i++) {
            while (result.peekFirst().length() == i) {
                s = result.pollFirst();
                String letter = letters[digits.charAt(i) - '2'];
                for (int j = 0; j < letter.length(); j++) {
                    result.addLast(s + letter.charAt(j));
                }
            }
        }
        return result;
    }

    @Source(17)
    public List<String> letterCombinations2(String digits) {
        if (digits == null || digits.length() == 0) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        letterCombinations(
                digits,
                0,
                new String[] {"abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"},
                new StringBuilder(),
                result);
        return result;
    }

    public void letterCombinations(
            String digits,
            int index,
            String[] letters,
            StringBuilder builder,
            List<String> result) {
        if (index == digits.length()) {
            result.add(builder.toString());
            return;
        }
        String letter = letters[digits.charAt(index) - '2'];
        for (int i = 0; i < letter.length(); i++) {
            builder.append(letter.charAt(i));
            letterCombinations(digits, index + 1, letters, builder, result);
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    /**
     * 给你一个 无重复元素 的正整数数组candidates 和一个目标正整数target，找出candidates中可以使数字和为目标数target 的 所有不同组合
     * ，并以列表形式返回。你可以按 任意顺序 返回这些组合。
     *
     * <p>candidates 中的 同一个 数字可以 无限制重复被选取 。如果至少一个数字的被选数量不同，则两种组合是不同的。
     *
     * <p>对于给定的输入，保证和为target 的不同组合数少于 150 个。
     */
    @Source(39)
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        combinationSum(candidates, target, 0, new ArrayDeque<>(), result);
        return result;
    }

    private void combinationSum(
            int[] candidates,
            int target,
            int index,
            Deque<Integer> path,
            List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = index; i < candidates.length; i++) {
            int expectValue = target - candidates[i];
            // 剪枝
            if (expectValue < 0) {
                break;
            }
            path.add(candidates[i]);
            combinationSum(candidates, expectValue, i, path, result);
            // 回溯
            path.removeLast();
        }
    }

    /**
     * 给定一个数组candidates和一个目标数target，找出candidates中所有可以使数字和为target的组合。candidates中的每个数字在每个组合中只能使用一次。
     *
     * <p>注意：解集不能包含重复的组合。
     */
    @Source(40)
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        combinationSum2(candidates, target, 0, new ArrayDeque<>(), result);
        return result;
    }

    private void combinationSum2(
            int[] candidates,
            int target,
            int index,
            Deque<Integer> path,
            List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = index; i < candidates.length; i++) {
            int expectValue = target - candidates[i];
            // 剪枝
            if (expectValue < 0) {
                break;
            }
            path.add(candidates[i]);
            combinationSum2(candidates, expectValue, i + 1, path, result);
            // 回溯
            path.removeLast();
            // 去重
            while (i < candidates.length - 1 && candidates[i] == candidates[i + 1]) {
                i++;
            }
        }
    }

    /**
     * 给你一个字符串 s，请你将 s 分割成一些子串，使每个子串都是 回文串 。返回 s 所有可能的分割方案。
     *
     * <p>回文串 是正着读和反着读都一样的字符串。
     */
    @Source(131)
    @Tag(Type.DP)
    public List<List<String>> partition(String s) {
        List<List<String>> ans = new ArrayList<>();
        int n = s.length();
        if (n == 0) {
            return ans;
        }
        boolean[][] dp = new boolean[n][n];
        for (int right = 0; right < n; right++) {
            for (int left = 0; left <= right; left++) {
                if (s.charAt(left) == s.charAt(right)
                        && (right - left <= 2 || dp[left + 1][right - 1])) {
                    dp[left][right] = true;
                }
            }
        }
        dfs(s, 0, dp, new LinkedList<>(), ans);
        return ans;
    }

    public void dfs(
            String s, int left, boolean[][] dp, Deque<String> path, List<List<String>> ans) {
        if (left == s.length()) {
            ans.add(new ArrayList<>(path));
            return;
        }

        for (int right = left; right < s.length(); right++) {
            if (dp[left][right]) {
                path.offer(s.substring(left, right + 1));
                dfs(s, right + 1, dp, path, ans);
                path.pollLast();
            }
        }
    }

    @Source(131)
    @Tag(Type.DP)
    public List<List<String>> partition2(String s) {
        List<List<String>> ans = new ArrayList<>();
        int n = s.length();
        if (n == 0) {
            return ans;
        }
        dfs2(s, 0, new Boolean[n][n], new LinkedList<>(), ans);
        return ans;
    }

    public void dfs2(
            String s, int left, Boolean[][] dp, Deque<String> path, List<List<String>> ans) {
        if (left == s.length()) {
            ans.add(new ArrayList<>(path));
            return;
        }

        for (int right = left; right < s.length(); right++) {
            if (isPalindrome(s, dp, left, right)) {
                path.offer(s.substring(left, right + 1));
                dfs2(s, right + 1, dp, path, ans);
                path.pollLast();
            }
        }
    }

    public boolean isPalindrome(String s, Boolean[][] dp, int left, int right) {
        // null标识未判断
        if (dp[left][right] != null) {
            return dp[left][right];
        }
        if (s.charAt(left) == s.charAt(right)) {
            if (right - left <= 2) {
                dp[left][right] = true;
            } else {
                dp[left][right] = isPalindrome(s, dp, left + 1, right - 1);
            }
        } else {
            dp[left][right] = false;
        }
        return dp[left][right];
    }

    /**
     * 有效 IP 地址 正好由四个整数（每个整数位于 0 到 255 之间组成，且不能含有前导 0），整数之间用 '.' 分隔。
     *
     * <p>例如："0.1.2.201" 和 "192.168.1.1" 是 有效 IP 地址，但是 "0.011.255.245"、"192.168.1.312" 和
     * "192.168@1.1" 是 无效 IP 地址。 给定一个只包含数字的字符串 s ，用以表示一个 IP 地址，返回所有可能的有效 IP 地址，这些地址可以通过在 s 中插入'.'
     * 来形成。你不能重新排序或删除 s 中的任何数字。你可以按 任何 顺序返回答案。
     *
     * <p>s 仅由数字组成
     */
    @Source(93)
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        restoreIpAddresses(s, 0, new ArrayList<>(4), result);
        return result;
    }

    private void restoreIpAddresses(String s, int left, List<String> path, List<String> result) {
        if (path.size() == 4) {
            result.add(String.join(".", path));
            return;
        }

        // 剪枝: 保证字符串可以被全部使用
        int min = Math.max(left + 1, s.length() - 3 * (3 - path.size()));
        // 剪枝: 保证可以有4个数字，以及当前数字 < 1000
        int max = Math.min(s.length() - 3 + path.size(), left + (s.charAt(left) == '0' ? 1 : 3));
        // [left, right)
        for (int right = min; right <= max; right++) {
            String numStr = s.substring(left, right);
            // 由于剪枝只需要判断是否小于256
            if (right - left < 3 || Integer.parseInt(numStr) < 256) {
                path.add(numStr);
                restoreIpAddresses(s, right, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    /**
     * 给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。
     *
     * <p>解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
     */
    @Source(78)
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        subsets(nums, 0, new ArrayList<>(nums.length), result);
        return result;
    }

    private void subsets(
            int[] nums, int startIndex, List<Integer> path, List<List<Integer>> result) {
        result.add(new ArrayList<>(path));
        for (int i = startIndex; i < nums.length; i++) {
            path.add(nums[i]);
            subsets(nums, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    /**
     * 给你一个整数数组 nums ，其中可能包含重复元素，请你返回该数组所有可能的子集（幂集）。
     *
     * <p>解集 不能 包含重复的子集。返回的解集中，子集可以按 任意顺序 排列。
     */
    @Source(90)
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        subsetsWithDup(nums, 0, new ArrayList<>(nums.length), result);
        return result;
    }

    private void subsetsWithDup(
            int[] nums, int startIndex, List<Integer> path, List<List<Integer>> result) {
        result.add(new ArrayList<>(path));
        for (int i = startIndex; i < nums.length; i++) {
            if (i > startIndex && nums[i] == nums[i - 1]) {
                continue;
            }
            path.add(nums[i]);
            subsetsWithDup(nums, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    /**
     * 给你一个整数数组 nums ，找出并返回所有该数组中不同的递增子序列，递增子序列中 至少有两个元素 。你可以按 任意顺序 返回答案。
     *
     * <p>数组中可能含有重复元素，如出现两个整数相等，也可以视作递增序列的一种特殊情况。
     */
    @Source(491)
    public List<List<Integer>> findSubsequences(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        findSubsequences(nums, 0, new ArrayList<>(nums.length), result);
        return result;
    }

    private void findSubsequences(
            int[] nums, int startIndex, List<Integer> path, List<List<Integer>> result) {
        if (path.size() > 1) {
            result.add(new ArrayList<>(path));
        }
        Set<Integer> hash = new HashSet<>();
        for (int i = startIndex; i < nums.length; i++) {
            if (hash.contains(nums[i])) {
                continue;
            }
            hash.add(nums[i]);
            if (path.size() == 0 || path.get(path.size() - 1) <= nums[i]) {
                path.add(nums[i]);
                findSubsequences(nums, i + 1, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    /** 给定一个不含重复数字的数组 nums ，返回其 所有可能的全排列 。你可以 按任意顺序 返回答案。 */
    @Source(46)
    public List<List<Integer>> permute(int[] nums) {
        List<Integer> list = Arrays.stream(nums).boxed().collect(Collectors.toList());
        List<List<Integer>> ans = new ArrayList<>(factorial(nums.length));
        permute(list, new ArrayList<>(nums.length), ans);
        return ans;
    }

    private static int factorial(int number) {
        return number <= 1 ? 1 : number * factorial(number - 1);
    }

    private void permute(List<Integer> nums, List<Integer> path, List<List<Integer>> ans) {
        if (nums.size() == 0) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.size(); i++) {
            Integer num = nums.remove(i);
            path.add(num);
            permute(nums, path, ans);
            path.remove(path.size() - 1);
            nums.add(i, num);
        }
    }

    /** 给定一个可包含重复数字的序列 nums ，按任意顺序 返回所有不重复的全排列。 */
    @Source(47)
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        List<Integer> list = Arrays.stream(nums).boxed().collect(Collectors.toList());
        List<List<Integer>> ans = new ArrayList<>(factorial(nums.length));
        permuteUnique(list, new ArrayList<>(nums.length), ans);
        return ans;
    }

    private void permuteUnique(List<Integer> nums, List<Integer> path, List<List<Integer>> ans) {
        if (nums.size() == 0) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.size(); i++) {
            if (i > 0 && nums.get(i).equals(nums.get(i - 1))) {
                continue;
            }
            Integer num = nums.remove(i);
            path.add(num);
            permuteUnique(nums, path, ans);
            path.remove(path.size() - 1);
            nums.add(i, num);
        }
    }

    @Source(47)
    public List<List<Integer>> permuteUnique2(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>(factorial(nums.length));
        permuteUnique(nums, new boolean[nums.length], new ArrayList<>(nums.length), ans);
        return ans;
    }

    private void permuteUnique(
            int[] nums, boolean[] flag, List<Integer> path, List<List<Integer>> ans) {
        if (path.size() == nums.length) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (flag[i]) {
                continue;
            }
            if (i > 0 && nums[i] == nums[i - 1] && !flag[i - 1]) {
                continue;
            }
            path.add(nums[i]);
            flag[i] = true;
            permuteUnique(nums, flag, path, ans);
            path.remove(path.size() - 1);
            flag[i] = false;
        }
    }
}
