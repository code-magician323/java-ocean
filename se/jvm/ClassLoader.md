## ClassLoader

### Introduce

1. 作用

   - 加载 class[本地或网络] 文件到内存, 最终由执行引擎执行
   - 检查由谁加载, 保证顺序[双亲委派+沙箱安全]
   - 将 class 文件的内容转换为运行时数据结构: `Reparse Class bytecode into object format uniformly required by JVM`

2. java 的类加载体系

   - BootstrapClassLoader[C++]: load `/lib/rt.jar` null
   - ExtensionClassLoader[Java]: load `/lib/ext/.*` sun.misc.Launcher\$ExtClassLoader@439f5b3d
   - AppClassLoader[system class loader]: load `$CLASSPATH` class sun.misc.Launcher\$AppClassLoader@18b4aac2
   - customClassLoader
   - lazy load: 使用时不认识才加载, 不是一下子都加载到内存的

![avatar](/static/image/java/ClassLoader.jpeg)

3. JDK 的类加载对象

   - ClassLoader
   - SecureClassLoader
   - URLClassLoader
   - ExtClassLoader
   - AppClassLoader

![avatar](/static/image/java/javase-jdk-classloader.png)

4. 自定义类加载器

   - 隔离加载类
   - 修改类加载的方式
   - 拓展加载源
   - 防止源码泄露

### 加载过程: `加载-连接-初始化`

1. load

   - 通过类的全限定类名获取[findClass]此类的二进制字节流: JVM 使用 `ClassName + PackageName + ClassLoader InstanceId`加载的类
   - 将这个字节流所代表的静态存储结构**转化为方法区的运行时数据结构**
   - 在内存中生成此类的 java.lang.Class 对象

2. link: 将加载到 JVM 中的二进制字节流的类数据信息合并到 JVM 的运行时状态中

   - 验证: **格式验证**[验证是否符合 class 文件规范], **语义验证**[final 子类问题, final 方法重写问题, 父子类间的方法签名问题(比如方法签名相同,但方法的返回值不同], **操作验证**[栈中的数据的操作, 常量池中的各种符号引用]
   - 准备: 为类的静态变量分配内存, 并将其`初始化为默认值`, final 修饰的 static 变量直接赋值; `不为实例变量分配初始化[等到堆上的对象创建时才初始化]`
   - 解析: 把类中的`符号引用`转化为`直接引用`, 解析该类创建时对其他类的必要引用, 对类所有属性/方法进行解析[保证属性/方法存在以及具备应的权限 NoSuchMethodError/NoSuchFieldError]

3. [init](#class-initialization)

   - 先父后子
   - 为静态变量赋值
   - 执行 static 代码块

![avatar](https://user-images.githubusercontent.com/42330329/70802885-a7ecf380-1ded-11ea-8869-51282b8f8b1e.jpg)

4. 热加载

   - Java 的每一个类加载器都会对他加载过的类保留一个缓存[导致无法实现热加载]
   - 解决: 在每次使用该类的时候创建一个新的 ClassLoader 进行加载
   - 很少使用, 因为类加器加载时一个过程, 容易出错, 且会产生非常多的垃圾

### diff between Initialization and Instance

1. 类实例化是指创建类的实例[对象]的过程
2. 类初始化是分配空间并给 static 的分配默认初始值

### Class Initialization

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

1. 通过子类使用父类的静态成员时, 只会初始化父类[是否初始化子类有 JVM 的实现决定]

   ```java
   // this will lead to super class init, and subclass will not init.
   LOG.info(String.valueOf(SubClass.value)); //  value is staic property
   ```

2. 创建指定类型的数组时, 不会触发该类型的加载

   ```java
   LOG.info(String.valueOf(new SuperClass[10]));
   ```

3. 常量存在常量池中, 直接使用不会触发加载

   ```java
   class ConstClass {
     public static final String HELLO = "hello";
     static {
       System.out.println("ConstClass init!");
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

1. introduce \*.Property file:

   - one kind configuration file, display such as `KEY = VALUE`

2. Properties class in Java.util package, and extends Hashtable
3. API

   - getProperty(String key) // get value by key
   - load(InputStream inStream) // load all keys and mapping values from input stream
   - setProperty(String key, String value) // implement by calling Hashtable.put() to set K-V
   - store(OutputStream out, String comments) // contrary to load(i), this method will write K-V to specify file
   - clear() // clear all loaded kvs

---

## Interview

### SPI

1. 配置 `resource/META-INF/services/cn.edu.ntu.javase.classloader.SpiInterface` 接口名文件, 内容是实现类

   ```js
   // 必须是实现类
   cn.edu.ntu.javase.classloader.SpiImpl;
   ```

2. ServiceLoader 会加载: 不需要自己写反射和 find 的代码

   ```java
   public static void main(String[] args) {
       SpiInterface spi = null;
       // load(class, loader) 进行制定类加载器进行加载
       ServiceLoader<SpiInterface> spis = ServiceLoader.load(SpiInterface.class);
       Iterator<SpiInterface> iterator = spis.iterator();

       if (iterator.hasNext()) {
         spi = iterator.next();
       }

       Optional.ofNullable(spi).ifPresent(System.out::println);
   }
   ```

### 沙箱安全

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

## sample

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

1. https://blog.csdn.net/u014634338/article/details/81434327
2. http://blog.itpub.net/31561269/viewspace-2222522
3. https://www.cnblogs.com/canacezhang/p/9237953.html
