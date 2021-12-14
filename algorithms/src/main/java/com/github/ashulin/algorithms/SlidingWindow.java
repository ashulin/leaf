package com.github.ashulin.algorithms;

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

import java.util.HashMap;

@Tag(Type.SLIDING_WINDOW)
public class SlidingWindow {
    /**
     * 给定一个含有n个正整数的数组和一个正整数 target 。
     *
     * <p>找出该数组中满足其和 ≥ target 的长度最小的 连续子数组[numsl, numsl+1, ..., numsr-1, numsr]
     * ，并返回其长度。如果不存在符合条件的子数组，返回 0 。
     *
     * @since 2021/12/14
     */
    @Source(209)
    @Tag(Type.ARRAY)
    public int minSubArrayLen(int target, int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int left = -1;
        int right = 0;
        int min = Integer.MAX_VALUE;
        int sum = nums[0];
        while (true) {
            if (sum >= target) {
                // 符合条件，收敛窗口
                min = Math.min(right - left, min);
                if (min == 1) {
                    return min;
                }
                left++;
                sum -= nums[left];
            } else {
                // 不符合条件，扩大窗口
                right++;
                if (right == nums.length) {
                    break;
                }
                sum += nums[right];
            }
        }
        // 未赋值即不存在符合条件的子数组
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    /**
     * 你正在探访一家农场，农场从左到右种植了一排果树。这些树用一个整数数组 fruits 表示，其中 fruits[i] 是第 i 棵树上的水果 种类 。
     *
     * <p>你想要尽可能多地收集水果。然而，农场的主人设定了一些严格的规矩，你必须按照要求采摘水果：
     *
     * <p>你只有 两个 篮子，并且每个篮子只能装 单一类型 的水果。每个篮子能够装的水果总量没有限制。 你可以选择任意一棵树开始采摘，你必须从 每棵 树（包括开始采摘的树）上 恰好摘一个水果
     * 。采摘的水果应当符合篮子中的水果类型。每采摘一次，你将会向右移动到下一棵树，并继续采摘。 一旦你走到某棵树前，但水果不符合篮子的水果类型，那么就必须停止采摘。 给你一个整数数组
     * fruits ，返回你可以收集的水果的 最大 数目。
     *
     * @since 2021/12/14
     */
    @Source(904)
    @Tag(Type.ARRAY)
    public int totalFruit(int[] fruits) {
        int type1 = -1;
        int type2 = -1;
        int left = 0;
        int right = 0;
        int sum1 = 0;
        int sum2 = 0;
        int max = 0;
        while (left <= right) {
            if ((type1 == -1 || type1 == fruits[right])
                    || (type2 == -1 || type2 == fruits[right])) {
                if (type1 == -1 || type1 == fruits[right]) {
                    type1 = fruits[right];
                    sum1++;
                } else {
                    type2 = fruits[right];
                    sum2++;
                }
                max = Math.max(max, sum1 + sum2);
                right++;
                if (right == fruits.length) {
                    break;
                }
            } else {
                if (type1 == fruits[left]) {
                    if (--sum1 == 0) {
                        type1 = -1;
                    }
                } else {
                    if (--sum2 == 0) {
                        type2 = -1;
                    }
                }
                left++;
            }
        }
        return max;
    }

    @Source(904)
    @Complexity(time = "O(n)", space = "O(n)")
    @Tag(Type.HASH_MAP)
    @Tag(Type.ARRAY)
    public int totalFruit2(int[] fruits) {
        Counter counter = new Counter();
        int left = 0;
        int right = 0;
        int max = 0;
        while (right < fruits.length) {
            counter.inc(fruits[right]);
            while (counter.size() > 2) {
                counter.dec(fruits[left]);
                left++;
            }
            max = Math.max(max, right - left + 1);
            right++;
        }
        return max;
    }

    public static class Counter extends HashMap<Integer, Integer> {
        protected int get(int k) {
            return containsKey(k) ? super.get(k) : 0;
        }

        public void inc(int k) {
            put(k, get(k) + 1);
        }

        public void dec(int k) {
            if (get(k) == 1) {
                remove(k);
            } else {
                put(k, get(k) - 1);
            }
        }
    }

    /**
     * 给你一个字符串 s 、一个字符串 t 。返回 s 中涵盖 t 所有字符的最小子串。如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。 注意： 对于 t
     * 中重复字符，我们寻找的子字符串中该字符数量必须不少于 t 中该字符数量。 如果 s 中存在这样的子串，我们保证它是唯一的答案。
     *
     * @since 2021/12/14
     */
    @Source(76)
    @Tag(Type.HASH_MAP)
    @Tag(Type.STRING)
    public String minWindow(String s, String t) {
        if (t.length() > s.length()) {
            return "";
        }
        CharCounter counter = new CharCounter(t);
        int left = 0;
        int right = 0;
        int min = Integer.MAX_VALUE;
        int minStartIndex = 0;
        char[] chars = s.toCharArray();
        while (right < chars.length) {
            counter.inc(chars[right]);
            while (counter.match()) {
                int length = right - left + 1;
                if (min > length) {
                    min = length;
                    minStartIndex = left;
                }
                counter.dec(chars[left]);
                left++;
            }
            right++;
        }
        return min == Integer.MAX_VALUE ? "" : s.substring(minStartIndex, minStartIndex + min);
    }

    private static class CharCounter extends Counter {
        Counter target;

        public CharCounter(String t) {
            target = new Counter();
            for (char c : t.toCharArray()) {
                target.inc(c);
            }
        }

        public boolean match() {
            for (Entry<Integer, Integer> entry : target.entrySet()) {
                if (get(entry.getKey().intValue()) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }
    }

    @Source(76)
    @Tag(Type.STRING)
    public String minWindow2(String s, String t) {
        int[] target = new int[('z' - 'A') + 1];
        for (char c : t.toCharArray()) {
            // 初始化所需char
            target[c - 'A']--;
        }
        char[] chars = s.toCharArray();
        int lenT = t.length();
        int count = 0;
        int min = Integer.MAX_VALUE;
        int minStartIndex = 0;
        for (int right = 0, left = 0; right < chars.length; right++) {
            target[chars[right] - 'A']++;
            // <= 0时即为所需的char，并记录已匹配的char总数
            if (target[chars[right] - 'A'] <= 0) {
                count++;
            }

            if (count == lenT) {
                // >0 即为不需要/数量已超过需要的char，收缩窗口
                while (target[chars[left] - 'A'] > 0) {
                    target[chars[left] - 'A']--;
                    left++;
                }
                int length = right - left + 1;
                if (min > length) {
                    min = length;
                    minStartIndex = left;
                }
            }
        }
        return min == Integer.MAX_VALUE ? "" : s.substring(minStartIndex, minStartIndex + min);
    }
}
