# Cloud Native Applications

[Cloud Native](https://pivotal.io/platform-as-a-service/migrating-to-cloud-native-application-architectures-ebook) 是一种应用开发风格，推荐在持续交付和价值驱动领域采用易于实现的开发方式。 [12-factor Applications](http://12factor.net/)拥有相似的规则，其中开发实践与交付和运营目标保持一致，例如：通过使用声明式编程、管理和监控。 Spring Cloud提供了一系列的工具与特性来促进这些开发风格，其所有组件都采用分布式，并且非常易于使用。

Spring Cloud 的大部分特性都基于 [Spring Boot](https://projects.spring.io/spring-boot)，除此之外的大部分由Spring Cloud 提供的特性包含在两个库中：Spring Cloud Context 和 Spring Cloud Commons 。

- Spring Cloud Context为Spring Cloud应用程序的`ApplicationContext`提供了基础服务和特殊服务（bootstrap context，加密，刷新作用域和环境端点）。
- Spring Cloud Commons是一组常用的抽象类；并已在基于Spring Cloud的框架中实现（例如：Spring Cloud Netflix 和 Spring Cloud Consul）。

如果使用Sun的JDK遇到`Illegal key size`异常，则需要安装Java 加密扩展（Java Cryptography Extension ）无限强度管理策略文件；

详细请查阅链接：

- [Java 6 JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html)
- [Java 7 JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)
- [Java 8 JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

提取文件到`JDK/jre/lib/security`文件夹（无论你使用的那个版本的JRE/JDK x64/x86）

> Spring Cloud是基于非限制性的Apache 2.0 license发布的。如果你发现文档有问题或做出贡献，请到[github](https://github.com/spring-cloud/spring-cloud-commons/tree/master/docs/src/main/asciidoc)上参与。

### 1.Spring Cloud Context

> 应用程序上下文服务

Spring Boot对于如何使用Spring构建应用程序有着一些约束， 例如：常用配置文件的位置，以及用于通用管理和监视任务的端点。 Spring Cloud基于以上约束，并且新增了一些系统可能需要的组件。

#### 1.1启动应用上下文

