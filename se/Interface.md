## 接口: 抽象方法和常量的集合

- 定义: `Java 接口是一系列方法的声明, 是一些方法特征的集合`, 一个接口只有方法的特征没有方法的实现, 因此这些方法可以在不同的地方被不同的类实现, 而这些实现可以具有不同的行为(功能)。
- 接口(Interface),`在JAVA编程语言中是一个抽象类型(Abstract Type)`, 它被用来要求类(Class)必须实现指定的方法, 使不同类的对象可以利用相同的界面进行沟通。接口通常以 interface 来宣告,
- 它仅能包含`方法签名`(Method Signature)以及`常数宣告`(变量宣告包含了 static 及 final), 一个接口不会包含方法的实现(仅有定义)。

## notice：

1. 使用 interface 来定义接口, java8 中可以有默认实现 default
2. 接口中所有的`成员变量`默认都是 `public static final` 修饰的, 在`声明时必须赋值`
   常量标识符的书写要求：字母都大写, 多个单次使用\_连接
3. 接口中所有的方法默认都是使用 `public abstract` 修饰, 接口没有构造方法
4. 实现接口用 `implements` 关键字, 若一个类即实现了接口又继承了父类, 则把 extends 关键字放在前面; [先继承后实现]
   即`先继承父类, 后实现多个接口`, 一个类可以实现多个无关接口, 使用 `,` 分割[可以利用他们模拟多重继承]
5. `接口也可以继承另一个接口, 使用 extends 关键字`
6. 实现接口中的类必须提供接口中所有方法的具体实现; 若为 `abstract` 则另当别论
7. 多个无关的类可以实现同一接口
8. `与继承类似, 接口和实现类之间存在多态性`
9. 接口无法被实例化, 但是可以被实现, 且可以声明变量

   ```java
   Comparable x; //这是允许的。
   ```

10. 在 Java 中, 接口类型可用来宣告一个变量, 他们可以`成为一个空指针`, 或是被绑定在一个`以此接口实现的对象`。

## 特征标

- 一个方法的特征仅包括方法的 `名字`, `参数的数目` 和 `种类`, 而不包括方法的返回类型, 参数的名字以及所抛出来的异常

## java8 中引入 default

### 接口继承多个父接口

```java
+---------------+         +------------+
|  Interface A  |         |Interface B |
+-----------^---+         +---^--------+
            |                 |
            |                 |
            |                 |
            +-+------------+--+
              | Interface C|
              +------------+
```

```java
interface A {
    default String say(String name) {
        return "hello " + name;
    }
}
interface B {
    default String say(String name) {
        return "hi " + name;
    }
}
interface C extends A,B{
    // 这里编译就会报错: error: interface C inherits unrelated defaults for say(String) from types A and B
}

interface C extends A,B{
    default String say(String name) {
        return "greet " + name;
    }
}
```

### 接口多层继承

```java
+---------------+
|  Interface A |
+--------+------+
         |
         |
         |
+--------+------+
|  Interface b |
+-------+-------+
        |
        |
        |
+-------+--------+
|   Interface C  |
+----------------+
```

- 很容易知道 C 会继承 B 的默认方法, 包括直接定义的默认方法, 覆盖的默认方法, 以及隐式继承于 A1 接口的默认方法。

  ```java
  interface A {
      default void run() {
          System.out.println("A.run");
      }

      default void say(int a) {
          System.out.println("A");
      }
  }
  interface B extends A{
      default void say(int a) {
          System.out.println("B");
      }

      default void play() {
          System.out.println("B.play");
      }
  }
  interface C extends B{

  }
  ```

### 多层多继承

```java
 +---------------+
|  Interface A1 |
+--------+------+
         |
         |
         |
+--------+------+         +---------------+
|  Interface A2 |         |  Interface B  |
+-------+-------+         +---------+-----+
        |       +---------+---------^
        |       |
        |       |
+-------+-------++
|   Interface C  |
+----------------+

```

```java
interface A1 {
    default void say(int a) {
        System.out.println("A1");
    }
}

interface A2 extends A1 {

}

interface B {
    default void say(int a) {
        System.out.println("B");
    }
}
// 必须重新写具有相同特征标的方法
interface C extends A2,B{
    default void say(int a) {
        B.super.say(a);
    }
}
```

### 复杂的

```java
+--------------+
 | Interface A1 |
 +------+------++
        |      ^+-------+
        |               |
+-------+-------+       |
|  Interface A2 |       |
+------------+--+       |
             ^--++      |
                 |      |
              +--+------+-----+
              |  Interface C  |
              +---------------+
```

```java
interface A1 {
    default void say() {
        System.out.println("A1");
    }
}
interface A2 extends A1 {
    default void say() {
        System.out.println("A2");
    }
}
interface C extends A2,A1{

}
static class D implements C {

}
public static void main(String[] args) {
    D d = new D();
    d.say(); // A2
}
```

### 类和接口的复合

- `子类优先继承父类的方法, 如果父类没有相同签名的方法, 才继承接口的默认方法`

```java
+-------------+       +-----------+
| Interface A |       |  Class B  |
+-----------+-+       +-----+-----+
            ^-+    +--+-----^
              |    |
          +---+----+-+
          |  Class C |
          +----------+
```

```java
interface A {
    default void say() {
        System.out.println("A");
    }
}
static class B {
    public void say() {
        System.out.println("B");
    }
}
static class C extends B implements A{

}
public static void main(String[] args) {
    C c = new C();
    c.say(); //B
}
```

### 结论

- 类优先于接口。 `如果一个子类继承的父类和接口有相同的方法实现。 那么子类继承父类的方法`
- `子类型中的方法优先于父类型中的方法[就近原则]`
- 如果以上条件都不满足, 则必须显示覆盖/实现其方法, 或者声明成 abstract。
