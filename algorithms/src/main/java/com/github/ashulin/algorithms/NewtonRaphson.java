package com.github.ashulin.algorithms;

import com.github.ashulin.algorithms.doc.Complexity;
import com.github.ashulin.algorithms.doc.Source;

/**
 * @author Li Zongwen
 * @since 2021/12/13
 */
@Complexity(time = "O(log n)", space = "O(1)")
public class NewtonRaphson {
    /**
     * 给你一个非负整数 x ，计算并返回x的 算术平方根 。
     *
     * <p>由于返回类型是整数，结果只保留 整数部分 ，小数部分将被 舍去 。
     *
     * <p>注意：不允许使用任何内置指数函数和算符，例如 pow(x, 0.5) 或者 x ** 0.5 。
     *
     * <p><a href = "https://zhuanlan.zhihu.com/p/240077462">牛顿-拉夫逊（拉弗森）方法</a> 比二分快
     *
     * @since 2021/12/13
     */
    @Source(69)
    public int sqrt(int x) {
        long xi = x;
        while (xi * xi > x) {
            xi = (xi + x / xi) >> 1;
        }
        return (int) xi;
    }
}
