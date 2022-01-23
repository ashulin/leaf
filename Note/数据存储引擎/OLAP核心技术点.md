# 浅谈OLAP系统核心技术点

OLAP系统广泛应用于BI、Reporting、Ad-hoc、ETL数仓分析等场景，本文主要从体系化的角度来分析OLAP系统的核心技术点，从业界已有的OLAP中萃取其共性，分为**谈存储，谈计算，谈优化器，谈趋势**4个章节。

（总结性质文章，知识面有限，如有错误欢迎指正:0）

## 谈存储

**列存的数据组织形式**

行存，可以看做NSM (N-ary Storage Model)组织形式，一直伴随着关系型数据库，对于OLTP场景友好，例如innodb[1]的B+树聚簇索引，每个Page中包含若干排序好的行，可以很好的支持tuple-at-a-time式的点查以及更新等；而列存（Column-oriented Storage），经历了早期的DSM（Decomposition Storage Model）[2]，以及后来提出的 PAX (Partition Attributes Cross) 尝试混合NSM和DSM，在C-Store论文[3]后逐渐被人熟知，用于OLAP，分析型不同于交易场景，存储IO往往是瓶颈，而列存可以只读取需要的列，跳过无用数据，避免IO放大，同质数据存储更紧凑，编码压缩友好，这些优势可以减少IO，进而提高性能。

**编码与压缩**

对于基本类型，例如数值、string等，列存可以使用合适的编码，减少数据体积，在C-Store论文中对于是否排序、NDV（Number of Distince Values）区分度，这4种排列组合，给出了一些方案，例如数值类型，无序且NDV小的，转成bitmap，然后bit-packing编码。其他场景的编码还有varint、delta、RLE（Run Length Encoding）、字符串字典编码（Dictionary Encoding）等，这些轻量级的编码技术仅需要多付出一些CPU，就可以节省不小的IO。对于复杂类型，嵌套类型的可以使用Google Dremel论文[4]提出Striping/Assembly算法（开源Parquet），使用Definition Level+Repetition Level做编解码。一些数值类型有时也可以尝试大一统的用bitshuffle[14]做转换，配合压缩效果也不错，例如KUDU[7]和百度Palo（Doris）中有应用。在编码基础上，还可以进行传统的压缩，例如lz4、snappy、zstd、zlib等，一般发现压缩率不理想时可以不启用。

一些其他的选项，包括HBase，实际存储的是纯二进制，仅支持Column Family，实际不是columnar format，一些序列化框架和Hadoop融合比较好的，例如Avro，也不是列式存储。

**存储格式**

现代的OLAP往往采用行列混存的方案，采用Data Block + Header/Footer的文件结构，例如Parquet、ORC，Data Block使用Row Group（Parquet的叫法，ORC叫做Stripe）-> Column Chunk -> Page 三层级，每一层又有metadata，Row Group meta包含row count，解决暴力count(*)，Column Chunk meta包含max、min、sum、count、distinct count、average length等，还有字典编码，解决列剪枝，并且提供基础信息给优化器，Page meta同样可以包含max、min等，跳页用于加速计算。

**存储索引**

在Parquet、ORC中，除了列meta信息外，不提供其他索引，在其他存储上，支持了更丰富的索引，索引可以做单独的块（Index Block），或者形成独立的文件。例如阿里云ADB[5]，对于cardinality较小的，可以做bitmap索引，多个条件下推使用and/or。倒排索引也是可选的，需要在空间和性能上有所折中，还可以支持全文检索。Bloom Filter可以按照page粒度做很多组，加速"in", "="查询，快速做page剪枝。另外，假设数据按照某个列或者某几个列是有序的，这样可以减少数据随机性，好处在于相似的数据对编码压缩有利，而且可以基于Row Group、Column Chunk、Page的meta做有效的过滤剪枝，有序列可以使用B-Tree、Masstree[6]（例如KUDU[7]），或者借鉴LevelDB的思想，在Index Block内对有序列做稀疏索引，方便二分查找，Index Block可以用LRU Cache尽量常驻内存，这样有利于按照排序列做点查（point query）和顺序扫描的范围查询（range query）。另外其他列也可以做稀疏有序索引。有序列如果是唯一，可以看做OLTP中主键的概念。

