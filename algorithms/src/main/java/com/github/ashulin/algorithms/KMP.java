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

/** Knuth-Morris-Pratt 字符串查找算法. */
@Complexity(time = "O(n)", space = "O(n)")
public class KMP {
    /**
     * 实现strStr()函数。
     *
     * <p>给你两个字符串haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串出现的第一个位置（下标从 0 开始）。如果不存在，则返回 -1 。
     *
     * <p>说明：
     *
     * <p>当needle是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。
     *
     * <p>对于本题而言，当needle是空字符串时我们应当返回 0 。这与 C 语言的strstr()以及 Java 的indexOf()定义相符。
     */
    @Source(28)
    public int strStr(String haystack, String needle) {
        int m = needle.length();
        if ("".equals(needle)) {
            return 0;
        }
        int n = haystack.length();
        if (n < m) {
            return -1;
        }
        int[] next = getAdjustNext(needle);
        char[] s = haystack.toCharArray();
        char[] p = needle.toCharArray();
        int i = 0, j = 0;
        while (i < s.length) {
            if (j == -1 || s[i] == p[j]) {
                i++;
                j++;
                if (j == p.length) {
                    return i - j;
                }
            } else {
                j = next[j];
            }
        }
        return -1;
    }

    /**
     * 前缀是指不包含最后一个字符的所有以第一个字符开头的连续子串； 后缀是指不包含第一个字符的所有以最后一个字符结尾的连续子串。
     *
     * <table border="3">
     *   <tr>
     *     <td> 字符串</td> <td> a </td> <td> a </td> <td> a </td> <td> b </td> <td> a </td> <td> a </td> <td> a </td>
     *   </tr>
     *   <tr>
     *     <td> 前缀</td><td>   </td> <td> a </td> <td> a </td> <td> b </td> <td> a </td> <td> a </td> <td> a </td>
     *   </tr>
     *   <tr>
     *      <td> 后缀</td> <td> a  </td> <td> a </td> <td> a </td> <td> b </td> <td> a </td> <td> a </td> <td>  </td>
     *   </tr>
     *   <tr>
     *      <td> 最长公共前后缀</td> <td> a  </td> <td> a </td> <td> a </td> <td>  </td> <td>  </td> <td>  </td> <td>  </td>
     *   </tr>
     * </table>
     *
     * <table border="2">
     *   <tr>
     *     <td> 匹配字符串</td> <td> a </td> <td> a </td> <td> a </td> <td> b </td> <td> a </td> <td> a </td> <td> a </td> <td> c </td><td> d </td>
     *   </tr>
     *   <tr>
     *     <td> next</td>      <td>  -1 </td> <td> 0</td> <td> 1 </td> <td> 2 </td> <td> 0 </td> <td> 1 </td> <td> 2 </td> <td> 3 </td> <td> 0 </td>
     *   </tr>
     *   <tr>
     *      <td> adjustNext</td> <td> -1  </td> <td> -1 </td> <td> -1 </td> <td> 2 </td> <td> -1 </td> <td> -1 </td> <td> -1 </td> <td> 3 </td> <td> 0 </td>
     *   </tr>
     * </table>
     */
    private int[] getAdjustNext(String pattern) {
        // 存储当前字符匹配失败时，可以继续判定的前缀尾下标
        int[] next = new int[pattern.length()];
        char[] p = pattern.toCharArray();
        next[0] = -1;
        // 后缀尾下标
        int suffix = 0;
        // 前缀尾下标
        int prefix = -1;
        while (suffix < p.length - 1) {
            // index == -1 表示当前无公共前缀，即可判断任意字符与首字符是否相同
            if (prefix == -1
                    // 只有当前的后缀与前缀相同时，才有判断下一个字符的必要
                    || p[suffix] == p[prefix]) {
                // 判断后缀尾与前缀尾下一个字符是否匹配
                prefix++;
                suffix++;
                if (p[suffix] == p[prefix]) {
                    // 后缀尾与前缀尾匹配，当字符与后缀尾匹配失败必定与前缀尾匹配失败
                    next[suffix] = next[prefix];
                } else {
                    // 后缀尾与前缀尾不匹配，当字符与后缀尾匹配失败时还可尝试与前缀尾匹配
                    next[suffix] = prefix;
                }
            } else {
                // 查找新的前缀尾下标
                prefix = next[prefix];
            }
        }
        return next;
    }

    /**
     * 给定一个非空的字符串，判断它是否可以由它的一个子串重复多次构成。给定的字符串只含有小写英文字母，并且长度不超过10000。
     *
     * <p>KMP：找出最后的最长公共前后缀，最长后缀串的前半部分即为最小重复串，判断当前字符串是否为最小重复串整数倍即可
     */
    @Source(459)
    public boolean repeatedSubstringPattern(String s) {
        if (s == null || s.length() < 2) {
            return false;
        }
        int[] next = new int[s.length()];
        char[] p = s.toCharArray();
        next[0] = -1;
        int prefix = -1;
        int suffix = 0;
        int minStart = s.length() / 2 + 1;
        // KMP next
        while (suffix < p.length - 1) {
            if (prefix == -1 || p[suffix] == p[prefix]) {
                prefix++;
                suffix++;
                next[suffix] = prefix;
                // 最后公共前后缀长度至少有字符串的一半
                if (suffix >= minStart && prefix < suffix - minStart) {
                    return false;
                }
            } else {
                prefix = next[prefix];
            }
        }
        // 判断最后的公共前后缀是否相等
        if (p[suffix] != p[next[suffix]]) {
            return false;
        }

        // 最长后缀串的前半部分即为最小重复串, 字符串一定是最小重复串重复整数倍
        return s.length() % (s.length() - next[suffix] - 1) == 0;
    }
}
