# Apache SeaTunnel CDC

| 开源组件     | Canal           | Debezium                          | Flink CDC                         |
| ------------ | --------------- | --------------------------------- | --------------------------------- |
| 支持数据库   | 仅支持MySQL     | 支持MySQL、Postgre SQL、Oracle 等 | 支持MySQL、Postgre SQL、Oracle 等 |
| 同步历史数据 | 不支持          | 单并行锁表                        | 多并行无锁                        |
| 输出端       | Kafka、RocketMQ | Kafka                             | Flink Connector                   |



## [Backgroud](https://github.com/apache/incubator-seatunnel/issues/2394)

Change data capture (CDC) refers to the process of identifying and capturing changes made to data in a database and then delivering those changes in real-time to a downstream process or system.

CDC is mainly divided into two ways: query-based and Binlog-based.
We know that MySQL has binlog (binary log) to record the user's changes to the database, so it is logical that one of the simplest and most efficient CDC implementations can be done using binlog. Of course, there are already many open source MySQL CDC implementations that work out of the box. Using binlog is not the only way to implement CDC (at least for MySQL), even database triggers can perform similar functions, but they may be dwarfed in terms of efficiency and impact on the database.

Typically, after a CDC captures changes to a database, it will publish the change events to a message queue for consumers to consume, such as Debezium, which persists MySQL (and also supports PostgreSQL, Mongo, etc.) changes to Kafka, and by subscribing to the events in Kafka, we can get the content of the changes and implement the functionality we need.

And as data synchronization, I think we need to support CDC as a feature, and I want to hear from you all how you think it can be implemented in SeaTunnel.

## Motivation

- Support parallel reading of historical data (Fast synchronization, billions of large table)
- Support reading incremental data (CDC)
- Support heartbeat detection (metrics, small traffic table)
- Support for dynamically adding new tables (Easier to operate and maintain)
- Support multi-table and sharding (Easy Configuration)
- Support Schema evolution(DDL)

## Overall Design

### Basic flow

![all-phase](resources/Apache%20SeaTunnel%20CDC/all-phase.png)

The CDC base process contains: 
1. Snapshot phase: Read the history data of the table
   - Minimum split granularity: the primary key range data of a table
2. Incremental phase: Read the incremental log change data for the table
   - Minimum split granularity: a table

### Snapshot phase

![snapshot-phase](resources/Apache%20SeaTunnel%20CDC/snapshot-phase.png)

The enumerator generates multiple `SnapshotSplit`s of a table and assigns them to the reader

```java
//  pseudo-code. 
public class SnapshotSplit implements SourceSplit {
    private final String splitId;
    private final TableId tableId;
    private final SeaTunnelRowType splitKeyType;
    private final Object splitStart;
    private final Object splitEnd;
}
```

When a `SnapshotSplit` reading is completed, the reader reports the high watermark of the split to the enumerator,
When all `SnapshotSplit`s  report high watermark, the enumerator start the incremental phase.

```java
//  pseudo-code. 
public class CompletedSnapshotSplitReportEvent implements SourceEvent {
    private final String splitId;
    private final Offset highWatermark;
}
```

#### Snapshot phase - SnapshotSplit read flow

![snapshot-read](resources/Apache%20SeaTunnel%20CDC/snapshot-read.png)

There are 4 steps:
1. log low watermark: get current log offset before reading snapshot data.
2. read SnapshotSplit data: Read the range data belonging to the split
     - **case 1**: step 1 & 2 cannot be atomized (MySQL)
     
     > Because we can't add table locks, and we can't add interval locks based on the low watermark, steps 1 & 2 are not atomic.

     - Exactly-once: use memory table to hold history data & filter the log data from the low to high watermark
     - At-least-once: direct output data & use low watermark instead of high watermark

     - **case 2**: step 1 & 2 can be atomized (Oracle)
     
     > You can use `for scn` to ensure the atomicity of the two steps

     - Exactly-once: direct output data & use low watermark instead of high watermark

3. log high watermark: 
     - `step 2 case 1 & Exactly-once`: get current log offset after reading snapshot data.
     - `other`: use low watermark instead of high watermark
