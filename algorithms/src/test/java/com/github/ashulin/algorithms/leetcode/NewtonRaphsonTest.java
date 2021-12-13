package com.github.ashulin.algorithms.leetcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Li Zongwen
 * @since 2021/12/13
 */
public class NewtonRaphsonTest {
    private final NewtonRaphson solution = new NewtonRaphson();

    @Test
    public void testSqrtWithNewton() {
        Assertions.assertEquals(1, solution.sqrt(1));
        Assertions.assertEquals(0, solution.sqrt(0));
        Assertions.assertEquals(1, solution.sqrt(2));
        Assertions.assertEquals(1, solution.sqrt(3));
        Assertions.assertEquals(2, solution.sqrt(4));
        Assertions.assertEquals(2, solution.sqrt(6));
        Assertions.assertEquals(2, solution.sqrt(8));
        Assertions.assertEquals(3, solution.sqrt(9));
        Assertions.assertEquals(46339, solution.sqrt(2147395599));
        Assertions.assertEquals(46340, solution.sqrt(2147395600));
        Assertions.assertEquals(46340, solution.sqrt(2147395601));
        Assertions.assertEquals(46340, solution.sqrt(2147483647));
    }
}
