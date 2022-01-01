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
    @Tag(Type.MATH)
    public int sqrt(int x) {
        long xi = x;
        while (xi * xi > x) {
            xi = (xi + x / xi) >> 1;
        }
        return (int) xi;
    }
}
