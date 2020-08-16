## Refelct

### Java Type System

1. RTTI[Run-Time Type Identification]: it assumes that we already know all the type information at compile time.

   - 编译时就知道使用的类型信息;
   - 在 Java 中所有的类型转换都是在运行时进行有效性检查;
   - RTTI 的含义: `在运行时识别一个对象的类型`

2. Reflect: it allows us to get and use type information in runtime.
   - 运行时获取并使用类型信息;
   - 它主要用于在编译阶段无法获得所有的类型信息的场景, 如各类框架.

### Class Object

1. introduce

   - Java 使用 Class 对象来执行 RTTI
   - 它包含了与类相关的所有信息
   - Class 是一个类
   - 对象照镜子得到的信息: 某个类的数据成员名、方法和构造器、到底实现了那些接口
   - `JRE 都为每个类保留一个不变的 Class 类型的对象`. 一个 Class 对象包含了特定某各类的相关信息
   - Class 对象是只能由系统创建对象
   - 一个类在 JVM 中只会有一个 Class 实例

2. [Class Loader](../ClassLoader.md)

   - [3]类加载器子系统, 主要完成将 class 二进制文件加载到 JVM 中, 并将其转换为 Class 对象的过程.
   - 当程序第一次对类的静态成员引用时, 就会加载这个类
     - 实际上构造函数也是类的静态方法, 因此使用 new 关键字创建类的新对象也会被当做对类的静态引用, 从而触发类加载器对类的加载.
   - Java 程序在它开始运行之前并非被全部加载, 各个部分是在需要时按需加载的
   - 类加载器在加载类之前, 首先检查这个类的 Class 是否已经加载
     - 如果尚未加载, 加载器会按照类名查找 class 文件, 并对字节码进行有效性校验
     - 一旦 Class 对象被载入内存, 它就用来创建这个类的所有对象

3. Get Class Object: 3 kinds

   - ClassName.class: 其在编译时会受到编译检测, 且该方法不会触发初始化逻辑
   - Class.forName: 可以在不知具体类型时, 通过一个字符串加载所对应的 Class 对象
   - object.getClass: 通过一个对象获取生成该对象的 Class 实例

   ```java
   // 1. by class property
   Class claz = Cat.class;
   // 2. call Object getClass()
   Class claz = new Cat().getClass();
   // 3. by class full name
   String className="cn.edu.ntu.javase.reflect.model.impl.Cat";
   claz=Class.forName(className);
   ```

   |  basic type   |  TYPE type   |
   | :-----------: | :----------: |
   | boolean.class | Boolean.TYPE |
   |  char.class   |  Char.TYPE   |
   |  byte.class   |  Byte.TYPE   |
   |  short.class  |  Short.TYPE  |
   |   int.class   | Integer.TYPE |
   |  long.class   |  Long.TYPE   |
   |  float.class  |  Float.TYPE  |
   | double.class  | Double.TYPE  |
   |  void.class   |  Void.TYPE   |

4. Class API 基础: Class 对象存储了一个 class 的所有信息, 当获取到 Class 对象后, 便能通过 API 获取到这些信息

- AnnotatedElement

  - AnnotatedElement 接口, **该接口代表程序中可以接受注解的程序元素**
  - 子类: Class / Method / Field / Package / Constructor / AccessibleObject
    - getAnnotation: 程序元素上存在的、指定类型的注解
    - getAnnotations: 返回该程序元素上存在的所有注解
    - AnnotationPresent: 判断该程序元素上是否包含指定类型的注解
    - getDeclaredAnnotations: 返回直接存在于此元素上的所有注释

- Member: 用于标记反射中简单元素

  - 子类: Class / Field / Method / Constructor
  - method:
    - getDeclaringClass: 元素所在类
    - getName: 元素名称
    - getModifiers: 元素修饰
    - isSynthetic:

- AccessibleObject: 可访问对象, 其对元素的可见性进行统一封装

  - 子类: Field / Constructor / Method
  - methods
    - isAccessible: 是否可访问
    - setAccessible: 重新访问性

- Executable: 可执行元素的一种封装, 可以获取方法签名相关信息

  - 子类:
    - Constructor
    - Method
  - methods:
    - getName: 获取名称
    - getModifiers: 获取修饰符
    - getTypeParameters: 获取类型参数(泛型)
    - getParameterTypes: 获取参数列表
    - getParameterCount: 获取参数数量
    - getGenericParameterTypes: 获取参数类型
    - getExceptionTypes: 获取异常列表
    - getGenericExceptionTypes: 获取异常列表

