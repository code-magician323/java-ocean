## ClassLoader

### ClassLoader

- 作用
  - 负责将 Class 加载到 JVM 中
  - 审查每个类由谁加载(父优先的等级加载机制)
  - 将 Class 字节码重新解析成 JVM 统一要求的对象格式
- 特点
  - 延迟加载
  - BootstrapClassLoader、ExtensionClassLoader 和 AppClassLoader 各司其职
  - 通过数组定义来引用类, 不会触发此类的初始化.
- 类加载器关系图片
  ![avatar](http://img.blog.itpub.net/blog/2018/12/03/8b3d13d45026563e.jpeg?x-oss-process=style/bb)
- 类的生命周期
  ![avatar](https://img-blog.csdn.net/20180805193923861?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTQ2MzQzMzg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### Java 代码初始化时的执行顺序

- **`静态优先, 父类优先, 初始化实例变量, 动态代码块, 构造函数`**
- 口诀： `从父到子， 模板先有； 静态加载， 只有一次`

```java
1、 初始化父类静态属性
2、 执行父类静态代码块
3、 初始化子类静态属性
4、 执行子类静态代码块
5、 初始化父类实例变量
6、 执行父类动态代码块
7、 执行父类构造方法
8、 初始化子类实例变量
9、 执行子类动态代码块
10、执行子类构造方法
```

### Properties

- 简介 \*.Property 文件:
  > 是一种配置文件, 文件的内容是格式是"键=值"的格式.
- Properties 类存在于胞 Java.util 中, 该类继承自 Hashtable

  - [常用方法](#demo#L23)

    > getProperty ( String key) // 得到 key 所对应的 value。
    > load ( InputStream inStream) // 从输入流中读取该文件中的所有键-值对
    > setProperty ( String key, String value) // 调用 Hashtable 的方法 put 方法, 设置键-值对
    > store ( OutputStream out, String comments) // 与 load 方法相反, 该方法将键-值对写入到指定的文件中去
    > clear () // 清除所有装载的键-值对

## demo

```java
public void testClassLoader() throws Exception {
    // 1 获取一个系统的类加载器
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    ClassLoader classLoader6 = this.getClass().getClassLoader();
    System.out.println(classLoader == classLoader6); //sun.misc.Launcher$AppClassLoader@456d3d51
    // 2 获取系统类加载器的父类: 扩展类加载器
    ClassLoader classLoader2 = classLoader.getParent();
    System.out.println(classLoader2); //sun.misc.Launcher$ExtClassLoader@6d4b473
    // 3 获取扩展类加载器的父类: 引导类加载器
    ClassLoader classLoader3 = classLoader2.getParent();
    System.out.println(classLoader3); //获取不到,返回null

    // 4 测试当前类由哪个类加载器加载
    Class<Person> clazz = (Class<Person>) Class.forName("Reflect");
    ClassLoader classLoader4 = clazz.getClassLoader();
    System.out.println(classLoader4);  // sun.misc.Launcher$AppClassLoader@456d3d51: 和上一个系统类加载器一样,说明是由系统类加载器加载

    // 5 测试 JDK 中 String 类是由哪个类加载器加载
    ClassLoader classLoader5 = String.class.getClassLoader();
    System.out.println(classLoader5); // null,说明由引导类加载器加载

    // 6 关于类加载器的一个主要方法:
    // 读取当前工程下的.properties的输入流: this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
    // InputStream inputStream=new FileInputStream("./src/jdbc.properties");
    // InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
    // Properties properties = new Properties();
    // properties.load(inputStream);
    // System.out.println(properties.getProperty("root"));
}
```

---

## 参考

[参考 1](https://blog.csdn.net/u014634338/article/details/81434327);
[参考 2](http://blog.itpub.net/31561269/viewspace-2222522/);

## [demo-code](https://github.com/Alice52/DemoCode/blob/master/java/javase/java-ClassLoader)
