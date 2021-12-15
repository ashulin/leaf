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
import java.util.List;
import java.util.Map;

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
}
