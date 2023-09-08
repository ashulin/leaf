# Redis 7.2 RDB 文件剖析

> 现有博客的大多还以 Redis 3.2 RDB 7为主，落后社区版本较多，并缺少一些编码细节或有误，最近阅读了 Redis 7.2 RDB 相关的源码，做个总结。

RDB (Redis Database Backup) 持久化是Redis把当前进程数据生成快照保存到硬盘的一种机制。其文件以`.rdb`
为后缀。

**术语/符号定义**

- HashTable：Redis 底层的 Dict
- `< >`：内容结构体，后续会给出详细描述
- `[ ]`：可不存在
- `{ | }`：互斥
- `...`：一个或多个
- `string-encoding`：[String 编码](#String-编码)
- `length-encoding`：[Length 编码](#Length-编码)

## RDB 操作符

| Byte | Operation Code                          | Description                                          |
| ---- | --------------------------------------- | ---------------------------------------------------- |
| 0xF5 | [FUNCTION2](#Function)                  | 保存 lua 等脚本信息                                  |
| 0xF6 | FUNCTION_PRE_GA                         | FUNCTION2 的预览测试版本，不会被使用                 |
| 0xF7 | [MODULE_AUX](#Module-Auxiliary-Fields)  | 保存 Module 的元数据                                 |
| 0xF8 | [IDLE](#LRU-Idle-Time)                  | Key Value 的 LRU 时间                                |
| 0xF9 | [FREQ](#LFU-Frequency)                  | Key Value 的 LFU 次数                                |
| 0xFA | [AUX](#Auxiliary-Fields)                | 保存 Redis 的元数据，如 Redis 版本号，RDB 创建时间等 |
| 0xFB | [RESIZE_DB](#Database-Size)             | 该数据库的 HashTable 信息                            |
| 0xFC | [EXPIRE_TIME_MS](#Expire-Time-(Millis)) | Key Value 的过期时间，单位：毫秒                     |
| 0xFD | [EXPIRE_TIME](#Expire-Time-(Second))    | Key Value 的过期时间，单位：秒                       |
| 0xFE | [SELECT_DB](#Database-Selector)         | 后续跟随该数据库编号                                 |
| 0xFF | EOF                                     | RDB 文件结束标识                                     |

### RDB 文件结构

目前 RDB 整体由顺序可划分为 4 部分：

#### 文件头

RDB 文件头占用 9 bytes，前 5 bytes 为 ASCII 魔数字符`REDIS`，后 4 bytes 为`RDB 版本号`

```
REDIS<rdb-version>
-----------16进制-----------
52 45 44 49 53              # "REDIS"
30 30 31 31                 # rdb-version: "0011" = 11
```

#### 头信息区

```
<AUX>...[<MODULE_AUX>...][<FUNCTION>...]
```

AUX：[Auxiliary Fields](#Auxiliary-Fields)

MODULE_AUX：[Module Auxiliary Fields](#Module-Auxiliary-Fields)

FUNCTION：[Function](#Function)

#### 数据区域

```
<database>...
database: <SELECT_DB><RESIZE_DB><key-value-pair>...
key-value-pair: [{<EXPIRETIME_MS>|<EXPIRETIME>}][{<IDLE>|<FREQ>}]<value-type><key><value>
```

SELECT_DB：[Database Selector](#Database-Selector)

RESIZE_DB：[Database Size](#Database-Size)

EXPIRETIME_MS：[Expire Time (Millis)](#Expire-Time-(Millis))

EXPIRETIME：[Expire Time (Second)](#Expire-Time-(Second))

IDLE：[LRU Idle Time](#LRU-Idle-Time)

FREQ：[LFU Frequency](#LFU-Frequency)

value-type：[RDB Type](#RDB数据类型)

key：使用 [String 编码](#String-编码)保存的Key 

value：[RDB Type 对应的编码类型所存储的数据](#RDB-Type-Mapping)

#### 尾信息区

```
[<MODULE_AUX>...]<EOF><checksum>

-----------16进制-----------
FF                          # EOF
EB F8 AD F2 59 99 D9 CE     # checksum: 整个 RDB 文件的 CRC 64 校验码, 使用 8 byte 表示
```

MODULE_AUX：[Module Auxiliary Fields](#Module-Auxiliary-Fields)

### Auxiliary Fields

```
0xFA <key><value>

key: string-encoding
value: string-encoding
```

用于保存 Redis 的元信息

key：目前存在以下值

- `redis-ver`：Redis 的详细版本号
- `redis-bits`：系统位数
- `ctime`：RDB 创建时间，单位：秒
- `used-mem`：Redis 已使用内存
- `repl-stream-db`：DB to select in server.master client
- `repl-id`：server current replication id
- `repl-offset`：server current replication offset
- `aof-base`：Load/save the RDB as AOF preamble.

### Module Auxiliary Fields

```
<id><when-opcode><when><entry>...<end>
entry: <module-opcode><data>

id: length-encoding
when-opcode: length-encoding, 值为常量 2
when: length-encoding
end: length-encoding, 值为常量 0, Module 结束标识
module-opcode: length-encoding, 除了 end(0) 外, 值包括 1,2,3,4,5; 标识 data 的编码类型
	1: signed int，length-encoding
	2: unsigned int, length-encoding
	3: float, binary-float-encoding, 4 bytes
	4: double, binary-double-encoding, 8 bytes
	5: string(bytes), string-encoding
```

binary-encoding：[浮点数 Binary 编码](#Binary)

```c
/* rdb.h */
/* Module serialized values sub opcodes */
#define RDB_MODULE_OPCODE_EOF   0   /* End of module value. */
#define RDB_MODULE_OPCODE_SINT  1   /* Signed integer. */
#define RDB_MODULE_OPCODE_UINT  2   /* Unsigned integer. */
#define RDB_MODULE_OPCODE_FLOAT 3   /* Float. */
#define RDB_MODULE_OPCODE_DOUBLE 4  /* Double. */
#define RDB_MODULE_OPCODE_STRING 5  /* String. */
```

### Function

```
0xF5 <function>

function: string-encoding, 用于保存 lua 等脚本信息
```

### Database Selector

```
0xFE <database-num>

database-num: length-encoding, 标识后续数据属于的数据库编号
```

### Database Size

```
0xFB <key-size><expire-key-size>

key-size: length-encoding, 该数据库所有 Key 的数量
expire-key-size: length-encoding, 设置了过期时间的 Key 的数量
```

### Expire Time (Second)

```
0xFD <expire-time>

expire-time: 4 bytes, key-value-pair 的过期时间戳, 单位为秒, encoding: little-endian
```

### Expire Time (Millis)

```
0xFC <expire-time-ms>

expire-time-ms: 8 bytes, key-value-pair 的过期时间戳, 单位为毫秒, encoding: little-endian
```

### LRU Idle Time

```
0xF8 <idle>

idle: length-encoding, key-value-pair 的 LRU idle 时间
```

### LFU Frequency

```
0xF9 <freq>

freq: 1 unsigned byte, key-value-pair 的 LFU 次数
```

## RDB 编码算法

### RDB Type Mapping

| Code | RDB Type & Encoding                            | Redis Type |
| ---- | ---------------------------------------------- | ---------- |
| 0    | [STRING](#String-编码)                         | STRING     |
| 1    | [LIST](#LinkedList-编码)                       | LIST       |
| 2    | [SET](#Set-编码)                               | SET        |
| 3    | [ZSET](#SortedSet-编码)                        | ZSET       |
| 4    | [HASH](#Hash-编码)                             | HASH       |
| 5    | [ZSET_2](#SortedSet-编码)                      | ZSET       |
| 6    | MODULE_PRE_GA                                  | -          |
| 7    | [MODULE_2](#Module-编码)                       | MODULE     |
| 9    | [HASH_ZIPMAP](#ZipMap)                         | HASH       |
| 10   | [LIST_ZIPLIST](#ZipList)                       | LIST       |
| 11   | [SET_INTSET](#IntSet)                          | SET        |
| 12   | [ZSET_ZIPLIST](#ZipList)                       | ZSET       |
| 13   | [HASH_ZIPLIST](#ZipList)                       | HASH       |
| 14   | [LIST_QUICKLIST](#QuickList(ZipLists)-编码)    | LIST       |
| 15   | [STREAM_LISTPACKS](#Stream(ListPacks)-编码)    | STREAM     |
| 16   | [HASH_LISTPACK](#ListPack)                     | HASH       |
| 17   | [ZSET_LISTPACK](#ListPack)                     | ZSET       |
| 18   | [LIST_QUICKLIST_2](#QuickList(ListPacks)-编码) | LIST       |
| 19   | [STREAM_LISTPACKS_2](#Stream(ListPacks)-编码)  | STREAM     |
| 20   | [SET_LISTPACK](#ListPack)                      | SET        |
| 21   | [STREAM_LISTPACKS_3](#Stream(ListPacks)-编码)  | STREAM     |

### Length 编码

用于保存**自然数**

获取第一个字节作为 Code：

| Code byte   | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| 00\|xx xxxx | 6 bits unsigned int：剩余的 6 bits 表示为值                  |
| 01\|xx xxxx | 14 bits unsigned int：剩余的 6 bits 和接下来的 1 byte 表示为值，encoding：big-endian |
| 1000 0000   | 32 bits unsigned int：接下来的 4 bytes 表示为值，encoding：big-endian |
| 1000 0001   | 64 bits unsigned int：接下来的 8 bytes 表示为值，encoding：big-endian |
| 11\|xx xxxx | String 编码使用，剩余的 6 bits 表示为 String 编码的 Code     |

### String 编码

可保存任意内容，实际为 byte[]

先使用 Length 编码获取 length，以及是否为 String 特殊编码

- 如果不是特殊编码，直接获取 length 长度的 bytes
- 如果是特殊编码，length 即为新的 Code：

| Code byte | Description                                                  |
| --------- | ------------------------------------------------------------ |
| 0         | 8 bits signed int：接下来的 1 byte 表示为值                  |
| 1         | 16 bits signed int：接下来的 2 byte 表示为值，encoding：little-endian |
| 2         | 32 bits signed int：接下来的 4 bytes 表示为值，encoding：little-endian |
| 3         | 表示后续内容为使用 LZF 算法压缩的 bytes                      |

```
LZF String: <source-length><target-length><source-bytes>
source-length: length-encoding
target-length: length-encoding, LZF 算法解压后的字节长度
source-bytes：source-length 长度的字节
```

使用 LZF 算法解压 source-bytes 方可得到实际 bytes

### 浮点数编码

#### Double String

> 只有 ZSET 使用

获取第一个字节作为 Code：

| Code byte | Description                                                |
| --------- | ---------------------------------------------------------- |
| 0xFF      | 负无穷                                                     |
| 0xFE      | 正无穷                                                     |
| 0xFD      | 非数值                                                     |
| 其他值    | 获取该 unsigned byte 长度的 bytes，作为字符串格式的 Double |

#### Binary

IEEE 754 标准浮点数二进制，以 little-endian 顺序存储

### LinkedList 编码

```
<size><element>...

size: length-encoding
element: string-encoding # 一共 size 个
```

在不同 Redis 版本中，会将其构建为不同的底层数据结构，请参考 [Redis 数据类型历史](#Redis-数据类型历史)，不再赘述

### Set 编码

```
<size><element>...

size: length-encoding
element: string-encoding # 一共 size 个
```

### Hash 编码

```
<size><entry>...
entry: <key><value> # 一共 size 个

size: length-encoding
key: string-encoding
value: string-encoding
```

### SortedSet 编码

```
<size><entry>...
entry: <element><score> # 一共 size 个

size: length-encoding
element: string-encoding
score: 
   当 RDB type 为 ZSET(3) 时使用 double-string-encoding
   当 RDB type 为 ZSET2(5) 时使用 binary-double-encoding, 8 bytes
```

### QuickList(ZipLists) 编码

```
<size><ziplist>...

size: length-encoding
ziplist: string-encoding # 一共 size 个
```

更多详见 [ZipList](#ZipList)

### QuickList(ListPacks) 编码

```
<size><node>...
node: <format><data> # 一共 size 个

size: length-encoding
data: string-encoding
format: length-encoding, 值为 1 或 2
  当值为 1 时, 代表 data 直接表示数据
  当值为 2 时, 代表 data 是 ListPack
```

更多详见 [ListPack](#ListPack)

### Module 编码

```
<id><entry>...<end>

id: length-encoding
```

entry、end：详见 [Module Aux ](#Module-Auxiliary-Fields)

### Stream(ListPacks) 编码

版本变化为新增属性的序列化

```
<STREAM>: 
<entry-size><entry>...<length><last-entry-ID>[<stream2-fields>]<cg-size><CONSUMER-GROUP>...

ID: <millis><sequence> # Stream ID
millis: length-encoding, 时间戳
sequence: length-encoding，顺序号

last-entry-id: 最后一个消息的 ID

entry-size: length-encoding, entry 的数量

entry: <rawid><listpack>
rawid: string-encoding
  entry-id 的字节形式，用于构建基数树 (Radix), 共 16 bytes:
  前 8 bytes 为 stream-id 的 millis
  后 8 bytes 为 stream-id 的 sequence
  均使用 big-endian 存储
listpack: string-encoding, 详见 ListPack

cg-size: length-encoding, CONSUMER-GROUP 的数量

stream2-fileds: <first-entry-ID><maximal-deleted-entry-ID><entries-add-count> 
  当 rdb type >= Stream2(19) 时存在

first-entry-id: 第一个消息的 ID
maximal-deleted-entry-id：最大已删除的消息 ID
entries-add-count: length-encoding, offset
```

```
<CONSUMER-GROUP>:
<cg-name><cg-id>[<cg-offset>]<pel-size><pel-entry>...<consumer-size><CONSUMER>...
pel-entry: <rawid><delivery-time><delivery-count>

cg-name: string-encoding, 消费组的名称
cg-id: <millis><sequence> # 消费组 ID
cg-offset: length-encoding, 消费组 offset, 当 rdb type >= Stream2(19) 时存在
pel-size: length-encoding, PEL(Pending Entry List) 该消费组待消费的消息列表 size
rawid: 16 bytes, 直接读取
delivery-time: 8 bytes, 单位为毫秒, encoding: little-endian
delivery-count: length-encoding
consumer-size: length-encoding
```

```
<CONSUMER>:
<cname><seen-time>[<active-time>]<pel-size><rawid>...
cname: string-encoding, 消费者名称
seen-time: 8 bytes, 单位为毫秒, encoding: little-endian
active-time: 8 bytes, 单位为毫秒, encoding: little-endian, 当 rdb type >= Stream3(21) 时存在
pel-size: length-encoding, 该消费者待消费的消息 ID 列表 size
rawid: 16 bytes, 直接读取
```

### IntSet

```
<INTSET> # string-encoding
INTSET: <encoding-type><size><element>...

encoding-type: 4 bytes unsignd int, little-endian，可选值为 (2,4,8)
size: 4 bytes unsignd int, little-endian
element: encoding-type 值数量的 bytes, signed int, little-endian, 一共 size 个
```

由整数组成的**有序**集合，使用柔性数组存储数据。

最初使用占用内存最小的 INTSET_ENC_INT16 作为编码，根据新加入的数据判断是否需要编码升级（删除数据不会降级）

```c
/* intset.c */
#define INTSET_ENC_INT16 (sizeof(int16_t))
#define INTSET_ENC_INT32 (sizeof(int32_t))
#define INTSET_ENC_INT64 (sizeof(int64_t))
/* intset.h */
typedef struct intset {
    uint32_t encoding;
    uint32_t length;
    int8_t contents[];
} intset;
```

### ZipMap

```
<ZIPMAP> # string-encoding
ZIPMAP: <size><entry>...<end>
entry: <zm-len><key><zm-len><free-len><value><free>

size: 1 byte, 当其 >= 254 时, 意味 ZipMap 的 size 需要遍历所有 entry 获取
end: 1 byte, 值为常量 0xFF, ZipMap 结束标识
zm-len: 先获取 1 byte, 当其 < 254 时, 即为长度值, 否则获取接下来的 4 bytes 以 little-endian 转换为无符号整数
free-len: 1 unsigned byte
key,value,free: 对应 len 长度的 bytes
  free 表示空闲字节, 比如先把 key "name" 的值设为 "redis 7.0", 然后再把 key "name" 的值设为 "redis", 就会产生 4 个空闲字节
```

```c
/* zipmap.c */
#define ZIPMAP_BIGLEN 254
#define ZIPMAP_END 255
/* The following macro returns the number of bytes needed to encode the length
 * for the integer value _l, that is, 1 byte for lengths < ZIPMAP_BIGLEN and
 * 5 bytes for all the other lengths. */
#define ZIPMAP_LEN_BYTES(_l) (((_l) < ZIPMAP_BIGLEN) ? 1 : sizeof(unsigned int)+1)
```

### ZipList

```
<zlbytes><tail-offset><size><entry>...<end>
entry: <pre-len><element-encoding>

zlbytes: 4 bytes unsigned int, encoding: little-endian, 该 ziplist 所占用的字节数
tail-offset: 4 bytes unsigned int, encoding: little-endian, 最后一个 entry 的 offset
size：2 bytes unsigned int, encoding: little-endian, 当其值为 2**16 - 1 时，意味 ZipList 的 size 需要遍历所有 entry 获取
end: 1 byte, 值为常量 0xFF, ZipList 结束标识

pre-len: 前一个 Entry 的长度
  先获取 1 byte, 当其 < 254 时, 即为长度值, 否则获取接下来的 4 bytes 以 little-endian 转换为无符号整数

element-encoding: 详见 ZipList 元素编码
```

#### ZipList 元素编码

| Code byte | Description                                                  |
| --------- | ------------------------------------------------------------ |
| 00xx xxxx | 6 bits string：剩余的 6 bits 表示为 string 的 len            |
| 01xx xxxx | 14 bits string：剩余的 6bits 和接下来的 1 byte表示为 string 的 len，encoding：big-endian |
| 1000 0000 | 32 bits string：接下来的 4 bytes 表示为 string 的 len，encoding：big-endian |
| 1100 0000 | 16 bits signed int：接下来的 2 bytes 表示为值，encoding：little-endian |
| 1101 0000 | 32 bits signed int：接下来的 4 bytes 表示为值，encoding：little-endian |
| 1110 0000 | 64 bits signed int：接下来的 8 bytes 表示为值，encoding：little-endian |
| 1111 0000 | 24 bits signed int：接下来的 3 bytes 表示为值，encoding：little-endian |
| 1111 1110 | 8 bits signed int：接下来的 1 byte 表示为值                  |
| 1111 1111 | ZipList 结束标识                                             |
| 1111 xxxx | 4 bits unsigned int：剩余的 4 bits 减 1 即为值，[0001, 1101] - 1 ==> [0, 12] |

### ListPack

```
<lpbytes><size><entry>...<end>
entry: <element-encoding><element-len>

lpbytes: 4 bytes unsigned int, encoding: little-endian, 该 listpack 所占用的字节数
size：2 bytes unsigned int，encoding: little-endian, 当其值为 2**16 - 1 时，意味 ListPack 的 size 需要遍历所有 entry 获取
end: 1 byte，值为常量 0xFF， ListPack 结束标识

element-len: <element-encoding> 的长度；
  用于从后向前遍历，其字节的第一个 bit 用于标识 element-len 是否结束，0 代表结束；剩余 7 bit 用于表示数据，以 big-endian 存储。
  例：0|0000010 1|0000100 ==> 0000010 0000100 ==> 260
  
element-encoding: 详见 ListPack 元素编码
```

#### ListPack 元素编码

| Code byte | Description                                                  |
| --------- | ------------------------------------------------------------ |
| 0xxx xxxx | 7 bits unsigned int：剩余的 7 bits 表示为值                  |
| 10xx xxxx | 6 bits string：剩余的 6 bits 表示为 string 的 len            |
| 110x xxxx | 13 bits signed int：剩余的 5 bits 和接下来的 1 byte表示为值，encoding：big-endian |
| 1110 xxxx | 12 bits string：剩余的 4 bits 和接下来的 1 byte 表示为 string 的 len，encoding：big-endian |
| 1111 0000 | 32 bits string：接下来的 4 bytes 表示为 string 的 len，encoding：little-endian |
| 1111 0001 | 16 bits signed int：接下来的 2 bytes 表示为值，encoding：little-endian |
| 1111 0010 | 24 bits signed int：接下来的 3 bytes 表示为值，encoding：little-endian |
| 1111 0011 | 32 bits signed int：接下来的 4 bytes 表示为值，encoding： little-endian |
| 1111 0100 | 64 bits signed int：接下来的 8 bytes 表示为值，encoding： little-endian |
| 1111 1111 | ListPack 结束标识                                            |

## Redis 数据类型历史

### String

- SDS：至今

### List

- LinkedList：Redis [-, 3.2)
- ZipList：Redis [-, 3.2)
- QuickList(ZipLists)：Redis [3.2, 7.0)
- QuickList(ListPacks)：Redis [7.0, -]

### Set

- Dict：至今
- IntSet：至今
- ListPack：Redis [7.2, -]

### ZSet

- SkipList：至今
- ZipList：Redis [2.4, 7.0)
- ListPack：Redis [7.0, -]

### Hash

- Dict：至今
- ZipMap：Redis [-, 2.6)
- ZipList：Redis [2.6, 7.0)
- ListPack：Redis [7.0, -]

### Module

- Module：Redis [4.0, -]

### Stream

- Stream(ListPacks)：Redis [5.0, -]

## RDB 版本历史

| RDB Version | Redis Version              |
|-------------|----------------------------|
| 1           | 2.2                        |
| 2           | 2.4                        |
| 3           | 2.9.0 (7dcc10b) - 3.0.0 RC |
| 4           | 2.5.2 (37180ed) - 2.6.0 RC |
| 5           | 2.5.6 (7f4f86f) - 2.6.0 RC |
| 6           | 2.6                        |
| 7           | 3.2                        |
| 8           | 4.0                        |
| 9           | 5.0                        |
| 10          | 7.0                        |
| 11          | 7.2                        |

### Version 2

- 新增编码类型：

```c
#define RDB_TYPE_HASH_ZIPMAP    9
#define RDB_TYPE_LIST_ZIPLIST  10
#define RDB_TYPE_SET_INTSET    11
#define RDB_TYPE_ZSET_ZIPLIST  12
```

### Version 3

- 新增毫秒精度的过期时间

```c
#define RDB_OPCODE_EXPIRETIME_MS 252
```

### Version 4

- Hash 类型新增 ZipList 编码，替代 ZipMap 编码

```c
#define RDB_TYPE_HASH_ZIPLIST  13
```

### Version 5

- RDB 文件末尾新增了 8 字节的校验码（CRC64 CheckSum）

### Version 6

- ZipList 新增数值编码类型

```c
/* ziplist.c */
#define ZIP_INT_24B (0xc0 | 3<<4) /* 24bits int 编码 */
#define ZIP_INT_8B 0xfe /* 8bits int 编码 */

/* 4bits int 编码 */
#define ZIP_INT_IMM_MIN 0xf1    /* 11110001 */
#define ZIP_INT_IMM_MAX 0xfd    /* 11111101 */
```

### Version 7

- List 类型新增 QuickList(ZipLists) 编码，替代 ZipList 编码，弃用 LinkedList 数据结构
- 新增`AUX`操作码，其中包含
    - `redis-ver`：Redis 的详细版本号
    - `redis-bits`：系统位数
    - `ctime`：RDB 创建时间
    - `used-mem`：Redis 已使用内存

- 新增`RESIZEDB`操作码

```c
#define RDB_TYPE_LIST_QUICKLIST 14
#define RDB_OPCODE_AUX        250
#define RDB_OPCODE_RESIZEDB   251
```

### Version 8

- 新增 64 位 Length 编码
- RDB_TYPE_ZSET_2：score 数据编码格式变更，使用 IEEE 754 标准编码替代 Double-String 编码
- 新增 Module 类型
- `AUX`新增属性：
    - `repl-stream-db`
    - `repl-id`
    - `repl-offset`
    - ``aof-preamble``
    - `lua`：用于存储 lua 脚本

```c
/* server.h */
#define OBJ_MODULE 5    /* Module object. */
```

```c
#define RDB_64BITLEN 0x81 

#define RDB_TYPE_ZSET_2 5
#define RDB_TYPE_MODULE_PRE_GA 6 
#define RDB_TYPE_MODULE_2 7 /* Module value with annotations for parsing without
                               the generating module being loaded. */

/* Module serialized values sub opcodes */
#define RDB_MODULE_OPCODE_EOF   0   /* End of module value. */
#define RDB_MODULE_OPCODE_SINT  1   /* Signed integer. */
#define RDB_MODULE_OPCODE_UINT  2   /* Unsigned integer. */
#define RDB_MODULE_OPCODE_FLOAT 3   /* Float. */
#define RDB_MODULE_OPCODE_DOUBLE 4  /* Double. */
#define RDB_MODULE_OPCODE_STRING 5  /* String. */
```

### Version 9

- 新增 Stream 类型
- 新增`MODULE_AUX`、`IDLE`、`FREQ`操作码

```
/* server.h */
#define OBJ_STREAM 6    /* Stream object. */
```

```
#define RDB_TYPE_STREAM_LISTPACKS 15

#define RDB_OPCODE_MODULE_AUX 247   /* Module auxiliary data. */
#define RDB_OPCODE_IDLE       248   /* LRU idle time. */
#define RDB_OPCODE_FREQ       249   /* LFU frequency. */
```

### Version 10

- Hash 类型新增 ListPack 编码，替代 ZipList 编码
- ZSet 类型新增 ListPack 编码，替代 ZipList 编码
- List 类型新增 QuickList2(ListPacks) 编码，替代 QuickList(ZipLists) 编码
- Stream 类型增加了一些元信息

- 新增`FUNCTION`操作码：替代`AUX`中的`lua`
- `AUX`中`aof-preamble`更名为`aof-base`

```
#define RDB_TYPE_HASH_LISTPACK 16
#define RDB_TYPE_ZSET_LISTPACK 17
#define RDB_TYPE_LIST_QUICKLIST_2   18
#define RDB_TYPE_STREAM_LISTPACKS_2 19

#define RDB_OPCODE_FUNCTION2  245   /* function library data */
#define RDB_OPCODE_FUNCTION_PRE_GA   246   /* old function library data for 7.0 rc1 and rc2 */
```

### Version 11

- Set 类型新增 ListPack 编码
- Stream 类型增加了一些元信息

```c
#define RDB_TYPE_SET_LISTPACK  20
#define RDB_TYPE_STREAM_LISTPACKS_3 21
```

## 参考链接

[Redis Github](https://github.com/redis/redis)

[RDB Version History](https://rdb.fnordig.de/version_history.html#rdb-version-history)

[Redis RDB Dump File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)