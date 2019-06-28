## Refelct

1.  关于反射 Class:

    - Class 是一个类
    - 对象照镜子得到的信息: 某个类的数据成员名、方法和构造器、到底实现了那些接口
    - `JRE 都为每个类保留一个不变的 Class 类型的对象`. 一个 Class 对象包含了特定某各类的相关信息
    - Class 对象是只能由系统创建对象
    - 一个类在 JVM 中只会有一个 Class 实例

2.  得到 Class 对象: 3 种方法

    ```java
    // 1.直接通过类名.class方式得到:
    Class claz = Person.class;
    // 2.通过调用对象的 getClass() 方法得到
    Class claz = person.getClass();
    // 3.通过全类名方式获取: 包名.类名
    String className="basical._InputStream";
    claz=Class.forName(className);
    ```

3.  得到 Class 对象后创建对象实例: `实际上就是调用那个无参构造器`.

    ```java
    Object obj = claz.newInstance(); // 调用方法[带有两个参数string int]
    // 若通过反射执行私有方法, 则必须加这句话:
    method.setAccessible(true);
    method.invoke(obj,"张壮壮", 12);
    ```

4.  ClassLoader:

    - 相关知识:
      1. 获取一个系统的类加载器
      ```java
      ClassLoader classLoader=ClassLoader.getSystemClassLoader();
      ```
      2. 获取系统列加载器的父类: 扩展类加载器
      ```java
      ClassLoader classLoader2=classLoader.getParent();
      ```
      3. 获取扩展类加载器的父类: 引导类加载器
      ```java
      ClassLoader classLoader3=classLoader2.getParent();
      ```
      4. 测试当前类由哪个类加载器加载
      ```java
      Class <Person>clazz = (Class<Person>) Class.forName("basical._Class");
      ClassLoader classLoader4=clazz.getClassLoader();
      ```
      5. 测试 JDK 中 String 类是由哪个类加载器加载
      ```java
      ClassLoader classLoader5=String.class.getClassLoader();
      ```
      6. 关于类加载器的一个主要应用: 读取当前工程下的.properties 的输入流:
      ```java
      this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
      ```
    - demo

    ```java
    @Test
    /**
        * 功能: 测试类加载器:
        *  带 ?? 的是一样的.
        */
    public void testClassLoader() throws Exception{
        // 1.获取一个系统的类加载器
        ClassLoader classLoader=ClassLoader.getSystemClassLoader();
        System.out.println(classLoader); //sun.misc.Launcher$AppClassLoader@456d3d51
        // 2.获取系统列加载器的父类: 扩展类加载器
        ClassLoader classLoader2=classLoader.getParent();
        System.out.println(classLoader2); //sun.misc.Launcher$ExtClassLoader@6d4b473
        // 3.获取扩展类加载器的父类: 引导类加载器
        ClassLoader classLoader3=classLoader2.getParent();
        System.out.println(classLoader3); //获取不到, 返回null
        // ??
        ClassLoader classLoader6=this.getClass().getClassLoader();
        System.out.println(classLoader6);

        // 4.测试当前类由哪个类加载器加载
        Class <Person>clazz=(Class<Person>) Class.forName("basical._Class");
        ClassLoader classLoader4=clazz.getClassLoader();
        System.out.println(classLoader4);  //sun.misc.Launcher$AppClassLoader@456d3d51: 和上一个系统类加载器一样, 说明是由系统类加载器加载

        // 5.测试JDK中String类是由哪个类加载器加载
        ClassLoader classLoader5=String.class.getClassLoader();
        System.out.println(classLoader5); //null,说明由引导类加载器加载
    }
    ```

5.  获取对象实例的方法: Method

    - 得到 Class 所对应的类中有哪些方法, `不能获取private方法`

    ```java
    Method []methods = claz.getMethods();
    ```

    - 获取所有的方法, `包括private方法`, 但`只获取当前类声明的方法`:

    ```java
    Method []methods=claz.getDeclaredMethods();
    ```

    - 获取指定的方法:

    ```java
    // 1.获取类的指定方法: getDeclaredMethod(String name,Class<?>...parameterTypes)
    // 1.1.获取无参数的 setName 方法
    Method method = claz.getDeclaredMethod("setName"); //无参
    // 1.2.获取的是带有String类型参数的setName方法
    Method method = claz.getDeclaredMethod("setName",String.class);
    // 1.3.获取的是带有String类型和Integer类型参数的setName方法
    Method method = claz.getDeclaredMethod("setName",String.class,Integer.class);
    // 2.获取类的方法数组:
    claz.getDeclaredMethods()
    // 3.通过method对象执行方法
    public Object invoke(Object obj,String MethodName,Object...args){//自定义
        // obj: 执行那个对象的方法; args: 执行方法是所需要传入的参数
        //1.获取参数列表
        Class[]parameterTypes = new Class[args.length];
        for(int i=0;i<parameterTypes.length;i++){
            parameterTypes[i]=args[i].getClass();
        }

        try {
            //2.获取方法对象
            Method method=obj.getClass().getDeclaredMethod(MethodName, parameterTypes);
            //3.执行方法
            return method.invoke(obj, args);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static main（String []args){
        Object obj = claz.newInstance();
        // get Methods
        method.invoke(obj, "张壮壮", 12)；
    }
    ```

    - 获取当前类的父类: 这里通过迭代可以遍历所有的方法, 包括由继承来的

    ```java
    Class claz = Class.forName("");
    Class superClaz = claz.getSuperclass();
    ```

    - **若通过反射执行私有方法, 则必须加这句话: `method.setAccessible(true);`**

