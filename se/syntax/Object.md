## Object

### 1. registerNatives()

### 2. getClass()

### 3. hashCode()

### 4. equals(Object obj)

1. 对于 ==

   > 如果作用于基本数据类型的变量, 则直接比较其存储的 "值" 是否相等;
   > 如果作用于引用类型的变量, 则比较的是所指向的对象的地址

2. equals() 方法是 Object 类的方法, 由于所有类都继承 Object 类, 也就继承了 equals() 方法.

   - equals 方法不能作用于基本数据类型的变量.
   - 如果没有对 equals 方法进行重写, 则比较的是引用类型的变量所指向的对象的地址;
   - 诸如 String、Date 等类对 equals 方法进行了重写的话, 比较的是所指向的对象的内容.

3. 重写 equals() 就要重写 hashcode()

   - 当两个对象 equals 比较为 true, 那么 hashcode 值应当相等, 反之亦然, 因为当两个对象 hashcode 值相等, 但是 equals 比较为 false
   - 成对重写, 即重写 equals 就应当重写 hashcode.

### 5. clone()

### 6. toString()

1. toString() 方法在 Object 类中定义, 其返回值是 String 类型, 返回类名和它的引用地址.
   `这里需要注意的是 ArrayList<Person> : Person 的 toString 方法会被迭代`

### 7. notify() / notifyAll()

### 8. wait() / wait(long timeout) / wait(long timeout, int nanos)

### 9. finalize()

---

## others

### 1. instanceof: 双目运算符

- syntax

  ```java
  // The compiler checks whether obj can be converted to the class type correctly.
  // If it cannot be converted, an error is reported directly.
  // If the type cannot be determined, it is compiled success.
  boolean result = obj instanceof Class/Interface-Name

  // obj should be Reference Type
  int i = 0;
  boolean flag = i instanceof Integer;                                              // compile error
  Integer i = new Integer(10);
  boolean flag = i instanceof Integer;                                              // compile success

  // null is a special symbol that can be any reference type.
  boolean flag = null instanceof Comparable;
  LOG.info("null can be obj, but it will not be any Object instance: " + flag);     // false
  ```

- extands

  ```java
  ArrayList arrayList = new ArrayList();
  boolean flag = arrayList instanceof List;                                         // true

  List list = new ArrayList();
  boolean flagB = list instanceof ArrayList;                                        // true
  ```
