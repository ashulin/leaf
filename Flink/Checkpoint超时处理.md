## Checkpoint超时与背压探索

### 现有处理方式

在Checkpoint有较多超时情况时，基于目前的Checkpoint机制，主要有以下几种情况调整：

1.  降低Source的并发度、拉取频率、拉取量；

2.  提高checkpoint发送频率；

3.  提高同时能够进行的checkpoint数量；

4.  提高checkpoint超时时间；

5.  启用非对齐checkpoint：作业会变为有状态，使用无状态重启可能造成数据丢失；

6.  容忍Checkpoint失败：在checkpoint失败时不会重启Job

    >   该方式只是允许checkpoint失败，在checkpoint失败时不会重启Job，遇到逻辑异常或未捕获异常时仍然会重启Job；  由于允许失败，所以在遇到未捕获异常时可能导致自动恢复的offset更旧，从而导致需要处理的数据更多。

以上的方式，主要方向是降低每个checkpoint需要处理的数据量，使得每一个checkpoint可控；

### 主动背压

#### 现状分析

但基于目前Flink的Checkpoint与buffer背压机制上来说：

1.   buffer背压传递到Source需要时间，且可能压力不能传递到Source算子；

2.   由于当Checkpoint达到最大并行数量值时，不会继续下发checkpoint；其会导致下一个checkpoint可能拉取了`timeout - maxConcurrent* interval` 而不是`interval`时间的数据；从而导致Checkpoint更难以完成；

     >   当maxConcurrent >= timeout /interval - 1时，不存在该问题，不基于该场景讨论

也就是说当下游算子压力足够大时，实际上checkpoint需要处理的数据量是不可控的；



通过以上分析，可以发现实际上只要我们可以在Checkpoint的达到最大并行数量时，最后进行的checkpoint在拉取了Checkpoint interval时间后，停止source的拉取动作，也就是主动背压，就可以达到可控的目的；

通过Source相关接口：CheckpointedFunction与CheckpointListener，可以知晓checkpoint的触发与完成/失败，从而进行主动背压；



#### 测试

**Checkpoint配置：**

-   启用EXACTLY_ONCE级别；
-   最大并行checkpoint数量：2；
-   checkpoint间隔：10s；
-   checkpoint超时时间：5min；
-   不容忍Checkpoint失败；

启用主动背压功能后，主要指标变化：

1.  checkpoint完成时间更短；
2.  GC时间更短，次数更少；
3.  数据拉取更平滑，消费性能有微小提升；

##### Checkpoint

**未启用：**

平均2min完成

![Snipaste_2021-11-08_14-44-17](https://pic4.zhimg.com/80/v2-d1ad16e5a557287b4406b259db784587_720w.jpg)

**启用：**

平均20s完成

![Snipaste_2021-11-08_13-34-48](https://pic4.zhimg.com/80/v2-0fa6a53f585dc87a361eca462ec9edc3_720w.jpg)

##### GC

**未启用：**

GC时间平均400ms，平均每分钟GC 2~15次

![Snipaste_2021-11-08_14-31-41](https://pic4.zhimg.com/80/v2-b1bb9340f4e67cb1e9864740ef6ad817_720w.jpg)

![Snipaste_2021-11-08_14-32-11](https://pic4.zhimg.com/80/v2-cabe47d2dd4fcd09ebfadf0364a5bf23_720w.jpg)

**启用：**

GC时间平均130ms，平均每分钟GC 0~2次

![Snipaste_2021-11-08_14-35-11](https://pic1.zhimg.com/80/v2-6d847a24f20f7908df97acf455d6e814_720w.jpg)

![Snipaste_2021-11-08_14-36-18](https://pic1.zhimg.com/80/v2-6360c57a86ccab9c2b735a74ec3934a0_720w.jpg)