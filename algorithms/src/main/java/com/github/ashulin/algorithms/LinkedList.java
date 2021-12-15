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
import com.github.ashulin.algorithms.doc.SourceType;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

import java.util.Objects;

/**
 * @author Li Zongwen
 * @since 2021/12/15
 */
@Tag(Type.LINKED_LIST)
public class LinkedList {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {}

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ListNode listNode = (ListNode) o;
            return val == listNode.val && Objects.equals(next, listNode.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, next);
        }
    }

    /** 给你一个链表的头节点 head 和一个整数 val ，请你删除链表中所有满足 Node.val == val 的节点，并返回 新的头节点 。 */
    @Source(203)
    @Complexity(time = "O(n)", space = "O(1)")
    @Tag(Type.SIM)
    public ListNode removeElements(ListNode head, int val) {
        // 虚拟头可以在头节点需要删除时不特殊处理
        ListNode dummyHead = new ListNode(0, head);
        ListNode current = dummyHead;
        while (current.next != null) {
            if (current.next.val == val) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
        return dummyHead.next;
    }

    @Source(203)
    @Complexity(time = "O(n)", space = "O(n)")
    @Tag(Type.RECURSIVE)
    @Tag(Type.SIM)
    public ListNode removeElements2(ListNode head, int val) {
        if (head == null) {
            return null;
        }
        head.next = removeElements(head.next, val);
        return head.val == val ? head.next : head;
    }

    /** 给你单链表的头节点 head ，请你反转链表，并返回反转后的链表。 */
    @Source(206)
    @Complexity(time = "O(n)", space = "O(1)")
    public ListNode reverseList(ListNode node) {
        if (node == null) {
            return null;
        }
        ListNode head = node;
        while (node.next != null) {
            ListNode temp = node.next;
            node.next = node.next.next;
            temp.next = head;
            head = temp;
        }
        return head;
    }

    @Source(206)
    @Complexity(time = "O(n)", space = "O(n)")
    @Tag(Type.RECURSIVE)
    public ListNode reverseList2(ListNode node) {
        if (node == null || node.next == null) {
            return node;
        }
        ListNode result = reverseList2(node.next);
        // 下一个node指向反转到自身
        node.next.next = node;
        // 自身为尾节点
        node.next = null;
        return result;
    }

    /** 给你一个链表，两两交换其中相邻的节点，并返回交换后链表的头节点。 */
    @Source(24)
    @Complexity(time = "O(n)", space = "O(1)")
    public ListNode swapPairs(ListNode head) {
        ListNode dummyHead = new ListNode(0, head);
        ListNode cur = dummyHead;
        while (cur.next != null && cur.next.next != null) {
            ListNode temp = cur.next.next;
            cur.next.next = temp.next;
            temp.next = cur.next;
            cur.next = temp;
            cur = cur.next.next;
        }
        return dummyHead.next;
    }

    /** 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。 */
    @Source(19)
    @Complexity(time = "O(n)", space = "O(1)")
    @Tag(Type.TWO_POINTERS)
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummyHead = new ListNode(0, head);
        ListNode slow = dummyHead;
        ListNode fast = dummyHead.next;
        for (int i = 1; i < n; i++) {
            fast = fast.next;
        }
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }

        ListNode delete = slow.next;
        slow.next = slow.next.next;
        delete.next = null;
        return dummyHead.next;
    }

    /**
     * 给定两个（单向）链表，判定它们是否相交并返回交点。请注意相交的定义基于节点的引用，而不是基于节点的值。
     * 换句话说，如果一个链表的第k个节点与另一个链表的第j个节点是同一节点（引用完全相同），则这两个链表相交。
     */
    @Source(value = 02_07, type = SourceType.CI6)
    @Tag(Type.TWO_POINTERS)
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode top = headA;
        ListNode bottom = headB;
        while (top != bottom) {
            top = top == null ? headB : top.next;
            bottom = bottom == null ? headA : bottom.next;
        }
        return top;
    }

    /**
     * 给定一个链表，返回链表开始入环的第一个节点。如果链表无环，则返回null。
     *
     * <p>如果链表中有某个节点，可以通过连续跟踪 next 指针再次到达，则链表中存在环。 为了表示给定链表中的环，评测系统内部使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0
     * 开始）。如果 pos 是 -1，则在该链表中没有环。 注意：pos 不作为参数进行传递，仅仅是为了标识链表的实际情况。 不允许修改链表!
     */
    @Source(142)
    @Tag(Type.TWO_POINTERS)
    @Tag(Type.SIN)
    @Tag(Type.MATH)
    public ListNode detectCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;
        // 存在null即不存在环
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                // 设从头结点到环形入口节点的节点数为x；
                // 设环的节点数为L；
                // 环形入口节点到fast指针与slow指针相遇节点的节点数为y (y <= L-1)：
                //      当slow步入环时，最差的情况，fast在slow前面一位，fast相对于slow为1次接近1步（步速差）
                //      在最差情况下，slow前进L-1步后与fast相遇；
                // 从相遇节点再到环形入口节点的节点数为z (z = L - y)；
                // 可得 slow: (x + y) * 2 = fast: x + n*(y + z) + y, n为fast在环中移动的圈数，易得 n >= 1;
                // => x = n*(y + z) - y = (n - 1)*(y + z) + z = (n - 1) * L + z
                // 以上可得，在相遇后，再从头结点以1的步长出发 即可与slow指针在环形入口相遇；
                ListNode newSlow = head;
                while (slow != newSlow) {
                    slow = slow.next;
                    newSlow = newSlow.next;
                }
                return slow;
            }
        }
        return null;
    }

    /** 设计链表的实现。您可以选择使用单链表或双链表。 */
    @Source(707)
    @Tag(Type.SIM)
    public static class MyLinkedList {
        private int size = 0;
        private ListNode head = new ListNode(0);

        public MyLinkedList() {}

        public int get(int index) {
            ListNode node = getNode(index);
            return node == null ? -1 : node.val;
        }

        private ListNode getNode(int index) {
            if (index < 0 || index >= size) {
                return null;
            }
            ListNode node = head;
            for (int i = 0; i <= index; i++) {
                node = node.next;
            }
            return node;
        }

        public void addAtHead(int val) {
            addAtIndex(0, val);
        }

        public void addAtTail(int val) {
            addAtIndex(size, val);
        }

        public void addAtIndex(int index, int val) {
            if (index > size) {
                return;
            }
            ListNode node = index < 1 ? head : getNode(index - 1);
            ListNode toAdd = new ListNode(val);
            toAdd.next = node.next;
            node.next = toAdd;
            size++;
        }

        public void deleteAtIndex(int index) {
            if (index < 0 || index >= size) {
                return;
            }
            ListNode node = index < 1 ? head : getNode(index - 1);
            node.next = node.next.next;
            size--;
        }
    }
}
