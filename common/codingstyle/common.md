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

1. 变长参数、条件编译、自动拆装箱、内部类
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

### others

1. try-with-resource
2. import static
3. 利用 unchecked 异常
4. 多使用链式编程
5. 多使用 Assert 而不是 throw
6. 多使用 Stream: 主要包括匹配、过滤、汇总、转化、分组、分组汇总等功能, 注意并行时的问题
7. java 的多返回值支持:

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
