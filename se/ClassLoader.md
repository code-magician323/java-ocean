- [ClassLoader](#classloader)
  - [ClassLoader](#classloader-1)
  - [diff between property and member](#diff-between-property-and-member)
  - [diff between Initialization and Instance](#diff-between-initialization-and-instance)
  - [Class Initialization](#class-initialization)
  - [ClassLoader strategy](#classloader-strategy)
  - [Properties](#properties)
- [demo](#demo)
- [Reference](#reference)
- [demo-code](#demo-code)

## ClassLoader

### ClassLoader

1. function

   - load .class file to memory, but executable determinte by Execution Engine
   - review who loaded each class, make sure load sequence
   - convert .class file content to runtime data struct in Method Area: `Reparse Class bytecode into object format uniformly required by JVM`

2. kind

   - BootstrapClassLoader[C++]: load `/lib/rt.jar` null
   - ExtensionClassLoader[Java]: load `/lib/ext/.*` sun.misc.Launcher\$ExtClassLoader@439f5b3d
   - AppClassLoader[system class loader]: load `$CLASSPATH` class sun.misc.Launcher\$AppClassLoader@18b4aac2
   - customClassLoader

3. feature
   - lazy load
   - BootstrapClassLoader[C++], ExtensionClassLoader[Java], AppClassLoader, customClassLoader perform their duties
   - reference class by array index will donot trigger class initialization
   - class loader diagram
     ![avatar](/static/image/java/ClassLoader.jpeg)
   - ClassLoader life cycle
     ![avatar](https://user-images.githubusercontent.com/42330329/70802885-a7ecf380-1ded-11ea-8869-51282b8f8b1e.jpg)

### diff between property and member

1. property refers to setXx
2. member refers to filed

### diff between Initialization and Instance

1. Class instantiation refers to the process of creating an instance(object) of a class
2. Class initialization refers to the process of assigning initial values to each class member(modified by static), which contains inlifeCycles

### Class Initialization

- base code

  ```java
  class SuperClass {
    public static int value = 123;

    static {
      System.out.println("SuperClass init!");
    }
  }

  class SubClass extends SuperClass {
    static {
      System.out.println("SubClass init!");
    }
  }
  ```

1. **`using parent static field will not trigger subClass init, but superClass will init. Whether trigger subClass init determinte by JVM implement`**

   ```java
   // this will lead to super class init, and subclass will not init.
   LOG.info(String.valueOf(SubClass.value)); //  value is staic property
   ```

2. using as DataType to new array will not trigger this class init

   ```java
   LOG.info(String.valueOf(new SuperClass[10]));
   ```

3. Constants are stored in the callee constant pool during compilation, so it will not init constants class

   ```java
   class ConstClass {
     public static final String HELLO = "hello";
     static {
       System.out.println("ConstClass init!");
     }
   }
   ```

### ClassLoader strategy

1. 双亲委派机制

- explain: `我爸是李刚， 有事找我爹`
- 会从 bootstrap 开始寻找可使用的类， 找不到会去相应的子类寻找， 在 AppClassLoader 中找不到时则返回给委托的发起者，由它到指定的文件系统或网络等 URL 中加载该类， 还找不到则会抛出 ClassNotFoundException
- feature: the strategy make sure custom code do not impact on java source code

2. 沙箱安全

```java
// custom String class
// 在类 java.lang.String 中找不到 main 方法
package java.lang;

public class String {
  public static void main(String[] args) {
    System.out.println("Custm String");
  }
}
```

### Sequence of execution at initialization

- **`静态优先, 父类优先, 初始化实例变量, 动态代码块, 构造函数`**
- 口诀： `从父到子， 模板先有； 静态加载， 只有一次`

```java
1. 初始化父类静态属性
2. 执行父类静态代码块
3. 初始化子类静态属性
4. 执行子类静态代码块

5. 初始化父类实例变量
6. 执行父类动态代码块
7. 执行父类构造方法
8. 初始化子类实例变量
9. 执行子类动态代码块
10. 执行子类构造方法

// 构造方法之后才是一般方法
```

### Properties

- introduce \*.Property file:
  > one kind configuration file, display such as `KEY = VALUE`
- Properties class in Java.util package, and extends Hashtable

- [commmon method](#demo#L23)

  > getProperty(String key) // get value by key
  > load(InputStream inStream) // load all keys and mapping values from input stream
  > setProperty(String key, String value) // implement by calling Hashtable.put() to set K-V
  > store(OutputStream out, String comments) // contrary to load(i), this method will write K-V to specify file
  > clear() // clear all loaded kvs

## demo

```java
public void testClassLoader() throws Exception {
    // 1 get application/system class loader
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    ClassLoader classLoader = this.getClass().getClassLoader();
    System.out.println(classLoader == classLoader6); //true: sun.misc.Launcher$AppClassLoader@456d3d51
    // 2 get application parent classloader: ExtClassLoader
    ClassLoader classLoader2 = classLoader.getParent();
    System.out.println(classLoader2); //sun.misc.Launcher$ExtClassLoader@6d4b473
    // 3 get ExtClassLoader parent classloader: bootstrap classloader
    ClassLoader classLoader3 = classLoader2.getParent();
    System.out.println(classLoader3); //null

    // 4 get Person Object ClassLoader
    Class<Person> clazz = (Class<Person>) Class.forName("Reflect");
    ClassLoader classLoader4 = clazz.getClassLoader();
    // sun.misc.Launcher$AppClassLoader@456d3d51
    System.out.println(classLoader4);

    // 5 get String ClassLoader
    ClassLoader classLoader5 = String.class.getClassLoader();
    System.out.println(classLoader5); // null

    // 6 usage of ClassLoader
    // load $classpath .properties file
    InputStream inputStream = new FileInputStream("./src/jdbc.properties");
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
    Properties properties = new Properties();
    properties.load(inputStream);
    System.out.println(properties.getProperty("root"));
}
```

---

## Reference

[reference 1](https://blog.csdn.net/u014634338/article/details/81434327);
[reference 2](http://blog.itpub.net/31561269/viewspace-2222522/);

## [demo-code](https://github.com/Alice52/DemoCode/blob/master/java/javase/java-ClassLoader)