**分布式存储**

DAC（Divide And Conquer）在分布式领域也是屡试不爽，要突破单机存储大小和IO限制，就需要把一个文件划分为若干小分片（sharding），以某个列做round-robin、constant、random、range、hash等，分布在不同的文件或者机器，形成分布式存储。第一类，存储计算一体的架构，基于单机磁盘（SATA、SSD、NVM），例如Greenpulm基于PostgreSQL，还有ClickHouse、百度Palo（Doris）等，是share nothing架构，可实现多副本，扩容需要reshard往往比较耗时。第二类，存储计算分离，文件存在分布式存储（GFS、HDFS）或者对象存储（S3、OSS、GCS），是share everthing（share storage）架构，好处在于扩展性和可用性的提高，由于存储网络延迟，所以一般都做批量、追加写，而非随机写，这把双刃剑也加大了OLAP在实时更新上难度，所以很多都放弃了实时写和ACID能力。存储计算分离的架构上，例如文件如果存在HDFS上，每个分片是一个HDFS block（例如128MB大小），便于高吞吐大块IO顺序读，一个Row Group大小等于block size，便于上层计算引擎例如Spark SQL作业并行计算。存储计算一体架构，可以更专心的设计文件和分片管理系统，采用Centralized Master + 多个Tablet架构，例如KUDU以及OLTP新兴的Tikv，分片的多副本依赖于一致性协议Multi-Paxos或者支持乱序提交的Raft协议，多个分片组成Raft-Group，这样可以打散一个表（文件）到多分片多副本的架构上，既保证了扩展性又保证了高可用。Centralized Master管理分片存放的位置，元数据，便于负载均衡、分裂合并等。

>   示例：数据按uid range分片。

```text
    shard1              shard2
+---------------+  +---------------+  
|uid|   date    |  |uid|   date    | 
+---------------+  +---------------+ 
| 1 | 2020-11-11|  | 3 | 2020-11-13|
| 2 | 2020-11-12|  | 4 | 2020-11-14|
+---------------+  +---------------+ 
```

>   示例：数据按uid hash分片，f(uid) = uid mod 2。

```text
    shard1              shard2
+---------------+  +---------------+  
|uid|   date    |  |uid|   date    | 
+---------------+  +---------------+ 
| 1 | 2020-11-11|  | 2 | 2020-11-12|
| 3 | 2020-11-13|  | 4 | 2020-11-14|
+---------------+  +---------------+ 
```

**数据进一步分区**

数据分片的基础上，可以进行更细粒度的分区（partition），便于做分区剪枝（partition prune），直接跳过不需要扫描的文件。分片（sharding）策略按照range，可以优化OLAP的范围查询和快速点查；按照hash分区，可以充分打散，有效解决hotspot热点。将二者结合，做二级分区（two-level），例如阿里云ADB、ClickHouse、KUDU，支持DISTRIBUTED BY HASH再PARTITION BY RANGE，而百度Palo（Doris）一般先按时间一级分区，更好做冷热数据区分，二级分区分桶采用hash。

>   示例：数据按照二级分区，一级分区uid hash分片，二级分区按date，形成4个文件。

```text
    shard1              shard2
+---------------+  +---------------+  
|uid|   date    |  |uid|   date    | 
+---------------+  +---------------+ 
| 1 | 2020-11-11|  | 2 | 2020-11-12|
+---------------+  +---------------+

+---------------+  +---------------+  
|uid|   date    |  |uid|   date    | 
+---------------+  +---------------+ 
| 3 | 2020-11-13|  | 3 | 2020-11-14| 
+---------------+  +---------------+ 
```

**实时写入和ACID**

随着实时数仓和HTAP，HSAP[8]等概念的兴起，对于传统数据处理的Lambda架构弊端就凸显出来，链路长，数据冗余，数据一致性不好保证等。融合OLTP的能力，第一点就是在之前所述的immutable table file上做实时增删改，要保证低延迟，高吞吐，可以借鉴LSM-Tree思想，优化写吞吐，将流式的低延迟随机写，最终变成聚批mini-batch的group commit顺序写，依赖write-ahead log保证持久性，最终形成Base + Delta的文件结构，读流程包括点查或者扫描，基于Base的同时，还需要merge Delta的变化，另外后台通过minor compaction和major compaction不断的合并Delta和Base，可以不断优化读性能，在阿里云ADB，KUDU，Google MESA[9]里面都采用了类似的方案。在读写一致性层面，需要提供ACID和事务隔离特性，比较好保证单行和mini-batch的原子性，持久性不言而喻，对于一致性和事务隔离，可以采用MVCC机制，每个写都带有version，很简单的实现带版本查一致性，快照一致性（snapshot isolation）。

