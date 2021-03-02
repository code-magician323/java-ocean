## ClassLoader

### Introduce

1. 作用

   - 加载 class 文件到内存, 最终由执行引擎执行
   - 检查由谁加载, 保证顺序[双亲委派+沙箱安全]
   - 将 class 文件的内容转换为运行时数据结构: `Reparse Class bytecode into object format uniformly required by JVM`

2. java 的类加载体系

   - BootstrapClassLoader[C++]: load `/lib/rt.jar` null
   - ExtensionClassLoader[Java]: load `/lib/ext/.*` sun.misc.Launcher\$ExtClassLoader@439f5b3d
   - AppClassLoader[system class loader]: load `$CLASSPATH` class sun.misc.Launcher\$AppClassLoader@18b4aac2
   - customClassLoader

![avatar](/static/image/java/ClassLoader.jpeg)

3. JDK 的类加载对象

   - ClassLoader
   - SecureClassLoader
   - URLClassLoader
   - ExtClassLoader
   - AppClassLoader

![avatar](/static/image/java/javase-jdk-classloader.png)

### 加载过程: `加载-连接-初始化`

1. load: 这个是由类加载器执行, 该步骤将查找字节码文件, 并根据字节码创建一个 Class 对象

   - find binary class file and load to memory
   - JVM use ClassName + PackageName + ClassLoader to load it to memory
   - and it labeled by this three element: ClassName + PackageName + ClassLoader Instance Id
   - So different class load by different ClassLoaders
   - Id exists extands, JVM wiill load superClass first

2. link: 在链接阶段将验证类中的字节码, 为静态域分配存储空间, 如果必要的话, 将解析这个类创建的对其他类的引用.

   - 链接过程负责对二进制字节码的格式进行校验、解析类中调用的接口和类
   - 校验是防止不合法的.class 文件
   - 对类中的所有属性、调用方法进行解析, 以确保其需要调用的属性、方法存在以及具备应的权限[例如 public、private 域权限等], 会造成 NoSuchMethodError/NoSuchFieldError 等错误信息

     1. 验证: 确保被加载的类的正确性
     2. 准备: 为类的静态变量分配内存，并将其`初始化为默认值`
     3. 解析: 把类中的符号引用转化为直接引用

