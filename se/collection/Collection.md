**Contents of Collection**

- [Collection](#collection)
  - [结构图](#%E7%BB%93%E6%9E%84%E5%9B%BE)
  - [知识点](#%E7%9F%A5%E8%AF%86%E7%82%B9)
- [List](#list)
- [Set](#set)
- [Map: HashMap 是其典型的实现, 不属于 Collection](./java8/feature/Hash-Modify.md)
- [demo-code](#demo-code)

## Collection

### 结构图

![avatar](/static/image/java/javase-collection.png)

1. Java 集合可分为 Set、List 和 Map 三种体系: collection 包含 Set、List、Queue; `不包含 Map`
   > Set: 无序、不可重复的集合
   > List: 有序, 可重复的集合
   > Map: 具有映射关系的集合
2. 小结

   - Collection: List{ LinkedList/ArrayList } Set{ HashSet/TreeSet }
   - Map: HashMap TreeMap
   - Iterator:遍历

     ```java
     // 不带泛型：
     Iterator iterator = collection.iterator();
     while(iterator.hasNext()){
       System.out.println(iterator.next());
     }
     // 带有泛型：
     Iterator<Map.Entry<String, Integer>> entries = map.entrySet().iterator();
     while (entries.hasNext()) {
       Map.Entry<String, Integer> entry = entries.next();
       System.out.println(entry.getKey() + ":" + entry.getValue());
     }
     ```

   - Collections/Arrays
     > Arrays.asList()
     > Collections.sort(List<?>, Comparator)

### 知识点

1. Collection: `List有序、 Set无序`
2. 用于集合添加元素的方法:

   ```java
   add(E e) //E为泛型元素
   addAll(Collection <? extends E>c)
   ```

3. 用于范围集合的方法:

   - 获取集合的长度: size()
   - 对集合进行遍历 的方法: iterator()可以得到对应的 Iterator 接口对象
   - 移除元素:

   ```java
   remove()  // 移除某一指定的元素通过equals方法来判断要移除的元素是否在集合中, 以及是否移除成功
   removeAll() //  collection.removeAll(collection);
   clear()  // 使集合中的元素置空  collection.clear();
   ```

   - 用于检测集合的方法

   ```java
   contains(obj)
   containsAll(Collection <? extends E>c)
   isEmpty()
   toArray() // Person []pers = persons.toArray(new Person[0]);
   ```

4. Iterator: 迭代器[在集合中无法获取某一个具体元素]

   - 获取 Iterator 接口对象
   - 使用 while 和 Iterator 遍历集合中的每一个元素: 具体使用 Iterator 接口的 `hasNext()` 和 `next()` 方法

   ```java
   Iterator iterator=collection.iterator();
   while(iterator.hasNext()){
       System.out.println(iterator.next());
   }
   ```

5. collection intersection

   ```java
   Collection a = new ArrayList(Arrays.asList("a", "b", "c", "d"));
   Collection b = new ArrayList(Arrays.asList("ab", "bc", "e", "f"));
   b.add("g");
   // a will become intersection
   a.retainAll(b);
   ```

6. 测试 Collections 工具类

   - 获取线程安全集合

     - **ArrayList、HashMap、HashSet...都不是线程安全的**
     - 调用 `Collections.synchronizedXxx()` 获取线程安全

     ```java
     // 线程安全集合
     Collections.synchronizedXxx()
     // 获取线程安全的List对象, 使用synchronizedList()
     java.util.List<Object> list = Collections.synchronizedList(new ArrayList<>());
     ```

   - 对 `Enumeration` 对象进行遍历: `hasMoreElements()` `netElement()`

   ```java
   Enumeration names = Collections.enumeration(new ArrayList<>());
   ```

   - 排序操作:

     ```java
     reverse(List) // 反转 List 中元素的顺序
     shuffle(List) // 对 List 集合元素进行随机排序
     sort(List) // 根据元素的自然顺序对指定 List 集合元素按升序排序
     sort(List, Comparator) // 根据指定的 Comparator 产生的顺序对 List 集合元素进行排序
     swap(List, int,  int) // 将指定 list 集合中i 处元素和 j 处元素进行交换
     Object max(Collection) // 根据元素的自然顺序,返回给定集合中的最大元素
     Object max(Collection, Comparator) // 根据 Comparator 指定的顺序, 返回给定集合中的最大元素
     Object min(Collection) //自然排序: 对象要实现Comparable接口
     Object min(Collection, Comparator) //定制排序
     int frequency(Collection, Object) // 返回指定集合中指定元素的出现次数
     boolean replaceAll(List list, Object oldVal, Object newVal) // 使用新值替换 List 对象的所有旧值
     ```

---

## List

1. 特性:

```js
List 代表元素有序、且可以重复的集合, 集合中每个元素都有 其对应得顺序索引  ArrayList 是其典型实现
List 允许使用重复元素, 可以通过索引来访问指定位置的元素
List默认按照元素添加顺序设置元素的索引
```

2. 返回一个只读形的 List, 不是 ArrayList 也不是 Vector

```java
Arrays.asList(Object …args);
```

3. 方法:

```java
void add(int index ,Object element)
boolean addAll(int index ,Collection elements)
Object get(int index)
int indexOf(Object obj)    // 获取指定元素的索引值 charAt(int i)
int lastIndexof(Object obj)
Object remove(int index)
Object set(int index ,Object element)   //替换
List subList(int fromIndex,int toIndex)
```

4. List 的遍历:

```java
List<String> list = new ArrayList<String>();
list.add("aaa");
list.add("bbb");
list.add("ccc");

// 1.超级for循环遍历
for(String attribute : list) {
    System.out.println(attribute);
}
// 2.对于ArrayList来说速度比较快, 用for循环, 以size为条件遍历:
for(int i = 0 ; i < list.size() ; i++) {
    system.out.println(list.get(i));
}
// 3.集合类的通用遍历方式, 从很早的版本就有, 用迭代器迭代
Iterator it = list.iterator();
while(it.hasNext()) {
    System.ou.println(it.next);
}
// 4.Stream()
STREAM().forEach(Consumer)
```

5. List 中元素的排序:

```java
Collections.sort(persons, new Comparator<Person>() {
    @Override
    public int compare(Person o1, Person o2) {
        return o1.getAge().compareTo(o2.getAge());
    }
});
```

---

## Set

1. Set 是 Collection 的子接口, Collection 中的方法都适应
2. Set 中不允许存放相同元素, 判断相等使用 equals 方法, 返回 true
3. HashSet

   - 不能保证元素的排列顺序
   - `HashSet不是线程安全的`
   - 集合元素可以使用 null
   - 对于 HashSet: `如果两个对象通过equals()方法返回true, 这两个对象的HashCode值也应该相同`

4. LinkedHashSet: 有序

   - LinkedHashSet 是 HashSet 的子类
   - 使用链表维护元素的次序, 这使得元素看起来是以插入形式保存的
   - LinkedHashSet 不许允许放相同元素

5. TreeSet:

   - 如果使用 TreeSet() 无参构造器创建一个 TreeSet 对象, 则要求放入其中的元素类必须实现 Comparable 接口, 所以其中不能放 null 元素
   - 必须放入同样类的对象, 否则可能会发生类型转换异常
   - 两个对象通过 Comparable 接口的 CompareTo(obj)方法的返回值来比较大小, 并进行升序排序
   - 当需要把一个对象放入 TreeSet 中, 重写该对象对应得 equals()方法时, 应该保证方法与 compareTo(obj)方法有一致的结果

   ```java
   Comparator<String> comparator=new Comparator<String>() {
       @Override
       public int compare(String o1, String o2) {
           // TODO Auto-generated method stub
           return 0;
       }
   };
   TreeSet<String> set=new TreeSet<>(comparator);
   ```

   - TreeSet 方法
     > Comparator comparator()
     > Object first()
     > Object last()
     > Object lower(Object e)
     > Object higher(Object e)
     > SortedSet subSet(fromElement, toElement)
     > SortedSet headSet(toElement)
     > SortedSet tailSet(fromElement)

6. TreeSet 的实现:

   - TreeSet[TreeMap 的键]的两种排序方式：自然排序[`对象重写 CompareTo`]、[`定制排序 comparator`]
     > 自然排序：被比较的实现类要实现 Comparable 接口的 CompareTo 方法
     > 定制排序：实现 Comparator 接口；创建 TreeSet 时做参数传入. TreeSet<Person>pers=new TreeSet<>(Compartor);

   ```java
   // 1.自然排序: 不需要Comparator;但是被比较的对象要实现Comparable接口并重写CompareTo方法
   @Test
   /**
   * 说明: 这里Person类没有实现Comparable接口中的CompareTo方法
   * TreeSet:必须实现comparator接口来定制对象(必须实现CompareTo或者是带有*CompareTo方法的类型)有序, 不可重复
   *   结果:
   *       Person [name=AA, age=10]
   *       Person [name=BB, age=11]
   *       Person [name=CC, age=12]
   *       Person [name=DD, age=13]
   *       Person [name=EE, age=14]
   */
   public void TestTreeSet() {
       Set<Person> persons = new TreeSet<>();

       persons.add(new Person("AA", 10));
       persons.add(new Person("DD", 13));
       persons.add(new Person("EE", 14));
       persons.add(new Person("BB", 11));
       persons.add(new Person("CC", 12));

       Iterator<Person> it = persons.iterator();
       while (it.hasNext()) {
           System.out.println(it.next());
       }
   }

   // 2.定制排序带有Comparator
   @Test
   /**
   * 说明: 这里Person2类没有实现CompareTo方法
   * TreeSet2:必须实现comparator接口来定制对象(必须实现CompareTo或者是带有*CompareTo方法的类型)有序, 不可重复
   *   结果:
   *       Person [name=AA, age=10]
   *       Person [name=BB, age=11]
   *       Person [name=CC, age=12]
   *       Person [name=DD, age=13]
   *       Person [name=EE, age=14]
   */
   public void TestTreeSet2() {
       Comparator<Person> comparator = (Person o1, Person o2) -> o1.getName().compareTo(o2.getName());

       Set<Person> persons = new TreeSet<>(Comparator.comparing(Person::getName));

       persons.add(new Person("AA", 10));
       persons.add(new Person("DD", 13));
       persons.add(new Person("EE", 14));
       persons.add(new Person("BB", 11));
       persons.add(new Person("CC", 12));

       Iterator<Person> it = persons.iterator();
       while (it.hasNext()) {
           System.out.println(it.next());
       }
   }
   ```

7. 小结
   - HashSet: 无序, 不可重复
   - LinkedHashSet: 有序, 不可重复, 可以 null
   - TreeSet: 必须实现 Comparator 接口来定制对象(必须实现 CompareTo 或者是带有 CompareTo 方法的类型, 所以不可以 null); 有序不可重复

---

## [demo-code](https://github.com/Alice52/DemoCode/tree/master/javase/java-Collection)
