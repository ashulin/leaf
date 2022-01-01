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

import com.github.ashulin.algorithms.doc.Source;
import com.github.ashulin.algorithms.doc.Tag;
import com.github.ashulin.algorithms.doc.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Hierholzer {
    /**
     * 给你一份航线列表 tickets ，其中 tickets[i] = [fromi, toi] 表示飞机出发和降落的机场地点。请你对该行程进行重新规划排序。
     *
     * <p>所有这些机票都属于一个从 JFK（肯尼迪国际机场）出发的先生，所以该行程必须从 JFK 开始。如果存在多种有效的行程，请你按字典排序返回最小的行程组合。
     *
     * <p>例如，行程 ["JFK", "LGA"] 与 ["JFK", "LGB"] 相比就更小，排序更靠前。 假定所有机票至少存在一种合理的行程。且所有的机票 必须都用一次 且
     * 只能用一次。
     */
    @Source(332)
    @Tag(Type.SIN)
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> map = new HashMap<>();
        for (List<String> ticket : tickets) {
            PriorityQueue<String> queue =
                    map.computeIfAbsent(ticket.get(0), key -> new PriorityQueue<>());
            queue.offer(ticket.get(1));
        }
        List<String> ans = new ArrayList<>();
        dfs("JFK", map, ans);
        return ans;
    }

    private void dfs(String start, Map<String, PriorityQueue<String>> map, List<String> ans) {
        while (map.containsKey(start) && map.get(start).size() > 0) {
            String end = map.get(start).poll();
            dfs(end, map, ans);
        }
        ans.add(0, start);
    }
}
