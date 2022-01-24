## 问题记录

### Consumer

#### 1. consumer group offset丢失

**表现：**

**Kafka Broker**的`offsets.retention.minutes`配置采用默认配置，在`1.1.x`版本中默认值为1440分钟，即为1天；该配置为Kafka ***GroupMatedataManager***处理offset以及group元数据的失效时间；

当超过该时间没有提交新的offset会导致采用`auto.reset.offset`的配置进行reset offset；

>   ***GroupMetadataManager***在启动时会开启一个定时执行的清理线程：`delete-expired-group-metadata`，该线程的主要工作是清理**__consumer_offsets**中失效的offset以及可删除的group信息。

**处理方案：**

加大Broker端的`offsets.retention.minutes`配置值；

#### 2. 不能提交offset

```log
org.apache.kafka.clients.consumer.CommitFailedException: Commit cannot be completed since the group has already rebalanced and assigned the partitions to another member. This means that the time between subsequent calls to poll() was longer than the configured max.poll.interval.ms, which typically implies that the poll loop is spending too much time message processing. You can address this either by increasing the session timeout or by reducing the maximum size of batches returned in poll() with max.poll.records
```

由于kafka的consumer心跳机制可知：如果consumer超过max.poll.interval.ms间隔没有发起poll请求，但heartbeat仍旧在发，就认为该consumer处于 livelock状态，就会将该consumer移出consumer group；
**处理方案：**

保证poll的频率大于`max.poll.interval.ms`；

### Producer

#### 1. KAFKA-7190

**表现：**

当topic流量较小，且topic数据均被消费且提交，在超过transactional.id.expiration.ms时间后，broker进行删除数据，且会删除掉缓存中的pid，这时producer再次发送数据时就会爆出UnknownProducerIdException；

**处理方案：**

升级至2.4.0及以后版本；

