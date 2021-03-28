## Generic[参数化类型]

1. 泛型使得数据的类别可以像参数一样由外部传递进来: 它提供了一种扩展能力, 更符合面向抽象开发的软件编程宗旨
2. 当具体的类型确定后, 泛型又提供了一种`类型检测的机制`: 编译层面的
3. 泛型提高了程序代码的可读性: 底层还是强转

   - 从集合中取出的对象都是 Object 类型[泛型擦除], 在具体操作时可能需要进行类型的强制转换;
   - 就有可能发生 `ClassCastException` 异常

4. `集合中的类型并不安全`, 可以向集合中放入任何引用类型的变量: 这个是由于集合是线程不安全的
5. 泛型与子类继承

   - 泛型类
   - 泛型方法
   - 泛型接口

6. 声明泛型: 在类中凡是可以使用类型的地方都可以使用泛型

### 泛型参数

1. E 代表 Element 的意思, 或者 Exception 异常的意思
2. K 代表 Key 的意思
3. V 代表 Value 的意思, 通常与 K 一起配合使用。
4. S 代表 Subtype 的意思
5. T 代表一般的任何类:
6. ? 代表 不确定的 java 类型: `List<Animal> listAnimals; List<? extends Animal> listAnimals`

   - 通配符其实在声明局部变量时是没有什么意义的
   - 但是当你为一个方法声明一个参数时

   ```java
   // ERROR: List<Object> objlist 不是 List<String> strlist 的父类
   static int countLegs (List<? extends Animal> animals) {
       int retVal = 0;
       for ( Animal animal : animals ) {
           retVal += animal.countLegs();
       }
       return retVal;
   }

   static int countLegs1 (List<Animal> animals) {
       int retVal = 0;
       for ( Animal animal : animals ) {
           retVal += animal.countLegs();
       }
       return retVal;
   }

   public static void main(String[] args) {
       List<Dog> dogs = new ArrayList<>();
       countLegs(dogs); // 不会报错
       countLegs1(dogs); // compile error, List<Dog> dogs 不是 List<Animal> animals 的子类
   }
   ```

   - 上界通配符 <? extends E>: ? 可以是 E 或者 E 的子类

   ```java
   private <K extends A, E extends B> E test(K arg1, E arg2){
       E result = arg2;

       return result;
   }
   ```

   - 下界通配符 <? super E>: ? 可以是 E 或者 E 的父类

   ```java
   private <T> void merge(List<? super T> dst, List<T> src){
       for (T t : src) {
           dst.add(t); // 父类容器装子类实例
       }
   }
   ```

7. ? 和 T 的区别

   - ? 和 T 都表示不确定的类型，区别在于我们可以对 T 进行操作, 但是对 ? 不行

   ```java
   T t = operate(); // 可以
   ? car = operate(); // 不可以
   ```

   - T 是一个`确定的`类型, 通常用于`泛型类和泛型方法的定义`
   - ? 是一个`不确定`的类型, 通常用于泛型方法的调用代码和形参

   ```java
   // 通过 T 来 确保 泛型参数的一致性
   public <T extends Number> void merge(List<T> dest, List<T> src)

   //通配符是 不确定的, 所以这个方法不能保证两个 List 具有相同的元素类型
   public void merge(List<? extends Number> dest, List<? extends Number> src) // error
   ```

   - 类型参数 T 可以多重限定而通配符 ? 不行

   ```java
   public <T extends Number & Object> void merge(List<T> dest, List<T> src)
   ```

   - 通配符可以使用超类限定而类型参数不行

   ```java
   T extends A // OK
   T super A // error
   ? extends A // OK
   ? super A // OK
   ```

   - Class<T> 和 Class<?> 区别
     1. Class<T> 在实例化的时候，T 要替换成具体类
     2. Class<?> 它是个通配泛型，? 可以代表任何类型，所以主要用于声明时的限制情况

   ```java
   // 需要强转
   MultiLimit multiLimit = (MultiLimit) Class.forName("com.glmapper.bridge.boot.generic.MultiLimit").newInstance();

   // 需要强转
   public static <T> createInstance(Class<T> clazz) throws IllegalException. InstantiationException {
       return clazz.newInstance();
   }
   MultiLimit multiLimit  = createInstance(MultiLimit.class);
   ```

   ```java
   public class A {
       public Class<?> clazz;
       public Class<T> clazzT; // error
   }
   public class B<T> {
       public Class<?> clazz;
       public Class<T> clazzT;
   }
   ```

   ```java
   public void change(Collection<?> collection) {
       collection.add(null);//在這個方法中, 传入任何数据都是错误的, 除了null
   }
   ```

