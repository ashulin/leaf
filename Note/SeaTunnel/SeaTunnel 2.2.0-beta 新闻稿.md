# SeaTunnel 2.2.0-beta 发布！API重构，连接器与引擎解偶！

>   在 Apache SeaTunnel(Incubating) 2.1.3 发布后一个多月的时间里，我们通过收集用户和开发者反馈，为大家带来 2.2.0-beta 版本。新版本对Connector API进行了重构，将连接器与引擎解偶，同时社区基于新API完成了大量连接器的开始，并修复了上个版本中存在的使用性问题，提高了稳定性和使用效率。
>
>   
>
>   本文将为大家介绍 Apache SeaTunnel(Incubating) 2.2.0-beta 版本更新内容具体情况

## 主要功能更新

### Connector API重构

社区与用户对于支持Spark3.x与Flink1.14的呼声很高，而现有的API难以同时支持多个引擎版本，为此社区将Connector API进行了重构(以后称为Connector-V2)，与引擎解偶，统一了连接器参数；

![image-20220929025235219](resources/SeaTunnel%202.2.0-beta%20%E6%96%B0%E9%97%BB%E7%A8%BF/SeaTunnel%20API.png)

>   架构演进可参考往期文章：
>
>   https://mp.weixin.qq.com/s/qpO2SVRl9KAY-Ib2kaTDig
>
>   https://mp.weixin.qq.com/s/Oz36jKFIDTXDkDx0GhDuGg

同时我们基于Connector-V2 支持了大量连接器，并适配了Flink 1.13.x 与 Spark 2.4.x，具体列表如下：

#### 同时支持Source与Sink

-   ClickHouse
-   DM
-   File
-   FTP
-   Greenplum
-   HDFS
-   Hive
-   HTTP
-   Hudi
-   Icebreg
-   IotDB
-   Kafka
-   Kudu
-   MongoDB
-   MySQL
-   OSS
-   Phoenix
-   Redis
-   Socket

#### 目前仅支持Source

-   FakeSource
-   Pulsar

#### 目前仅支持Sink

-   AssertSink
-   ConsoleSink
-   Datahub
-   DingTalk
-   ElasticSearch
-   Email
-   Feishu
-   Neo4j
-   Sentry
-   Wechat

### 新增元数据（Catalog）管理功能

2.2.0-beta 版本新增了 Catalog API 和 MySQL Catalog，用于管理、发现元数据；

### 新增统一格式化（Format）功能

2.2.0-beta 版本还新增了 Format API 和 JSON Format，用于非结构化、半结构化连接器对数据进行格式化；

**具体功能更新：**

