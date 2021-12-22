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

import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

@Complexity(time = "O(1)", space = "O(1)")
public class StackAndQueue {

    @Source(232)
    @Tag(Type.SIM)
    class MyQueue {
        Stack<Integer> stack1;
        Stack<Integer> stack2;

        /** Initialize your data structure here. */
        public MyQueue() {
            stack1 = new Stack<>(); // 负责进栈
            stack2 = new Stack<>(); // 负责出栈
        }

        /** Push element x to the back of queue. */
        public void push(int x) {
            stack1.push(x);
        }

        /** Removes the element from in front of queue and returns that element. */
        public int pop() {
            pushElements();
            return stack2.pop();
        }

        /** Get the front element. */
        public int peek() {
            pushElements();
            return stack2.peek();
        }

        /** Returns whether the queue is empty. */
        public boolean empty() {
            return stack1.isEmpty() && stack2.isEmpty();
        }

        private void pushElements() {
            if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }
        }
    }

    @Source(225)
    @Tag(Type.SIM)
    class MyStack {

        Queue<Integer> queue1; // 和栈中保持一样元素的队列
        Queue<Integer> queue2; // 辅助队列

        /** Initialize your data structure here. */
        public MyStack() {
            queue1 = new java.util.LinkedList<>();
            queue2 = new java.util.LinkedList<>();
        }

        /** Push element x onto stack. */
        public void push(int x) {
            queue2.offer(x); // 先放在辅助队列中
            while (!queue1.isEmpty()) {
                queue2.offer(queue1.poll());
            }
            Queue<Integer> queueTemp;
            queueTemp = queue1;
            queue1 = queue2;
            queue2 = queueTemp;
        }

        /** Removes the element on top of the stack and returns that element. */
        public int pop() {
            return queue1.poll();
        }

        /** Get the top element. */
        public int top() {
            return queue1.peek();
        }

        /** Returns whether the stack is empty. */
        public boolean empty() {
            return queue1.isEmpty();
        }
    }

    /**
     * 给定一个只包括 '('，')'，'{'，'}'，'['，']'的字符串 s ，判断字符串是否有效。
     *
     * <p>有效字符串需满足：
     *
     * <p>左括号必须用相同类型的右括号闭合。 左括号必须以正确的顺序闭合。
     */
    @Source(20)
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char ch : s.toCharArray()) {
            if (ch == '(') {
                stack.push(')');
            } else if (ch == '{') {
                stack.push('}');
            } else if (ch == '[') {
                stack.push(']');
            } else if (stack.isEmpty() || stack.pop() != ch) {
                return false;
            }
        }
        return stack.isEmpty();
    }

    /**
     * 给出由小写字母组成的字符串S，重复项删除操作会选择两个相邻且相同的字母，并删除它们。
     *
     * <p>在 S 上反复执行重复项删除操作，直到无法继续删除。
     *
     * <p>在完成所有重复项删除操作后返回最终的字符串。答案保证唯一。
     */
    @Source(1047)
    public String removeDuplicates(String s) {
        char[] chars = s.toCharArray();
        int top = -1;
        for (int i = 0; i < chars.length; i++) {
            if (top == -1 || chars[top] != chars[i]) {
                chars[++top] = chars[i];
            } else {
                top--;
            }
        }
        return String.valueOf(chars, 0, top + 1);
    }

    /**
     * 根据 逆波兰表示法，求表达式的值。
     *
     * <p>有效的算符包括 +、-、*、/ 。每个运算对象可以是整数，也可以是另一个逆波兰表达式。
     *
     * <p>输入：tokens = ["2","1","+","3","*"] 输出：9 解释：该算式转化为常见的中缀算术表达式为：((2 + 1) * 3) = 9
     */
    @Source(150)
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for (String token : tokens) {
            if ("+".equals(token) || "-".equals(token) || "*".equals(token) || "/".equals(token)) {
                Integer num1 = stack.pop();
                Integer num2 = stack.pop();
                if ("+".equals(token)) {
                    stack.push(num2 + num1);
                }
                if ("-".equals(token)) {
                    stack.push(num2 - num1);
                }
                if ("*".equals(token)) {
                    stack.push(num2 * num1);
                }
                if ("/".equals(token)) {
                    stack.push(num2 / num1);
                }
            } else {
                stack.push(Integer.valueOf(token));
            }
        }
        return stack.pop();
    }

    /**
     * 给你一个整数数组 nums，有一个大小为k的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 k个数字。滑动窗口每次只向右移动一位。
     *
     * <p>返回滑动窗口中的最大值。
     */
    @Source(239)
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length < 2) {
            return nums;
        }
        // 队首为最大值的下标
        Deque<Integer> deque = new LinkedList<>();
        int[] result = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; ++i) {
            // 保证单调递增
            while (!deque.isEmpty() && nums[i] >= nums[deque.peekLast()]) {
                deque.pollLast();
            }
            // 添加当前数值的下标
            deque.offerLast(i);

            // 弹出不属于窗口的元素
            while (deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            // 保存窗口的最大值
            if (i + 1 >= k) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    /**
     * 给你一个整数数组 nums 和一个整数 k ，请你返回其中出现频率前 k 高的元素。
     *
     * <p>你可以按 任意顺序 返回答案
     */
    @Source(347)
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>(nums.length);
        for (int num : nums) {
            map.compute(num, (key, value) -> value == null ? 1 : ++value);
        }
        // 固定大小为K的小顶堆
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[k];
        // 小顶堆倒序遍历
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll().getKey();
        }
        return result;
    }

    /**
     * 给你一个字符串 path ，表示指向某一文件或目录的Unix 风格 绝对路径 （以 '/' 开头），请你将其转化为更加简洁的规范路径。
     *
     * <p>在 Unix 风格的文件系统中，一个点（.）表示当前目录本身；此外，两个点
     * （..）表示将目录切换到上一级（指向父目录）；两者都可以是复杂相对路径的组成部分。任意多个连续的斜杠（即，'//'）都被视为单个斜杠 '/' 。
     * 对于此问题，任何其他格式的点（例如，'...'）均被视为文件/目录名称。
     *
     * <p>请注意，返回的 规范路径 必须遵循下述格式：
     *
     * <p>始终以斜杠 '/' 开头。 两个目录名之间必须只有一个斜杠 '/' 。 最后一个目录名（如果存在）不能 以 '/' 结尾。
     * 此外，路径仅包含从根目录到目标文件或目录的路径上的目录（即，不含 '.' 或 '..'）。 返回简化后得到的 规范路径 。
     *
     * <p>path = "/a/./b/../../c/" 输出："/c"
     */
    @Source(71)
    public String simplifyPath(String path) {
        Stack<Character> stack = new Stack<>();
        stack.push('/');
        int pointCount = 0;
        for (char c : path.toCharArray()) {
            switch (c) {
                case '.':
                    stack.push(c);
                    pointCount++;
                    break;
                case '/':
                    if (stack.peek() == '/') {
                        break;
                    }
                    if (pointCount == 2) {
                        backFolder(stack);
                    } else if (pointCount == 1) {
                        stack.pop();
                    } else {
                        stack.push(c);
                    }
                    pointCount = 0;
                    break;
                default:
                    stack.push(c);
                    pointCount = Integer.MIN_VALUE;
                    break;
            }
        }
        if (pointCount == 1) {
            stack.pop();
        } else if (pointCount == 2) {
            backFolder(stack);
        }
        if (stack.peek() == '/' && stack.size() != 1) {
            stack.pop();
        }
        char[] chars = new char[stack.size()];
        for (int i = chars.length - 1; i >= 0; i--) {
            chars[i] = stack.pop();
        }
        return new String(chars);
    }

    public void backFolder(Stack<Character> stack) {
        int separatorCount = 0;
        while (!stack.isEmpty() && separatorCount != 2) {
            if (stack.pop() == '/') {
                separatorCount++;
            }
        }
        stack.push('/');
    }

    @Source(71)
    public String simplifyPath2(String path) {
        String[] splits = path.split("/");
        Stack<String> stack = new Stack<>();
        for (String split : splits) {
            if ("..".equals(split)) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!".".equals(split) && !"".equals(split)) {
                stack.push(split);
            }
        }
        if (stack.isEmpty()) {
            return "/";
        }
        return "/" + String.join("/", stack);
    }
}
