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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(Type.HASH_MAP)
public class HashMap {

    /**
     * 给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。
     *
     * <p>注意：若s 和 t中每个字符出现的次数都相同，则称s 和 t互为字母异位词。 s 和 t 仅包含小写字母
     */
    @Source(242)
    @Tag(Type.STRING)
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        int[] hash = new int[26];
        for (char c : s.toCharArray()) {
            hash[c - 'a']--;
        }

        for (char c : t.toCharArray()) {
            if (hash[c - 'a']++ >= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 给你两个字符串：ransomNote 和 magazine ，判断 ransomNote 能不能由 magazine 里面的字符构成。
     *
     * <p>如果可以，返回 true ；否则返回 false 。
     *
     * <p>magazine 中的每个字符只能在 ransomNote 中使用一次。 ransomNote 和 magazine 由小写英文字母组成
     */
    @Source(383)
    @Tag(Type.STRING)
    public boolean canConstruct(String t, String s) {
        if (s.length() < t.length()) {
            return false;
        }
        int[] hash = new int[26];
        for (char c : s.toCharArray()) {
            hash[c - 'a']--;
        }

        for (char c : t.toCharArray()) {
            if (hash[c - 'a']++ >= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 给你一个字符串数组，请你将 字母异位词 组合在一起。可以按任意顺序返回结果列表。
     *
     * <p>字母异位词 是由重新排列源单词的字母得到的一个新单词，所有源单词中的字母通常恰好只用一次。
     *
     * <p>strs[i] 仅包含小写字母
     *
     * <p>strs = ["eat", "tea", "tan", "ate", "nat", "bat"] 输出:
     * [["bat"],["nat","tan"],["ate","eat","tea"]]
     */
    @Source(49)
    @Tag(Type.STRING)
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> hash = new java.util.HashMap<>(strs.length);
        for (String str : strs) {
            int[] counter = new int[26];
            for (char c : str.toCharArray()) {
                counter[c - 'a']++;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < counter.length; i++) {
                if (counter[i] > 0) {
                    builder.append((char) (i + 'a'));
                    builder.append(counter[i]);
                }
            }
            List<String> array =
                    hash.computeIfAbsent(builder.toString(), (key) -> new ArrayList<>());
            array.add(str);
        }
        return new ArrayList<>(hash.values());
    }

    /**
     * 给定两个字符串s和 p，找到s中所有p的异位词的子串，返回这些子串的起始索引。不考虑答案输出的顺序。
     *
     * <p>异位词 指由相同字母重排列形成的字符串（包括相同的字符串）。 s 和 p 仅包含小写字母
     */
    @Source(438)
    @Complexity(time = "O(n+m)", space = "O(1)")
    @Tag(Type.STRING)
    @Tag(Type.TWO_POINTERS)
    @Tag(Type.SLIDING_WINDOW)
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }
        int[] hash = new int[26];
        for (char c : p.toCharArray()) {
            hash[c - 'a']++;
        }
        int counter = 0;
        char[] chars = s.toCharArray();
        for (int fast = 0, slow = 0; fast < s.length(); ) {
            if (hash[chars[fast++] - 'a']-- > 0) {
                counter++;
            }
            if (counter == p.length()) {
                result.add(slow);
            }
            if (fast - slow == p.length() && hash[chars[slow++] - 'a']++ >= 0) {
                counter--;
            }
        }
        return result;
    }

    @Source(349)
    @Complexity(time = "O(m+n)", space = "O(m)")
    @Tag(Type.ARRAY)
    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> hash = new HashSet<>();
        List<Integer> result = new ArrayList<>(Math.min(nums1.length, nums2.length));
        for (int i : nums1) {
            hash.add(i);
        }
        for (int i : nums2) {
            if (hash.remove(i)) {
                result.add(i);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    /**
     * 给你两个整数数组nums1 和 nums2
     * ，请你以数组形式返回两数组的交集。返回结果中每个元素出现的次数，应与元素在两个数组中都出现的次数一致（如果出现次数不一致，则考虑取较小值）。可以不考虑输出结果的顺序。
     */
    @Source(350)
    @Complexity(time = "O(m+n)", space = "O(min(n, m))")
    @Tag(Type.ARRAY)
    public int[] intersect(int[] nums1, int[] nums2) {
        int[] less = nums1;
        int[] more = nums2;
        if (less.length > more.length) {
            less = nums2;
            more = nums1;
        }
        Map<Integer, Integer> hash = new java.util.HashMap<>(less.length);
        for (int i : less) {
            hash.compute(i, (key, value) -> value == null ? 1 : ++value);
        }
        List<Integer> result = new ArrayList<>(less.length);
        for (int i : more) {
            hash.compute(
                    i,
                    (key, value) -> {
                        if (value == null) {
                            return null;
                        }
                        if (value > 0) {
                            result.add(key);
                        }
                        return --value;
                    });
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 编写一个算法来判断一个数 n 是不是快乐数。
     *
     * <p>「快乐数」定义为：
     *
     * <p>对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。 然后重复这个过程直到这个数变为 1，也可能是 无限循环 但始终变不到 1。 如果 可以变为
     * 1，那么这个数就是快乐数。 如果 n 是快乐数就返回 true ；不是，则返回 false 。
     */
    @Source(202)
    @Complexity(time = "O(logn)", space = "O(logn)")
    public boolean isHappy(int n) {
        Set<Integer> hash = new HashSet<>();
        int cur = n;
        while (!hash.contains(cur)) {
            hash.add(cur);
            int sum = getNext(cur);
            if (sum == 1) {
                return true;
            }
            cur = sum;
        }
        return false;
    }

    public int getNext(int n) {
        int sum = 0;
        while (n > 0) {
            int d = n % 10;
            n = n / 10;
            sum += d * d;
        }
        return sum;
    }

    /**
     * 9 -> 81.
     *
     * <p>99 -> 162.
     *
     * <p>999 -> 243.
     *
     * <p>9,999 -> 324.
     *
     * <p>9,999,999,999,999 -> 1,053.
     *
     * <p>收敛在243及以内，由于一定会形成环形链表，快乐数时快慢指针一定在 数1 相遇；
     */
    @Source(202)
    @Complexity(time = "O(n)", space = "O(1)")
    @Tag(Type.TWO_POINTERS)
    @Tag(Type.SIN)
    @Tag(Type.MATH)
    public boolean isHappy2(int n) {
        int slow = n;
        int fast = n;
        do {
            slow = getNext(slow);
            fast = getNext(fast);
            fast = getNext(fast);
        } while (slow != fast);

        return fast == 1;
    }
}
