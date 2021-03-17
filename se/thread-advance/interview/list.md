## List 与线程

1. 证明线程不安全: ConcurrentModificationException

   ```java
   // 同时读写一个 List 会出现 ConcurrentModificationException 异常
   // 并发修改导致
   public static void threadSafe() {
       ArrayList<String> unsafeList = new ArrayList<>();
       IntStream.rangeClosed(1, 1000).forEach(
               i -> new Thread(() -> {
                               String uuid = UUID.fastUUID().toString();
                               unsafeList.add(uuid);
                               log.info("{}", unsafeList);
                           }, "AAA" + i).start());
   }
   ```

2. ArrayList 是线程不安全的

   - 多线程操作 {@link ArrayList } 会出现 {@link java.util.ConcurrentModificationException}
   - 线程不安全: add 方法没有加锁
   - solution[3]

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
        private E get(Object[] a, int index) {
            return (E) a[index];
        }

        // 写时赋值容器: 读写分离思想
        //   1. 往一个容器 Object[] 追加元素时, 不直接往当前元素追加,
        //   2. 而是先将当前容器的元素进行 copy 到新的容器中 Object [] new, 之后再新的容器中加锁的追加元素
        //   3. 之后将原数组的地址指向新的数组
        //   4. 好处: 可以无锁的并发读
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

1. 线程安全之 synchronizedSet

   ```java
   Set<String> hashSet = Collections.synchronizedSet(new HashSet<>());
   ```

2. 线程安全之 CopyOnWriteArraySet

   ```java
   private final CopyOnWriteArraySet<E> al;

   public CopyOnWriteArraySet() {
       al = new CopyOnWriteArraySet<E>();
   }
   public boolean add(E e) {
       return al.addIfAbsent(e);
   }
   ```

## HashMap

1. 线程安全之 synchronizedMap

   ```java
   Map<String, String> map = Collections.synchronizedMap(new HashMap<String, String>());
   ```

2. 线程安全之 ConcurrentHashMap

   ```java
   public V put(K key, V value) {
       return putVal(key, value, false); //  synchronized
   }
   ```
