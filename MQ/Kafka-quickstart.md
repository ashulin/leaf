## Kafka

本教程假设您的服务器是一个全新的环境，并没有安装kafka及zookeeper。由于Unix和Windows平台的kafka命令脚本不同，在Windows平台上需要使用bin\Windows\而不是bin/，并且需要修改脚步的扩展名为.bat。

第1步：下载安装包

 

```
> tar -xzf kafka_2.11-1.0.0.tgz
> cd kafka_2.11-1.0.0
```

第2步：启动服务

 

```
> bin/zookeeper-server-start.sh config/zookeeper.properties
[2013-04-22 15:01:37,495] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig)
...
```

启动kafka服务

```shell
> bin/kafka-server-start.sh config/server.properties &
[2013-04-22 15:01:47,028] INFO Verifying properties (kafka.utils.VerifiableProperties)
[2013-04-22 15:01:47,051] INFO Property socket.send.buffer.bytes is overridden to 1048576 (kafka.utils.VerifiableProperties)
...
```

第3步：创建一个主题（Topic）

创建一个名为test的主题，这个主题带有一个分区（partition）和一个副本因子。

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

除了手动创建主题，你也可以配置你的代理服务器（broker），当发布一个不存在的主题时可以自动创建主题。

第4步：发生消息

kafka自带一个命令行客户端，它可以从文件或者标准输入中接收输入，并将输入发送到kafka集群上。默认情况下，每一行都视为独立的消息发送。

运行生产者脚本，然后发生一些消息到后台服务。

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
This is a message
This is another message
```

第5步：启动一个消费者

kafka还自带了命令行消费者，它可以将消息dump到标准输出。

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
This is a message
This is another message
```

如果在不同的终端上执行上述命令，那么在生产者终端输入的消息应该在消费者终端可以看到。

所有的命令行工具都有其他选项，不带参数运行可以显示出详细使用方法。

第6步：设置多代理集群

到目前为止，我们只允许了单例代理服务器，对于kafka来说，一个代理是只有一个单节点的集群，因此多代理集群只是比开始多了一些代理实例，其他没有什么太大的不同。为了演示，我们部署一个有三个节点的集群（所有的节点仍部署在本地机器上）。

首先，为每个代理生成一个配置文件（在Windows上使用Copy命令实现）：

```
> cp config/server.properties config/server-1.properties
> cp config/server.properties config/server-2.properties
```

然后，编辑新文件，设置以下属性：

```
config/server-1.properties:
    broker.id=1
    listeners=PLAINTEXT://:9093
    log.dir=/tmp/kafka-logs-1

config/server-2.properties:
    broker.id=2
    listeners=PLAINTEXT://:9094
    log.dir=/tmp/kafka-logs-2
```

broker.id属性是每个代理节点的唯一永久名字。因为都在一台服务器上运行这些代理，我们必须重新定义端口和日志记录，以免同一端口上的数据冲突。

我们已经有了Zookeeper服务和单个节点服务，所以我们只需要启动两个新节点：

```
> bin/kafka-server-start.sh config/server-1.properties &
...
> bin/kafka-server-start.sh config/server-2.properties &
...
```

现在创建一个具有三个副本因子的主题：

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
```

到此，我们就创建了一个集群，但如何才能知道哪个节点在干什么，需要运行“describe topics”命令查看：

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic	PartitionCount:1	ReplicationFactor:3	Configs:
	Topic: my-replicated-topic	Partition: 0	Leader: 1	Replicas: 1,2,0	Isr: 1,2,0
```

第一行是所有分区的在要，每个附加行是对每个分区的描述，由于我们只有一个分区，因此这里只有一行。

“Leader"，负责指定分区中所有的读写节点，每个节点将是一部分随机选择的分区中的领导者。

”Replicas“，分区日志节点的列表集合。

“ISR"，是一组”in-sync“状态的节点列表。这个列表包括当前活着的并且与leader保持同步的replicas，ISR是Replicas的一个子集。

注意：这个例子中节点1是topic唯一分区中的leader。

我们可以运行相同的命令看看我们原来创建的主题的状态：

```shell
bin/kafka-topics.sh --describe --zookeeper 192.168.41.130:2181,192.168.41.132:2181,192.168.41.133:2181 --topic test
```



