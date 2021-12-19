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
}
