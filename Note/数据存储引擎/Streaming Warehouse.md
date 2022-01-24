## 需求点

-   OLAP分析；
-   流更新；
-   点查：通过B+ Tree；
-   流读：通过Log实现；
-   批读，点查：通过Snapshot、LSM、列式存储实现；
-   批写；

## 技术点

1.   LSM-Tree（Log Structured-Merge Tree，日志结构化并归树）：是一种分层，有序，面向磁盘的数据结构，其核心思想是充分了利用了磁盘批量的顺序写要远比随机写性能高出很多；
2.   列式存储；
3.   WAL（Write Ahead Log，预写日志）：changelog；
4.   B+ Tree；
5.   Bloomfilter（布隆过滤器）；
6.   data skipping（跳跃表）；
6.   Radix Trie（基数特里树/压缩前缀树）；
7.   MPP（Massively Parallel Processing，大规模并行处理架构）；
8.   SQL Pushdown；
9.   Table format：metadata的组织方式;
10.   Table Evolution：数据表演化；
      1.    Schema Evolution：模式演化；
      2.   Partition Evolution：分区演化；
11.   ACID；
12.   MVCC；
13.   OLTP（Online Transactional Processing，在线事务性处理引擎）；
14.   OLAP（Online Analytical Processing，在线分析性引擎）；
15.   **HTAP**（Hybrid Transactional/Analytical Processing，混合事务 / 分析处理引擎）：开源如TiDB；

## 参考

[Apache Flink不止于计算，数仓架构或兴起新一轮变革](https://mp.weixin.qq.com/s/8Nm1SplBxo80R3UaKqUFoQ)

[FLIP-188: Introduce Built-in Dynamic Table Storage](https://cwiki.apache.org/confluence/display/FLINK/FLIP-188%3A+Introduce+Built-in+Dynamic+Table+Storage)