6.  获取对象实例的属性: Field:封装信息

    - 获取字段: 该字段可能是私有的, 也可能是父类的

    ```java
    Field []fields = claz.getDeclaredFields();
    Field field = claz.getDeclaredField("age");
    // 父类的字段:
    public  static Field _getField(Class<?> clazz,String fieldName) {
        //这里通过循环
        for(;clazz!=Object.class;clazz= (Class<?>)clazz.getSuperclass()){
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
    // public void set (Object obj,Object value) //obj为字段所在的对象; value:要设置的值
    field.set(person,"张壮壮");
    ```

    - 若该字段为私有, 则需要调用`setAccessible(true)`方法

7.  获取对象实例的构造器: Constructor

    ```java
    Class<Person>claz = (Class<Person>)Class.forName(className);
    // 获取构造器对象:
    Constructor<Person> constructors = claz.getConstructors();
    // 获取指定的构造器:
    Constructor<Person>constructor = claz.getConstructor(String.class,Integer.class);
    // 调用构造器的newInstance()方法创建对象:
    Object obj = constructor.newInstance("张壮壮", 12)；
    ```

8.  Annotation:

    ```java
    // 功能: 对setAge()方法进行限制: min=18,max=100,超出这个范围都会抛出异常
    // 1.声明注解:
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    public @interface AgeValidator {
        public int min();
        public int max();
    }
    // 2.对相应的方法添加注解:
    @AgeValidator(min=18,max=100)
    public void setAge(int age) {
        this.age = age;
    }
    // 3.调用注解的属性进行判断:
        3.1 创建一个对象实例, 得到带有Annotation的方法:
        3.2 得到该方法的Annotation对象:
        3.3 使用注解: 强制转换;和属性一样
    ```

9.  反射和泛型:

    - 获取带泛型参数的父类: `getGenericSuperClass`; 返回值为: BaseDao<Employee,?????
    - Type 的子接口: `ParameraterizedType`
    - 可以调用 `ParameraterizedType` 的 `Type[]getActuall=TypeArguments()`获取泛型参数的数组
    - demo

    ```java
    /**
    * 功能: 通过反射, 获取定义Class时声明的父类的泛型参数的类型: getSupperClassGenericType(Class<?> clazz,int index)
    * 如: public EmployeeDao extends BaseDao<Employee,String>
    * @param clazz:子类对应的Class对象
    * @param index:子类继承父类时传入的泛型索引, 从0开始
    */
    public static Class<?> getSupperClassGenericType(Class<?> clazz, int index) {
        //1.获取带泛型父类的Class对象
        Type type = clazz.getGenericSuperclass();
        if(type instanceof ParameterizedType){
            //2.强制转换, 获取实际参数数组
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type []args = parameterizedType.getActualTypeArguments();
            if(args!=null&args.length>0){
                return (Class<?>) args[index];
            }
        }
        return null;
    }
    ```

10. 反射小结:

    - Class 是一个类: **一个描述类的类, 封装了描述方法的 Method, 描述字段的 Field, 描述构造器的 Constructor**
    - 如何得到 Class 对象: 3 种方法

    ```java
    Person.class;
    person.getClass();
    Class.forName("className");
    ```

    - 关于 Method:
      1. 如何获取 Method:
      ```java
        getDeclaredMethod(String methodName,Class...args)  //获取带有指定参数类型的方法
        getDeclaredMethods() //返回方法列表
      ```
      2. 如何带有 private 修饰的 Method:
      ```js
        如果方法是 private 修饰的, 则需要先调用 Method的setAccessible(true),使其变为可访问
        之后调用 method.invoke(obj,Object...args);
      ```
    - 关于 Field:
      1. 如何获取 Method:
      ```java
      getField(String fieldName);????
      Field []fields=claz.getDeclaredFields();
      Field field=claz.getDeclaredField("age");
      ```
      2. 如何获取 Field 值:
      ```js
      如果方法是 private 修饰的, 则需要先调用 Method 的 setAccessible(true),使其变为可访问
      之后调用 field.get(Object obj); //这里 method 是要获取的属性, obj 是属性所在的对象
      ```
      3. 如何设置 Field 的值:
      ```java
      field.set(Object obj,Object val); //这里 method 是要设置的属性, obj 是属性所在的对象, val 是要设置的值
      ```

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

## [demo-code](https://github.com/Alice52/DemoCode/tree/master/javase/java-reflect)
