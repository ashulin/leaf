# Spring Framework

Spring 是一个开源应用框架，旨在降低应用程序开发的复杂度。

- 它是轻量级、松散耦合的。

    > 它的轻量级主要是相对于 EJB 。随着 Spring 的体系越来越庞大，大家被 Spring 的配置搞懵逼了，所以后来出了 Spring Boot 。

- 它具有分层体系结构，允许用户选择组件，同时还为 J2EE 应用程序开发提供了一个有凝聚力的框架。

- 它可以集成其他框架，如 Spring MVC、Hibernate、MyBatis 等，所以又称为框架的框架( 粘合剂、脚手架 )。

## Spring 模块

以下是`4.3.x`版本的 Spring Framework 的模块图：

![Spring Framework](https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/htmlsingle/images/spring-overview.png)

## Spring 核心理念

### IOC & DI

IOC：控制反转是把传统上由程序代码直接操控的对象的调用权交给容器，通过容器来实现对象组件的装配和管理。

DI：容器向对象注入所需对象的行为即为依赖注入；

- 它将最小化应用程序中的代码量。
- 它以最小的影响和最少的侵入机制促进松耦合。
- 它支持即时的实例化和延迟加载 Bean 对象。
- 它将使您的应用程序易于测试，因为它不需要单元测试用例中的任何单例或 JNDI 查找机制。

#### DI 的多种方式

- 接口注入
- 构造函数注入
- setter 注入，一般采用该方式；

### AOP

AOP(Aspect-Oriented Programming)，即**面向切面编程**, 它与 OOP( Object-Oriented Programming, 面向对象编程) 相辅相成， 提供了与 OOP 不同的抽象软件结构的视角。

- 在 OOP 中，以类( Class )作为基本单元
- 在 AOP 中，以**切面( Aspect )**作为基本单元。

## Spring 容器

Spring 提供了两种 IoC 容器，分别是 BeanFactory、ApplicationContext 。

**BeanFactory**

> BeanFactory 在 `spring-beans` 项目提供。

BeanFactory ，就像一个包含 Bean 集合的工厂类。它会在客户端要求时实例化 Bean 对象。

**ApplicationContext**

> ApplicationContext 在 `spring-context` 项目提供。

ApplicationContext 接口扩展了 BeanFactory 接口，它在 BeanFactory 基础上提供了一些额外的功能。内置如下功能：

- MessageSource ：管理 message ，实现国际化等功能。
- ApplicationEventPublisher ：事件发布。
- ResourcePatternResolver ：多资源加载。
- EnvironmentCapable ：系统 Environment（profile + Properties）相关。
- Lifecycle ：管理生命周期。
- Closable ：关闭，释放资源
- InitializingBean：自定义初始化。
- BeanNameAware：设置 beanName 的 Aware 接口。

另外，ApplicationContext 会自动初始化非懒加载的 Bean 对象们。

### 容器初始化过程

```java
// AbstractApplicationContext.java

@Override
public void refresh() throws BeansException, IllegalStateException {
	synchronized (this.startupShutdownMonitor) {
		// 准备刷新上下文环境
		prepareRefresh();

		// 创建并初始化 BeanFactory
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

		// 填充BeanFactory功能
		prepareBeanFactory(beanFactory);

		try {
			// 提供子类覆盖的额外处理，即子类处理自定义的BeanFactoryPostProcess
			postProcessBeanFactory(beanFactory);

			// 激活各种BeanFactory处理器
			invokeBeanFactoryPostProcessors(beanFactory);

			// 注册拦截Bean创建的Bean处理器，即注册 BeanPostProcessor
			registerBeanPostProcessors(beanFactory);

			// 初始化上下文中的资源文件，如国际化文件的处理等
			initMessageSource();

			// 初始化上下文事件广播器
			initApplicationEventMulticaster();

			// 给子类扩展初始化其他Bean
			onRefresh();

			// 在所有bean中查找listener bean，然后注册到广播器中
			registerListeners();

			// 初始化剩下的单例Bean(非延迟加载的)
			finishBeanFactoryInitialization(beanFactory);

			// 完成刷新过程,通知生命周期处理器lifecycleProcessor刷新过程,同时发出ContextRefreshEvent通知别人
			finishRefresh();
		} catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception encountered during context initialization - " +
						"cancelling refresh attempt: " + ex);
			}

			//  销毁已经创建的Bean
			destroyBeans();

			// 重置容器激活标签
			cancelRefresh(ex);

			// 抛出异常
			throw ex;
		} finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			resetCommonCaches();
		}
	}
}
```