-   支持MySQL元数据管理 [#2042](https://github.com/apache/incubator-seatunnel/pull/2042)
-   支持JSON格式化 [#2014](https://github.com/apache/incubator-seatunnel/pull/2014)
-   支持Clickhouse无密码：[#2393](https://github.com/apache/incubator-seatunnel/pull/2393)
-   支持Flink中同时使用多种Split Transform [#2268](https://github.com/apache/incubator-seatunnel/pull/2268)
-   在Spark中支持被代理的Redis Sink [#2150](https://github.com/apache/incubator-seatunnel/issues/2150)

## 优化项

-   统一解析命令行参数 [#2470](https://github.com/apache/incubator-seatunnel/pull/2470)
-   添加插件下载脚本 [#2831](https://github.com/apache/incubator-seatunnel/pull/2831)
-   优化License [#2798](https://github.com/apache/incubator-seatunnel/pull/2798)
-   重构E2E模块
-   移除连接器的dist模块 [#2709](https://github.com/apache/incubator-seatunnel/pull/2709)
-   优化依赖管理 [#2606](https://github.com/apache/incubator-seatunnel/issues/2606)
-   优化maven shade打包 [#2665](https://github.com/apache/incubator-seatunnel/pull/2665)
-   升级 Junit4 版本至 5.9.0 [#2305](https://github.com/apache/incubator-seatunnel/pull/2305)

## Bug修复

根据用户的反馈，我们修复了一些使用性问题，比如命令行某些特殊参数解析错误，进一步提高了稳定性。

-   修复命令行变量带有 ',' 时的解析错误 [#2523](https://github.com/apache/incubator-seatunnel/pull/2523)
-   修复Zip压缩可能写入到错误路径 [#2843](https://github.com/apache/incubator-seatunnel/pull/2843)
-   修复Spark数据流被两次获取 [#2764](https://github.com/apache/incubator-seatunnel/pull/2764)
-   修复Windows环境文件下进行E2E测试报路径异常 [#2715](https://github.com/apache/incubator-seatunnel/pull/2715)

## 文档更新

根据用户的反馈，我们修复了一些文档问题，并添加了部分引导文档，使用户或贡献者可以更快上手SeaTunnel

-   修复Kafka文档参数错误 [#2863](https://github.com/apache/incubator-seatunnel/pull/2863)
-   修复JDBC文档参数默认值错误 [#2776](https://github.com/apache/incubator-seatunnel/pull/2776)
-   修复Flink SQL连接器中ES文档的单词错误 [#2634](https://github.com/apache/incubator-seatunnel/pull/2634)
-   添加checkstyle插件在SeaTunnel中的使用引导 [#2535](https://github.com/apache/incubator-seatunnel/pull/2535)
-   添加贡献者在更新/新增第三方依赖时的License处理引导 [#2494](https://github.com/apache/incubator-seatunnel/pull/2494)

## 总结和展望

作为一个Apache 孵化项目，Apache SeaTunnel 社区迅速发展，这得益于开源社区所有贡献者们的无私贡献和开源布道， 更离不开广大SeaTunnel 用户群体的积极反馈。作为 Connector API重构后的第一个beta版本，SeaTunnel社区期待大家的积极试用与反馈。SeaTunnel 社区将会继续做好开源社区建设，在接下来的社区规划中，主要有四个方向：

-   扩大与完善 Connector & Catalog 生态

    支持更多 Connector & Catalog，如TiDB、Doris、Stripe等，并完善现有的连接器，提高其可用性与性能等；

    支持CDC连接器，用于支持实时增量同步场景；

    >   对连接器感兴趣的同学可以关注该Umbrella：https://github.com/apache/incubator-seatunnel/issues/1946

-   支持引擎的更多版本

    如Spark 3.x, Flink 1.14.x等

    >   对支持Spark 3.3 感兴趣的同学可以关注该PR：https://github.com/apache/incubator-seatunnel/pull/2574

-   支持更多数据集成场景 （SeaTunnel Engine）

    用于解决整库同步、表结构变更同步、任务失败影响粒度大等现有引擎不能解决的痛点；

    >   对engine感兴趣的同学可以关注该Umbrella：https://github.com/apache/incubator-seatunnel/issues/2272

-   更简单易用（SeaTunnel Web）

    提供Web界面以DAG/SQL等方式使操作更简单，更加直观的展示Catalog、Connector、Job等；

    接入调度平台，使任务管理更简单；
    
    >   对Web 感兴趣的同学可以关注我们的Web子项目：https://github.com/apache/incubator-seatunnel-web

## 致谢

感谢所有为该版本做出贡献的 41 位社区贡献者，正是大家的付出与努力，让我们能够快速推出这个版本，也欢迎更多的同学参与到 Apache SeaTunnel(Incubating) 社区贡献中。

**完整贡献者列表：**

>   从commit 信息提取，按字母排序

531651225, Bibo, Bruce Lee, Carl-Zhou-CN, Chris Ho, ChunFu Wu, Coen, Eric, Hisoka, Jared Li, Kerwin, Kirs, Laglangyue, Li Hongyu, LinZhaoguan, Namgung Chan, Qishang Zhong, Saintyang, TaoZex, TyrantLucifer, Xiao Zhao, Zongwen Li, chessplay, dijie, hailin0, hk__lrzy, ic4y, iture123, lcyyyyyy, liugddx, liuyehan, luketalent, mans2singh, miaoze8, qianmoQ, songjianet, stormrise, tiezhu, xiaofu, zhangyuge1, 巧克力黑