```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic test
Topic:test	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: test	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```

可以看到原来的主题没有副本，只有唯一的服务器0。

让我们发布一些消息到我们的新主题上：

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-replicated-topic
...
my test message 1
my test message 2
^C
```

然后我们消费这些消息：

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
^C
```

现在测试一下容错行，broker1是领导者，结束它的进程

```
> ps aux | grep server-1.properties
7564 ttys002    0:15.91 /System/Library/Frameworks/JavaVM.framework/Versions/1.8/Home/bin/java...
> kill -9 7564
```

Windows上：

```
> wmic process get processid,caption,commandline | find "java.exe" | find "server-1.properties"
java.exe    java  -Xmx1G -Xms1G -server -XX:+UseG1GC ... build\libs\kafka_2.10-0.10.2.0.jar"  kafka.Kafka config\server-1.properties    644
> taskkill /pid 644 /f
```

领导权以及转移到另外一个备份节点上了，Node1也不在同步的副本集中：

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic PartitionCount:1 ReplicationFactor:3 Configs:
 Topic: my-replicated-topic Partition: 0 Leader: 2 Replicas: 1,2,0 Isr: 2,0
```

但是消息仍然可以被消费，即使原来负责写任务的领导者已经不在了：

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
^C
```

第7步：使用kafka连接导入/导出数据

从控制台写入数据和回写到控制台是一个很好的入门例子，但是你可能想使用其他数据源的数据或者其他的kafka系统的导出数据。相比于很多系统需要编写特定的集成代码，可以使用kafka连接到系统去导入导出数据。

kafka connect是包含在kafka中的一个工具，用来导入和导出数据到kafka。它是connectors的一个可扩展工具，其执行特定的逻辑，用来和外部系统交互。在这个快速入门中，我们将会教你如何使用kafka connect做一些简单的连接器从一个文件导入数据到kafka的主题。以及将主题数据导出到一个文件。

首先，创建一些原始数据开始测试：

```
> echo -e "foo\nbar" > test.txt
```

Windows上

```
> echo foo> test.txt
> echo bar>> test.txt
```

然后，我们启动两个独立运行的连接器，他们在一个独立的，专用的进程中运行。我们输入三个配置文件作为参数。第一个是kafka链接过程中的通用配置，例如连接到kafka的代理服务器的配置和数据的序列化格式配置。其余的配置文件用用创建指定的连接器。这些配置文件包含连接器的唯一的名字，需要实例化的连接器类，以及创建该连接器需要的其他配置。

```
> bin/connect-standalone.sh config/connect-standalone.properties config/connect-file-source.properties config/connect-file-sink.properties
```

这些实例文件，使用前面已经启动的本地集群的默认配置，建立两个连接器；第一个是源连接器，它从输入文件中读取每行的内容，并发生给topic，第二个是sink connector，他负责从kafka的topic中读取消息，并将这些消息逐行输出到文件。

在启动过程中，你会看到一些表示该连接器被实例化的日志信息。一旦kafka进程开始运行，源连接器应该开始从test.txt文件中读取每行的信息，并将其发布到主题connect-test上，而sink连接器从主题connect-test上读取消息，并将其写入文件test.sink.txt。我们可以检查输出文件的内容验证数据都已经通过管道传送过来：

```
> more test.sink.txt
foo
bar
```

注意，数据被存储在主题connect-test中，所以我们可以运行控制台消费者消费主题中的数据（或者使用特定的消费者代码来出来）。

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-test --from-beginning
{"schema":{"type":"string","optional":false},"payload":"foo"}
{"schema":{"type":"string","optional":false},"payload":"bar"}
...
```

连接器不断的处理数据，我们可以将数据追加到文件中，并能看到数据通过管道移动。

```
> echo Another line>> test.txt
```

你可以看到，在空中台消费者输出和sink文件中出现了Another line。

第8步：使用Kafka Streams处理数据

Kafka Streams是Kafka的客户端库，用于实时流处理和分析存储在kafka代理服务器上的数据。

Java和Scala可以使用Kafka Streams方便的开发和部署应用程序，同时kafka服务端集群技术的优点，使这些应用程序具有高度的可扩展性、容错、分布式等特点。快速入门中的示例演示了如何使用kafka streams来构建一个应用程序