### 泛型类

1. 定义

   ```java
   public class MultiType <E,T> {
       E value1;
       T value2;

       public E getValue1(){
           return value1;
       }

       public T getValue2(){
           return value2;
       }
   }
   ```

2. 只要在对泛型类创建实例的时候, 在尖括号中赋值相应的类型便是, T 就会被替换成对应的类型

### 泛型方法

1. 定义

   ```java
   public static <A extends Comparable<A>> A max(Collection<A> xs) {
      Iterator<A> xi = xs.iterator();
      A w = xi.next();
      while (xi.hasNext()) {
          A x = xi.next();
          if (w.compareTo(x) < 0)
              w = x;
      }
      return w;
   }
   ```

2. 泛型对象和方法

   ```java
   public class GenericWildcard<E> {

       @Deprecated
       public static <T extends Person> void mergeSameType(List<T> dest, List<T> src) {
           src.forEach(x -> dest.add(x));
       }

       public <T extends E> void mergeSameType2(List<T> dest, List<T> src) {
           src.forEach(x -> dest.add(x));
       }

       public static <T> void merge(List<? super T> dest, List<T> src) {
           src.forEach(x -> dest.add(x));
       }
   }
   ```

3. 在类(不一定是泛型类)中使用泛型方法
4. 在方法的返回值前面使用 `<E> E` 声明泛型类型, 则是在方法的返回值、参数、方法体中都可以使用该类型.

   ```java
   public <E> E getProperty(Integer id)
   ```

### 泛型接口

1. 定义

   ```java
   public interface Iterable<T> { }
   ```

2. sample

   ```java
   public abstract class BaseGenericMethod<T> {

       public T produce() {
           Type type = this.getClass().getGenericSuperclass();
           ParameterizedType parameterizedType = (ParameterizedType) type;
           Class<T> clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];

           return createInstance(clazz);
       }

       @SneakyThrows
       public T createInstance(Class<T> clazz) {

           return clazz.newInstance();
       }

       public abstract int hash();
   }

   public class GenericMethodImpl extends BaseGenericMethod<Person> {
       @Override
       public int hash() {
           Person instance = this.createInstance(Person.class);
           return Objects.hash(instance);
       }
   }

   //private GenericMethodImpl genericMethod = new GenericMethodImpl();
   // Person person = genericMethod.produce();
   ```

## 泛型与子类继承

### 泛型擦除

1. Code sharing 方式为每个泛型类型`创建唯一`的字节码表示, 并且将该泛型类型的实例`都映射到这个唯一的字节码表示上`.

   ```java
   // 当泛型内包含静态变量
   public class StaticTest{
       public static void main(String[] args){
           GT<Integer> gti = new GT<Integer>();
           gti.var=1;
           GT<String> gts = new GT<String>();
           gts.var=2;
           System.out.println(gti.var);
       }
   }
   class GT<T>{
       public static int var=0;
       public void nothing(T x){}
   }
   ```