## 谈计算

**查询步骤**

SQL语言是OLAP的标配，一个完整的SQL查询步骤包括

1.   SQL词法解析，语法解析；
2.   形成抽象语法树（AST）；
3.   校验检查；
4.   AST转成关系代数表达式（relational algebra）；
5.   根据关系代数表达式生成逻辑执行计划（logical plan）；
6.   经过优化器生成最优的执行计划；
7.   根据执行计划生成物理执行计划（physical plan）；
8.   最终交由执行器执行并返回结果；

由SQL到AST的过程，类库和工具较多，C++可用Lex/Yacc，Java可用JavaCC/ANTLR，也可以自己手写实现。由AST到关系代数表达式，可以使用visitor模式遍历。下一章节谈优化器。本节聚焦在物理执行计划后的执行阶段。

**OLAP数据建模分类**

ROLAP和MOLAP。Relational OLAP（ROLAP）对SQL支持好，查询灵活，使用组合模型，雪花或者星型模型组织多张表。ROLAP计算的数据规模往往小于离线大数据计算（Hive/Spark），ROLAP产品很多，包括传统的Greenpulm、Vertica、Teradata，Sql-on-Hadoop系的Presto、Impala、Spark SQL、HAWQ，云计算厂商的阿里云ADB、Google BigQuery，AWS RedShift，有学术界出品的MonetDB[10]，还有新兴的ClickHouse。

如果把查询阶段分为

```text
                   cache
                     /\
                     |
pre-computing -> computing -> post computing
```

上面的提到的存储技术更多是为了ROLAP在computing阶段优化考虑的，如果把计算中的熵前置到pre-computing阶段做预计算，也可以大幅优化computing阶段。Multidimensional OLAP（MOLPA）可以把数据预计算，有些场景下不一定需要细粒度的fact，可以严格区分维度列和指标列，使用Kylin、Druid等，利用上卷（roll-up）做数据立方体（data cube），这样可以大大减少OLAP场景下聚合查询的IO，另外百度Palo、Google MESA，基于上卷操作做物化视图，也减少了IO消耗，所以他们对于高并发查询支持普遍较好，但是缺点就在于查询不够灵活，数据有冗余。下文主要针对ROLAP谈计算。

**计算引擎分类**

物理执行计划往往是一个DAG，每个节点都是一种operator，最下游的叶子节点一般都是TableScan operator，这个DAG的分布式执行器就是计算引擎（Query Engine），分为两个流派。

第一类是基于离线计算引擎，例如Hive on MR，Spark SQL，阿里云MaxCompute，支持超大规模的数据，进行了容错保证，多个stage落盘（spill to disk），使用resource manager调度和queueing，作业可能持续非常长的时间，占用大量资源，并发低。

第二类是MPP，例如Greenpulm、Presto、Impala、阿里云ADB，RedShift支持大规模数据，不需要reource manager耗时的分配资源和调度任务，long-running的task manager，只需要轻量级的调度，查询一般不容错，算子并行执行，并行度有限制避免straggler node影响TP99，相比基于离线的计算引擎往往是短任务，查询耗时不会太长。

Presto、Impala属于Sql-on-Hadoop MPP，利用Hive metastore，直接读取Parquet、ORC等文件格式，Greenpulm、RedShift基于PostgreSQL，阿里云ADB采用私有的数据存储技术，计算存储分离的架构，存储表到分布式存储盘古上。

**MPP架构**

