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
import com.github.ashulin.algorithms.doc.SourceType;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

@Tag(Type.STRING)
public class Strings {

    /**
     * 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 s 的形式给出。
     *
     * <p>不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
     */
    @Source(344)
    @Tag(Type.TWO_POINTERS)
    public void reverseString(char[] s) {
        int l = 0;
        int r = s.length - 1;
        while (l < r) {
            char temp = s[l];
            s[l] = s[r];
            s[r] = temp;
            l++;
            r--;
        }
    }

    /**
     * 给定一个字符串 s 和一个整数 k，从字符串开头算起，每计数至 2k 个字符，就反转这 2k 字符中的前 k 个字符。
     *
     * <p>如果剩余字符少于 k 个，则将剩余字符全部反转。 如果剩余字符小于 2k 但大于或等于 k 个，则反转前 k 个字符，其余字符保持原样。
     */
    @Source(541)
    @Tag(Type.TWO_POINTERS)
    public String reverseStr(String s, int k) {
        if (s == null || s.length() < 2) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length - 1; i += 2 * k) {
            int l = i;
            int r = (l + k) - 1;
            if (r >= chars.length) {
                r = chars.length - 1;
            }
            while (l < r) {
                char temp = chars[l];
                chars[l] = chars[r];
                chars[r] = temp;
                l++;
                r--;
            }
        }
        return new String(chars);
    }

    /** 请实现一个函数，把字符串 s 中的每个空格替换成"%20" */
    @Source(value = 05, type = SourceType.OFFER)
    @Tag(Type.TWO_POINTERS)
    public String replaceSpace(String s) {
        int spaceCounter = 0;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c == ' ') {
                spaceCounter++;
            }
        }
        if (spaceCounter == 0) {
            return s;
        }
        char[] result = new char[chars.length + 2 * spaceCounter];
        int top = result.length - 1;
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == ' ') {
                result[top--] = '0';
                result[top--] = '2';
                result[top--] = '%';
            } else {
                result[top--] = chars[i];
            }
        }
        return new String(result);
    }

    /**
     * 给你一个字符串 s ，逐个翻转字符串中的所有 单词 。
     *
     * <p>单词 是由非空格字符组成的字符串。s 中使用至少一个空格将字符串中的 单词 分隔开。
     *
     * <p>请你返回一个翻转 s 中单词顺序并用单个空格相连的字符串。
     *
     * <p>说明：
     *
     * <p>输入字符串 s 可以在前面、后面或者单词间包含多余的空格。 翻转后单词间应当仅用一个空格分隔。 翻转后的字符串中不应包含额外的空格。
     */
    @Source(151)
    @Tag(Type.TWO_POINTERS)
    public String reverseWords(String s) {
        char[] chars = s.toCharArray();
        int len = chars.length;
        // 添加一个空格位，避免特殊处理最后一个单词是否添加空格
        char[] container = new char[len + 1];
        int startIndex = container.length;
        int slow = 0;
        int fast = 0;
        while (fast < len) {
            // 找到单词的第一个字符位置
            while (fast < len && chars[fast] == ' ') {
                fast++;
            }
            slow = fast;
            // 确定单词的长度
            while (fast < len && chars[fast] != ' ') {
                fast++;
            }
            if (slow >= len) {
                break;
            }

            // 复制单词并添加一个空格
            int r = startIndex;
            int l = startIndex - fast + slow;
            while (l < r) {
                container[l++] = chars[slow++];
                startIndex--;
            }
            container[--startIndex] = ' ';
        }
        // 最后一个空格
        startIndex++;
        return new String(container, startIndex, container.length - startIndex);
    }

    /** 不使用新的空间 */
    @Source(151)
    @Tag(Type.TWO_POINTERS)
    public String reverseWords(char[] chars) {
        // 翻转整个字符串
        reverse(chars, 0, chars.length - 1);
        int len = chars.length;
        int slow = 0;
        int fast = 0;
        while (fast < len) {
            while (fast < len && chars[fast] == ' ') {
                fast++;
            }
            slow = fast;
            while (fast < len && chars[fast] != ' ') {
                fast++;
            }
            if (slow >= len) {
                break;
            }
            // 翻转单词
            reverse(chars, slow, fast - 1);
        }
        boolean first = true;
        int relLen = 0;
        fast = 0;
        while (fast < len) {
            while (fast < len && chars[fast] == ' ') {
                fast++;
            }
            if (!first && fast < len) {
                chars[relLen++] = ' ';
            }
            while (fast < len && chars[fast] != ' ') {
                chars[relLen++] = chars[fast++];
                if (first) {
                    first = false;
                }
            }
        }
        return new String(chars, 0, relLen);
    }

    public void reverse(char[] chars, int start, int end) {
        int l = start;
        int r = end;
        while (l < r) {
            char temp = chars[l];
            chars[l] = chars[r];
            chars[r] = temp;
            l++;
            r--;
        }
    }

    /**
     * 字符串的左旋转操作是把字符串前面的若干个字符转移到字符串的尾部。请定义一个函数实现字符串左旋转操作的功能。比如，输入字符串"abcdefg"和数字2，该函数将返回左旋转两位得到的结果"cdefgab"。
     */
    @Source(value = 58, type = SourceType.OFFER)
    public String reverseLeftWords(String s, int n) {
        char[] result = new char[s.length()];
        int index = result.length - n;
        for (int i = 0; i < n; i++) {
            result[index++] = s.charAt(i);
        }
        index = 0;
        for (int i = n; i < s.length(); i++) {
            result[index++] = s.charAt(i);
        }
        return new String(result);
    }

    /** 不使用新的空间 */
    @Source(value = 58, type = SourceType.OFFER)
    @Tag(Type.TWO_POINTERS)
    public String reverseLeftWords(char[] chars, int n) {
        reverse(chars, 0, n - 1);
        reverse(chars, n, chars.length - 1);
        reverse(chars, 0, chars.length - 1);
        return new String(chars);
    }
}
