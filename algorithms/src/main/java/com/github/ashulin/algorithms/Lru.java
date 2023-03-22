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

import java.util.ArrayDeque;
import java.util.HashMap;

public class Lru {
    private final int len;
    private final ArrayDeque<Long> keys;
    private final HashMap<Long, Long> container;

    public Lru(int len) {
        this.len = len;
        this.keys = new ArrayDeque<>(len);
        this.container = new HashMap<>(len);
    }

    public Long get(Long key) {
        Long value = container.get(key);
        if (value == null) {
            return -1L;
        }
        keys.remove(key);
        keys.addLast(key);
        return value;
    }

    public void put(Long key, Long value) {
        Long oldValue = container.get(key);
        if (oldValue != null) {
            keys.remove(key);
        } else {
            if (keys.size() == len) {
                container.remove(keys.removeFirst());
            }
        }
        keys.addLast(key);
        container.put(key, value);
    }

    public int size() {
        return keys.size();
    }
}
