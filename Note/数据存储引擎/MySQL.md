# 	MySQL

## 基本概念

- 关系型数据库管理系统软件
- MySQL是单进程多线程（而oracle是多进程），也就是说MySQL实例在系统上表现就是一个服务进程，即进程；
- MySQL实例是线程和内存组成，实例才是真正用于操作数据库文件的；
- 一般情况下一个实例操作一个或多个数据库；集群情况下多个实例操作一个或多个数据库;

## 数据类型

### 数字类型

#### 整型

| type      | Storage | Minumun Value        | Maximum Value        |
| --------- | ------- | -------------------- | -------------------- |
|           | (Bytes) | (Signed/Unsigned)    | (Signed/Unsigned)    |
| TINYINT   | 1       | -128                 | 127                  |
|           |         | 0                    | 255                  |
| SMALLINT  | 2       | -32768               | 32767                |
|           |         | 0                    | 65535                |
| MEDIUMINT | 3       | -8388608             | 8388607              |
|           |         | 0                    | 16777215             |
| INT       | 4       | -2147483648          | 2147483647           |
|           |         | 0                    | 4294967295           |
| BIGINT    | 8       | -9223372036854775808 | 9223372036854775807  |
|           |         | 0                    | 18446744073709551615 |

**整型（N）形式**

int(N)我们只需要记住两点：

- 无论N等于多少，int永远占4个字节
- **N表示的是显示宽度，不足的用0补足，超过的无视长度而直接显示整个数字，但这要整型设置了unsigned zerofill才有效**

#### 浮点型

| 属性         | 存储空间 | 精度   | 精确性        | 说明                           |
| ------------ | -------- | ------ | ------------- | ------------------------------ |
| FLOAT(M, D)  | 4 bytes  | 单精度 | 非精确        | 单精度浮点型，m总个数，d小数位 |
| DOUBLE(M, D) | 8 bytes  | 双精度 | 比Float精度高 | 双精度浮点型，m总个数，d小数位 |

- float、double类型存在精度丢失问题，即**写入数据库的数据未必是插入数据库的数据**

#### 定点数DECIMAL

- 高精度的数据类型，常用来存储交易相关的数据；不会存在精度丢失问题
- DECIMAL(M,N).M代表总精度，N代表小数点右侧的位数（标度）
- 1 < M < 254, 0 < N < 60;
- 存储空间变长

### 时间类型

| 类型      | 字节   | 例                   | 精确性             |
| --------- | ------ | -------------------- | ------------------ |
| DATE      | 三字节 | 2015-05-01           | 精确到年月日       |
| TIME      | 三字节 | 11:12:00             | 精确到时分秒       |
| DATETIME  | 八字节 | 2015-05-01 11::12:00 | 精确到年月日时分秒 |
| TIMESTAMP |        | 2015-05-01 11::12:00 | 精确到年月日时分秒 |

- MySQL在`5.6.4`版本之后，`TIMESTAMP`和`DATETIME`支持到微秒。
- `TIMESTAMP`会根据系统时区进行转换，`DATETIME`则不会
- 存储范围的区别
    - `TIMESTAMP`存储范围：1970-01-01 00::00:01 to 2038-01-19 03:14:07
    - `DATETIME`的存储范围：1000-01-01 00:00:00 to 9999-12-31 23:59:59
- 一般使用`TIMESTAMP`国际化
- 如存时间戳使用数字类型`BIGINT`

### 字符串类型

| 类型    | 单位 | 最大                        | 特性                         |
| ------- | ---- | --------------------------- | ---------------------------- |
| CHAR    | 字符 | 最大为255字符               | 存储定长，容易造成空间的浪费 |
| VARCHAR | 字符 | 可以超过255个字符           | 存储变长，节省存储空间       |
| TEXT    | 字节 | 总大小为65535字节，约为64KB | -                            |

- TEXT在MySQL内部大多存储格式为溢出页，效率不如CHAR
- Mysql默认为utf-8，那么在英文模式下1个字符=1个字节，在中文模式下1个字符=3个字节。

text和varchar是一组既有区别又有联系的数据类型，其联系在于**当varchar(M)的M大于某些数值时，varchar会自动转为text**：

- M>255时转为tinytext
- M>500时转为text
- M>20000时转为mediumtext