这里每一个方法都非常重要，需要一个一个地解释说明。

## Spring 设计模式

## Spring Bean

### 配置方式

单纯从 Spring Framework 提供的方式，一共有三种：

- 1、XML 配置文件。

    Bean 所需的依赖项和服务在 XML 格式的配置文件中指定。这些配置文件通常包含许多 bean 定义和特定于应用程序的配置选项。它们通常以 bean 标签开头。例如：

    ```
    <bean id="studentBean" class="org.edureka.firstSpring.StudentBean">    <property name="name" value="Edureka"></property></bean>
    ```

- 2、注解配置。

    您可以通过在相关的类，方法或字段声明上使用注解，将 Bean 配置为组件类本身，而不是使用 XML 来描述 Bean 装配。默认情况下，Spring 容器中未打开注解装配。因此，您需要在使用它之前在 Spring 配置文件中启用它。例如：

    ```
    <beans><context:annotation-config/><!-- bean definitions go here --></beans>
    ```

- 3、Java Config 配置。

    Spring 的 Java 配置是通过使用 @Bean 和 @Configuration 来实现。

    - `@Bean` 注解扮演与 `<bean />` 元素相同的角色。

    - `@Configuration` 类允许通过简单地调用同一个类中的其他 `@Bean` 方法来定义 Bean 间依赖关系。

    - 例如：

        ```
        @Configurationpublic class StudentConfig {        @Bean    public StudentBean myStudent() {        return new StudentBean();    }    }
        ```

        - 是不是很熟悉 😈

目前主要使用 **Java Config** 配置为主。当然，三种配置方式是可以混合使用的。例如说：

- Dubbo 服务的配置，艿艿喜欢使用 XML 。
- Spring MVC 请求的配置，艿艿喜欢使用 `@RequestMapping` 注解。
- Spring MVC 拦截器的配置，艿艿喜欢 Java Config 配置。

### Bean 循环依赖

- 首先 A 完成初始化第一步并将自己提前曝光出来（通过 ObjectFactory 将自己提前曝光），在初始化的时候，发现自己依赖对象 B，此时就会去尝试 get(B)，这个时候发现 B 还没有被创建出来
- 然后 B 就走创建流程，在 B 初始化的时候，同样发现自己依赖 C，C 也没有被创建出来
- 这个时候 C 又开始初始化进程，但是在初始化的过程中发现自己依赖 A，于是尝试 get(A)，这个时候由于 A 已经添加至缓存中（一般都是添加至三级缓存 `singletonFactories` ），通过 ObjectFactory 提前曝光，所以可以通过 `ObjectFactory#getObject()` 方法来拿到 A 对象，C 拿到 A 对象后顺利完成初始化，然后将自己添加到一级缓存中
- 回到 B ，B 也可以拿到 C 对象，完成初始化，A 可以顺利拿到 B 完成初始化。到这里整个链路就已经完成了初始化过程了

### Bean 生命周期