5.  Get instance by Class Object: `实际上就是调用那个无参构造器`.

    ```java
    // 调用方法[带有两个参数string int]
    Object obj = claz.newInstance();
    method.setAccessible(true);
    method.invoke(obj,"张壮壮", 12);

    // 调用构造器的 newInstance() 方法创建对象:
    Constructor<Person>constructor = claz.getConstructor(String.class, Integer.class);
    Object obj = constructor.newInstance("张壮壮", 12);
    ```

6.  Method

    - Get Methods

    ```java
    // get Class Object methods and donot contain private method
    Method []methods = claz.getMethods();

    // get all Class Object methods and do contain private method
    // and it only contain current class method, donot contain super class
    Method []methods=claz.getDeclaredMethods();
    ```

    - Get specify Method

    ```java
    // 1. get specify Method: getDeclaredMethod(String name,Class<?>...parameterTypes)
    // 1.1 获取无参数的 setName 方法
    Method method = claz.getDeclaredMethod("setName");
    // 1.2 获取的是带有 String 类型参数的 setName 方法
    Method method = claz.getDeclaredMethod("setName", String.class);
    // 1.3 获取的是带有 String 类型和 Integer 类型参数的 setName 方法
    Method method = claz.getDeclaredMethod("setName", String.class, Integer.class);

    // 2. invoke specify method
    // 2.1 不定参数执行
    public Object invoke(Object obj, String MethodName, Object...args){
        // obj: 执行那个对象的方法;
        // args: 执行方法是所需要传入的参数
        // 1. 获取参数列表
        Class[]parameterTypes = new Class[args.length];
        for(int i = 0;i < parameterTypes.length; i++){
            parameterTypes[i] = args[i].getClass();
        }

        try {
            // 2. 获取方法对象
            Method method = obj.getClass().getDeclaredMethod(MethodName, parameterTypes);
            // 3. 执行方法
            return method.invoke(obj, args);
        }catch (Exception e) {}
        return null;
    }

    // 2.1 确定参数执行
    public static main（String []args){
        Object obj = claz.newInstance();
        // get Methods
        method.invoke(obj, "张壮壮", 12)；
    }
    ```

    - Get Super class: 这里通过迭代可以遍历所有的方法, 包括由继承来的

    ```java
    Class claz = Class.forName("");
    Class superClaz = claz.getSuperclass();
    ```

    - **若通过反射执行私有方法, 则必须加这句话: `method.setAccessible(true);`**

    | 方法                                             | 含义                 |
    | :----------------------------------------------- | :------------------- |
    | getMethods                                       | 获取 public 方法     |
    | getMethod(String name, Class<?>… parameterTypes) | 获取特定 public 方法 |
    | getDeclaredMethods                               | 获取所有方法         |
    | getDeclaredMethod                                | 获取特定方法         |

    - others

    | 方法          | 含义             |
    | :------------ | :--------------- |
    | getReturnType | 获取方法返回类型 |
    | invoke        | 调用方法         |
    | isBridge      | 是否为桥接方法   |
    | isDefault     | 是否为默认方法   |

7.  Field:

    - 该字段可能是私有的, 也可能是父类的

    ```java
    // itself
    Field []fields = claz.getDeclaredFields();
    Field field = claz.getDeclaredField("age");
    // parent
    public  static Field getSupperField(Class<?> clazz, String fieldName) {
        for(;clazz != Object.class; clazz = (Class<?>)clazz.getSuperclass()){
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
    ```

    - 获取指定对象的指定字段的值

    ```java
    Field field = claz.getDeclaredField("age");
    // public Object get(Object obj)
    Object val = field.get(person);
    ```

    - 设置指定字段的指定值:

    ```java
    // obj 为字段所在的对象; value: 要设置的值
    // public void set (Object obj, Object value)
    field.set(person, new Person());
    ```

    - 若该字段为私有, 则需要调用`setAccessible(true)`方法

    |         方法          |         含义         |
    | :-------------------: | :------------------: |
    |       getFields       |   获取 public 字段   |
    | getField(String name) | 获取特定 public 字段 |
    |   getDeclaredFields   |   获取所有的的属性   |
    |   getDeclaredField    |     获取特定字段     |

    - others

    |      方法      |      含义       |
    | :------------: | :-------------: |
    | isEnumConstant |  是否枚举常量   |
    |    getType     |    获取类型     |
    |      get       |   获取属性值    |
    |   getBoolean   | 获取 boolean 值 |
    |    getByte     |  获取 byte 值   |
    |    getChar     |  获取 chat 值   |
    |    getShort    |  获取 short 值  |
    |     getInt     |   获取 int 值   |
    |    getLong     |  获取 long 值   |
    |    getFloat    |  获取 float 值  |
    |   getDouble    | 获取 double 值  |
    |      set       |   设置属性值    |
    |   setBoolean   | 设置 boolean 值 |
    |    setByte     |  设置 byte 值   |
    |    setChar     |  设置 char 值   |
    |    setShort    |  设置 short 值  |
    |     setInt     |   设置 int 值   |
    |    setLong     |  设置 long 值   |
    |    setFloat    |  设置 float 值  |
    |   setDouble    | 设置 double 值  |

