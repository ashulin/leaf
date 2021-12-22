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
