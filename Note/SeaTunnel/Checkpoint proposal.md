## SeaTunnel Checkpoint Proposal

### Backgroud & Motivation

I think the **[Chandy-Lamport algorithm](https://en.wikipedia.org/wiki/Chandy%E2%80%93Lamport_algorithm)** can be used to implement distributed state snapshots (hereinafter referred to as *Checkpoint*);

Based on the current Source & Sink V2 API of SeaTunnel, the required features are:

1. Checkpoint supports both real-time and offline operations;

    - When the job restored, only the Subtask (Vertex of the execution graph) that has not been completed is restored;
2. Jobs can be restored normally when the user changes the parallelism of the Source;

We found that the Checkpoint implemented by Flink and Spark has a large scope of impact: When a job has multiple tables, a table failure will affect the entire job;
So we expect to be able to improve based on this problem:

3.   Minimize the unit of Checkpoint execution and reduce the impact of restore;

### Overall Design

![Overall](./resources/Checkpoint%20proposal/Overall.png)

1.   Convert the DAG to execution graph, and use [Topological Sorting](https://en.wikipedia.org/wiki/Topological_sorting) to identify the execution graph as n (n >= 1) pipelines;
2.   CheckpointCoordinator creates a separate CheckpointTask for each Pipeline;

     - CheckpointTask will manage its own associated pipeline, and the restore unit is pipeline;
3.   Jobs can be restored normally when the user changes the parallelism of the Source;
4.   For the completed subtask, Checkpoint will be performed normally, and the State will be retained (Apache SeaTunel will not have a super large state, such as Join);

     - During the Restore phase, the Completed Subtask will not be deployed;

### The Execution Unit (Pipeline)

Case: Two Kafka tables are written to the corresponding HDFS.

Job DAG:

![DAG](./resources/Checkpoint%20proposal/DAG.png)

Execution Graph:

![ExecutionGraph](./resources/Checkpoint%20proposal/ExecutionGraph.png)

By topological sorting, we can get

{Enumerator#1, Reader#1#1, Reader#1#2, Writer#1#1, Writer#1#2, AggregatedCommitter#1},

{Enumerator#2, Reader#2#1, Writer#2#1, AggregatedCommitter#2}

two queues, namely two Pipelines;

![Pipeline](./resources/Checkpoint%20proposal/Pipeline.png)

### Support for Completed Subtask

Case: Two MySQL instances are sub-database and sub-table, which are synchronized to other storage after aggregation;

![Completed-1](./resources/Checkpoint%20proposal/Completed-1.png)

We assume that the amount of data of instance Source#2 is smaller, and enter the Completed Status first;

![Completed-2](./resources/Checkpoint%20proposal/Completed-2.png)

If the pipeline fails/restarts-manually, the Subtask of Source#2 can not be deployed in the Restore phase to reduce resource usage;

![Completed-3](./resources/Checkpoint%20proposal/Completed-3.png)

### Support for parallelism changes

#### Plan A: Keyed State Redistribute[currently expected]

![Redistribute-1](./resources/Checkpoint%20proposal/Redistribute-1.png)

In the checkpoint execution phase, the ID of the subtask is used as the state key and saved to the state backend;

In the checkpoint restore phase, use `subtaskId = key % parallelism` to calculate the restored subtask ID;

##### SinkState

For the state of sink, this satisfies the requirements;

##### SourceState

For the state of the source, the subtask of the source still needs to continue processing;

**The following is the job of SourceTask, which is not managed by Checkpoint;**

We assume that the source is kafka, the task has 1 topic and 6 partitions, and the parallelism is changed from 2 to 3. After restored, the following figure is shown.

![SeaTunnel Snapshot-SourceState-1.drawio](./resources/Checkpoint%20proposal/SourceState-1.png)

We found that splits cannot be assigned to new readers;
In order for the new reader to be assigned to split, we need the ReaderTask to use `SourceSplitEnumerator#addSplitsBack` to return the split to the enumerator;

![SeaTunnel Snapshot-SourceState-2.drawio](./resources/Checkpoint%20proposal/SourceState-2.png)

The split is reassigned by the enumerator, and the restore phase is completed;

![SeaTunnel Snapshot-SourceState-3.drawio](./resources/Checkpoint%20proposal/SourceState-3.png)

#### Plan B: Uniform state redistribute

![Redistribute-2](./resources/Checkpoint%20proposal/Redistribute-2.png)

#### Deprecated Plan

Enumerator and readers of special processing source: 

![Source-1](./resources/Checkpoint%20proposal/Source-1.png)

In the checkpoint execution phase, the response barrier is the same as that of a normal task,

![Source-2](./resources/Checkpoint%20proposal/Source-2.png)

During the Restore phase, the state of the reader will be restored to the enumerator;

Use the `SourceSplitEnumerator#addSplitsBack` method to restore the split state, and then the enumerator can assign the split to the reader whose parallelism has been changed.

