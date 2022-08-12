# Flink集成Spring

## Maven

由于任务需要上传到Flink环境，每个应用程序应尽可能的以最低依赖项打包；

### 依赖包配置

#### Flink核心依赖

>   Flink 本身包含运行所需的一组类和依赖，比如协调、网络通讯、checkpoint、容错处理、API、算子(如窗口操作)、 资源管理等，这些类和依赖形成了 Flink 运行时的核心。当 Flink 应用启动时，这些依赖必须可用。
>
>   这些核心类和依赖被打包在 `flink-dist` jar 里。它们是 Flink `lib` 文件夹下的一部分，也是 Flink 基本容器镜像的一部分。 这些依赖类似 Java `String` 和 `List` 的核心类库(`rt.jar`, `charsets.jar`等)。
>
>   Flink 核心依赖不包含连接器和类库（如 CEP、SQL、ML 等），这样做的目的是默认情况下避免在类路径中具有过多的依赖项和类。 实际上，我们希望尽可能保持核心依赖足够精简，以保证一个较小的默认类路径，并且避免依赖冲突

如`flink-streaming`、`flink-clients`、`flink-java`等Flink核心依赖；

在maven引用核心依赖项时，设置作用域为 `provided`

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-java</artifactId>
  <version>1.12.1</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-streaming-java_2.12</artifactId>
  <version>1.12.1</version>
  <scope>provided</scope>
</dependency>
```

#### 应用依赖

>   大多数应用需要依赖特定的连接器或其他类库，例如 Kafka、Cassandra 的连接器等。这些连接器不是 Flink 核心依赖的一部分，因此必须作为依赖项手动添加到应用程序中。

使用Maven默认作用域`compile`

```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka_2.12</artifactId>
    <version>1.12.1</version>
</dependency>
```

### maven-shade-plugin

使用maven-shade-plugin插件将项目打包为包含声明连接器和库所需的所有依赖项的应用程序 JAR；

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.1.1</version>
                <dependencies>
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-maven-plugin</artifactId>
                        <version>${spring-boot.version}</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <filters>
                                <filter>
                                    <!-- 不要拷贝 META-INF 目录下的签名，否则会引起 SecurityExceptions 。 -->
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                            <!-- 合并文件内容 -->
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.handlers</resource>
                                </transformer>
                                <transformer
                                        implementation="org.springframework.boot.maven.PropertiesMergingResourceTransformer">
                                    <resource>META-INF/spring.factories</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.schemas</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

## 

## XML

### 定义Spring`applicationContext.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-3.0.xsd"
       default-autowire="byName">

    <context:component-scan base-package="com.winxuan"/>
    <import resource="conf/dubbo/consumer.xml"/>
</beans>
```

### 定义ApplicationContext工具类

```java
public class ServiceUtils {
    private static ApplicationContext applicationContext;
	private static ApplicationContext applicationContextInstance(){
		if (applicationContext == null){
			synchronized (ServiceUtils.class){
				if (applicationContext == null){
					applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
				}
			}
		}
		return applicationContext;
	}
	public static <T> T getBean(Class<T> c) {
		return applicationContextInstance().getBean(c);
	}

	public static <T> T getBean(String beanName, Class<T> c) {
		return applicationContextInstance().getBean(beanName, c);
	}
}
```

**不能通过以下方式初始化以及使用：**该方式在flink下游调用时会报*NullPointException*

```java
	private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
	public static <T> T getBean(Class<T> c) {
		return applicationContext.getBean(c);
	}
```

## Annotation

### 配置类定义注解

```java
// 包扫描
@ComponentScan("com.winxuan")
// 资源引入
@ImportResource({
        "classpath:conf/dubbo/consumer.xml"
})
// 启用自动注解注入
@EnableAutoConfiguration
```

### 定义ApplicationContext工具类

```java
public class ServiceUtils {
    private static ApplicationContext applicationContext;
    
	private static ApplicationContext applicationContextInstance(){
		if (applicationContext == null){
			synchronized (ServiceUtils.class){
				if (applicationContext == null){
                    // 写入定义注解的类
					applicationContext = new AnnotationConfigApplicationContext(FlinkApplication.class);
				}
			}
		}
		return applicationContext;
	}
    
	public static <T> T getBean(Class<T> c) {
		return applicationContextInstance().getBean(c);
	}

	public static <T> T getBean(String beanName, Class<T> c) {
		return applicationContextInstance().getBean(beanName, c);
	}
}
```



## 使用

```java
@Data
@Component
public class UserConfig {
    @Value("${comm.channel.select.config.trace.shops}")
    private Set<Long> shops;
}
```

```java
@Configuration
public class FlinkStreamingConfiguration{
    @Bean("flinkEnvironment")
    public StreamExecutionEnvironment getFlinkEnvironment() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // DO SOMETHING
        return env;
    }
}
```

不使用`@Value`注入属性，需要使用`@PropertySource`：

由于并非使用SpringApplication启动服务，不会直接加载*application.properties*等文件；

*yaml*文件需要自定义实现***PropertySourceFactory***

```java
@Configuration
@ConfigurationProperties(prefix = "flink-properties")
@PropertySource(value = "classpath:application.properties")
@Data
public class FlinkProperties {
    private String jobName;
    private String brokers;
    private String zookeeper;
    private String groupId;
    private boolean terminate;
    private long terminationGracePeriodMs;
}
```

## 