8.  获取对象实例的构造器: Constructor

    ```java
    Class<Person>claz = (Class<Person>)Class.forName(className);
    // 获取构造器对象:
    Constructor<Person> constructors = claz.getConstructors();
    // 获取指定的构造器:
    Constructor<Person>constructor = claz.getConstructor(String.class, Integer.class);
    // 调用构造器的 newInstance() 方法创建对象:
    Object obj = constructor.newInstance("张壮壮", 12);
    ```

    |                   方法                   |            含义            |
    | :--------------------------------------: | :------------------------: |
    |               newInstance                | 使用默认构造函数实例化对象 |
    |             getConstructors              |    获取 public 构造函数    |
    | getConstructor(Class<?>… parameterTypes) |  获取特定 public 构造函数  |
    |         getDeclaredConstructors          |     获取所有的构造函数     |
    |          getDeclaredConstructor          |      获取特定构造函数      |

9.  Annotation:

    ```java
    // use setAge() to limit: min=18, max=100, else will throw exceptions

    // 1. define Annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.METHOD})
    public @interface AgeValidator {
        public int min();
        public int max();
    }
    // 2. add Annotation in method
    @AgeValidator(min = 18, max = 100)
    public void setAge(int age) {
        this.age = age;
    }
    // 3. 调用注解的属性进行判断:
        3.1 创建一个对象实例, 得到带有 Annotation 的方法:
        3.2 得到该方法的 Annotation 对象:
        3.3 使用注解: 强制转换; 和属性一样
    ```

10. 反射和泛型:

    - 获取带泛型参数的父类: `getGenericSuperClass`
    - Type 的子接口: `ParameraterizedType`
    - 可以调用 `ParameraterizedType` 的 `Type[]getActuall=TypeArguments()` 获取泛型参数的数组
    - demo

      ```java
      /**
      * 功能: 通过反射, 获取定义Class时声明的父类的泛型参数的类型: getSupperClassGenericType(Class<?> clazz, int index)
      * 如: public EmployeeDao extends BaseDao<Employee, String> 返回值为: BaseDao<Employee, String>
      *
      * @param clazz: 子类对应的 Class 对象
      * @param index: 子类继承父类时传入的泛型索引, 从 0 开始
      */
      public static Class<?> getSupperClassGenericType(Class<?> clazz, int index) {
          //1. 获取带泛型父类的 Class 对象
          Type type = clazz.getGenericSuperclass();
          if(type instanceof ParameterizedType){
              // 2. 强制转换, 获取实际参数数组
              ParameterizedType parameterizedType = (ParameterizedType) type;
              Type []args = parameterizedType.getActualTypeArguments();
              if(args != null && args.length > 0){
                  return (Class<?>) args[index];
              }
          }
          return null;
      }
      ```

11. others methods

|        方法         |                           含义                            |
| :-----------------: | :-------------------------------------------------------: |
|     isInstance      |                判断某对象是否是该类的实例                 |
|  isAssignableFrom   |            判定此 Class 对象所表示的类或接口与            |
|          -          |        指定的 Class 参数所表示的类或接口是否相同,         |
|          -          | 或是否是其超类或超接口. 如果是则返回 true; 否则返回 false |
|   getClassLoader    |               获取加载当前类的 ClassLoader                |
| getResourceAsStream |                根据该 ClassLoader 加载资源                |
|     getResource     |                根据该 ClassLoader 加载资源                |

