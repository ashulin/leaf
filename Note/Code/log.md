### 转换到SLF4J

-   ***commons-logging(JCL)*** ：`jcl-over-slf4j`
-   ***log4j1***：`log4j-over-slf4j`
-   ***log4j2***：`log4j-to-slf4j`
-   ***java.util.logging(JUL)***：`jul-to-slf4j`

### SLF4J绑定实现

-   ***commons-logging(JCL)*** ：`slf4j-jcl`
-   ***log4j1***：`slf4j-log4j12`
-   ***log4j2***：
    -   `log4j-slf4j-impl` SLF4J 1.7.x releases or older.
    -   `log4j-slf4j18-impl` SLF4J 1.8.x releases or newer.
-   ***java.util.logging(JUL)***：`slf4j-jdk14`
-   ***logback***：`logback-classic`

![click to enlarge](http://www.slf4j.org/images/concrete-bindings.png)

![slf4j适配](http://www.slf4j.org/images/legacy.png)

[slf4j Bridging legacy APIs](http://www.slf4j.org/legacy.html)

[Log4j 2 SLF4J Binding](https://logging.apache.org/log4j/2.x/log4j-slf4j-impl/)

