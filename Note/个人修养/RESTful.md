## RESTful

> 一种软件架构风格、设计风格，而不是标准，只是提供了一组设计原则和约束条件。它主要用于客户端和服务器交互类的软件。基于这个风格设计的软件可以更简洁，更有层次，更易于实现缓存等机制。

`REST : Resource Representational State Transfer `

- Transfer：通俗来讲就是：资源在网络中以某种表现形式进行状态转移。分解开来：
- Resource：资源，即数据（前面说过网络的核心）。比如 newsfeed，friends等；
- Representational：某种表现形式，比如用JSON，XML，JPEG等；
- State Transfer：状态变化。通过HTTP动词实现。

**URL定位资源，用HTTP动词（GET,POST,DELETE,DETC）描述操作。**

1. Server提供的RESTful API中，URL中只使用名词来指定资源，原则上不使用动词。“资源”是REST架构或者说整个网络处理的核心。 
2.  用HTTP协议里的动词来实现资源的添加，修改，删除等操作。即通过HTTP动词来实现资源的状态扭转： 
   - GET`SELECT` 用来获取资源；
   - POST`CREATE`用来新建资源（也可以用于更新资源）；
   - PUT`UPDATE `用来更新资源；
   - DELETE`DELETE`用来删除资源；
   - PATCH`UPDATE ` 在服务器更新资源（客户端提供改变的属性）；

**Server的API如何设计才满足RESTful要求?** 

1. URL root : https://example.org/api/v1/; https://api.example.com/v1/

2. API versioning : 可以放在URL里面，也可以用HTTP的header/api/v1/ 

3. URI使用名词而不是动词，且推荐用复数。

4. 保证 HEAD 和 GET 方法是安全的，不会对资源状态有所改变（污染）。 

5. 资源的地址推荐用嵌套结构。 

6. 使用正确的HTTP Status Code表示访问状态。

7. 在返回结果用明确易懂的文本 。

   