11. ReflectinUtils 工具类

    ```java
    import java.lang.reflect.Field;
    import java.lang.reflect.Method;
    import java.lang.reflect.ParameterizedType;
    import java.lang.reflect.Type;

    /**
    * 反射中封装的函数:
    * 1.获取 methodName 方法, 该方法可能是私有的, 也可能是父类的(私有)方法:
    * getMethod(Class<?> clazz, String methodName, Class<?>...paramerTypes)
    * 2.执行方法:
    * *invoke2(String className,String methodName, Object...args)
    * *invoke(Object obj,String methodName, Object...args)
    * 3.获取定义Class时声明的父类的泛型参数的类型:
    * *getSupperClassGenericType(Class<?> clazz, int index)
    */
    public class ReflectionUtils {
        /**
        * 功能: 通过反射, 获取定义 Class 时声明的父类的泛型参数的类型
        * 如: public EmployeeDao extends BaseDao<Employee,String>
        *
        * @param clazz:子类对应的           class 对象
        * @param index:子类继承父类时传入的泛型索引, 从0开始
        */
        public static Class<?> getSupperClassGenericType(Class<?> clazz, int index) {
            // 1.获取带泛型父类的Class对象
            Type type = clazz.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                // 2.强制转换, 获取实际参数数组
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] args = parameterizedType.getActualTypeArguments();
                if (args != null & args.length > 0) {
                    return (Class<?>) args[index];
                }
            }
            return null;
        }

        /**
        * 功能: 获取 methodName 方法, 该方法可能是私有的, 也可能是父类的(私有)方法
        *
        * @param clazz
        * @param methodName
        * @param paramerTypes
        */
        public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramerTypes) {
            // 1.这里通过循环,遍历查找方法, 包括父类中的方法
            for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    return clazz.getDeclaredMethod(methodName, paramerTypes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
        * @param clazz
        * @param fieldName
        *
        * @return Field
        *
        * @funtion: get the specific field.
        */
        public static Field getField(Class<?> clazz, String fieldName) {
                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
        }

        /**
        * 功能: 获取 methodName 方法, 该方法可能是私有的, 也可能是父类的(私有)方法
        *
        * @param clazz
        * @param methodName
        * @param paramerTypes
        */
        public static Method getField(Class<?> clazz, String methodName, Class<?>... paramerTypes) {
            // 1.这里通过循环,遍历查找方法, 包括父类中的方法
            for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    return clazz.getDeclaredMethod(methodName, paramerTypes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
        * 自己定义的执行对象方法的方法1:invoke2(String className,String methodName, Object...args)
        *
        * @param className:  调用方法所在的对象的全类名
        * @param methodName: 方法名, 该方法可能是私有方法, 也可能是父类的(私有)方法
        * @param args:       方法所需的参数
        */
        public static Object invoke(String className, String methodName, Object... args) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?>[] paramerTypes = (Class<?>[]) new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramerTypes[i] = args[i].getClass();
                }
                Method method = getMethod(clazz, methodName, paramerTypes);
                Object obj = clazz.newInstance();
                method.setAccessible(true);
                return method.invoke(obj, args);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
        * 自己定义的执行对象方法的方法1:invoke(Object obj,String methodName, Object...args)
        *
        * @param obj:        调用方法所在的对象实例
        * @param methodName: 方法名, 该方法可能是私有方法；
        * @param args:       方法所需的参数
        */
        public static Object invoke(Object obj, String methodName, Object... args) throws Exception {
            //1.找到这个方法
            Class<?> clazz = obj.getClass();
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            Method method = getMethod(clazz, methodName, parameterTypes);
            //2.执行方法
            method.setAccessible(true);
            return method.invoke(obj, args);
        }

        /**
        * 自己定义的执行对象方法的方法1:invoke(Object obj,String methodName, Object...args)
        *
        * @param clazz:
        * @param methodName: 方法名, 该方法可能是私有方法；
        * @param args: 方法所需的参数
        */
        public static Object invoke(Class<?> clazz, String methodName, Object... args) throws Exception {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            Method method = getMethod(clazz, methodName, parameterTypes);
            method.setAccessible(true);
            Object obj = clazz.newInstance();
            return method.invoke(obj, args);
        }
    }
    ```

### [Proxy](./Proxy.md)

1. 代理是为了提供额外的或不同的操作而插入的用来代替 "实际" 对象的对象.

### SPI's Plugin: Service Provider Interface

1. 用来做服务的扩展发现, 是一种动态替换发现的机制.
2. usage
   - 在 JAR 包的 `META-INF/services/` 目录下建立一个文件, 文件名是接口的全限定名,
   - 文件的内容可以有多行, 每行都是该接口对应的具体实现类的全限定名.
   - 然后使用 ServiceLoader.load(Interface.class) 对插件进行加载

### Conclusion

1. getXXX 和 getDeclaredXXX, 两者主要区别在于获取元素的可见性不同:
   - 一般情况下 getXXX 返回 public 类型的元素
   - 而 getDeclaredXXX 获取所有的元素, 其中包括 private / protected / public / package

### Reference

1. https://www.cnblogs.com/canacezhang/p/9237953.html
2. https://www.geekhalo.com/2018/08/16/java/basics/reflection/
3. [sample code](https://github.com/Alice52/DemoCode/tree/master/javase/java-reflect)