通用的MPP架构组成由coordinator，worker，metastore，scheduler组成，各个产品名称不同而已。通过metastore可以获取表元信息、分区/分片位置、辅助coordinator做校验等。coordinator负责从SQL到物理执行计划的生成以及执行，一个计划往往被切分为多个plan fragment，plan fragment之间通过添加ExchangeOperator来传递数据（例如shuffle），逻辑上plan fragment等同于stage，scheduler管理所有worker节点，coordinator调用scheduler分发stage到不同的worker节点执行，就形成了很多task。一个task，包含一个或者多个operator算子，最简单的算子实现就是解释执行（interpreted）的模式。算子包括Project、Filter、TableScan、HashJoin、Aggregation等，叶子节点一般是TableScan，拉取存储中数据。MPP架构就是充分利用分布式的特性，让算子分布式的并行计算，同时task内部也可以做并行处理，加速查询。

**计算执行**

数据流。DAG在进行数据流动时，采用pipeline方式，也就是上游stage不用等下游stage完全执行结束就可以拉取数据并执行计算。数据不落盘，算子之间通过内存直接拷贝到socket buffer发送，需要保证内存足够大，否则容易OOM。

火山模型（Volcano-style），是一种Row-Based Streaming Iterator Model算子的实现，只需要open、next、close三个函数，就可以实现数据从底向上的“拉”取，驱动计算进行。

向量化执行（Vectorized query）。MonetDB论文提出了火山模型的改进方案——向量化执行，火山模型tuple-at-a-time的实现，每个算子执行完传递一行给上游算子继续执行，函数调用过多，且大量的虚函数调用，条件分支预测失败，直接现象就是CPU利用率低（low IPC）。而现代的CPU有多级流水线可以实现指令级并行，超标量（super scalar）实现乱序执行，对于forloop可以有效优化，超线程还能实现线程级并行，而CPU多级的Cache，以及cache line的有效利用避免cache miss，再配合编译器的优化，都会大大加速计算过程。向量化执行的思想就是算子之间的输入输出是一批（Batch，例如上千行）数据，这样可以让计算更多的停留在函数内，而不是频繁的交互切换，提高了CPU的流水线并行度，而且还可以使用SIMD指令，例如AVX指令集来实现数据并行处理。实际实现中，例如Impala各个算子的input虽然是RowBatch，但除了TableScan算子，其他的也是火山模型执行式的row by row处理，TableScan读存储，列式内存布局加速pushdown的filter执行，aggregation下推后还可以使用SIMD指令加速聚合。但是向量化也会带来额外的开销，就是物化中间结果（materlization），以牺牲物化的开销换取更高的计算性能。

动态代码生成（codegen）。解释执行（interpreted）的算子，因为面向通用化设计，大数据集下往往效率不高，可以使用codegen动态生成算子逻辑，例如Java使用ASM或者Janino，C++使用LLVM IR，这样生成的算子更贴近计算，减少了冗余和虚函数调用，还可以多个算子糅合成一个函数。另外表达式计算的codegen还可以做的更极致，一些简单的计算可以做成汇编指令，进一步加速。

关于向量化或者codegen，孰优孰劣，论文Everything You Always Wanted to Know About Compiled and Vectorized Queries But Were Afraid to Ask [11]进行了深入的对比。二者也可以融合，通过codegen生成向量化执行代码，另外也不一定做wholestage codegen，和解释执行也可以一起配合。

计算的耗时有一部分会损耗在IO、CPU的闲置上。内存的布局和管理，行式布局还是列式布局，对于CPU Cache是否友好，内存池还是按需分配，都会影响着系统的吞吐，C++可自行维护Arena或者使用jemalloc等框架，而Java的heap memory比较低效还影响GC，因此使用Unsafe API操作堆外内存。另外Arrow的兴起，也对于跨进程通信后，不必进行数据反序列化、内存分配再拷贝，就可以读取列式的数据，也进一步加速了计算。

**常见算子实现**

TableScan算子直读底层数据源，例如Presto，抽象了很好了connector，可对接多种数据源（Hadoop，对象存储等），一般都支持projection、filter，因此可以做filter pushdown和projection pushdown到TableScan，另外在做predicate的时候可以使用lazy materialization（延迟物化）的技巧去short circuit掉先不需要的列。

