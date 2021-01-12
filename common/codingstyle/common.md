## common coding style

### 泛型

1. 泛型接口

   ```java
   public interface Comparable<T> {
       public int compareTo(T other);
   }

   @Getter
   @Setter
   @ToString
   public class UserVO implements Comparable<UserVO> {
       private Long id;

       @Override
       public int compareTo(UserVO other) {
           return Long.compare(this.id, other.id);
       }
   }
   ```

2. 泛型类

   ```java
   @Data
   @ToString
   public class Point<T extends Number> {
       private T x;
       private T y;
   }
   ```

3. 泛型方法

   ```java
   public static <K, V> Map<K, V> newHashMap(K[] keys, V[] values) {
       if (ArrayUtils.isEmpty(keys) || ArrayUtils.isEmpty(values)) {
           return Collections.emptyMap();
       }

       Map<K, V> map = new HashMap<>();
       int length = Math.min(keys.length, values.length);
       for (int i = 0; i < length; i++) {
           map.put(keys[i], values[i]);
       }
       return map;
   }
   ```

### 语法糖

1. 条件编译、、
2. switch: String

   ```java
   public static void main(String[] args) {
       String str = "world";
       switch (str) {
           case "hello":
               System.out.println("hello");
               break;
           case "world":
               System.out.println("world");
               break;
           default:
               break;
       }
   }

   // javac
    public static void main(String args[]) {
       String str = "world";
       String s;
       switch((s = str).hashCode()) {
       default:
           break;
       case 99162322:
           if(s.equals("hello"))
               System.out.println("hello");
           break;
       case 113318802:
           if(s.equals("world"))
               System.out.println("world");
           break;
       }
   }
   ```

