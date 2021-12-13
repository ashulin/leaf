## SpringMVC配置

#### Maven导入依赖包

```java
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <maven.compiler.source>1.8</maven.compiler.source>
  <maven.compiler.target>1.8</maven.compiler.target>
  <!--依赖包版本控制-->
  <spring.version>4.3.16.RELEASE</spring.version>
  <junit.version>4.12</junit.version>
  <mysql.version>5.1.45</mysql.version>
</properties>
<dependencies>
  <!--junit单元测试包-->
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>${junit.version}</version>
  </dependency>
  <!--mysql连接包-->
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>${mysql.version}</version>
  </dependency>
  <!--spring-mvc所需包-->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
  </dependency>
  <!--servlet支持-->
  <dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```
#### DispatcherServlet—前端控制器

```java
<!--WEB-INF/web.xml-->
<!--spring前端控制器-->
<servlet>
  <servlet-name>springmvc</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <!--如果不配置，默认加载WEB-INF/${servlet-name}-servlet.xml-->
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:spring-mvc.xml</param-value>
  </init-param>
</servlet>
<!--spring前端控制器映射方式-->
<servlet-mapping>
  <servlet-name>springmvc</servlet-name>
  <url-pattern>*.do</url-pattern>
</servlet-mapping>
```
#### ViewResolver—视图解析器

```java
<!--resources/springmvc.xml-->
<!--视图解析器-->
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"/>
```
### 注解方式配置适配器&映射器

```java
<!--注解适配器-->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
<!--注解映射器-->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>
<!--<mvc:annotation-driven/>会同时加载适配器和映射器，并会加载多个参数，一般使用这个-->
<mvc:annotation-driven/>
<!--扫描注入Bean-->
<context:component-scan base-package="pri.ryan.controller"/>
```
##### 注解方式的Controller

```java
<!--注解标记为Controller-->
@Controller
public class AnnotationController {
    <!--注解url映射方式-->
    @RequestMapping("/queryData")
    public ModelAndView queryData() throws Exception{
        /*数据对象*/
        List list = new ArrayList();
        /*获取ModelAndView*/
        ModelAndView modelAndView = new ModelAndView();
        /*添加需要返回的数据对象*/
        modelAndView.addObject("Object",list);
        /*制定仕途*/
        modelAndView.setViewName("/WEB-INF/jsp/simple.jsp");
        return modelAndView;
    }
}
```
### 非注解方式配置适配器&映射器

#### HandlerAdapter—处理器适配器

```java
<!--resources/springmvc.xml-->
    <!--处理器适配器-->
    <!--Controller接口适配器-->
    <bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter"/>
    <!--HttpRequestHandler接口适配器-->
    <bean class="org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter"/>
```
处理器适配器如何判断可以支持?

```java
<!--SimpleControllerHandlerAdapter.class-->
public class SimpleControllerHandlerAdapter implements HandlerAdapter {
    <!--判断是否适配的方法-->
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }
}
<!--HttpRequestHandlerAdapter.class-->
public class HttpRequestHandlerAdapter implements HandlerAdapter {
    <!--判断是否适配的方法-->
    public boolean supports(Object handler) {
        return handler instanceof HttpRequestHandler;
    }
}
```
##### 支持的接口

```java
<!--Controller.class-->
public interface Controller {
    ModelAndView handleRequest(HttpServletRequest var1, HttpServletResponse var2) throws Exception;
}
<!--HttpRequestHandler.class-->
public interface HttpRequestHandler {
    void handleRequest(HttpServletRequest var1, HttpServletResponse var2) throws ServletException, IOException;
}
```
##### Handler—控制层编写

```java
public class SimpleController implements Controller{
    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        /*数据对象*/
        /*一般由Service层返回数据对象，此为最简单案例*/
        List list = new ArrayList();
        /*获取ModelAndView*/
        ModelAndView modelAndView = new ModelAndView();
        /*添加需要返回的数据对象*/
        modelAndView.addObject("Object",list);
        /*指定视图*/
        modelAndView.setViewName("/WEB-INF/jsp/simple.jsp");
        return modelAndView;
    }
}
public class TestController implements HttpRequestHandler{
    @Override
    public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        /*数据对象*/
        /*一般由Service层返回数据对象，此为最简单案例*/
        List list = new ArrayList();
        /*设置数据*/
        httpServletRequest.setAttribute("Object",list);
        /*转发视图*/
        httpServletRequest.getRequestDispatcher("/WEB-INF/jsp/simple.jsp").forward(httpServletRequest,httpServletResponse);
    }
}
```
#### HandlerMapping—处理器映射器

```java
<!--resources/springmvc.xml-->
<!--处理器映射器
将bean的name作为url进行查找，需要配置Handler时指定bean-name-->
<bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>
<!--简单url映射-->
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    <property name="mappings">
        <props>
            <!--  <prop key="${url}">${bean.id}</prop>  -->
            <prop key="/test.do">testController</prop>
        </props>
    </property>
</bean>
```
#### Handler—处理器配置

```java
<!--resources/springmvc.xml-->
<!--配置Handler-->
<!--BeanNameUrl配置方式-->
<bean name="/simple.do" class="pri.ryan.controller.SimpleController"/>
<!--SimpleUrl配置方式-->
<bean id="testController" class="pri.ryan.controller.TestController"/>
```
