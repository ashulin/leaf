# Redis

**Remote Dictionary Server**：远程字典服务器

是一个基于内存的高性能 Key-Value 数据库；开源，使用C语言编写；

## 优点

### **1. 速度快**

因为数据存在内存中，类似于 HashMap ，HashMap 的优势就是查找和操作的时间复杂度都是O (1) 。

> Redis 本质上是一个 Key-Value 类型的内存数据库，很像Memcached ，整个数据库统统加载在内存当中进行操作，定期通过异步操作把数据库数据 flush 到硬盘上进行保存。
>
> 因为是纯内存操作，Redis 的性能非常出色，每秒可以处理超过 10 万次读写操作，是已知性能最快的 Key-Value 数据库。

### **2. 支持丰富数据类型**

支持 String ，List，Set，Sorted Set (ZSet)，Hash 。

> Redis 的出色之处不仅仅是性能，Redis 最大的魅力是支持保存多种数据结构，此外单个 Value 的最大限制是1GB，不像 Memcached只能保存1MB的数据，因此Redis可以用来实现很多有用的功能。比方说：
>
> - 用他的 List 来做 FIFO 双向链表，实现一个轻量级的高性能消息队列服务。
> - 用他的 Set 可以做高性能的 tag 系统等等。

### **3. 丰富的特性**

- 订阅发布 Pub / Sub 功能
- Key 过期策略
- 事务
- 支持多个 DB
- 计数

并且在 Redis 5.0 增加了 Stream 功能，一个新的强大的支持多播的可持久化的消息队列，提供类似 Kafka 的功能。

### 4. 持久化存储

Redis 提供 RDB 和 AOF 两种数据的持久化存储方案，解决内存数据库最担心的万一 Redis 挂掉，数据会消失掉。

###  5. 高可用

内置 Redis Sentinel ，提供高可用方案，实现主从故障自动转移。

内置 Redis Cluster ，提供集群方案，实现基于槽的分片方案，从而支持更大的 Redis 规模。

## 缺点

- 1、由于 Redis 是内存数据库，所以，单台机器，存储的数据量，跟机器本身的内存大小。虽然 Redis 本身有 Key 过期策略，但是还是需要提前预估和节约内存。如果内存增长过快，需要定期删除数据。

    > 另外，可使用 Redis Cluster、Codis 等方案，对 Redis 进行分区，从单机 Redis 变成集群 Redis 。

- 2、如果进行完整重同步，由于需要生成 RDB 文件，并进行传输，会占用主机的 CPU ，并会消耗现网的带宽。不过 Redis2.8 版本，已经有部分重同步的功能，但是还是有可能有完整重同步的。比如，新上线的备机。

- 3、修改配置文件，进行重启，将硬盘中的数据加载进内存，时间比较久。在这个过程中，Redis 不能提供服务。

## Redis 和 Memcached 的区别

### **1. Redis 支持复杂的数据类型**

- Memcached 仅提供简单的字符串。
- Redis 提供复杂的数据类型，丰富的数据操作。

也因为 Redis 支持复杂的数据类型，Redis 即使晚于 Memcached 推出，却获得更多开发者的青睐。

Redis 相比 Memcached 来说，拥有更多的数据类型，能支持更丰富的数据操作。如果需要缓存能够支持更复杂的结构和操作，Redis 会是不错的选择。

###  **2. Redis 原生支持集群模式**

- 在 Redis3.x 版本中，官方便能支持 Cluster 模式。
- Memcached 没有原生的集群模式，需要依靠客户端来实现往集群中分片写入数据。

###  **3. 性能对比**

- Redis 只使用单核，而 Memcached 可以使用多核，所以平均每一个核上 Redis在存储小数据时比 Memcached 性能更高。
- 在 100k 以上的数据中，Memcached 性能要高于 Redis 。虽然 Redis 最近也在存储大数据的性能上进行优化，但是比起 Memcached，还是稍有逊色。

