# Hibernate概述

一个Java的持久化ORM框架
## 什么是框架
​	规范行为，简化代码，使程序员集中于业务编写  

- Hibernate 应用在dao层框架
- 使用它实现curd操作，它的最底层就是JDBC  

## Hibernate好处

- 不用写复杂的jdbc代码
- 允许开发者采用面向对象的方式来操作数据库,不需要写sql语句实现
- 完成对象的持久化操作
- 消除针对特定数据库厂商的sql代码
- 开源的优秀,成熟,轻量级框架
- 通过xml配置
- 在一定的程度上是一个强主键的dao层框架，
  因为Hibernate框架对数据操作都是按照id进行操作的。
  那么建议；创建表的时候，必须要有id

## 与Mybatis/iBatis区别

- 比hibernate更灵活,运行速度快
- 开发慢,需要sql语句,且熟练使用sql语句优化

## Hibernate底层思想

### ORM思想
javabean又被称为实体类
orm(object relational mapping) 对象关系映射

- 让实体类和数据库表进行一一对应关系
- 让实体类属性和表中字段一一对应
- 实体类实例与表里的记录一一对应
- 不需要直接操作数据库表，而操作表对应实体类对象
- orm采用元数据来描述对象-关系映射细节,元数据通常采用xml格式.

# Hibernate入门

## 搭建环境
JAVA SE EE 环境下都可以
#### 步骤
1. 导入jar包 require目录  mysql连接jar包 额外日志信息jar包
2. 创建entity类
3. 在实体类包中创建xml(*.hbm.xml) ——先引入xml约束
		<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
4. 配置xml  
5. 创建hibernate核心配置文件.
	- 位置必须在src目录下
	- 名称必须为 hibernate.cfg.xml
	- 而且hibernate运行时仅加载这一个xml文件
			<!DOCTYPE hibernate-configuration PUBLIC
          "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
          "dtd/hibernate-configuration-3.0.dtd">
- hibernate.hbm2ddl.auto参数的作用主要用于：自动创建|更新|验证数据库表结构
	- create-drop ：每次加载hibernate时根据model类生成表，但是sessionFactory一关闭,表就自动删除。
	- update：最常用的属性，第一次加载hibernate时根据model类会自动建立起表的结构，以后加载hibernate时根据 model类自动更新表结构，即使表结构改变了但表中的行仍然存在不会删除以前的行。
	- create：每次加载hibernate时都会删除上一次的生成的表，然后根据你的model类再重新来生成新表.
	- validate ：每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。

#### 使用
1. 加载hibernate核心配置文件
2. 创建SessionFactory对象
3. 使用SessionFactory创建Session对象
4. 开启事务
5. **写具体逻辑curd操作**
6. 提交事务
	. 关闭资源		

### eclipse 反向生成Entities类

### require目录的包
1. antlr-2.7.7.jar〓 ---> Hierbante辅助包
2. dom4j-1.6.1.jar〓 ---> dom4J技术对xml文件解析包
3. hibernate-commons-annotations-4.0.5.Final.jar 〓 ---> Hibernate注解核心包
4. hibernate-core-4.3.8.Final.jar〓 ---> Hibernate框架核心包
5. hibernate-jpa-2.1-api-1.0.0.Final.jar 〓 ---> JPA框架支持包，融合了JPA框架
6. jandex-1.1.0.Final.jar〓 ---> Hibernate辅助包
7. javassist-3.18.1-GA.jar〓 ---> 字节码辅助包
8. jboss-logging-3.1.3.GA.jar〓 ---> 日志包
9. jboss-logging-annotations-1.2.0.Beta1.jar〓 ---> 注解日志包
10. jboss-transaction-api_1.2_spec-1.0.0.Final.jar 〓 ---> 事务支持包
***
# Hibernate核心API
### Configuration
- 获取实例: new Configuration()
- cfg.configure("可选路径")
	- 无参,默认获取src下的hibernate.cfg.xml配置文件
	- 加载核心配置文件

### SessionFactory
- 保存了当前的数据库配置信息和所有映射关系以及预定义的SQL语句这个对象是线程安全的
- 获取实例:cfg.buildSessionFactory()
	- 根据核心配置文件,有数据库配置,映射文件部分,若有update配置,会自动创建表.
	- 因此,这个创建过程特别耗费资源,因此一般一个项目只创建一个SessionFactory对象

### Session
- 特点 session类似于JDBC中的Connection
- 是一个单线程对象，线程不安全
- 获取实例: sessionFactory.openSession()
- 主要方法:
	- save()
	- update()
	- saveOrUpdate()
	- delete()
	- get()和load()
- session对象是单线程对象
	- session对象不能共用,

### Transaction
- 事务对象
- 获取实例 session.beginTransaction()
- 主要方法:
	- commit()
	- rollback()
> #### eclipse引入dtd约束文件
window -> preferences -> 搜索xml Catalog
-> add 按钮 -> location:本地dtd路径 keytype:uri key:原本的xml中的url;
配置完后重启eclipse

### Hibernate主键生成策略
- 要求实体类中有一个属性作为唯一值,对应表的主键
	- <generator class=""></generator>
	- sequence 要求数据库支持序列 Oracle
	- **native 根据数据库来自动生成对应类型**
	- uuid 32位16进制字符串
	- increment 有hibernate负责自增长
	- identity 由数据库负责自增长

