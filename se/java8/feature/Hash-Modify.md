## HashMap

### Map: HashMap 是其典型的实现, 不属于 Collection

1. Map 用以保存具有映射关系的数据, 因此 Map 集合例保存着两组值 `<key ,values>`
2. Map 中的 `key values` 可以是 `任何引用类型` 的数据
3. Map 中的 key `不允许重复`, 即同一个 Map 对象的任何两个 key 通过 equals 方法比较都要返回 false
4. key 和 values 之间单项一对一关系, 即通过指定的 key 总能找到 `唯一` 的 values
5. HashMap 定义了 HashSet: `HashMap 中的 key 就是 HashSet 里的元素`
6. `LinkedHashMap` 是有序的
7. TreeMap:

   - 如果使用 TreeMap() 无参构造器创建一个 TreeMap 对象, 则要求放入其中的元素类 `必须实现Comparable接口`, 所以其中不能放 null 元素
   - `必须放入同样类的对象`, 否则可能会发生类型转换异常
   - 两个对象通过 Comparable 接口的 `CompareTo(obj)` 方法的返回值来比较大小, 并进行`升序排序`
   - 当需要把一个对象放入 TreeMap 中, 重写该对象对应得 equals() 方法时, 应该保证方法与 compareTo(obj)方法有一致的结果

   ```java
   Comparator<String> comparator=new Comparator<String>() {
       @Override
       public int compare(String o1, String o2) {
           // TODO Auto-generated method stub
           return 0;
       }
   };
   TreeMap<String> set=new TreeMap<>(comparator);
   ```

8. 方法:

```java
// 1.添加元素
put(key,value)   ;put(map);	putAll(Map<? extends K,? extends V> map)

// 2.从map中取出元素
// 2.1得到键的集合:
Set map.KeySet()
// 2.2 利用键得到值
Value map.get(key)
//2.3 得到值的集合:
Collection<V> map.values()
//2.4 得到键值对的集合 entrySet() :
Set<Map.Entry<K,V>> entrySet()
for(Map.Entry<String, Object> entry : map.entrySet()){
    String key = entry.getKey();
    Object val = entry.getValues();
}
// 2.5 判断值是否在:
Boolean map.containsValue(value)
Boolean map.containsKey(key)
// 2.6 移除:
map.remove(key)
// 2.7 工具方法:
size()
isEmpty()

// 3.遍历:
// 3.1 map.entrySet()得到键值对
Map<Integer, Integer> map = new HashMap<Integer, Integer>();
for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
}
// 3.2在for-each循环中遍历 keys 或 values
Map<Integer, Integer> map = new HashMap<Integer, Integer>();
for (Integer key : map.keySet()) {
    System.out.println("Key = " + key);
}
for (Integer value : map.values()) {
    System.out.println("Value = " + value);
}
// 3.3使用 entrySet().Iterator() 遍历
Map<Integer, Integer> map = new HashMap<Integer, Integer>();
Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator();
while (entries.hasNext()) {
    Map.Entry<Integer, Integer> entry = entries.next();
    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
}
// 3.4不使用泛型:
Map map = new HashMap();
Iterator entries = map.entrySet().iterator();
while (entries.hasNext()) {
    Map.Entry entry = (Map.Entry) entries.next();
    Integer key = (Integer)entry.getKey();
    Integer value = (Integer)entry.getValue();
    System.out.println("Key = " + key + ", Value = " + value);
}
// 3.5 ForEach
map.forEach((k, v) -> System.out.println("key: "+k+"; value: "+v));
```

9. TreeMap 中的排序

   ```java
   /**
    * 按照key排序: 键已经实现了Comparator接口
    */
   Comparator<String> comparator = new Comparator<String>() {
       @Override
       public int compare(String o1, String o2) {
           return o1.compareTo(o2);
       }
   };
   Map<String, Integer> map2 = new TreeMap<>(comparator);
   map2.putAll(map);

   /**
    * 案值排序:相对计较麻烦
    * 原理: 将 map 中的键值对放入 list 列表中, Collections.sort(List<T> list, Comparator<? super T> c), 之后装入map中
    */
   // 将 map 中的键值对放入 list 列表中
   List<Map.Entry<String, Integer>> entriesList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
   Collections.sort(entriesList,new Comparator<Map.Entry<String, Integer>>() {
       @Override
       public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
           return o1.getValue().compareTo(o2.getValue());
       }
   });

   Map<String, Integer> map3 = new LinkedHashMap<>(); //LinkedHashMap是有序的

   Iterator<Map.Entry<String, Integer>>iterator = entriesList.iterator();
   Entry<String, Integer>entry=null;
   while(iterator.hasNext()){
      entry=iterator.next();
       map3.put(entry.getKey(),entry.getValue());
   } // over

   //这个方法也是可以的
   for(Map.Entry<String, Integer>ma:entriesList){
       map3.put(ma.getKey(), ma.getValue());
   }
   ```

---

### HashMap Source

