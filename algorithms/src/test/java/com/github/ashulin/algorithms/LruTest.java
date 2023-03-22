/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ashulin.algorithms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LruTest {

    @Test
    public void lru() {
        Lru lru = new Lru(3);
        lru.put(1L, 1L);
        lru.put(2L, 2L);
        lru.put(2L, 3L);
        lru.put(3L, 3L);
        lru.put(4L, 4L);
        Assertions.assertEquals(3, lru.size());
        Assertions.assertEquals(-1L, lru.get(1L));
        lru.get(3L);
        lru.put(5L, 30L);
        lru.put(6L, 60L);
        Assertions.assertEquals(-1L, lru.get(4L));
        Assertions.assertEquals(3L, lru.get(3L));
    }
}