3. [init: 如果该类有超类, 则对其进行初始化, 执行静态初始化器和静态初始化块; 初始化被延时到对静态方法或非常数静态域进行首次访问时才执行](#class-initialization)

   - 调用了 new;
   - 反射调用了类中的方法;
   - 子类调用了初始化[先执行父类静态代码和静态成员, 再执行子类静态代码和静态变量, 然后调用父类构造器, 最后调用自身构造器]
   - JVM 启动过程中指定的初始化类。

4. feature

   - lazy load
   - BootstrapClassLoader[C++], ExtensionClassLoader[Java], AppClassLoader, customClassLoader perform their duties
   - reference class by array index will donot trigger class initialization
   - class loader diagram

   - ClassLoader life cycle
     ![avatar](https://user-images.githubusercontent.com/42330329/70802885-a7ecf380-1ded-11ea-8869-51282b8f8b1e.jpg)

     - 类的准备阶段: 需要做是为类变量[static 变量]分配内存并设置默认值[如果类变量是 final 的, 编译时 javac 就会为它赋上值]
     - 类的初始化阶段: 需要做的是执行类构造器
     - 类构造器: 编译器收集所有静态语句块和类变量的赋值语句, 按语句在源码中的**顺序**合并生成类构造器]

5. 热加载

   - Java 的每一个类加载器都会对他加载过的类保留一个缓存[导致无法实现热加载]
   - 解决: 在每次使用该类的时候创建一个新的 ClassLoader 进行加载
   - 很少使用, 因为类加器加载时一个过程, 容易出错, 且会产生非常多的垃圾

### how to load .class file

1. 从本地内存系统中直接加载
2. 通过网络下载 .class 文件
3. 子类调用了初始化[父类静态代码 > 子类静态代码 > 父类构造器 > 子类构造器]
4. JVM 启动过程中指定的初始化类

### diff between property and member

1. property refers to setXx
2. member refers to filed

### diff between Initialization and Instance

1. 类实例化是指创建类的实例[对象]的过程
2. 类初始化是指为每个类成员[由 static 修改]分配初始值的过程, 该过程包含于 LifeCycles

### Class Initialization

- 当程序第一次对类的静态成员引用时, 就会加载这个类

  - 实际上构造函数也是类的静态方法, 因此使用 new 关键字创建类的新对象也会被当做对类的静态引用, 从而触发类加载器对类的加载.

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

   > one kind configuration file, display such as `KEY = VALUE`

2. Properties class in Java.util package, and extends Hashtable

3. API

   - getProperty(String key) // get value by key
   - load(InputStream inStream) // load all keys and mapping values from input stream
   - setProperty(String key, String value) // implement by calling Hashtable.put() to set K-V
   - store(OutputStream out, String comments) // contrary to load(i), this method will write K-V to specify file
   - clear() // clear all loaded kvs

### method

1. ClassLoader.getSystemClassLoader() // it will always return AppClassLoader

---

## Interview

### 双亲委派机制

![avatar](/static/image/java/javase-classlodaer.png)

1. 定义&过程:

   - explain: `向上委托加载, 向下委托创建`
   - 想使用某个类时会优先从 bootstrap 开始寻找可使用的类, 找不到会去相应的子类寻找,
   - 在 AppClassLoader 中找不到时则向下委托创建[由它到指定的文件系统或网络等 URL 中加载该类],
   - 最终都没有的话则会抛出 ClassNotFoundException
   - [双亲委派导致的]一个在第三方 jar 内的 class, 如果在自己的项目中重写了, 则会使用自己项目中的
     1. 自己项目在启动时 AppCL 会加载所有的类
     2. 使用该类是会优先使用上层的, 上层中能找到项目中自定义的, 所以不会使用第三方 jar 的 class

2. 优点: 使自己创建的一些同名类不会污染源代码中的[比如重写了 Object 方法]
3. core code

   ```java
    protected Class<?> loadClass(String name, boolean resolve)
       throws ClassNotFoundException
   {
       synchronized (getClassLoadingLock(name)) {
           // First, check if the class has already been loaded
           Class<?> c = findLoadedClass(name);
           if (c == null) {
               long t0 = System.nanoTime();
               try {
                   if (parent != null) {
                       c = parent.loadClass(name, false);
                   } else {
                       c = findBootstrapClassOrNull(name);
                   }
               } catch (ClassNotFoundException e) {
                   // ClassNotFoundException thrown if class not found
                   // from the non-null parent class loader
               }

               if (c == null) {
                   // If still not found, then invoke findClass in order
                   // to find the class.
                   long t1 = System.nanoTime();
                   c = findClass(name);

                   // this is the defining class loader; record the stats
                   sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                   sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                   sun.misc.PerfCounter.getFindClasses().increment();
               }
           }

           // 热加载机制会将编译阶段可以抛出的问题转移到运行时
           if (resolve) {
               resolveClass(c);
           }
           return c;
       }
   }
   ```

4. 打破双亲委派机制: 重写 loadClass 实现优先加载

   ```java
   // 在这里打破双亲委派机制: 此时的原系统中的 Class 与第三方的 Class 不能互转
   @Override
   public Class<?> loadClass(String name) throws ClassNotFoundException {
    // 在使用该类的时候, 发现符合规则, 就直接加载, 不走双亲委派机制
    if (name.startsWith("cn.edu.ntu")) {
      return findClass(name);
    }

    return super.loadClass(name);
   }
   ```

### SPI

1. 配置 `resource/META-INF/services/cn.edu.ntu.javase.classloader.SpiInterface` 接口名文件, 内容是实现类

   ```js
   // 必须是实现类
   cn.edu.ntu.javase.classloader.SpiImpl;
   ```

2. ServiceLoader 会加载

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
