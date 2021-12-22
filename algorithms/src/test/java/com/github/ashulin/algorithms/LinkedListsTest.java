package com.github.ashulin.algorithms;

import com.github.ashulin.algorithms.LinkedLists.ListNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Li Zongwen
 * @since 2021/12/15
 */
public class LinkedListsTest {
    LinkedLists solution = new LinkedLists();
    ListNode listNode;

    @BeforeEach
    public void before() {
        listNode =
                new ListNode(
                        1,
                        new ListNode(
                                2,
                                new ListNode(
                                        6,
                                        new ListNode(
                                                3,
                                                new ListNode(
                                                        4, new ListNode(5, new ListNode(6)))))));
    }

    @Test
    public void testRemoveElements() {
        Assertions.assertEquals(
                new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5))))),
                solution.removeElements(listNode, 6));

        Assertions.assertNull(solution.removeElements(new ListNode(6, new ListNode(6)), 6));
    }

    @Test
    public void testRemoveElements2() {
        Assertions.assertEquals(
                new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5))))),
                solution.removeElements2(listNode, 6));

        Assertions.assertNull(solution.removeElements2(new ListNode(6, new ListNode(6)), 6));
    }

    @Test
    public void testMyLinkedList() {
        LinkedLists.MyLinkedList list = new LinkedLists.MyLinkedList();
        list.addAtHead(1);
        list.addAtTail(3);
        list.addAtIndex(1, 2);
        list.get(1);
        list.deleteAtIndex(1);
        list.get(1);
    }

    @Test
    public void testReverseList() {
        Assertions.assertEquals(
                new ListNode(
                        6,
                        new ListNode(
                                5,
                                new ListNode(
                                        4,
                                        new ListNode(
                                                3,
                                                new ListNode(
                                                        6, new ListNode(2, new ListNode(1))))))),
                solution.reverseList(listNode));

        Assertions.assertEquals(
                new ListNode(6, new ListNode(5)),
                solution.reverseList(new ListNode(5, new ListNode(6))));
        Assertions.assertEquals(new ListNode(6), solution.reverseList(new ListNode(6)));
    }

    @Test
    public void testReverseList2() {
        Assertions.assertEquals(
                new ListNode(
                        6,
                        new ListNode(
                                5,
                                new ListNode(
                                        4,
                                        new ListNode(
                                                3,
                                                new ListNode(
                                                        6, new ListNode(2, new ListNode(1))))))),
                solution.reverseList2(listNode));

        Assertions.assertEquals(
                new ListNode(6, new ListNode(5)),
                solution.reverseList2(new ListNode(5, new ListNode(6))));
        Assertions.assertEquals(new ListNode(6), solution.reverseList2(new ListNode(6)));
    }

    @Test
    public void testSwapPairs() {
        Assertions.assertEquals(
                new ListNode(
                        2,
                        new ListNode(
                                1,
                                new ListNode(
                                        3,
                                        new ListNode(
                                                6,
                                                new ListNode(
                                                        5, new ListNode(4, new ListNode(6))))))),
                solution.swapPairs(listNode));

        Assertions.assertEquals(
                new ListNode(6, new ListNode(5)),
                solution.swapPairs(new ListNode(5, new ListNode(6))));
        Assertions.assertEquals(new ListNode(6), solution.swapPairs(new ListNode(6)));
        Assertions.assertEquals(
                new ListNode(2, new ListNode(1, new ListNode(3))),
                solution.swapPairs(new ListNode(1, new ListNode(2, new ListNode(3)))));
    }

    @Test
    public void testRemoveNthFromEnd() {
        Assertions.assertEquals(
                new ListNode(
                        1,
                        new ListNode(
                                2,
                                new ListNode(
                                        6, new ListNode(4, new ListNode(5, new ListNode(6)))))),
                solution.removeNthFromEnd(listNode, 4));
        Assertions.assertNull(solution.removeNthFromEnd(new ListNode(6), 1));
        Assertions.assertEquals(
                new ListNode(1, new ListNode(2)),
                solution.removeNthFromEnd(new ListNode(1, new ListNode(2, new ListNode(3))), 1));
    }
}