![Bean 生命周期](http://static.iocoder.cn/2a90a57e3bb96cc6ffa2619babe72bc4)

Spring Bean 的**初始化**流程如下：

- 实例化 Bean 对象

    - Spring 容器根据配置中的 Bean Definition(定义)中**实例化** Bean 对象。

        > Bean Definition 可以通过 XML，Java 注解或 Java Config 代码提供。

    - Spring 使用依赖注入**填充**所有属性，如 Bean 中所定义的配置。

- Aware 相关的属性，注入到 Bean 对象

    - 如果 Bean 实现 **BeanNameAware** 接口，则工厂通过传递 Bean 的 beanName 来调用 `#setBeanName(String name)` 方法。
    - 如果 Bean 实现 **BeanFactoryAware** 接口，工厂通过传递自身的实例来调用 `#setBeanFactory(BeanFactory beanFactory)` 方法。

- 调用相应的方法，进一步初始化 Bean 对象

    - 如果存在与 Bean 关联的任何 **BeanPostProcessor** 们，则调用 `#preProcessBeforeInitialization(Object bean, String beanName)` 方法。
    - 如果 Bean 实现 **InitializingBean** 接口，则会调用 `#afterPropertiesSet()` 方法。
    - 如果为 Bean 指定了 **init** 方法（例如 `<bean />` 的 `init-method` 属性），那么将调用该方法。
    - 如果存在与 Bean 关联的任何 **BeanPostProcessor** 们，则将调用 `#postProcessAfterInitialization(Object bean, String beanName)` 方法。

## Spring 注解

- @Component
- @Controller
- @Service
- @Repository
- @Autowired
- @Qualifier
- @Required

## Spring 事务

### 事务特性

- 原子性
- 一致性
- 隔离性
- 持久性

### 事务隔离级别

- 读未提交RU
- 读已提交RC
- 可重复读RR
- 串行化

### 事务传播级别

- 支持当前事务
    - PROPAGATION_REQUIRED：如果当前存在事务，则使用该事务。如果当前没有事务，则创建一个新的事务。
    - PROPAGATION_SUPPORTS：如果当前存在事务，则使用该事务。如果当前没有事务，则以非事务的方式继续运行。
    - PROPAGATION_MANDATORY： 如果当前存在事务，则使用该事务。如果当前没有事务，则抛出异常。

- 不支持当前事务
    - PROPAGATION_REQUIRES_NEW：创建一个新的事务。 如果当前存在事务，则把当前事务挂起。
    - PROPAGATION_NOT_SUPPORTED：以非事务方式运行。如果当前存在事务，则把当前事务挂起。
    - PROPAGATION_NEVER：以非事务方式运行。如果当前存在事务，则抛出异常。
- 嵌套
    - PROPAGATION_NESTED：如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行。如果当前没有事务，则创建一个新的事务。

## Spring AOP

### Aspect

Aspect 由 **PointCut** 和 **Advice** 组成。

- 它既包含了横切逻辑的定义，也包括了连接点的定义。
- Spring AOP 就是负责实施切面的框架，它将切面所定义的横切逻辑编织到切面所指定的连接点中。

AOP 的工作重心在于如何将增强编织目标对象的连接点上, 这里包含两个工作:

1. 如何通过 **PointCut** 和 **Advice** 定位到特定的 **JoinPoint** 上。
2. 如何在 Advice 中编写切面代码。

**可以简单地认为, 使用 @Aspect 注解的类就是切面**

### Advice

Advice ，**通知**。

- 特定 JoinPoint 处的 Aspect 所采取的动作称为 Advice 。
- Spring AOP 使用一个 Advice 作为拦截器，在 JoinPoint “周围”维护一系列的**拦截器**。

### **有哪些类型的 Advice？**

- Before - 这些类型的 Advice 在 JoinPoint 方法之前执行，并使用 `@Before` 注解标记进行配置。
- After Returning - 这些类型的 Advice 在连接点方法正常执行后执行，并使用 `@AfterReturning` 注解标记进行配置。
- After Throwing - 这些类型的 Advice 仅在 JoinPoint 方法通过抛出异常退出并使用 `@AfterThrowing` 注解标记配置时执行。
- After Finally - 这些类型的 Advice 在连接点方法之后执行，无论方法退出是正常还是异常返回，并使用`@After` 注解标记进行配置。
- Around - 这些类型的 Advice 在连接点之前和之后执行，并使用 `@Around` 注解标记进行配置。

### JoinPoint 

JoinPoint ，**切点**，程序运行中的一些时间点, 例如：

- 一个方法的执行。
- 或者是一个异常的处理。

在 Spring AOP 中，JoinPoint 总是方法的执行点。

### PointCut 

PointCut ，**匹配** JoinPoint 的谓词(a predicate that matches join points)。

> 简单来说，PointCut 是匹配 JoinPoint 的条件。

- Advice 是和特定的 PointCut 关联的，并且在 PointCut 相匹配的 JoinPoint 中执行。即 `Advice => PointCut => JoinPoint` 。
- 在 Spring 中, 所有的方法都可以认为是 JoinPoint ，但是我们并不希望在所有的方法上都添加 Advice 。**而 PointCut 的作用**，就是提供一组规则(使用 AspectJ PointCut expression language 来描述) 来匹配 JoinPoint ，给满足规则的 JoinPoint 添加 Advice 。

### 关于 JoinPoint 和 PointCut 的区别

JoinPoint 和 PointCut 本质上就是**两个不同纬度上**的东西。

- 在 Spring AOP 中，所有的方法执行都是 JoinPoint 。而 PointCut 是一个描述信息，它修饰的是 JoinPoint ，通过 PointCut ，我们就可以确定哪些 JoinPoint 可以被织入 Advice 。
- Advice 是在 JoinPoint 上执行的，而 PointCut 规定了哪些 JoinPoint 可以执行哪些 Advice 。

或者，我们在换一种说法：

1. 首先，Advice 通过 PointCut 查询需要被织入的 JoinPoint 。
2. 然后，Advice 在查询到 JoinPoint 上执行逻辑。

### 实现方式

实现 AOP 的技术，主要分为两大类：

- ① **静态代理** - 指使用 AOP 框架提供的命令进行编译，从而在编译阶段就可生成 AOP 代理类，因此也称为编译时增强。

    - 编译时编织（特殊编译器实现）

    - 类加载时编织（特殊的类加载器实现）。

        > 例如，SkyWalking 基于 Java Agent 机制，配置上 ByteBuddy 库，实现类加载时编织时增强，从而实现链路追踪的透明埋点。
        >
        > 感兴趣的胖友，可以看看 [《SkyWalking 源码分析之 JavaAgent 工具 ByteBuddy 的应用》](http://www.kailing.pub/article/index/arcid/178.html) 。

- ② **动态代理** - 在运行时在内存中“临时”生成 AOP 动态代理类，因此也被称为运行时增强。目前 Spring 中使用了两种动态代理库：

    - JDK 动态代理
    - CGLIB

Spring AOP 中的动态代理主要有两种方式，

- JDK 动态代理

    JDK 动态代理通过反射来接收被代理的类，并且要求被代理的类必须实现一个接口。JDK动态代理的核心是 InvocationHandler 接口和 Proxy 类。

- CGLIB 动态代理

    如果目标类没有实现接口，那么 Spring AOP 会选择使用 CGLIB 来动态代理目标类。当然，Spring 也支持配置，**强制**使用 CGLIB 动态代理。
    CGLIB（Code Generation Library），是一个代码生成的类库，可以在运行时动态的生成某个类的子类，注意，CGLIB 是通过继承的方式做的动态代理，因此如果某个类被标记为 `final` ，那么它是无法使用 CGLIB 做动态代理的。

注解实现实例：

```java
@Component
@Aspect
public class ExpiredAopAdviseDefine {
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 定义一个 Pointcut, 使用 切点表达式函数 来描述对哪些 Join point 使用 advise.
    @Pointcut("within(SomeService)")
    public void pointcut() {
    }

    // 定义 advise
    @Around("pointcut()")
    public Object methodInvokeExpiredTime(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 开始
        Object retVal = pjp.proceed();
        stopWatch.stop();
        // 结束

        // 上报到公司监控平台
        reportToMonitorSystem(pjp.getSignature().toShortString(), stopWatch.getTotalTimeMillis());

        return retVal;
    }


    public void reportToMonitorSystem(String methodName, long expiredTime) {
        logger.info("---method {} invoked, expired time: {} ms---", methodName, expiredTime);
        //
    }
}
```