Join算子的实现，如果两个表都很小，最简单的利用in-memory hash join、simple nested loop join；一大一小，可以广播小表（broadcast），一般维度表都比较小，如果大表有索引，扫描小表，根据大表做index lookup join，否则基于小表做build table，大表做probe table，实现hash join；两个大表，如果两个表的join key的一级分区策略相同，则可以很好的对齐，避免大表shuffle，直接在大表的shard做local join，如果不能对齐，则两个表按照join key shuffle到其他节点，重分布式后再做join；另外如果两个表的join key有序，还可以使用sort-merge join。

**资源管理与调度**

MPP架构下coordinator需要scheduler调度task到worker节点，对于长计算任务或者ETL任务，会占用很多资源，导致OLAP的并发度受限，其他请求需要排队，因此很难服务对外在线请求，为了迎合混合负载，传统scheduler简单粗暴的调度和资源管理已经无法满足要求，因此可以进行任务的fine grained schedule避免空闲资源，请求间对资源的使用尽量的隔离，避免bad query吃满资源，简单的策略可以通过label化集群，或者用SQL hint实现，区分长短计算任务，让更多的短任务也可以快速得到响应。当OLAP系统足够高性能后，更好的资源管理和调度，将会提升OLAP为一个支持高并发、低延迟的，可对外提供在线服务的系统，而不仅仅是一个in-house的分析系统。

## 谈优化器

查询优化器不光是传统数据库DB2、Oracle、MySQL的核心，在OLAP里也至关重要。AST转为SQL形式化表达语言——关系代数表达式（relational algebra），代码实现就是一颗关系运算符组成的树，查询优化主要是围绕着“等价交换”的原则做相应的转换，优化关系代数表达式。关系代数的基本运算包括投影（project）、选择（select）、并（union）、差（set difference）、连接（join）等。优化器分为Rule-Based Optimizer (RBO) 和Cost-Based Optimizer (CBO) 两类。

**RBO**

会将原有表达式裁剪掉，遍历一系列规则（Rule），只要满足条件就转换，生成最终的执行计划。一些常见的规则包括分区裁剪（Partition Prune）、列裁剪、谓词下推（Predicate Pushdown）、投影下推（Projection Pushdown）、聚合下推、limit下推、sort下推、常量折叠（Constant Folding）、子查询内联转join等。

**CBO**

会将原有表达式保留，基于统计信息 + 代价模型，尝试探索生成等价关系表达式，最终取代价最小的执行计划。CBO的实现有两种模型，Volcano模型，Cascades模型，很流行的Calcite[12]使用Volcano模型，比如Flink、Hive都基于此，Orca使用Cascades模型，在Greenpulm中使用。优化器需要尽量的高效，高效的生成搜索空间、动态规划遍历搜索空间（top down、bottom up、depth-first等），高效的剪枝策略等都可以加速优化过程。统计信息包括表数据大小，row count。查询列的trait metadata（min、max、cardinality等），sortness、可利用的索引，直方图（Histogram）分布统计等。Join是OLAP最消耗吞吐的算子之一，也是ROLAP对于分析最强大的地方，可以进行多表的关联查询，常见的优化手段包括，join reorder，使用left-deep tree还是bushy tree执行join，以及如何选择join算法实现（上节提到的各种join实现的选择），结合高效索引结构实现的index join，group by下推、top-n下推等。

## 谈趋势

OLAP领域经历了从RDBMS建立起来的SQL + OLAP，到ETL + 专有OLAP的数仓阶段，目前仍在不断演进，更多的云厂商也加入这个领域，展示出、也正经历着如下的趋势。

**实时分析**。传统的OLAP需要做各种pipeline、ETL导入数据，这样的架构会存储多份数据，冗余并且一致性不好保证，也引入过多的技术栈和复杂度，也不能满足实时分析，即使mini-batch的处理仍然需要最快数分钟。业界的趋势在于赋予OLAP高吞吐实时写，提供实时查询能力，例如上游数据源，经过流计算系统，老的架构基于lambda，写历史数据到存储再清洗，实时数据入一些NoSQL，使用方需要做各种数据源merge操作，流行的方式是流计算系统直接写OLAP，这样避免了数据孤岛，保证了链路简单，阿里云hologres团队提出的HSAP（Hybrid Serving/Analytical Processing）[8]正是这种理念。