更多关于性能的对比，可以看看 [《Memcached 与 Redis 的关键性能指标比较》](https://www.jianshu.com/p/34f90813d7c9) 。

### **4. 内存使用效率对比**

- 简单的 Key-Value 存储的话，Memcached 的内存利用率更高，可以使用类似内存池。
- 如果 Redis 采用 hash 结构来做 key-value 存储，由于其组合式的压缩， 其内存利用率会高于 Memcached 。

另外，Redis 和 Memcached 的内存管理方法不同。

- Redis 采用的是包装的 malloc/free ， 相较于 Memcached 的内存管理方法 tcmalloc / jmalloc 来说，要简单很多 。

### **5. 网络 IO 模型**

- Memcached 是多线程，非阻塞 IO 复用的网络模型，原型上接近 Nignx 。
- Redis 使用单线程的 IO 复用模型，自己封装了一个简单的 AeEvent 事件处理框架，主要实现了 epoll, kqueue 和 select ，更接近 Apache 早期的模式。

### **6. 持久化存储**

- Memcached 不支持持久化存储，重启时，数据被清空。
- Redis 支持持久化存储，重启时，可以恢复已持久化的数据。

## Redis 线程模型

redis 内部使用文件事件处理器 `file event handler`，这个文件事件处理器是单线程的，所以 redis 才叫做单线程的模型。它采用 IO 多路复用机制同时监听多个 socket，根据 socket 上的事件来选择对应的事件处理器进行处理。

文件事件处理器的结构包含 4 个部分：

- 多个 socket
- IO 多路复用程序
- 文件事件分派器
- 事件处理器（连接应答处理器、命令请求处理器、命令回复处理器）

多个 socket 可能会并发产生不同的操作，每个操作对应不同的文件事件，但是 IO 多路复用程序会监听多个 socket，会将 socket 产生的事件放入队列中排队，事件分派器每次从队列中取出一个事件，把该事件交给对应的事件处理器进行处理。

来看客户端与 redis 的一次通信过程：

- 客户端 socket01 向 redis 的 server socket 请求建立连接，此时 server socket 会产生一个 `AE_READABLE` 事件，IO 多路复用程序监听到 server socket 产生的事件后，将该事件压入队列中。文件事件分派器从队列中获取该事件，交给`连接应答处理器`。连接应答处理器会创建一个能与客户端通信的 socket01，并将该 socket01 的 `AE_READABLE` 事件与命令请求处理器关联。
- 假设此时客户端发送了一个 `set key value` 请求，此时 redis 中的 socket01 会产生 `AE_READABLE` 事件，IO 多路复用程序将事件压入队列，此时事件分派器从队列中获取到该事件，由于前面 socket01 的 `AE_READABLE` 事件已经与命令请求处理器关联，因此事件分派器将事件交给命令请求处理器来处理。命令请求处理器读取 socket01 的 `key value` 并在自己内存中完成 `key value` 的设置。操作完成后，它会将 socket01 的 `AE_WRITABLE` 事件与令回复处理器关联。
- 如果此时客户端准备好接收返回结果了，那么 redis 中的 socket01 会产生一个 `AE_WRITABLE` 事件，同样压入队列中，事件分派器找到相关联的命令回复处理器，由命令回复处理器对 socket01 输入本次操作的一个结果，比如 `ok`，之后解除 socket01 的 `AE_WRITABLE` 事件与命令回复处理器的关联。

### 为什么 Redis 单线程模型也能效率这么高？

1.   C语言实现；

2.   纯内存操作。

     >   Redis 为了达到最快的读写速度，将数据都读到内存中，并通过异步的方式将数据写入磁盘。所以 Redis 具有快速和数据持久化的特征。
     >
     >   如果不将数据放在内存中，磁盘 I/O 速度为严重影响 Redis 的性能。

3.   核心是基于非阻塞的 IO 多路复用机制。

4.   单线程反而避免了多线程的频繁上下文切换问题。

     >   Redis 利用队列技术，将并发访问变为串行访问，消除了传统数据库串行控制的开销

5.   Redis 全程使用 hash 结构，读取速度快，还有一些特殊的数据结构，对数据存储进行了优化，如压缩表，对短数据进行压缩存储，再如，跳表，使用有序的数据结构加快读取的速度。也因为 Redis 是单线程的，所以可以实现丰富的数据结构，无需考虑并发的问题。

## 持久化

### **持久化方式**

Redis 提供了两种方式，实现数据的持久化到硬盘。

- 1、【全量】RDB (Redis Database Backup)持久化，是指在指定的时间间隔内将内存中的**数据集快照**写入磁盘。实际操作过程是，fork 一个子进程，先将数据集写入临时文件，写入成功后，再替换之前的文件，用二进制压缩存储。
- 2、【增量】AOF (append only file)持久化，以日志的形式记录服务器所处理的每一个**写、删除操作**，查询操作不会记录，以文本的方式记录，可以打开文件看到详细的操作记录。

### **RDB (Snapshot)**

#### Redis Database Backup

>   Redis数据库备份文件

#### 优点

- 灵活设置备份频率和周期。你可能打算每个小时归档一次最近 24 小时的数据，同时还要每天归档一次最近 30 天的数据。通过这样的备份策略，一旦系统出现灾难性故障，我们可以非常容易的进行恢复。
- 非常适合冷备份，对于灾难恢复而言，RDB 是非常不错的选择。因为我们可以非常轻松的将一个单独的文件压缩后再转移到其它存储介质上。推荐，可以将这种完整的数据文件发送到一些远程的安全存储上去，比如说 Amazon 的 S3 云服务上去，在国内可以是阿里云的 OSS 分布式存储上。
- 性能最大化。对于 Redis 的服务进程而言，在开始持久化时，它唯一需要做的只是 fork 出子进程，之后再由子进程完成这些持久化的工作，这样就可以极大的避免服务进程执行 IO 操作了。也就是说，RDB 对 Redis 对外提供的读写服务，影响非常小，可以让 Redis 保持高性能。
- 恢复更快。相比于 AOF 机制，RDB 的恢复速度更更快，更适合恢复数据，特别是在数据集非常大的情况。

#### 缺点

- 如果你想保证数据的高可用性，即最大限度的避免数据丢失，那么 RDB 将不是一个很好的选择。因为系统一旦在定时持久化之前出现宕机现象，此前没有来得及写入磁盘的数据都将丢失。

    > 所以，RDB 实际场景下，需要和 AOF 一起使用。

- 由于 RDB 是通过 fork 子进程来协助完成数据持久化工作的，因此，如果当数据集较大时，可能会导致整个服务器停止服务几百毫秒，甚至是 1 秒钟。

    > 所以，RDB 建议在业务低谷，例如在半夜执行。

### **AOF (Redo log)**

#### Append Only File

>   仅追加到文件

#### 优点

- 该机制可以带来更高的

    数据安全性

    ，即数据持久性。Redis 中提供了 3 种同步策略，即每秒同步、每修改(执行一个命令)同步和不同步。

    - 事实上，每秒同步也是异步完成的，其效率也是非常高的，所差的是一旦系统出现宕机现象，那么这一秒钟之内修改的数据将会丢失。
    - 而每修改同步，我们可以将其视为同步持久化，即每次发生的数据变化都会被立即记录到磁盘中。可以预见，这种方式在效率上是最低的。
    - 至于无同步，无需多言，我想大家都能正确的理解它。

- 由于该机制对日志文件的写入操作采用的是append模式，因此在写入过程中即使出现宕机现象，也不会破坏日志文件中已经存在的内容。

    - 因为以 append-only 模式写入，所以没有任何磁盘寻址的开销，写入性能非常高。
    - 另外，如果我们本次操作只是写入了一半数据就出现了系统崩溃问题，不用担心，在 Redis 下一次启动之前，我们可以通过 redis-check-aof 工具来帮助我们解决数据一致性的问题。

- 如果日志过大，Redis可以自动启用 **rewrite** 机制。即使出现后台重写操作，也不会影响客户端的读写。因为在 rewrite log 的时候，会对其中的指令进行压缩，创建出一份需要恢复数据的最小日志出来。再创建新日志文件的时候，老的日志文件还是照常写入。当新的 merge 后的日志文件 ready 的时候，再交换新老日志文件即可。

- AOF 包含一个格式清晰、易于理解的日志文件用于记录所有的**修改操作**。事实上，我们也可以通过该文件完成数据的重建。

#### 缺点

- 对于相同数量的数据集而言，AOF 文件通常要大于 RDB 文件。RDB 在恢复大数据集时的速度比 AOF 的恢复速度要快。
- 根据同步策略的不同，AOF 在运行效率上往往会慢于 RDB 。总之，每秒同步策略的效率是比较高的，同步禁用策略的效率和 RDB 一样高效。
- 以前 AOF 发生过 bug ，就是通过 AOF 记录的日志，进行数据恢复的时候，没有恢复一模一样的数据出来。所以说，类似 AOF 这种较为复杂的基于命令日志/merge/回放的方式，比基于 RDB 每次持久化一份完整的数据快照文件的方式，更加脆弱一些，容易有 bug 。不过 AOF 就是为了避免 rewrite 过程导致的 bug ，因此每次 rewrite 并不是基于旧的指令日志进行 merge 的，而是基于当时内存中的数据进行指令的重新构建，这样健壮性会好很多。

### **如何选择**

- 不要仅仅使用 RDB，因为那样会导致你丢失很多数据

- 也不要仅仅使用 AOF，因为那样有两个问题，第一，你通过 AOF 做冷备，没有 RDB 做冷备，来的恢复速度更快; 第二，RDB 每次简单粗暴生成数据快照，更加健壮，可以避免 AOF 这种复杂的备份和恢复机制的 bug 。

- Redis 支持同时开启开启两种持久化方式，我们可以综合使用 AOF 和 RDB 两种持久化机制，用 AOF 来保证数据不丢失，作为数据恢复的第一选择; 用 RDB 来做不同程度的冷备，在 AOF 文件都丢失或损坏不可用的时候，还可以使用 RDB 来进行快速的数据恢复。

    - 如果同时使用 RDB 和 AOF 两种持久化机制，那么在 Redis 重启的时候，会使用 **AOF** 来重新构建数据，因为 AOF 中的**数据更加完整**。

        > 一般来说， 如果想达到足以媲美 PostgreSQL 的数据安全性， 你应该同时使用两种持久化功能。如果你非常关心你的数据， 但仍然可以承受数分钟以内的数据丢失，那么你可以只使用 RDB 持久化。
        >
        > 有很多用户都只使用 AOF 持久化，但并不推荐这种方式：因为定时生成 RDB 快照（snapshot）非常便于进行数据库备份， 并且 RDB 恢复数据集的速度也要比AOF恢复的速度要快，除此之外，使用 RDB 还可以避免之前提到的 AOF 程序的 bug。

在 Redis4.0 版本开始，允许你使用 RDB-AOF 混合持久化方式，详细可见 [《Redis4.0 之 RDB-AOF 混合持久化》](https://yq.aliyun.com/articles/193034) 。也因此，RDB 和 AOF 同时使用，是希望达到安全的持久化的推荐方式。

另外，RDB 和 AOF 涉及的知识点蛮多的，可以看看：

- [《Redis 设计与实现 —— RDB》](https://redisbook.readthedocs.io/en/latest/internal/rdb.html)
- [《Redis 设计与实现 —— AOF》](https://redisbook.readthedocs.io/en/latest/internal/aof.html)

如下是老钱对这块的总结，可能更加适合面试的场景：

- bgsave 做镜像全量持久化，AOF 做增量持久化。因为 bgsave 会耗费较长时间，不够实时，在停机的时候会导致大量丢失数据，所以需要 AOF 来配合使用。在 Redis 实例重启时，会使用 bgsave 持久化文件重新构建内存，再使用 AOF 重放近期的操作指令来实现完整恢复重启之前的状态。

- 对方追问那如果突然机器掉电会怎样？取决于 AOF 日志 sync 属性的配置，如果不要求性能，在每条写指令时都 sync 一下磁盘，就不会丢失数据。但是在高性能的要求下每次都 sync 是不现实的，一般都使用定时 sync ，比如 1 秒 1 次，这个时候最多就会丢失 1 秒的数据。

- 对方追问 bgsave 的原理是什么？你给出两个词汇就可以了，fork 和 cow 。fork 是指 Redis 通过创建子进程来进行 bgsave 操作。cow 指的是 copy on write ，子进程创建后，父子进程共享数据段，父进程继续提供读写服务，写脏的页面数据会逐渐和子进程分离开来。

    > 艿艿：这里 bgsave 操作后，会产生 RDB 快照文件。

实际案例：

- 来自飞哥，**主 aof ，从 rdb + aof**

## 数据过期策略

Redis 的过期策略，就是指当 Redis 中缓存的 key 过期了，Redis 如何处理。

Redis 提供了 3 种数据过期策略：

-   被动删除：当读/写一个已经过期的 key 时，会触发惰性删除策略，直接删除掉这个过期 key 。
-   主动删除：由于惰性删除策略无法保证冷数据被及时删掉，所以 Redis 会定期主动淘汰一批已过期的 key 。
-   主动删除：当前已用内存超过 maxmemory 限定时，触发主动清理策略，即 [「数据“淘汰”策略」](http://svip.iocoder.cn/Redis/Interview/#) 。

在 Redis 中，同时使用了上述 3 种策略，即它们**非互斥**的。

## 数据淘汰策略

Redis 内存数据集大小上升到一定大小的时候，就会进行数据淘汰策略。

Redis 提供了 8 种数据淘汰策略：

### 1. volatile-lru (Least Recently Used)

>   跟使用的最后一次时间有关，淘汰最近使用时间离现在最久的。

从已设置过期时间的数据集中挑选最近最少使用的数据淘汰。redis并不是保证取得所有数据集中最近最少使用的键值对，而只是随机挑选的几个键值对中的， 当内存达到限制的时候无法写入非过期时间的数据集。

### 2. volatile-ttl (Time To Live）

从已设置过期时间的数据集中挑选将要过期的数据淘汰。redis 并不是保证取得所有数据集中最近将要过期的键值对，而只是随机挑选的几个键值对中的， 当内存达到限制的时候无法写入非过期时间的数据集。

> 这种策略使得我们可以向Redis提示哪些key更适合被eviction。

### 3. volatile-random

从已设置过期时间的数据集中任意选择数据淘汰。当内存达到限制的时候无法写入非过期时间的数据集。

### 4. allkeys-lru

从数据集中挑选最近最少使用的数据淘汰。当内存达到限制的时候，对所有数据集挑选最近最少使用的数据淘汰，可写入新的数据集。

> 如果我们的应用对缓存的访问符合幂律分布，也就是存在相对热点数据，或者我们不太清楚我们应用的缓存访问分布状况，我们可以选择allkeys-lru策略。

### 5. allkeys-random

从数据集中任意选择数据淘汰，当内存达到限制的时候，对所有数据集挑选随机淘汰，可写入新的数据集。

> 如果我们的应用对于缓存key的访问概率相等，则可以使用这个策略。

### 6. no-enviction

当内存达到限制的时候，不淘汰任何数据，不可写入任何数据集，所有引起申请内存的命令会报错。

### 7. volatile-lfu (Least Frequently Used)

从已设置过期时间的数据集挑选使用频率最低的数据淘汰;

>   跟使用的次数有关，淘汰使用次数最少的。

### 8. allkeys-lfu

从数据集中挑选使用频率最低的数据淘汰；

### LRU 算法

Redis 的 LRU 算法，**并不是一个严格的 LRU 实现**。这意味着 Redis 不能选择最佳候选键来回收，也就是最久未被访问的那些键。相反，Redis 会尝试执行一个近似的 LRU 算法，通过采样一小部分键，然后在采样键中回收最适合(拥有最久未被访问时间)的那个。

### **回收进程工作流程**

- 一个客户端运行了新的命令，添加了新的数据
- Redis 检查内存使用情况，如果大于 maxmemory 的限制, 则根据设定好的策略进行回收。
- Redis 执行新命令……

所以我们不断地穿越内存限制的边界，通过不断达到边界然后不断地回收回到边界以下（跌宕起伏）。

## 数据类型

>   Redis是Key Value内存数据库，Key的数据类型为String，这里说的数据类型是指Redis的Value支持的数据类型

- 字符串String
- 字典Hash
- 列表List
- 集合Set
- 有序集合 SortedSet (ZSet)

说上面的就行了，下面的可以了解：

- HyperLogLogs（基数统计）

    >   这个结构可以非常省内存的去统计各种计数，比如注册 IP 数、每日访问 IP 数、页面实时UV、在线用户数，共同好友数等。

- Bitmap（位图）

    >   用户ID是整型，统计大量用户的独立访问统计等，可以做基数统计的事，需要较准确的预估；

- Geo

- Pub / Sub

- BloomFilter

- RedisSearch

- Redis-ML

- JSON

## 数据结构

>   这里指的是Redis Value的数据类型由什么数据结构构成，更底层 

Redis 3.0 包含：SDS、哈希表、跳表、整数集合、双向链表、压缩列表

Redis新版本包含：SDS、哈希表、跳表、整数集合、quicklist、listpack

### SDS（Simple Dynamic String）

简单动态字符串用于支持`String类型`；

#### C 语言字符串的缺陷

Redis由C语言实现，但未直接选择char* 字符数组来实现字符串，是由于：

-   **会识别'\0'字符为字符串结束**，不能保存像图片、音频、视频文化这样的二进制数据；
-   **获取字符串长度的时间复杂度是 O(N)**；
-   字符串操作函数不高效且不安全，**字符串是不会记录自身的缓冲区大小**，容易导致缓冲区溢出；

```Java
// 使用Java表示方便理解
public class SDS {
    private long len; // 记录了字符串长度，获取字符串长度O(1)；
    private long alloc; // 记录分配给字符数组的空间长度，用于判断空间是否足够，方便扩容；
    private char flags; // 标记SDS的类型，共5种：SDS HDR(5,8,16,32,64)，数字代表len与alloc实际分配的字节数；
    private char[] buf[]; // 字符数组，用来保存实际数据。不仅可以保存字符串，也可以保存二进制数据。
}
```

### Dict 字典

用于支持`Hash与Set类型`；

类似Java 1.6版本的HashMap，采用**「链式哈希」**Map + 链表的底层结构组成；

>   Redis 5.0中采用Map + Listpack组成，以前采用Map + ZipList组成；

字典中其中有两个哈希表，用于扩容时复制替换（也类似与Java的HashMap扩容）；

### IntSet 整数集合

用于支持`Set类型`；

整数集合是 Set 对象的底层实现之一。当一个 Set 对象只包含整数值元素，并且元素数量不多时，就会使用整数集这个数据结构作为底层实现。

存在3种类型，16、32、64字节的整数集合；

只支持由低字节升级到高字节，不能降级；

### SkipList 跳表

用于支持`ZSet类型`；

跳跃表主要由以下部分构成：

-   表头（head）：负责维护跳跃表的节点指针。
-   跳跃表节点：保存着元素值，以及多个层。
-   层：保存着指向其他元素的指针。高层的指针越过的元素数量大于等于低层的指针，为了提高查找的效率，程序总是从高层先开始访问，然后随着元素值范围的缩小，慢慢降低层次。

### LinkedList 双向链表

**Redis 3.2 之前当List对象数据较多时，使用双向链表**；

即以C语言实现的双向链表；

```java
// 双向节点
public class ListNode<T> {
    T val;
    ListNode next;
    ListNode prev;
}

public class LinkedList<T> {
    long len; // 节点数量
    ListNode<T> head; // 头节点
    ListNode<T> tail; // 尾节点
    
    dup(); // 节点值复制
    free(); // 节点值释放
    match(); // 比较节点值
}
```

Redis 3.0 中当List对象数据较多时，使用双向链表；

### ZipList 压缩列表

**Redis 5.0 之前**当List对象数据较少时，使用压缩列表；同时用于Hash、ZSet类型.

压缩列表是 Redis 为了节约内存而开发的，它是**由连续内存块组成的顺序型数据结构**，有点类似于数组。

-   zlbytes：记录整个压缩列表占用对内存字节数；
-   zltail：记录压缩列表「尾部」节点距离起始地址由多少字节，也就是列表尾的偏移量；
-   zllen：记录压缩列表包含的节点数量；
-   entry：节点
    -   prelen：前一个节点的长度；
    -   encoding：记录当前节点的实际数据类型以及长度；
    -   data：节点数据； 
-   zlend：标记压缩列表的结束点，固定值 0xFF（十进制255）；

#### 压缩列表的缺陷

压缩列表节点的 prevlen 属性会根据前一个节点的长度进行不同的空间大小分配：

-   如果前一个**节点的长度小于 254 字节**，那么 prevlen 属性需要用 **1 字节的空间**来保存这个长度值；
-   如果前一个**节点的长度大于等于 254 字节**，那么 prevlen 属性需要用 **5 字节的空间**来保存这个长度值；

如果一个压缩列表中有多个连续的、长度在 250～253 之间的节点，当新增一个254字节的头节点时，会导致后续所有节点扩展prelen空间，造成「连锁更新」；

空间扩展操作也就是重新分配内存，因此**连锁更新一旦发生，就会导致压缩列表占用的内存空间要多次重新分配，这就会直接影响到压缩列表的访问性能**。

所以说，**虽然压缩列表紧凑型的内存布局能节省内存开销，但是如果保存的元素数量增加了，或是元素变大了，会导致内存重新分配，最糟糕的是会有「连锁更新」的问题**。

因此，**压缩列表只会用于保存的节点数量不多的场景**，只要节点数量足够小，即使发生连锁更新，也是能接受的。

### QuickList 快表

**Redis 3.2 及之后**，用于支持List类型；

其实 quicklist 就是「双向链表 + 压缩列表」组合，因为一个 quicklist 就是一个链表，而链表中的每个元素又是一个压缩列表。

### ListPack

quicklist 虽然通过控制 quicklistNode 结构里的压缩列表的大小或者元素个数，来减少连锁更新带来的性能影响，但是并没有完全解决连锁更新的问题。

因为 quicklistNode 还是用了压缩列表来保存元素，压缩列表连锁更新的问题，来源于它的结构设计，所以要想彻底解决这个问题，需要设计一个新的数据结构。

于是，Redis 在 5.0 新设计一个数据结构叫 listpack，目的是替代压缩列表，它最大特点是 listpack 中每个节点不再包含前一个节点的长度了，压缩列表每个节点正因为需要保存前一个节点的长度字段，就会有连锁更新的隐患。

**结构：**

-   bytes：总字节数；
-   entrys：节点数；
-   entry：节点
    -   encoding：定义该元素的编码类型，会对不同长度的整数和字符串进行编码；
    -   data：实际存放的数据；
    -   len：encoding+data的总长度；
-   tail：结尾标识

### Rax 基数树

Rax 是 Redis 内部比较特殊的一个数据结构，它是一个有序字典树 (基数树 Radix Tree)，按照 key 的字典序排列，支持快速地定位、插入和删除操作。 

Rax 被用在 Redis Stream 结构里面用于存储消息队列，在 Stream 里面消息 ID 的前缀是时间戳 + 序号，这样的消息可以理解为时间序列消息。使用 Rax 结构进行存储就可以快速地根据消息 ID 定位到具体的消息，然后继续遍历指定消息之后的所有消息。

## 使用场景

- 数据缓存
- 会话缓存
- 时效性数据
- 访问频率
- 计数器
- 社交列表
- 记录用户判定信息
- 交集、并集和差集
- 热门列表与排行榜
- 最新动态
- 分布式锁
- 消息队列

## 实现分布式锁

### 普通分布式锁

Redis 实现分布式锁，需要考虑如下几个方面：

#### 正确的获得锁

同时执行setnx（判断key不存在）和 expire（设置过期时间）命令，保证原子性：

```lua 
SET key value [EX seconds] [PX milliseconds] [NX|XX]
```

#### 正确的释放锁

>   使用 Lua 脚本，比对锁持有的是不是自己。如果是，则进行删除来释放。

如果线程 A 成功获取到了锁，并且设置了过期时间 30 秒，但线程 A 执行时间超过了 30 秒，锁过期自动释放，此时线程 B 获取到了锁；随后 A 执行完成，线程 A 使用 DEL 命令来释放锁，但此时线程 B 加的锁还没有执行完成，线程 A 实际释放的线程 B 加的锁。

```lua
// 加锁
String uuid = UUID.randomUUID().toString().replaceAll("-","");
SET key uuid NX EX 30
// 解锁
if (redis.call('get', KEYS[1]) == ARGV[1])
    then return redis.call('del', KEYS[1])
else return 0
end
```

#### 超时解锁导致并发

如果线程 A 成功获取锁并设置过期时间 30 秒，但线程 A 执行时间超过了 30 秒，锁过期自动释放，此时线程 B 获取到了锁，线程 A 和线程 B 并发执行。但业务上不允许并发；

一般有两种方式解决该问题：

-   将过期时间设置足够长，确保代码逻辑在锁释放之前能够执行完成。
-   为获取锁的线程增加守护线程，为将要过期但未释放的锁增加有效时间。

#### 锁的重入性

>   一个线程在执行一个带锁的方法，该方法中又调用了另一个需要相同锁的方法，则该线程可以直接执行调用的方法，而无需重新获得锁；

当线程在持有锁的情况下再次请求加锁，如果一个锁支持一个线程多次加锁，那么这个锁就是可重入的。如果一个不可重入锁被再次加锁，由于该锁已经被持有，再次加锁会失败。Redis 可通过对锁进行重入计数，加锁时加 1，解锁时减 1，当计数归 0 时释放锁。

#### 未获得到锁的等待机制

-   可以通过客户端轮询的方式解决该问题，当未获取到锁时，等待一段时间重新获取锁，直到成功获取锁或等待超时。这种方式比较消耗服务器资源，当并发量比较大时，会影响服务器的效率。
-   另一种方式是使用 Redis 的发布订阅（Pub/Sub）功能，当获取锁失败时，订阅锁释放消息，获取锁成功后释放时，发送锁释放消息。
-   一些业务场景，可能需要支持获得不到锁，直接返回 false ，不等待。

1. 普通分布式锁问题

    -   **集群主备切换**：当主节点挂掉时，从节点会取而代之，但客户端无明显感知。当客户端 A 成功加锁，指令还未同步，此时主节点挂掉，从节点提升为主节点，新的主节点没有锁的数据，当客户端 B 加锁时就会成功。

    -   **集群脑裂**：集群脑裂指因为网络问题，导致 Redis master 节点跟 slave 节点和 sentinel 集群处于不同的网络分区，因为 sentinel 集群无法感知到 master 的存在，所以将 slave 节点提升为 master 节点，此时存在两个不同的 master 节点。Redis Cluster 集群部署方式同理。

        当不同的客户端连接不同的 master 节点时，两个客户端可以同时拥有同一把锁。

###  Redlock分布式锁

简化的说是对n个Redis Server，并发的进行上锁，需要`n/2 + 1`个Server上锁成功，就认为成功；如果失败，释放锁；

## 缓存失效

### 缓存穿透

访问一个缓存和数据库都不存在的 key，此时会直接打到数据库上，并且查不到数据，没法写缓存，所以下一次同样会打到数据库上。

此时，缓存起不到作用，请求每次都会走到数据库，流量大时数据库可能会被打挂。此时缓存就好像被“穿透”了一样，起不到任何作用。

**解决方案：**

1.   **接口校验**：在正常业务流程中可能会存在少量访问不存在 key 的情况，但是一般不会出现大量的情况，所以这种场景最大的可能性是遭受了非法攻击。可以在最外层先做一层校验：用户鉴权、数据合法性校验等，例如商品查询中，商品的ID是正整数，则可以直接对非正整数直接过滤等等。
2.   **缓存空值**：当访问缓存和DB都没有查询到值时，可以将空值写进缓存，但是设置较短的过期时间，该时间需要根据产品业务特性来设置。
3.   **布隆过滤器**：使用布隆过滤器存储所有可能访问的 key，不存在的 key 直接被过滤，存在的 key 则再进一步查询缓存和数据库。

### 缓存击穿

某一个热点 key，在缓存过期的一瞬间，同时有大量的请求打进来，由于此时缓存过期了，所以请求最终都会走到数据库，造成瞬时数据库请求量大、压力骤增，甚至可能打垮数据库。

解决方案：

1.   **加互斥锁**。在并发的多个请求中，只有第一个请求线程能拿到锁并执行数据库查询操作，其他的线程拿不到锁就阻塞等着，等到第一个线程将数据写入缓存后，直接走缓存。

>   JVM 锁保证了在单台服务器上只有一个请求走到数据库，通常来说已经足够保证数据库的压力大大降低，同时在性能上比分布式锁更好。

2.   **热点数据不过期**。直接将缓存设置为不过期，然后由定时任务去异步加载数据，更新缓存。

这种方式适用于比较极端的场景，例如流量特别特别大的场景，使用时需要考虑业务能接受数据不一致的时间，还有就是异常情况的处理，不要到时候缓存刷新不上，一直是脏数据，那就凉了

### 缓存雪崩

大量的热点 key 设置了相同的过期时间，导在缓存在同一时刻全部失效，造成瞬时数据库请求量大、压力骤增，引起雪崩，甚至导致数据库被打挂。

缓存雪崩其实有点像“升级版的缓存击穿”，缓存击穿是一个热点 key，缓存雪崩是一组热点 key。

解决方案：

1.   **过期时间打散**。既然是大量缓存集中失效，那最容易想到就是让他们不集中生效。可以给缓存的过期时间时加上一个随机值时间，使得每个 key 的过期时间分布开来，不会集中在同一时刻失效。
2.   **热点数据不过期**。该方式和缓存击穿一样，也是要着重考虑刷新的时间间隔和数据异常如何处理的情况。
3.   **加互斥锁**。该方式和缓存击穿一样，按 key 维度加锁，对于同一个 key，只允许一个线程去计算，其他线程原地阻塞等待第一个线程的计算结果，然后直接走缓存即可。

## Java 客户端

使用比较广泛的有三个 Java 客户端：

- Redisson

    > Redisson ，是一个高级的分布式协调 Redis 客服端，能帮助用户在分布式环境中轻松实现一些 Java 的对象 (Bloom filter, BitSet, Set, SetMultimap, ScoredSortedSet, SortedSet, Map, ConcurrentMap, List, ListMultimap, Queue, BlockingQueue, Deque, BlockingDeque, Semaphore, Lock, ReadWriteLock, AtomicLong, CountDownLatch, Publish / Subscribe, HyperLogLog)。

- Jedis

    > Jedis 是 Redis 的 Java 实现的客户端，其 API 提供了比较全面的 Redis 命令的支持。
    >
    > Redisson 实现了分布式和可扩展的 Java 数据结构，和 Jedis 相比，Jedis 功能较为简单，不支持字符串操作，不支持排序、事务、管道、分区等 Redis 特性。
    >
    > Redisson 的宗旨是促进使用者对 Redis 的关注分离，从而让使用者能够将精力更集中地放在处理业务逻辑上。

- Lettuce

    > Lettuc e是一个可伸缩线程安全的 Redis 客户端。多个线程可以共享同一个 RedisConnection 。它利用优秀 Netty NIO 框架来高效地管理多个连接。

Redis 官方推荐使用 Redisson 或 Jedis 。
Spring Boot 2.x 内置使用 Lettuce 。

## Redis 主从同步

#### **Redis 主从同步**

Redis 的主从同步(replication)机制，允许 Slave 从 Master 那里，通过网络传输拷贝到完整的数据备份，从而达到主从机制。

- 主数据库可以进行读写操作，当发生写操作的时候自动将数据同步到从数据库，而从数据库一般是只读的，并接收主数据库同步过来的数据。
- 一个主数据库可以有多个从数据库，而一个从数据库只能有一个主数据库。
- 第一次同步时，主节点做一次 bgsave 操作，并同时将后续修改操作记录到内存 buffer ，待完成后将 RDB 文件全量同步到复制节点，复制节点接受完成后将 RDB 镜像加载到内存。加载完成后，再通知主节点将期间修改的操作记录同步到复制节点进行重放就完成了同步过程。

#### **好处**

通过 Redis 的复制功，能可以很好的实现数据库的读写分离，提高服务器的负载能力。主数据库主要进行写操作，而从数据库负责读操作。

## Redis 事务

和众多其它数据库一样，Redis 作为 NoSQL 数据库也同样提供了事务机制。在Redis中，MULTI / EXEC / DISCARD / WATCH 这四个命令是我们实现事务的基石。相信对有关系型数据库开发经验的开发者而言这一概念并不陌生，即便如此，我们还是会简要的列出 Redis 中事务的实现特征：

- 1、在事务中的所有命令都将会被串行化的顺序执行，事务执行期间，Redis 不会再为其它客户端的请求提供任何服务，从而保证了事物中的所有命令被原子的执行。

- 2、和关系型数据库中的事务相比，在 Redis 事务中如果有某一条命令执行失败，其后的命令仍然会被继续执行。

- 3、我们可以通过 MULTI 命令开启一个事务，有关系型数据库开发经验的人可以将其理解为 `"BEGIN TRANSACTION"` 语句。在该语句之后执行的命令都，将被视为事务之内的操作，最后我们可以通过执行 EXEC / DISCARD 命令来提交 / 回滚该事务内的所有操作。这两个 Redis 命令，可被视为等同于关系型数据库中的 COMMIT / ROLLBACK 语句。

- 4、在事务开启之前，如果客户端与服务器之间出现通讯故障并导致网络断开，其后所有待执行的语句都将不会被服务器执行。然而如果网络中断事件是发生在客户端执行 EXEC 命令之后，那么该事务中的所有命令都会被服务器执行。

- 5、当使用 Append-Only 模式时，Redis 会通过调用系统函数 write 将该事务内的所有写操作在本次调用中全部写入磁盘。然而如果在写入的过程中出现系统崩溃，如电源故障导致的宕机，那么此时也许只有部分数据被写入到磁盘，而另外一部分数据却已经丢失。

    > Redis 服务器会在重新启动时执行一系列必要的一致性检测，一旦发现类似问题，就会立即退出并给出相应的错误提示。此时，我们就要充分利用 Redis 工具包中提供的 redis-check-aof 工具，该工具可以帮助我们定位到数据不一致的错误，并将已经写入的部分数据进行回滚。修复之后我们就可以再次重新启动Redis服务器了。

🦅 **如何实现 Redis CAS 操作？**

在 Redis 的事务中，WATCH 命令可用于提供CAS(check-and-set)功能。

假设我们通过 WATCH 命令在事务执行之前监控了多个 keys ，倘若在 WATCH 之后有任何 Key 的值发生了变化，EXEC 命令执行的事务都将被放弃，同时返回 `nil` 应答以通知调用者事务执行失败。

具体的示例，可以看看 [《Redis 事务锁 CAS 实现以及深入误区》](https://www.jianshu.com/p/0244a875aa26) 。

# 参考链接

[服务端思维 × Redis实战（二） 内存淘汰机制](http://blog.720ui.com/2016/redis_action_02_maxmemory_policy/)

[JeffreyLcm ×《Redis 面试题》](https://segmentfault.com/a/1190000014507534)

[烙印99 ×《史上最全 Redis 面试题及答案》](https://www.imooc.com/article/36399)

[yanglbme ×《Redis 和 Memcached 有什么区别？Redis 的线程模型是什么？为什么单线程的 Redis 比多线程的 Memcached 效率要高得多？》](https://github.com/doocs/advanced-java/blob/master/docs/high-concurrency/redis-single-thread-model.md)

[老钱×《天下无难试之 Redis 面试题刁难大全》](https://zhuanlan.zhihu.com/p/32540678)

 [yanglbme×《Redis 的持久化有哪几种方式？不同的持久化机制都有什么优缺点？持久化机制具体底层是如何实现的？》](https://github.com/doocs/advanced-java/blob/master/docs/high-concurrency/redis-persistence.md)

[Redis进阶 - 数据结构：底层数据结构详解](https://pdai.tech/md/db/nosql-redis/db-redis-x-redis-ds.html)