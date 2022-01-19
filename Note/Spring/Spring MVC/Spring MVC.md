# SpringMVC

## Spring 框架的 7 个模块
![Spring 框架](https://www.ibm.com/developerworks/cn/java/wa-spring1/spring_framework.gif)

## SpringMVC是什么？

>Spring Web MVC是一种基于Java的实现了Web MVC设计模式的请求驱动类型的轻量级Web框架，即使用了MVC架构模式的思想，将web层进行职责解耦，基于请求驱动指的就是使用请求-响应模型，框架的目的就是帮助我们简化开发。

## SpringMVC运行流程图

![Spring 流程图](../../resources/SpringMVC流程.png)

**核心架构的具体流程步骤如下：**

- 首先用户发送请求——>DispatcherServlet，前端控制器收到请求后自己不进行处理，而是委托给其他的解析器进行处理，作为统一访问点，进行全局的流程控制；
- DispatcherServlet——>HandlerMapping， HandlerMapping将会把请求映射为HandlerExecutionChain对象（包含一个Handler处理器（页面控制器）对象、多个HandlerInterceptor拦截器）对象，通过这种策略模式，很容易添加新的映射策略；
- DispatcherServlet——>HandlerAdapter，HandlerAdapter将会把处理器包装为适配器，从而支持多种类型的处理器，即适配器设计模式的应用，从而很容易支持很多类型的处理器；
- HandlerAdapter——>处理器功能处理方法的调用，HandlerAdapter将会根据适配的结果调用真正的处理器的功能处理方法，完成功能处理；并返回一个ModelAndView对象（包含模型数据、逻辑视图名）；
- ModelAndView的逻辑视图名——> ViewResolver， ViewResolver将把逻辑视图名解析为具体的View，通过这种策略模式，很容易更换其他视图技术；
- View——>渲染，View会根据传进来的Model模型数据进行渲染，此处的Model实际是一个Map数据结构，因此很容易支持其他视图技术；
- 返回控制权给DispatcherServlet，由DispatcherServlet返回响应给用户，到此一个流程结束。

## Spring MVC优势

- 清晰的角色划分
  - 前端控制器（DispatcherServlet）
  - 请求到处理器映射（HandlerMapping）
  - 处理器适配器（HandlerAdapter）
  - 视图解析器（ViewResolver）
  - 处理器或页面控制器（Controller）
  - 验证器（Validator）
  - 命令对象（Command  请求参数绑定到的对象就叫命令对象）
  - 表单对象（Form Object 提供给表单展示和提交到的对象就叫表单对象）

- 分工明确，而且扩展点相当灵活，可以很容易扩展

- 由于命令对象就是一个POJO，无需继承框架特定API，可以使用命令对象直接作为业务对象

- 和Spring 其他框架无缝集成，是其它Web框架所不具备的

- 可适配，通过HandlerAdapter可以支持任意的类作为处理器

- 可定制性，HandlerMapping、ViewResolver等能够非常简单的定制

- 功能强大的数据验证、格式化、绑定机制

- 利用Spring提供的Mock对象能够非常简单的进行Web层单元测试

## 注解

### @Controller

它将一个类标记为 Spring Web MVC **控制器** Controller 。

需配置：

 ```xml
 < context:component-scan base-package = "com.host.app.web" />
<!--以便注册带有@Controller、@Service、@Repository、@Compoent等注释的类成为Spring的Bean,base-package属性指定了需要扫描的类包。-->
 ```

### @RequestMapping

请求路径映射，可以标注类，也可以标注方法，可以指定请求类型（post、get、put、delete、patch...）默认不指定为全部接收。

```java
@RequestMapping(value="/getName", method = RequestMethod.GET)
```

### @GetMapping

Spring4.3之后加入的注解，相当于指定请求方法的`RequestMapping`;同样的还有`@PostMapping`，`@DeleteMapping`

### **@ResponseBody**

放在方法上，表示此方法返回的数据放在body体中，而不是跳转页面。一般用于ajax请求，返回json数据。

### **@RestController**

这个是**@Controller**和**@ResponseBody**的注解组合，提供 Restful API；返回什么样的数据格式，根据客户端的 `"ACCEPT"` 请求头来决定。

### **@PathVariable**

路径绑定变量，用于绑定路径中 “/{a}/”的变量。

```java
@RequestMapping(value="/index/{id}",method=RequestMethod.GET)  
public String edit(@PathVariable("id")Integer id,Map<String , Object>map){}
```

### @RequestParam

用于数据绑定，接收url中的参数（即 **Content-Type : application/x-www-form-urlencoded**类型的内容;

```java
@RequestMapping("/index")  
public String list(@RequestParam(value="pageNo",required=false,defaultValue="1")String pageNoStr,Map<String, Object>map){}
//defaultValue 表示设置默认值，required 铜过boolean设置是否是必须要传入的参数，value 值表示接受的传入的参数。
```

### **@RequestBody**

用于数据绑定，接收body体中的参数，而不是url。所以请求一定是post（即不是 **Content-Type : application/x-www-form-urlencoded** 类型的内容）

```java
@RequestMapping(value = "/something", method = RequestMethod.PUT)
public void handle(@RequestBody String body, Writer writer) throws IOException {
    writer.write(body);
}
```

### @Validated

**通过注解对表单参数进行验证；**

先引入依赖hibernate-validator, validation-api；

```java
@PostMapping("/test")
public void save(@Validated Girl girl){
}
```

### **@RequestHeader**

该注解用于将请求头的信息映射到方法的形参上。

### **@CoookieValue**

放在方法参数前，用来获取request header cookie中的参数值。

### @SessionAttributes

该注解允许我们有选择地指定Model中哪些属性需要转存到HttpSession中。只能用在类上，不能声明在方法上。

### @ModelAttribute

该注释只支持一个属性value，类型为String。注意的是，使用该注释的方法会在Controller每个方法执行前被执行。因此在一个Controller映射到多个URL时，要谨慎使用。

```java
@Controller
@RequestMapping("/attribute")
public class ModelAttributeController {
	@ModelAttribute
    public void userModel(String name, String password, Model mv) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        mv.addAttribute("user", user);
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Model mv) {
        //User user = (User) mv.asMap().get("user");
        return "welcome";
    }
}
```

### **@ResponseStatus**

定义处理器功能处理方法/异常处理器返回的状态码和原因；

### @Component

泛指组件，当组件不要好归类时，可以使用这个注解进行标注

### **@ExceptionHandler**

注解式声明异常处理器；