4. if high > low watermark, read range log data

#### Snapshot phase - MySQL Snapshot Read & Exactly-once

![mysql-snapshot-read](resources/Apache%20SeaTunnel%20CDC/mysql-snapshot-read.png)

Because we can't determine where the query statement is executed between the high and low water levels, in order to ensure the exact-once of the data, we need to use the memory table to temporarily save the data.

1. log low watermark: get current log offset before reading snapshot data.
2. read SnapshotSplit data: read the range data belonging to the split and write to the memory table.
3. log high watermark: get current log offset after reading snapshot data.
4. read range log data: read log data and write to memory table
5. output the data of the memory table and release memory usage.

### Incremental phase

![Incremental-phase](resources/Apache%20SeaTunnel%20CDC/Incremental-phase.png)

When all snapshot splits report the water level, start the incremental phase.

Combine all snapshot splits and water level information to get `LogSplit`s

We want to minimize the number of log connections:

-   In the incremental phase, only one reader works by default, and the user can also configure the option to specify the number (cannot exceed the number of readers)
-   A reader gets at most one connection

```java
//  pseudo-code. 
public class LogSplit implements SourceSplit {
    private final String splitId;
    /**
     * All the tables that this log split needs to capture.
     */
    private final List<TableId> tableIds;
    /**
     * Minimum watermark for SnapshotSplits for all tables in this LogSplit
     */
    private final Offset startingOffset;
    /**
     * Obtained by configuration, may not end
     */
    private final Offset endingOffset;
    /**
     * SnapshotSplit information for all tables in this LogSplit.
     * </br> Used to support Exactly-Once.
     */
    private final List<CompletedSnapshotSplitInfo> completedSnapshotSplitInfos;
    /**
     * Maximum watermark in SnapshotSplits per table.
     * </br> Used to delete information in completedSnapshotSplitInfos, reducing state size.
     * </br> Used to support Exactly-Once.
     */
    private final Map<TableId, Offset> tableWatermarks;
}
```

```java
//  pseudo-code. 
public class CompletedSnapshotSplitInfo implements Serializable {
    private final String splitId;
    private final TableId tableId;
    private final SeaTunnelRowType splitKeyType;
    private final Object splitStart;
    private final Object splitEnd;
    private final Offset watermark;
}
```

![Incremental-read](resources/Apache%20SeaTunnel%20CDC/Incremental-read.png)

**Exactly-Once**: 

-   phase 1:  Use *completedSnapshotSplitInfos* filter before the watermark data.
-   phase 2:  A table no longer needs to be filtered, delete the data belonging to the table in *completedSnapshotSplitInfos*, because the following data needs to be processed.

**At-Least-Once:** Not filter data, and *completedSnapshotSplitInfos* doesn't need any data.

### Dynamic discovery of new tables

![add-table](resources/Apache%20SeaTunnel%20CDC/add-table.png)

Case 1: When a new table is discovered, the enumerator is in the snapshot phase and directly assigns a new split.

Case 2: When a new table is discovered, the enumerator is in the increment phase.

#### Dynamic discovery of new tables in the increment phase.

1.   Suspend the LogSplit reader. (Do we need to suspend the reader now if there is an idle reader?)
2.   Reader performs suspend operation.
3.   Reader report current log offset. (If it is not reported, the reader needs to support the combination of LogSplit)
4.   Assign SnapshotSplit to reader.
5.   Reader execution snapshot phase read.
6.   Reader report all SnapshotSplit watermark. 
7.   Assign a new LogSplit to the reader.
8.   The reader starts incremental reading again and ACK to the enumerator.

### Multiple structured tables

![mutil-table-dag](resources/Apache%20SeaTunnel%20CDC/mutil-table-dag.png)

This feature expects that the source can support the reading of multiple structure tables, and then use the side stream output to be consistent with the single table stream.

Also since this will involve changes to the DAG and translation modules, I also expect support for defining partitioners (Hash and forward).

Some features have been implemented, can see https://github.com/apache/incubator-seatunnel/issues/2490
