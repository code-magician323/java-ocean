## List 与线程

1. ArrayList

   - 底层是空 Object 的数组: `transient Object[] elementData;`
   - add 时会初始化容量为 10 的数组并添加元素: `DEFAULT_CAPACITY = 10;`
   - 扩容时: `elementData = Arrays.copyOf(elementData, newCapacity);`

2. ArrayList 是线程不安全的

   - 多线程操作 {@link ArrayList } 会出现 {@link java.util.ConcurrentModificationException}
   - 线程不安全: add 方法没有加锁
   - solution

     1. Vector: `add synchronized`

        ```java
        public synchronized boolean add(E e) {
            modCount++;
            ensureCapacityHelper(elementCount + 1);
            elementData[elementCount++] = e;
            return true;
        }
        ```

     2. Collections#synchronizedList(List): `synchronized (mutex) {list.add(index, element);}`

        ```java
        public void add(int index, E element) {
            synchronized (mutex) {list.add(index, element);}
        }
        ```

     3. CopyOnWriteArrayList: `ReentrantLock + volatile` = `jmm`, `并发读`

        ```java
        public boolean add(E e) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                // 获取主内存中的数据
                Object[] elements = getArray();
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len + 1);
                newElements[len] = e;
                // 写回主内存, 并通知其他持有该变量的线程
                setArray(newElements);
                return true;
            } finally {
                lock.unlock();
            }
        }
        ```

## HashSet

1. HashSet 是线程不安全的
2. HashSet 底层时 HashMap

   ```java
   public HashSet() {
       map = new HashMap<>(); // this.loadFactor = 0.75;
   }

   // Dummy value to associate with an Object in the backing Map
   private static final Object PRESENT = new Object();
   public boolean add(E e) {
       return map.put(e, PRESENT)==null;
   }
   ```

3. 线程安全之 CopyOnWriteArraySet

   ```java
   private final CopyOnWriteArrayList<E> al;

   public CopyOnWriteArraySet() {
       al = new CopyOnWriteArrayList<E>();
   }
   public boolean add(E e) {
       return al.addIfAbsent(e);
   }
   ```

4. 线程安全之 synchronizedSet

   ```java
   Set<String> hashSet = Collections.synchronizedSet(new HashSet<>());
   ```

## HashMap

1. HashSet 是线程不安全的
2. 线程安全之 ConcurrentHashMap

   ```java
   public V put(K key, V value) {
       return putVal(key, value, false); //  synchronized
   }
   ```

3. 线程安全之 synchronizedMap

   ```java
   Map<String, String> map = Collections.synchronizedMap(new HashMap<String, String>());
   ```
