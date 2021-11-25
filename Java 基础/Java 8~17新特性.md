# Java 8

1.   **Lamdba**表达式
     -   `()->`取代匿名类
     -   方法引用/构造器引用`::`
     -   `@FunctionalInterface`函数式接口
2.   **Stream API** 简化集合操作
3.   `Optional<T>` 解决null指针异常
4.   接口的 `default`方法与`static`方法
5.   `LocalDate` 等新**时间和日期API**
6.   **注解**
     -   注解的类型扩展
     -   支持重复注解
7.   **JVM中Metaspace取代PermGen空间**
8.   HashMap底层的变化

# Java 9~17

## 1. JShell



## 2. Textarea：`"""`

Preview: JDK 13 | Release: JDK 15

能够使得字符串所见即所得：

-   没有换行字符（\n）
-   没有连接字符（+）
-   双引号没有使用转义字符（\）

**限制：开始分隔符必须单独成行**

>    textarea 和 原始的长字符串处理方式产生的对象指向同一内存地址

```java
String stringBlock =
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        "    <body>\n" +
        "        <h1>\"Hello World!\"</h1>\n" +
        "    </body>\n" +
        "</html>\n";

String textBlock = """
        <!DOCTYPE html>
        <html>
            <body>
                <h1>"Hello World!"</h1>
            </body>
        </html>
        """;

String s = """""";
// Error:| illegal text block open delimiter sequence, missing line terminator| String s = """""";
```

不同于传统字符串的是，在编译期，文字块要顺序通过如下三个不同的编译步骤：

-   为了降低不同平台间换行符的表达差异，编译器把文字内容里的换行符统一转换成 LF（\u000A）；
-   为了能够处理 Java 源代码里的缩进空格，要删除所有文字内容行和结束分隔符共享的前导空格，**以及所有文字内容行的尾部空格**；
    -   为了能够选择保留尾部空格可以使用新的转义字符：`\s`
    -   为了保持代码格式，可以在需要断行的地方添加转义字符：`\`
-   最后处理转义字符，这样开发人员编写的转义序列就不会在第一步和第二步被修改或删除。

## 3. record

Preview: JDK 14 | Release: JDK 16

>   像`Enum `一样，**Record**也是一个特殊的类输入Java。它旨在用于仅创建类以充当**普通数据载体**的地方。
>
>   Record旨在消除设置和从实例获取数据所需的所有代码，Record将这种责任转移给生成编译器。

-   内置了**构造方法**、`equals` 、`hashCode` 、 `toString` 以及不可变参数的与参数同名`get`的实现
-   由于不可变所以**线程安全**；

**限制**：

-   隐含父类***java.lang.Record***，无法使用`extend`；
-   该类被标记为 `final` ，不可被继承；
-   不能声明可变的变量，也不能支持实例初始化的方法；
    -   其变量是不可变的；
    -   可以定义静态变量；
    -   可以使用注解；
    -   可以对构造函数override添加参数验证；
-   不能声明`native`方法；

```java
// class
public final class Circle implements Shape {
    private final double radius;

    public Circle(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException(
                "The radius of a circle cannot be negative [" + radius + "]");
        }
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
    
    public double radius() {
        return this.radius;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Circle circle = (Circle) o;
        return Double.compare(circle.radius, radius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius);
    }

    @Override
    public String toString() {
        return "Circle[radius=" + radius +"]";
    }
}
```

```java
// record
public record Circle(double radius) implements Shape {
    
    public Circle {
        if (radius < 0) {
            throw new IllegalArgumentException(
                "The radius of a circle cannot be negative [" + radius + "]");
        }
    }
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

## 4. sealed

Preview: JDK 15 | Release: JDK 17

使用 `sealed` 类修饰符，限制基类的扩展性，`permits`指定许可的子类；

许可类的声明需要满足下面的三个条件：

-   许可类必须和封闭类处于同一模块（module）或者包空间（package）里，也就是说，在编译的时候，封闭类必须可以访问它的许可类；
-   许可类必须是封闭类的直接扩展类；
-   许可类必须声明是否继续保持封闭：
    -   许可类可以声明为终极类`final`，从而关闭扩展性；
    -   许可类可以声明为封闭类`sealed`，从而延续受限制的扩展性；
    -   许可类可以声明为解封类`non-sealed`, 从而支持不受限制的扩展性。

```

package co.ivi.jus.sealed.propagate;

public abstract sealed class Shape {
    public final String id;

    public Shape(String id) {
        this.id = id;
    }

    public abstract double area();
    
    public static non-sealed class Circle extends Shape {
        // snipped
    }
    
    public static sealed class Square extends Shape {
        // snipped
    }
    
    public static final class ColoredSquare extends Square {
        // snipped
    }

    public static class ColoredCircle extends Circle {
        // snipped
    }
}
```

需要注意的是，**由于许可类必须是封闭类的直接扩展，因此许可类不具备传递性**。也就是说，上面的例子中，ColoredSquare 是 Square 的许可类，但不是 Shape 的许可类。

## 5. instanceof 改进

可在`instanceof` 之后添加类型转换后的本地变量

```java

public final class Shadow {
    private static final Rectangle rect = null;

    public static boolean isSquare(Shape shape) {
        if (shape instanceof Rectangle rect) {
            // Field rect is shadowed, local rect is in scope
            System.out.println("This should be the local rect: " + rect);
            return rect.length() == rect.width();
        }

        // Field rect is in scope, local rect is not in scope here
        System.out.println("This should be the field rect: " + rect);
        return shape instanceof Shape.Square;
    }
}
```

### 6. switch 增强

**表达式改进**

Preview: JDK 12 | Release: JDK 14

```java
String temperature ="";
switch (season) {
    case SPRING:
    case AUTUMN:
        temperature  = "温暖";
        break;
    case SUMMER:
        temperature  = "炎热";
        break;
    case WINTER:
        temperature  = "寒冷";
        break;
    default:
       temperature  = "忽冷忽热";
}
// now
String temperature = switch (season) {
    case null           -> "无"
    case SPRING, AUTUMN -> "温暖";
    case SUMMER         -> "炎热";
    case WINTER         -> "寒冷";
    default             -> "忽冷忽热";
}
```