2. 将多种泛型类形实例映射到唯一的字节码表示是通过类型擦除`[type erasue]`实现的

   - 将所有的泛型参数用其最左边界[最顶级的父类型]类型替换
   - 移除所有的参数泛型

   ```java
   Map<String, String> map = new HashMap<String, String>();
   map.put("name", "hollis");
   // javac
   Map map = new HashMap(); // Map<Object, Object> map
   map.put("name", "hollis");
   map.getClass(); // HashMap

   List<Integer> list = new ArrayList<Integer>();
   list.add(66);
   int num = list.get(0);
   // javac
   List list = new ArrayList();
   list.add(Integer.valueOf(66));
   int num = ((Integer) list.get(0)).intValue();
   ```

   ```java
   public static <A extends Comparable<A>> A max(Collection<A> xs) {
       Iterator<A> xi = xs.iterator();
       A w = xi.next();
       while (xi.hasNext()) {
           A x = xi.next();
           if (w.compareTo(x) < 0)
               w = x;
       }
       return w;
   }

   // javac
   public static Comparable max(Collection xs){
       Iterator xi = xs.iterator();
       Comparable w = (Comparable)xi.next();
       while(xi.hasNext())
       {
           Comparable x = (Comparable)xi.next();
           if(w.compareTo(x) < 0)
               w = x;
       }
       return w;
   }
   ```

   ```java
   public class Erasure <T>{
        T object;

        public Erasure(T object) {
            this.object = object;
        }

        public void add(T object) {

        }
   }

    // add(T object) 泛型擦除之后时add(Object object)
   ```

   ```java
   List<Integer> ls = new ArrayList<>();
   ls.add(23);
   // ls.add("text"); // Error
   Method method = ls.getClass().getDeclaredMethod("add",Object.class);
   method.invoke(ls,"test");
   method.invoke(ls,42.9f);
   ```

3. interview

   ```java
   List<String> l1 = new ArrayList<String>();
   List<Integer> l12 = new ArrayList<Integer>();
   List<Integer> l2 = new LinkedList<Integer>();

   System.out.println(l1.getClass()); // class java.util.ArrayList
   System.out.println(l2.getClass()); // class java.util.LinkedList
   System.out.println(l1.getClass() == l12.getClass()); // true
   ```

## [create generic object](https://github.com/Alice52/java-ocean/issues/136)

1. create basic type

   ```java
    @SneakyThrows
   public static <T> T CreateBasicType(Class<T> clazz, T init) {
       Constructor constructor = clazz.getConstructor(wrapperPrimitiveMap.get(clazz));

       return (T) constructor.newInstance(init);
   }

   // Integer integer = CreateWithGeneric.CreateBasicType(Integer.class, 1);
   ```

2. generic array

   ```java
   // T[] array = new T[]; // compile error
   public static <T> T[] getArray(Class<T> componentType, int length)
   {
       return (T[]) Array.newInstance(componentType, length);
   }
   // Integer[] array = CreateWithGeneric.createArray(Integer.class, 2); [null, null]
   ```

3. generic list

   ```java
   // this method cannot create basic type
   public static <T> List<T> getList(Class<T> componentType) {
       List<T> list = new ArrayList<>();
       T t = componentType.newInstance();
       list.add(t);

       return list;
   }

   @SneakyThrows
    public static <T> List<T> createList(Class<T> componentType) {
        List<T> list = new ArrayList<>();
        T t;
        if (wrapperPrimitiveMap.containsKey(componentType)) t = null;
        else t = componentType.newInstance();
        list.add(t);

        return list;
    }
    // List<Person> people = CreateWithGeneric.createList(Person.class); // [Person{name='null', age=0}]

    @SneakyThrows
    public static <T> List<T> createList(Class<T> componentType, int length) {
    return Arrays.asList((T[]) Array.newInstance(componentType, length));
    }
    // List<Integer> list1 = CreateWithGeneric.createList(Integer.class, 2); // [null, null]
   ```

4. object

   ```java
   public class GenericsArrayT<T> {
       private Object[] array;

       public GenericsArrayT(int size) {
           array = new Object[size];
       }

       public void put(int index, T item) {
           array[index] = item;
       }

       public T get(int index) {
           return (T) array[index];
       }

       public T[] rap() {
           return (T[]) array;
       }
   }
   ```

## reference

1. https://mp.weixin.qq.com/s/ceoIJaT1HPtWdXiUPC74Qw
2. https://mp.weixin.qq.com/s/RwMQDwbnQLgjsV3ADJ1tqw
3. https://blog.csdn.net/briblue/article/details/76736356
4. [应用](https://github.com/Alice52/tutorials-sample/blob/master/db/jdbc/jdbc-sample/src/main/java/cn/edu/ntu/jdbc/sample/generics/dao/BaseDAO.java)
