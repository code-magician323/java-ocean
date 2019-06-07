## Annotation

1. Annotation 定义

- 使用 @interface 来声明注解
- 使用接口中方法的方式声明注解的注解: 其中返回值称为属性的类型, 方法名为属性的名称

2.  Annotation: 注解

    - 注解概述：

      > Annotation 其实就是代码里的特殊标记, 这些标记可以在编译, 类加载, 运行时 被读取, 并执行相应的处理. 通过使用 Annotation, 程序员可以在不改变原有逻辑的情况下, 在源文件中嵌入一些补充信息.
      > Annotation 可以像修饰符一样被使用, 可用于修饰包,类, 构造器, 方法, 成员变量, 参数, 局部变量的声明, 这些信息被保存在 Annotation 的 "name=value" 对中.
      > Annotation 能被用来为程序元素(类, 方法, 成员变量等) 设置元数据

    - 基本的 Annotation:

      > 使用 Annotation 时要在其前面增加 @ 符号, 并把该 Annotation 当成一个修饰符使用. 用于修饰它支持的程序元素
      > 三个基本的 Annotation:

            ```java
            @Override: 限定重写父类方法, 该注释只能用于方法
            @Deprecated: 用于表示某个程序元素(类, 方法等)已过时
            @SuppressWarnings: 抑制编译器警告.
            ```

    - 自定义 Annotation:

      > 定义新的 Annotation 类型使用 @interface 关键字
      > Annotation 的成员变量在 Annotation 定义中以无参数方法的形式来声明. 其方法名和返回值定义了该成员的名字和类型.
      > 可以在定义 Annotation 的成员变量时为其指定初始值, 指定成员变量的初始值可使用 default 关键字
      > 没有成员定义的 Annotation 称为标记; 包含成员变量的 Annotation 称为元数据 Annotation

    - 提取 Annotation 信息: 反射中的问题
    - JDK 的元 Annotation：修饰注解的注解
      > DK 的元 Annotation 用于修饰其他 Annotation 定义
      > @Retention: 只能用于修饰一个 Annotation 定义, 用于指定该 Annotation 可以保留多长时间, @Rentention 包含一个 RetentionPolicy 类型的成员变量, 使用 @Rentention 时必须为该 value 成员变量指定值:
      ```java
      RetentionPolicy.CLASS: 编译器将把注释记录在 class 文件中. 当运行 Java 程序时, JVM 不会保留注释. 这是默认值
      RetentionPolicy.RUNTIME:编译器将把注释记录在 class 文件中. 当运行 Java 程序时, JVM 会保留注释. 程序可以通过反射获取该注释
      RetentionPolicy.SOURCE: 编译器直接丢弃这种策略的注释
      ```
      > @Target: 用于修饰 Annotation 定义, 用于指定被修饰的 Annotation 能用于修饰哪些程序元素. @Target 也包含一个名为 value 的成员变量.
      > @Documented: 用于指定被该元 Annotation 修饰的 Annotation 类将被 javadoc 工具提取成文档.
      > @Inherited: 被它修饰的 Annotation 将具有继承性.如果某个类使用了被
      > @Inherited 修饰的 Annotation, 则其子类将自动具有该注释

3) 注解的示例：

   ```java
   /**
   * 说明:
   *   1.使用@interface来声明注解
   *   2.使用接口中方法的方式声明注解的注解:其中返回值称为 * 属性的类型，方法名为属性的名称
   */
   @Target(value={ElementType.TYPE,ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)   //元注解
   public @interface HelloAnnotation {
           //使用接口中方法的方式声明注解的注解
           String major();
           int age();
           @Deprecated //@override	 @SuppressWarnings
           String school() default "南通";
   }
   ```

## demo

```java

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 说明：
 *  1.使用 @interface 来声明注解
 *  2. 使用接口中方法的方式声明注解的注解: 其中返回值称为属性的类型, 方法名为属性的名称
 */
@Target(value={ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)   //元注解
public @interface HelloAnnotation {
    // 使用接口中方法的方式声明注解的注解
    String major();
    int age();
    @Deprecated		//@override	 @SuppressWarnings
    String school() default "南通";
}


import basical.HelloAnnotation;

@HelloAnnotation(age=12,major="Java")
public class TestAnnotation {
    @HelloAnnotation(age=12,major="Java")
    public void test() {
    }
}
```