3. 泛型: **Code specialization 和`Code sharing`**

   - Code sharing 方式为每个泛型类型`创建唯一`的字节码表示, 并且将该泛型类型的实例`都映射到这个唯一的字节码表示上`.
   - 将多种泛型类形实例映射到唯一的字节码表示是通过类型擦除`[type erasue]`实现的
     1. 将所有的泛型参数用其最左边界[最顶级的父类型]类型替换
     2. 移除所有的参数泛型

   ```java
   Map<String, String> map = new HashMap<String, String>();
   map.put("name", "hollis");

   // javac
   Map map = new HashMap(); // Map<Object, Object> map
   map.put("name", "hollis");
   map.getClass(); // HashMap
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

4. 自动拆装箱

   - 装箱: `Integer.valueOf(i)`
   - 拆箱: `i.intValue();`
   - 注意 NPE 问题

5. 变长参数

   - `...`: 会转化为 `一个数组`
   - sample

   ```java
   public static void print(String... strs)
   {
       for (int i = 0; i < strs.length; i++)
       {
           System.out.println(strs[i]);
       }
   }

   // javac
   public static transient void print(String strs[])
   {
       for(int i = 0; i < strs.length; i++)
           System.out.println(strs[i]);

   }
   ```

   - why transient?: TODO:

6. [枚举](https://github.com/Alice52/java-ocean/blob/feature-zack/memorabilia/Enumuration.md)

   - 本质: 一个继承了`java.lang.Enum`的 `final` 类
   - method:
     1. valueOf():T
     2. values(): T[]
   - sample

   ```java
   public enum T {
       SPRING, SUMMER;
   }

   // javac
   public final class T extends Enum
   {
       private T(String s, int i)
       {
           super(s, i);
       }
       public static T[] values()
       {
           T at[];
           int i;
           T at1[];
           System.arraycopy(at = ENUM$VALUES, 0, at1 = new T[i = at.length], 0, i);
           return at1;
       }

       public static T valueOf(String s)
       {
           return (T)Enum.valueOf(demo/T, s);
       }

       public static final T SPRING;
       public static final T SUMMER;
       private static final T ENUM$VALUES[];
       static
       {
           SPRING = new T("SPRING", 0);
           SUMMER = new T("SUMMER", 1);
           ENUM$VALUES = (new T[] {
               SPRING, SUMMER
           });
       }
   }
   ```

7. 内部类

   - 只是编译时的概念, 会生成两个完全不同的 .class 文件
   - [reference](https://github.com/Alice52/java-ocean/blob/feature-zack/se/inner%20class/Innerclass.md)

8. 数值字面量

   - 编译器并不认识在数字字面量中的 `_`

   ```java
   public static void main(String... args) {
       int i = 10_000;
       System.out.println(i);
   }

   // javac
   public static void main(String[] args)
   {
       int i = 10000;
       System.out.println(i);
   }
   ```

9. for-each

   - 如果是 [] 则会编译成 for-i
   - 如果是可迭代的则会使用 iterator: `Iterator 在工作的时候是不允许被迭代的对象被改变的`

   ```java
   public static void main(String... args) {
       String[] strs = {"A", "B", "C"};
       for (String s : strs) {
           System.out.println(s);
       }
       List<String> strList = ImmutableList.of("A", "B", "C");
       for (String s : strList) {
           System.out.println(s);
       }
   }

   // javac
   public static transient void main(String args[])
   {
       String strs[] = {"A", "B", "C"};
       String args1[] = strs;
       int i = args1.length;
       for(int j = 0; j < i; j++)
       {
           String s = args1[j];
           System.out.println(s);
       }

       List strList = ImmutableList.of("A", "B", "C");
       String s;
       for(Iterator iterator = strList.iterator(); iterator.hasNext(); System.out.println(s))
           s = (String)iterator.next();

   }
   ```

   - ConcurrentModificationException: `Iterator 在工作的时候是不允许被迭代的对象被改变的`

   ```java
   // Iterator 是工作在一个独立的线程中, 并且拥有一个 mutex 锁.
   // Iterator 被创建之后会建立一个指向原来对象的单链索引表, 当原来的对象数量发生变化时, 这个索引表的内容不会同步改变
   // 所以当索引指针往后移动的时候就找不到要迭代的对象,
   // 所以按照 fail-fast 原则 Iterator 会马上抛出java.util.ConcurrentModificationException异常
   for (Student stu : students) {
       if (stu.getId() == 2)
           students.remove(stu);   // throw exception
   }
   // 可以使用 `Iterator.remove()` 移除元素
   ```

10. lambda

    ```java
    public static void main(String... args) {
        List<String> strList = ImmutableList.of("A", "B", "C");
        strList.forEach( s -> { System.out.println(s); } );
    }

    // javac
    public static /* varargs */ void main(String ... args) {
        ImmutableList strList = ImmutableList.of((Object)"A", (Object)"B", (Object)"C");
        strList.forEach((Consumer<String>)LambdaMetafactory.metafactory(
                null,
                null,
                null,
                (Ljava/lang/Object;)V,
                lambda$main$0(java.lang.String ),
                (Ljava/lang/String;)V)());
    }

    private static /* synthetic */ void lambda$main$0(String s) {
        System.out.println(s);
    }
    ```

### others

1. try-with-resource
2. import static
3. 利用 unchecked 异常
4. 多使用链式编程
5. 多使用 Assert 而不是 throw
6. 多使用 Stream: 主要包括匹配、过滤、汇总、转化、分组、分组汇总等功能, 注意并行时的问题
7. foreach 两层一定要`外小内大`, 不要在里面处理异常
8. java 的多返回值支持:

   - `Apache 的 Pair[2] 类和 Triple[3] 类`

   ```java
   public static class PointAndDistance {
       private Point point;
       private Double distance;
   }

   public static PointAndDistance getNearest(Point point, Point[] points) {
       // 计算最近点和距离
       ...

       // 返回最近点和距离
       return new PointAndDistance(nearestPoint, nearestDistance);
   }
   ```

   ```java
   public static Pair<Point, Double> getNearest(Point point, Point[] points) {
       // 计算最近点和距离
       ...

       // 返回最近点和距离
       return ImmutablePair.of(nearestPoint, nearestDistance);
   }
   ```