### 实体类操作
#### 根据id查询
- session.get(Book.class,1)
	- 参数1 实体类的class
	- 参数2 id值
	- 返回 实体类类型

#### 单实体修改操作
- session.update(实体类对象)
	- get()获得实体类
	- 修改实体类值
	- 调用
-执行过程,在对象中找到id值,再做修改

#### 删除操作
- session.delete()
	- 根据id查询的对象删除
	- 或者自己创建实体对象,设置id,再进行删除

#### 实体类状态
- 瞬时态:对象里没有id值,对象与session没有关联;一般用于做添加操作

- 持久态:有id值且与session有关联;

- 脱管态:有id值,对象与session没有关联

## Hibernatede缓存

#### 特点
- 一级缓存
	- 默认打开的
	- 使用范围:是session的范围(开启到关闭)
	- 只存储持久态数据

- 方法:
	-  1)flush ： 修改一级缓存数据针对内存操作，需要在session执行flush操作时，将缓存变化同步到数据库,只有在缓存数据与快照区不同时，生成update语句
    - 2)clear ： 清除所有对象 一级缓存
    - 3)evict ： 清除一级缓存指定对象
    - 4)refresh ：重新查询数据库，更新快照和一级缓存
- 二级缓存
	- 几乎不用,大多使用redis替代了
	- 默认关闭的
	- 使用范围:sessionFactory范围

#### 执行过程
- 首先在一级缓存中查找,若有数据则返回
- 若没有则访问数据库,并把返回数据存入缓存中
- 缓存中没有直接存储对象,而是存储属性对应值

#### 特性
- 持久态会自动更新数据库,即不调用update()
	- 一级缓存创建时,同时还会创建一个快照区(副本)
	- 缓存存入数据时会同时放入缓存区和快照区
	- 修改持久态对象值,同时会修改一级缓存对应的值,但不修改快照区的值
	- 提交事务时,会比较缓冲区和快照区,不相同则更新缓存内容到数据库

#### 事务规范写法
```java
try{
	开启事务
	提交事务
}
catch(Exception e){
	回滚事务
}
finally{
	关闭session
}
```

#### session与本地线程绑定
- 底层是ThreadLocal
- 步骤
  - 在核心配置文件中配置
     ```java
     	<property name="current_session_context_class">thread</property>			
     ```
  - 调用sessionFactory.getCurrentSession();
- 获取绑定的session时,close()方法报错
	
	- 不需要手动关闭,由操作系统关闭

## Query对象,Criteria对象,SQLQuery对象

### Query对象
不需要sql语句,但要hql语句
>#### hql与sql的区别
- 使用sql操作的时表和字段
- hql操作实体类和属性

查询所有: from 实体类名称
- 创建Query对象,session.createQuery();
- 调用query.list(),返回List<实体类> 对象;

### Criteria对象
不需要写语句
- 主要为了解决多条件查询问题，以面向对象的方式添加条件，无需拼接HQL语句
- 获得实例:session.createCriteria(实体类.class);
- 调用criteria.list(),返回List<实体类> 对象;

### SQLQuery对象
使用sql语句
- 获得实例:session.createSQLQuery();
- sqlQuery.list(),返回List<Object[]>对象
- sqlQuery.addEntity(实体类.class)可以让list返回List<实体类>对象

## Hibernate 一对多操作
#### 一对多映射配置
- 在A表中一个实体有多个B表的实体 用Set集合来保存
		<!--
        表示商品所属的分类
        name属性的值：因为在Good商品实体中，
		用sort表示Sort实体类，所有这里写sort
        class属性的值：sort类的全路径
        column属性的值：外键的名称
         -->
        <many-to-one name="sort" class="XXX.entity.Sort" column="gsid"></many-to-one>

- B表属于A表
		 <!--
       	 在多的这一边使用set标签来设置对应关系
        name属性的值：因为在Sort中使用getSet保存good属性。
        column属性的值：外键的名称，由于在Hibernate使用双向维护外键
        所有在这边的column的值必须要和另一边的值一致
        class属性的值：Good实体类的全路径。
         -->
       <set name="getSet">
           <key column="gsid"></key>
           <one-to-many class="XXX.entity.Good" />
       </set>

#### 级联操作
- 级联保存
	- 简化写法 把多方放入一方 且 只需保存一方
			 <set name="xxx" cascade="sava-update">
- 级联删除


## inverse:
- 将关联关系的维护交给多方，在保存的时候可以减少update语句的生成，提高Hibernate的执行效率
- inverse只存在于集合标记的元素中
- inverse=false 的为主动方
- 维护指的是当主控放进行增删改查操作时，会同时对关联关系进行对应的更新
- 单向one-to-many关联关系中，不可以设置inverse="true",因为被控方的映射文件中没有主控方的信息
-  多对多：属性在独立表中,关系的两端 inverse不能都设为false.

## cascade：
- 指的是级联操作，比如当我们保存一方数据的时候，可以自动将关联到的多方数据也插入到数据库当中
- 指当主控方执行某项操作时，是否要对被关联方也执行相同的操作
- 只有涉及到关系的元素才有cascade属性
	- delete 删除两张表里的指定数据
	- delete-orphan 删除子表
	- save-update 同步插入和更新

## 锁
悲观锁
query.setLockMode(被加锁对象,LockMode.UPGRADE);
乐观锁