1. HashMap = array + Linked list[8] + Red and black binary tree
2. HashMap.get(K k): The time complexity of the value is O(1):
3. HashMap.put(K k, V v)

   - source

   ```java
   // initialCapacity

   // loadFactor: 0.75

   // threshold = initialCapacity * loadFactor

   // Create a regular (non-tree) node
   Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
       return new Node<>(hash, key, value, next);
   }

   static final int hash(Object key) {
       int h;
       return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
   }

   // hash put
   final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                  boolean evict) {
       Node<K,V>[] tab; Node<K,V> p; int n, i;
       if ((tab = table) == null || (n = tab.length) == 0)
           n = (tab = resize()).length;
       if ((p = tab[i = (n - 1) & hash]) == null) // (n - 1): if no minus 1, index will be max or min
           tab[i] = newNode(hash, key, value, null);
       else {
           Node<K,V> e; K k;
           if (p.hash == hash &&
               ((k = p.key) == key || (key != null && key.equals(k))))
               e = p;
           else if (p instanceof TreeNode)
               e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
           else {
               for (int binCount = 0; ; ++binCount) {
                   if ((e = p.next) == null) {
                       p.next = newNode(hash, key, value, null);
                       if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                           treeifyBin(tab, hash);
                       break;
                   }
                   if (e.hash == hash &&
                       ((k = e.key) == key || (key != null && key.equals(k))))
                       break;
                   p = e;
               }
           }
           if (e != null) { // existing mapping for key
               V oldValue = e.value;
               if (!onlyIfAbsent || oldValue == null)
                   e.value = value;
               afterNodeAccess(e);
               return oldValue;
           }
       }

       // modCount: record HashMap modify times, used in put()/get()/remove()/Iterator() etc.
       // HashMap is not thread safe. In the iteration, modCount is assigned to the expectedModCount property of the iterator and then iterated.
       // If the HashMap is modified by another thread during the iteration, the value of modCount will change, then throw ConcurrentModificationException
       ++modCount;
       if (++size > threshold)
           resize();
       afterNodeInsertion(evict);
       return null;
   }
   ```

   - sample

   ```java
   Map<String, String> map = new HashMap<>();
   // static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

   map.put("zack", "HH");  // put index 7
   System.out.println("zack hashCode: " + "zack".hashCode());  // 3730895  1110001110110111001111

   Integer a = 132564; // Integer hashCode is selt value
   System.out.println("132564 hashCode: " + a.hashCode());  // 132564

   /**
           h: key.hashCode()
            : 0000 0000 0011 1000 1110 1101 1100 1111
            : 0000 0000 0000 0000 0000 0000 0011 1000 1110 1101 1100 1111
    h >>> 16: 0000 0000 0000 0000 0000 0000 0011 1000
       hash: h ^ (h >>> 16)
           : 0000 0000 0011 1000 1110 1101 1111 0111
       n - 1: 15
           : 0000 0000 0000 0000 0000 0000 0000 1111
       index: (n - 1) & hash
           0000 0000 0000 0000 0000 0000 0000 0111  // 7
   */
   ```

## JDK

### JDK 1.7

- 核心: 数组[默认 16 个元素] + 链表
- 具体实现:
  通过计算这个值的 `hashCode`, 之后运算为数组的索引, 若该索引值无内容, 则直接填入; 否则, 使用 `equals` 运算, 相等就取代; 不等时就生成链表放入`头结点`. 当哈希因子大于 0.75 时, 扩容数组之后对所有的元素重新运算并放入新的数组, 以此减少 `哈希碰撞`.
- 哈希因子: 默认是 0.75
- 减少哈希碰撞: `重写 hashCode 和 equals 方法`

### JDK 1.8

- 核心: 数组[默认 16 个元素] + 链表 + 红黑树
- 具体实现:
  在 JDK 1.7 的基础上, 把新加元素添加到链表的`尾节点`, 当哈希碰撞时链表内元素大于 `8` 且总元素大于 `64` 时, 就会将链表转换为 `红黑树`.
- JDK 1.8 该插入在链表的 `头结点` 为 `尾结点` 是因为在 JDK 1.7 多线程时会出现环导致的死循环.
- 红黑树的优点:
  - 增删改查的效率会提高
  - 数组扩容时, 不需要将 hashCode 重新运算为 数组的索引: `要么在原先的index上, 要么在 index + old_length 上`

## ConcurrentHashMap

### JDK 1.7

- 核心: 锁分段机制, 一共 16 段, 每段里有一个链表: 16 元素
- ConcurrentLevel[默认的并发级别]: 16

### JDK 1.8

- 核心: 去除段机制, 采用 `CAS` 算法

## 底层内存结构

### JDK 1.7

- 栈:
- 堆: 垃圾的回收区
- 方法区(堆的永久区[PremGen]): 存放一些类信息、核心类库. `几乎不会被 GC 回收`

### JDK 1.8

- 栈:
- 堆: 垃圾的回收区
- 元空间[MetaSpace]: 直接使用电脑的物理内存, 堆的永久区被取消