**HTAP**。事务处理和分析处理在一个数据库中提供，是最理想的状态，但是二者的技术体系往往又很难融合，因此现在很多数据库厂商都在做这方面的尝试，保证数据一致性是很大的挑战，一种思路是从OLTP到OLAP，多副本存储时，有些副本是专门为OLAP定制的，使用专用的OLAP引擎提供查询，另外就是赋予ACID和事务能力到OLAP系统中，使得OLAP也支持INSERT/DELETE/UPDATE操作。

**云原生**。传统的OLAP，例如Exadata等依赖于高端硬件，很多on-premise的解决方案也面临扩展性和成本问题，云原生的架构通过虚拟化技术，可实现更好的弹性计算，如果采用存储计算分离的架构还可以实现弹性存储，这些水平扩展的机制可以很好的兼顾高性能、成本和扩展性。

**多模数据结构分析**。不仅限于结构化数据，半结构化、非结构化的数据分析也逐渐在OLAP中应用，包括向量检索，JSON、ARRAY检索等。

**软硬一体化**。计算方面，更好利用多核并行，使得查询满足NUMA-aware，亲核性（affinity）可以进一步榨干系统的吞吐，使用FPGA、GPU硬件加速，利用这些硬件提供的超高带宽和深度流水线可以加速一些向量计算和聚合操作；存储方面，随着存储查询带宽增大、延迟降低，可以应用更多新存储，例如Intel 傲腾NVM 3D-XPoint SSD[13]提供2.6G/s的顺序读吞吐，高并发点查延迟可控制在10几个us；网络方面，基于RDMA网络，DPDK等技术可替换传统的tcp，做kernel bypass，降低网络延迟。上层的OLAP软件可以基于这些新硬件做更深度的定制，提供更极致的性能。

------

**参考资料**

[1] [从MySQL InnoDB物理文件格式深入理解索引]([从MySQL InnoDB物理文件格式深入理解索引](https://zhuanlan.zhihu.com/p/103582178))

[2] [A DECOMPOSITION STORAGE MODEL](http://www.inf.ufpr.br/eduardo/ensino/ci763/papers/DSM-columns.pdf)

[3] [C-Store: A Column-oriented DBMS](http://www.vldb.org/archives/website/2005/program/paper/thu/p553-stonebraker.pdf)

[4] [Dremel: Interactive Analysis of Web-Scale Datasets](https://static.googleusercontent.com/media/research.google.com/zh-CN//pubs/archive/36632.pdf)

[5] [AnalyticDB: Real-time OLAP Database System at Alibaba Cloud](http://www.vldb.org/pvldb/vol12/p2059-zhan.pdf)

[6] [Cache craftiness for fast multicore key-value storage](https://pdos.csail.mit.edu/papers/masstree:eurosys12.pdf)

[7] [Kudu: Storage for Fast Analytics on Fast Data](https://kudu.apache.org/kudu.pdf)

[8] [数据仓库、数据湖、流批一体，终于有大神讲清楚了]([阿里云Hologres：数据仓库、数据湖、流批一体，终于有大神讲清楚了！](https://zhuanlan.zhihu.com/p/140867025))

[9] [Mesa: Geo-Replicated, Near Real-Time, Scalable Data Warehousing](https://static.googleusercontent.com/media/research.google.com/zh-CN//pubs/archive/42851.pdf)

[10] [MonetDB/X100: Hyper-Pipelining Query Execution](https://w6113.github.io/files/papers/monetdb-cidr05.pdf)

[11] [Everything You Always Wanted to Know About Compiled and Vectorized Queries But Were Afraid to Ask](https://www.vldb.org/pvldb/vol11/p2209-kersten.pdf)

[12] [Apache Calcite: A Foundational Framework for Optimized Query Processing Over Heterogeneous Data Sources](https://arxiv.org/pdf/1802.10233.pdf)

[13] [Intel Optane Series]([Intel® Optane™ DC SSD Series](https://www.intel.com/content/www/us/en/products/memory-storage/solid-state-drives/data-center-ssds/optane-dc-ssd-series.html))

[14] [bitshuffle](https://github.com/kiyo-masui/bitshuffle)

## 文章来源

[浅谈OLAP系统核心技术点 - neoReMinD的文章 - 知乎](https://zhuanlan.zhihu.com/p/163236128)