所以过大的内容varchar和text没有区别，同时varchar(M)和text的区别在于：

- 单行64K即65535字节的空间，varchar只能用63352/65533个字节，但是text可以65535个字节全部用起来
- text可以指定text(M)，但是M无论等于多少都没有影响
- text不允许有默认值，varchar允许有默认值

varchar和text两种数据类型，使用建议是**能用varchar就用varchar而不用text（存储效率高）**，varchar(M)的M有长度限制，之前说过，如果大于限制，可以使用mediumtext（16M）或者longtext（4G）。

至于text和blob，简单过一下就是**text存储的是字符串而blob存储的是二进制字符串**，简单说blob是用于存储例如图片、音视频这种文件的二进制数据的。

## 数据库存储引擎

|              |     InnoDB     | MyISAM |
| :----------: | :------------: | :----: |
|     事务     |      支持      | 不支持 |
|   存储限制   |      64TB      |   无   |
|    锁粒度    |      行锁      |  表锁  |
| 崩溃后的恢复 |      支持      | 不支持 |
|     外键     |      支持      | 不支持 |
|   全文检索   | 5.7 版本后支持 |  支持  |

[github × 数据库存储引擎](https://github.com/jaywcjlove/mysql-tutorial/blob/master/chapter3/3.5.md)

## 三范式

第一范式（1NF）：强调属性的原子性约束，表的属性不可以再分割；

第二范式（2NF）：强调记录的唯一性约束，表必须有一个主键，其他非主键列必须完全依赖与主键列；

第三范式（3NF）：强调属性的冗余性约束，非主键列必须直接依赖与主键列，而非间接；

反范式：即采用部分违反范式的结构以提高数据库的读取性能；

## 索引

索引，类似于书籍的目录，想找到一本书的某个特定的主题，需要先找到书的目录，定位对应的页码。

**优点：**

1. 提高数据的检索速度，降低数据库IO成本：使用索引的意义就是通过缩小表中需要查询的记录的数目从而加快搜索的速度。
2. 降低数据排序的成本，降低CPU消耗：索引之所以查的快，是因为先将数据排好序，若该字段正好需要排序，则正好降低了排序的成本。

**缺点：**

1. 占用存储空间：索引实际上也是一张表，记录了主键与索引字段，一般以索引文件的形式存储在磁盘上。
2. 降低更新表的速度：表的数据发生了变化，对应的索引也需要一起变更，从而减低的更新速度。否则索引指向的物理数据可能不对，这也是索引失效的原因之一。

### 索引类型

#### 从数据结构角度

1. B+树索引(O(log(n)))：关于B+树索引，可以参考 [MySQL索引背后的数据结构及算法原理](http://blog.codinglabs.org/articles/theory-of-mysql-index.html)

    - 一个m阶的B+ Tree每个节点最多有m个子节点
    - 除了叶子节点和根节点外至少有Ceil(m/2)个子节点，根节点为非叶子节点至少有2个子节点；
    - 每个节点非叶子节点保存n个关键字（索引值），Ceil(m/2)<=n<=m且有序；
    - 叶子节点在同一层，非叶子节点不带有数据，只带有子节点指针和索引值；

    - 叶子节点维护了一个链表；所以**范围查询快，且自排序**；

    - Innodb主键为聚集索引，即主键索引的叶子节点保存有整个数据行的数据（通过主键索引不需要额外的IO获取数据）；
    - 辅助索引（非聚集索引）为保存主键索引的指针（当辅助索引为覆盖索引时，即索引列包含了所有需要查询的数据列时，也不需要再通过主键索引的指针IO获取数据）；

2. hash索引：
    - 仅仅能满足"=","IN"和"<=>"查询，**不能使用范围查询**，**不能使用索引排序**；
    - 其检索效率非常高，索引的检索可以一次定位，不像B-Tree 索引需要从根节点到枝节点，最后才能访问到页节点这样多次的IO访问，所以 Hash 索引的查询效率要远高于 B-Tree 索引；当然遇到大量Hash值相等的情况后性能并不一定就会比B-Tree索引高
    - 只有Memory存储引擎显示支持hash索引；
    - 在任何时候都不能避免表扫描；
    - 不能利用部分索引键查询；对于组合索引，Hash 索引在计算 Hash 值的时候是组合索引键合并后再一起计算 Hash 值；

3. FULLTEXT索引:

    - 只有MyISAM和InnoDB 5.6引擎支持；

    - 不支持中文；5.7版本之后通过使用ngram插件开始支持中文；

        > 常用的全文索引引擎的解决方案有 Elasticsearch、Solr 等等。最为常用的是 Elasticsearch 。

4. R-Tree索引（用于对GIS数据类型创建SPATIAL索引）

    - 仅支持geometry数据类型，支持该类型的存储引擎只有myisam、bdb、innodb、ndb、archive几种。

#### **从物理存储角度**

1. 聚集索引（clustered index）：InnoDB的主键为聚集索引，其他索引为非聚集索引；

2. 非聚集索引（non-clustered index）：MyISAM所有索引都是非聚集索引；

#### **从逻辑角度**

1. 主键索引：主键索引是一种特殊的唯一索引，不允许有空值
2. 普通索引或者单列索引
3. 多列索引（复合索引）：复合索引指多个字段上创建的索引，只有在查询条件中使用了创建索引时的第一个字段，索引才会被使用。使用复合索引时遵循最左前缀集合
4. 唯一索引或者非唯一索引
5. 空间索引：空间索引是对空间数据类型的字段建立的索引，MYSQL中的空间数据类型有4种，分别是GEOMETRY、POINT、LINESTRING、POLYGON。
    MYSQL使用SPATIAL关键字进行扩展，使得能够用于创建正规索引类型的语法创建空间索引。创建空间索引的列，必须将其声明为NOT NULL，空间索引只能在存储引擎为MYISAM的表中创建

## 事务

事务是指对一系列的数据库操作进行统一的提交或回滚操作；

### 事务特征

- 原子性（Atomicity）：保证事务执行过程中发生异常时，会将事务回滚到开始前状态；
- 一致性（Consistency ）：保证事务执行前后数据库数据的完整性以及业务逻辑的一致性；
- 持久性（Durability ）：即事务结束后，对数据的修改是永久的；
- 隔离性（Isolation）：保证多个事务并发对数据的读写操作；

### 事务隔离级别

- 读未提交（Read Uncommit）：即事务未进行提交操作时，其他事务也能查询到其修改后的数据；
- 读已提交（Read Commit）：即事务对数据的操作，再其提交之前对其他事务来说是不可见的；
- 可重复读（Repeatable Read）：即对同一事务而言，在事务中进行相同的查询操作能得到相等的结果；
- 序列化（Serializable）：将事务串行化执行；

**并发导致的问题**

- 脏读：主要针对未提交的数据而言，又称无效数据读出，当事务A读取了事务B修改后的数据，此时事务B执行了回滚操作，就发生可脏读；
- 不可重复读：主要针对在其他事务提交后的数据本身值对比，会导致事务在事务中进行相同的查询时得到不同的数据；
- 幻读：主要针对在其他事务提交后的数据行对比，即事务在进行多次查询时得到不同数量的结果集；

## MVCC

MVCC解决了幻行的情况，当然这只是针对查询操作；

### MVCC依赖数据

#### 行记录隐藏字段

- db_row_id，行ID，用来生成默认聚簇索引（**聚簇索引**，保存的数据在物理磁盘中按顺序保存，这样相关数据保存在一起，提高查询速度）
- db_trx_id，事务ID，新开始一个事务时生成，实例内全局唯一，deleted_bit，删除标记位，删除时设置
- db_roll_ptr，undo log指针，指向对应记录当前的undo log

#### undo log

- 用于行记录回滚，同时用于实现MVCC

    

    ![img](https://upload-images.jianshu.io/upload_images/2100026-19f3a841076a7140.png?imageMogr2/auto-orient/strip|imageView2/2/w/1182/format/webp)

- 因为事务在修改页时，要先记 undo ，在记 undo 之前要记 undo 的 redo， 然后修改数据页，再记数据页修改的 redo。 redo（里面包括 undo 的修改）一定要比数据页先持久化到磁盘。
- 当事务需要回滚时，因为有 undo，可以把数据页回滚到前镜像的状态。
- 崩溃恢复时，如果 redo log 中事务没有对应的 commit 记录，那么需要用 undo 把该事务的修改回滚到事务开始之前。如果有 commit 记录，就用 redo 前滚到该事务完成时并提交掉。

### 操作方式

#### update

- 行记录数据写入undo log,事务的回滚操作就需要undo log
- 更新行记录数据，当前事务ID写入db_trx_id，undo log指针写入db_roll_ptr

#### delete

- 和update一样，只增加deleted_bit设置

#### insert

- 生成undo log
- 插入行记录数据，当前事务ID写入db_trx_id， db_roll_ptr为空

这样设计使得读操作很简单，性能很好，并且也能保证只会读到符合标准的行，不足之处是每行记录都需要额外的储存空间，需要做更多的行检查工作，以及额外的维护工作

## 锁

### 乐观锁

CAS使用的机制，一般使用version进行控制；

### 悲观锁

-   **共享锁**：又称读锁，不堵塞，多个用户可以同时读一个资源，互不干扰。
    -   当前读：`SELECT...LOCK IN SHARE MODE`
-   **排他锁**：又称写锁，一个写锁会阻塞其他的读锁和写锁，这样可以只允许一个用户进行写入，防止其他用户读取正在写入的资源。
    -   `SELECT...FOR UPDATE`
    -   `UPDATE`/`INSERT`/`DELETE`

innoDB只对**快照读无锁**，**当前读与写操作都是悲观锁**；

### 粒度分类

-   表锁`table lock`：系统开销最小，会锁定整张表，MyIsam 使用表锁。

-   行锁`record lock`：最大程度的支持并发处理，但是也带来了最大的锁开销，InnoDB 使用行锁。

-   间隙锁`gap lock`：间隙锁属于行锁中的一种，间隙锁是在事务加锁后其锁住的是表记录的某一个区间，当表的相邻ID之间出现空隙则会形成一个区间，遵循左开右闭原则。

    >   用于防止写倾斜、幻读；

-   区间锁`next-key lock`：即 `record lock` + `gap lock `，是通过在 index 上加 lock 实现的；
    -   如果 index 为 unique index ，则降级为 record lock 行锁；
    -   如果是普通 index ，则为 next-key lock ；
    -   如果没有 index ，则直接锁住全表，即表锁；

### 状态锁

状态锁包括意向共享锁和意向排它锁，把他们区分为状态锁的一个核心逻辑，是因为这两个锁都是都是描述是否可以对某一个表进行加表锁的状态。

**意向锁的解释**：当一个事务试图对**整个表**进行加锁（共享锁或排它锁）之前，首先需要获得对应类型的意向锁 ；**作用：**为了防止加表锁时需要遍历整个表中的行项是否已有锁；

##### 意向共享锁

当一个事务试图对**整个表**进行加共享锁之前，首先需要获得这个表的意向共享锁。

##### 意向排他锁

当一个事务试图对**整个表**进行加排它锁之前，首先需要获得这个表的意向排它锁。

## 日志

MySQL日志主要包括错误日志、查询日志、**慢查询日志**、**事务日志**、**二进制日志**几大类。

**慢查询日志**用于定位慢查询，一般由DBA关注并报告给研发人员；

**二进制日志**`binlog`主要用于`MySQL Server`进行**主从复制**和**数据恢复**；

**事务日志**包括**回滚日志**`undo log`与**重做日志**`redo log`，其中`undo log`用于`InnoDB引擎`达成`MVCC`，`redo log`用于达成数据页修改，以及配合binlog达到崩溃恢复能力；

### binlog

 用于记录数据库执行的**写入性操作(不包括查询)**信息，以二进制的形式保存在磁盘中。`binlog` 是 MySQL的逻辑日志，并且由 Server 层进行记录，使用任何存储引擎的 MySQL数据库都会记录 `binlog` 。

-   **逻辑日志**：可以简单理解为记录的就是sql语句 。
-   **物理日志**：MySQL数据最终是保存在数据页中的，物理日志记录的就是数据页变更 。

#### 使用场景

在实际应用中， `binlog` 的主要使用场景有两个：

1.  **主从复制** ：在 `Master` 端开启 `binlog` ，然后将 `binlog`发送到各个 `Slave` 端， `Slave` 端重放 `binlog` 从而达到主从数据一致。
2.  **数据恢复** ：通过使用 `mysqlbinlog` 工具来恢复数据。

>   相关CDC工具：
>
>   1.   Maxwell：只支持MySQL，支持全量+增量
>   2.   Canal：只支持MySQL，只支持增量
>   3.   Debezium：支持多种数据源，支持全量+增量
>   4.   Flink CDC：支持多种数据源，支持并发全量+增量

#### 刷盘时机

对于 `InnoDB` 存储引擎而言，只有在事务提交时才会记录`binlog` ，此时记录还在内存中，那么 `biglog`是什么时候刷到磁盘中的呢？`mysql` 通过 `sync_binlog` 参数控制 `biglog` 的刷盘时机，取值范围是 `0-N`：

-   0：不去强制要求，由系统自行判断何时写入磁盘；
-   1：每次 `commit` 的时候都要将 `binlog` 写入磁盘；
-   N：每N个事务，才会将 `binlog` 写入磁盘。

从上面可以看出， `sync_binlog` 最安全的是设置是 `1` ，这也是`MySQL 5.7.7`之后版本的默认值。但是设置一个大一些的值可以提升数据库性能，因此实际情况下也可以将值适当调大，牺牲一定的一致性来获取更好的性能。

#### 日志格式

`binlog` 日志有三种格式，分别为 `STATMENT` 、 `ROW` 和 `MIXED`。

>   在 `MySQL 5.7.7` 之前，默认的格式是 `STATEMENT` ， `MySQL 5.7.7` 之后，默认值是 `ROW`。日志格式通过 `binlog-format` 指定。

-   `STATMENT`：基于`SQL` 语句的复制( `statement-based replication, SBR` )，每一条会修改数据的sql语句会记录到`binlog` 中  。

-   -   优点：不需要记录每一行的变化，减少了 binlog 日志量，节约了 IO  , 从而提高了性能；
    -   缺点：在某些情况下会导致主从数据不一致，比如执行sysdate() 、  slepp()  等 。

-   `ROW`：基于行的复制(`row-based replication, RBR` )，不记录每条sql语句的上下文信息，仅需记录哪条数据被修改了 。

-   -   优点：不会出现某些特定情况下的存储过程、或function、或trigger的调用和触发无法被正确复制的问题 ；
    -   缺点：会产生大量的日志，尤其是`alter table` 的时候会让日志暴涨

-   `MIXED`：基于`STATMENT` 和 `ROW` 两种模式的混合复制(`mixed-based replication, MBR` )，一般的复制使用`STATEMENT` 模式保存 `binlog` ，对于 `STATEMENT` 模式无法复制的操作使用 `ROW` 模式保存 `binlog`

### redo log

**作用：**

-   确保事务的持久性，事务日志通过redo log和innoDB存储引擎的日志缓冲(InnoDB Log Buffer)实现。

-   防止在发生故障的时间点，尚有脏页未写入磁盘，在重启mysql服务的时候，根据redo log进行重做，从而达到事务的持久性这一特性。

**内容：**

**物理日志**，记录的是物理数据页面的修改的信息，其redo log是顺序写入redo log file的物理文件中去的。

**什么时候产生：**

事务开始之后就产生redo log，redo log的落盘并不是随着事务的提交才写入的，而是在事务的执行过程中，便开始写入redo log文件中。

>   这就是DBA们口中常说的“日志先行”(Write-Ahead Logging)

**什么时候释放：**

当对应事务的脏页写入到磁盘之后，redo log的使命也就完成了，**重做日志占用的空间就可以重用（被覆盖）**。

### redo log与binlog

二进制日志的作用之一是还原数据库的，这与redo log很类似，很多人混淆过，但是两者有本质的不同

-   作用不同：redo log是保证事务的持久性的，是事务层面的，binlog作为还原的功能，是数据库层面的（当然也可以精确到事务层面的），虽然都有还原的意思，但是其保护数据的层次是不一样的。
-   内容不同：redo log是物理日志，是数据页面的修改之后的物理记录，binlog是逻辑日志，可以简单认为记录的就是sql语句
-   另外，两者日志产生的时间，可以释放的时间，在可释放的情况下清理机制，都是完全不同的。
-   恢复数据时候的效率，基于物理日志的redo log恢复数据的效率要高于语句逻辑日志的binlog

关于事务提交时，redo log和binlog的写入顺序，为了保证主从复制时候的主从一致（当然也包括使用binlog进行基于时间点还原的情况），是要严格一致的，MySQL通过两阶段提交过程来完成事务的一致性的，也即redo log和binlog的一致性的，理论上是先写redo log，再写binlog，两个日志都提交成功（刷入磁盘），事务才算真正的完成。

### innodb log buffer 刷新策略

-   Master Thread 每秒一次执行刷新Innodb_log_buffer到redo/undo logfile；
-   每个事务提交时会将redo/undo log刷新到redo/undo logfile；
-   当redo/undo log缓存可用空间 少于一半时，redo/undo log缓存被刷新到redo/undo logfile；

### undo log

**作用**：保存了事务发生之前的数据的一个版本，可以用于回滚，同时可以提供多版本并发控制下的读（MVCC），也即非锁定读；

**内容**：逻辑格式的日志，在执行undo的时候，仅仅是将数据从逻辑上恢复至事务之前的状态，而不是从物理页面上操作实现的。

**什么时候产生：**

事务开始之前，将当前事务的版本生成`undo log`，undo 也会产生 redo 来保证undo log的可靠性

>   undo是在事务开始之前保存的被修改数据的一个版本，产生undo日志的时候，同样会伴随类似于保护事务持久化机制的redolog的产生。

**什么时候释放：**

当事务提交之后，undo log并不能立马被删除，而是放入待清理的链表，由purge线程判断是否由其他事务在使用undo段中表的上一个事务之前的版本信息，决定是否可以清理undo log的日志空间。

**物理文件：**

MySQL5.6之前，undo表空间位于共享表空间的回滚段中，共享表空间的默认的名称是ibdata，位于数据文件目录中。

MySQL5.6之后，undo表空间可以配置成独立的文件，但是提前需要在配置文件中配置，完成数据库初始化后生效且不可改变undo log文件的个数，如果初始化数据库之前没有进行相关配置，那么就无法配置成独立的表空间了。



## 查询执行顺序

```mysql
(7)     SELECT 
(8)     DISTINCT <select_list>
(1)     FROM <left_table>
(3)     <join_type> JOIN <right_table>
(2)     ON <join_condition>
(4)     WHERE <where_condition>
(5)     GROUP BY <group_by_list>
(6)     HAVING <having_condition>
(9)     ORDER BY <order_by_condition>
(10)    LIMIT <limit_number>
```

## 语法

### 分类

- **DDL**（**Data Definition Language**）**数据库定义语言**

    > CREATE、ALTER、DROP、TRUNCATE、COMMENT、RENAME

- **DML**（**Data Manipulation Language**）**数据操纵语言**

    > SELECT、INSERT、UPDATE、DELETE、MERGE、CALL、EXPLAIN PLAN、LOCK TABLE

- **DCL**（**Data Control Language**）**数据库控制语言**

    > GRANT 授权、REVOKE 取消授权

- **TCL**（**Transaction Control Language**）**事务控制语言**

    > SAVEPOINT 设置保存点、ROLLBACK  回滚、SET TRANSACTION

查看变量用select
- 局部变量：select var_name;
- 用户变量：select @var_name;
- 全局变量：select @@var_name;

### 数据库

```MySQL
# 创建数据库
CREATE DATABASE [IF NOT EXISTS] database_name;
# 删除数据库
DROP DATABASE [IF EXISTS] database_name;
# 使用数据库
USE database_name;
# 显示所有数据库名称
SHOW DATABASES;
# 查看当前数据库名称
SELECT DATABASE();
# 查看可用字符集
SHOW CHARACTER SET;
```

### 表

```mysql
# 创建表
CREATE TABLE [IF NOT EXISTS] table_name(
    column_name data_type[size] [NOT NULL|NULL] [DEFAULT value] 
    [AUTO_INCREMENT] [UNIQUE [KEY]] [[PRIMARY] KEY]
) engine=table_type 
[DEFAULT] CHARACTER SET [=] charset_name 
[COLLATE [=] collation_name;
# 显示所有表
SHOW TABLES;
# 显示建表语句
SHOW CREATE TABLE table_name;
# 显示列结构
SHOW COLUMNS FROM table_name;
# 删除表
DROP TABLE [IF EXISTS] table_name;
# 删除所有数据
TRUNCATE TABLE table_name;
# 更改列定义
ALTER TABLE tbl_name  
CHANGE [COLUMN] old_col_name new_col_name column_definition [FIRST|AFTER col_name]
|MODIFY [COLUMN] col_name column_definition [FIRST | AFTER col_name]
# 添加列
ALTER TABLE tbl_name 
ADD [COLUMN] col_name column_definition
[FIRST | AFTER col_name]
# 删除列
ALTER TABLE tbl_name 
DROP [COLUMN] col_name
# 更改表字符集
ALTER TABLE tbl_name
[DEFAULT] CHARACTER SET [=] charset_name [COLLATE [=] collation_name]
# 更改表引擎
ALTER TABLE tbl_name
engine = table_type
# 更改表名
ALTER TABLE tbl_name
RENAME [TO|AS] new_tbl_name
##
RENAME TABLE tbl_name TO new_tbl_name
```

### 数据

```mysql
# 查询语句
SELECT [DISTINCT] column_1, column_2, ... 
FROM table_1
[INNER | LEFT |RIGHT] JOIN table_2 ON conditions
WHERE conditions 
GROUP BY column_1 
HAVING group_conditions # 可以使用函数
ORDER BY column_1 [ASC | DESC]
LIMIT offset, length
[FOR UPDATE | LOCK IN SHARE MODE];# 获取排他锁

# 插入语句
INSERT [IGNORE] INTO table_name(column1,column2...)# IGNORE忽略异常行
VALUES (value1,value2,...)

# 更新语句
UPDATE [LOW_PRIORITY] [IGNORE] table_name 
[INNER JOIN | LEFT JOIN] T1 ON T1.C1 = T2. C1
SET T1.C2 = T2.C2, 
    T2.C3 = expr
WHERE
    condition;

# 删除语句
DELETE T1, T2
FROM T1
INNER JOIN T2 ON T1.key = T2.key
WHERE condition

# 删除再插入语句，相当于先调用DELETE删除冲突行，再INSERT;
REPLACE INTO cities(id,population) VALUES(2,3696820);

# 交集
INTERSECT
# 并集
UNION [ALL|DISTINCT]
```

### 索引

```mysql
# 建表时索引定义
{UNIQUE|FULLTEXT|SPATIAL} [INDEX|KEY] [index_name] (col_name [(length)] [ASC | DESC],...)
# 创建索引
CREATE [UNIQUE|FULLTEXT|SPATIAL] INDEX index_name
[USING [BTREE|HASH|RTREE]]
ON table_name (column_name [(length)] [ASC | DESC],...)
##
ALTER TABLE tbl_name
ADD {UNIQUE|FULLTEXT|SPATIAL} [INDEX|KEY] [index_name](col_name [(length)] [ASC | DESC],...)
# 删除索引
DROP INDEX index_name ON table_name;
# 修改索引名
ALTER TABLE tbl_name
RENAME {INDEX|KEY} old_index_name TO new_index_name
# 查看语句是否使用索引
EXPLAIN SELECT...;
```

### 主外键

```mysql
# 删除主外键
ALTER TABLE tbl_name
DROP PRIMARY KEY
|DROP FOREIGN KEY fk_symbol
# 添加外键
ALTER TABLE tbl_name
ADD [CONSTRAINT [symbol]] FOREIGN KEY[index_name] (col_name, ...)
REFERENCES tbl_name (col_name,...)
[ON DELETE reference_option]
[ON UPDATE reference_option]
reference_option:
    RESTRICT | CASCADE | SET NULL | NO ACTION | SET DEFAULT
# 添加主键
ALTER TABLE tbl_name
ADD [CONSTRAINT [symbol]] PRIMARY KEY
[USING {BTREE | HASH}] (col_name [(length)] [ASC | DESC],...)
[index_option] ...
```

### 事务

```mysql
# 不能在事务中使用DDL（数据定义语句）
# 开始事务
START TRANSACTION
# 回归
ROLLBACK;
# 提交
COMMIT;
```

### 存储过程

```mysql
# 新建存储过程
DELIMITER // #标准分隔符 - 分号(;)更改为：//
CREATE PROCEDURE GetAllProducts([IN|OUT|INOUT] param_name param_type(param_size))
   BEGIN
   DECLARE variable_name datatype(size) DEFAULT default_value;# 定义变量
   SET variable_name = value; # 设置变量值
   DECLARE cursor_name CURSOR FOR SELECT_statement;# 定义游标
   DECLARE finished INTEGER DEFAULT 0;
   DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;# 定义游标异常处理
   OPEN cursor_name;# 开始游标遍历
   FETCH cursor_name INTO variables list;#将一行结果放入到变量列表中
   CLOSE cursor_name;# 关闭游标
   # IF ELSE语句
   IF expression THEN 
      statements;
   ELSEIF elseif-expression THEN
   	  elseif-statements;
   ELSE
   	  else-statements;
   END IF;
   # CASE WHEN语句（Swith case）
   CASE  case_expression 
      WHEN when_expression_1 THEN commands
      WHEN when_expression_2 THEN commands
      ...
      ELSE commands
   END CASE;
   # WHILE语句
   WHILE expression DO 
      statements
   END WHILE
   # REPEAT语句（do while）
   REPEAT
    statements;
   UNTIL expression
   END REPEAT
   # LOOP语句 while(true)
   loop_name: LOOP
   LEAVE loop_name # break;
   ITERATE loop_name # continue;
   END LOOP
   SELECT *  FROM products;
   END //
DELIMITER ;
# 调用存储过程
CALL STORED_PROCEDURE_NAME();
# 显示存储过程语句
SHOW CREATE {PROCEDURE | FUNCTION} sp_name;
# 查看存储过程
SHOW { PROCEDURE | FUNCTION } STATUS [ WHERE expr LIKE  ' pattern ' ] ; 
# 删除存储过程
DROP { PROCEDURE| FUNCTION }[if exists] sp_name; 
# 自定义函数
CREATE FUNCTION funciton_name(func_parameter...) RETURNS type [DETERMINISTIC]
BEGIN
RETURN type_value
END;
```

### 视图

```mysql
# 创建或修改视图
CREATE [OR REPLACE] [ALGORITHM = {MERGE  | TEMPTABLE | UNDEFINED}]
VIEW [database_name].[view_name] 
AS [SELECT  statement]
WITH [CASCADED|LOCAL] CHECK OPTION;# 保持视图的一致性，即插入更新等操作需符合视图约束
# 显示视图语句
SHOW CREATE VIEW [database_name].[view_ name];
# 修改视图
[ALTER|CREATE OR REPLACE] VIEW
AS [SELECT  statement]
WITH [CASCADED|LOCAL] CHECK OPTION;
# 删除视图
DROP VIEW [IF EXISTS] [database_name].[view_name]
# 查看所有视图
SELECT * FROM information_schema.TABLES WHERE table_type = 'view';
```

### 触发器

```mysql
# 创建触发器
CREATE TRIGGER trigger_name [AFTER|BEFORE] [UPDATE|DELETE|INSERT]
ON table_name
FOR EACH ROW [FOLLOWS|PRECEDES] existing_trigger_name
BEGIN
...
END;
# 显示所有触发器
SHOW TRIGGERS [FROM database_name];
# 删除触发器
DROP TRIGGER trigger_name;
```

### 隔离

- 查看当前会话隔离级别
    - select @@tx_isolation;
    - select @@transaction_isolation `MySQL8.0`
- 查看系统当前隔离级别
    - select @@global.tx_isolation;
    - select @@global.transaction_isolation`MySQL8.0`
- 设置[当前会话/系统当前]隔离级别
    - set [session/global] transaction isolatin level [隔离等级];



# 参考

[CSDN × 深入理解MySQL索引原理和实现——为什么索引可以加速查询？](https://blog.csdn.net/tongdanping/article/details/79878302)

[博客园 × MySQL的btree索引和hash索引的区别](https://www.cnblogs.com/vicenteforever/articles/1789613.html)

[zouzls.github × SQL查询之执行顺序解析](http://zouzls.github.io/2017/03/23/SQL查询之执行顺序解析/)

[易百 × MySQL语句教程](https://www.yiibai.com/mysql/stored-procedure.html)

[MySQL × 5.7参考手册](https://dev.mysql.com/doc/refman/5.7/en/create-function.html)

[MySQL中的redolog，undolog，以及binlog的区别及各自作用是什么？](https://zhuanlan.zhihu.com/p/453853258)

[乐观锁和 MVCC 的区别？ - 邓澎波的回答 - 知乎](https://www.zhihu.com/question/27876575/answer/2277853914)