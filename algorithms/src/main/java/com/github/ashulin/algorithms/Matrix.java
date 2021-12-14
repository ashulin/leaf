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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Li Zongwen
 * @since 2021/12/14
 */
@Tag(Type.MATRIX)
@Tag(Type.ARRAY)
public class Matrix {
    /**
     * 给你一个正整数 n ，生成一个包含 1 到 n2 所有元素，且元素按顺时针顺序螺旋排列的 n x n 正方形矩阵 matrix 。 输入：n = 3
     * 输出：[[1,2,3],[8,9,4],[7,6,5]]
     */
    @Source(59)
    @Complexity(time = "O(n^2)", space = "O(1)")
    @Tag(Type.SIM)
    public int[][] generateMatrix(int n) {
        int[][] result = new int[n][n];

        // 分4边处理正方形：[startIndex, endIndex)
        int low = 0, high = n - 1;
        int num = 1;
        while (low <= high) {
            // top
            for (int i = low; i < high; i++) {
                result[low][i] = num++;
            }
            // right
            for (int i = low; i < high; i++) {
                result[i][high] = num++;
            }
            // bottom
            for (int i = high; i > low; i--) {
                result[high][i] = num++;
            }
            // left
            for (int i = high; i > low; i--) {
                result[i][low] = num++;
            }
            low++;
            high--;
        }
        // 处理中心一个值的情况
        if (n % 2 == 1) {
            result[n >> 1][n >> 1] = num;
        }
        return result;
    }

    /**
     * 给你一个 m 行 n 列的矩阵 matrix ，请按照 顺时针螺旋顺序 ，返回矩阵中的所有元素。。 输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]
     * 输出：[1,2,3,6,9,8,7,4,5]
     */
    @Source(54)
    @Source(value = 29, type = SourceType.OFFER)
    @Complexity(time = "O(m*n)", space = "O(1)")
    @Tag(Type.SIM)
    public List<Integer> spiralOrder(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        int size = n * m;
        List<Integer> result = new ArrayList<>(size);
        int top = 0, bottom = n - 1, left = 0, right = m - 1;
        while (top <= bottom && left <= right) {
            for (int i = left; i < right; i++) {
                result.add(matrix[top][i]);
            }
            for (int i = top; i < bottom; i++) {
                result.add(matrix[i][right]);
            }
            // 剩一个至右的横条时，至右处理时会剩余最后一个数据由至左处理，至左继续处理会导致数据溢出
            for (int i = right; i > left && result.size() < size; i--) {
                result.add(matrix[bottom][i]);
            }
            // 同上
            for (int i = bottom; i > top && result.size() < size; i--) {
                result.add(matrix[i][left]);
            }
            left++;
            right--;
            top++;
            bottom--;
        }
        // 剩余中心值
        if (n == m && n % 2 == 1) {
            result.add(matrix[n >> 1][n >> 1]);
        }
        return result;
    }
}
