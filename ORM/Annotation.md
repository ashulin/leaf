## Spring Data JPA常用注解

#### @Enumerated 

```java
/**
* 用于标记枚举，默认为保存枚举的顺序值。
*/
public @interface Enumerated {
    EnumType value() default EnumType.ORDINAL;
}
public enum EnumType {
    ORDINAL,
    STRING;
    private EnumType() {
    }
}
```

#### @Column

```java
/**
* 用于标记属性为字段。
*/
public @interface Column {
	/*字段名称，默认为属性的名称*/
    String name() default "";
	/*是否唯一*/
    boolean unique() default false;
	/*是否可以为空*/
    boolean nullable() default true;
	
    boolean insertable() default true;

    boolean updatable() default true;

    String columnDefinition() default "";

    String table() default "";

    int length() default 255;

    int precision() default 0;

    int scale() default 0;
}
```