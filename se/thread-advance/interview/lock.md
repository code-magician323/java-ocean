## java lock: `volatile + lock/cas`

### 公平锁/公平锁

1. 概念

   - 公平锁: 指多个线程按照申请锁的顺序获取锁, 类似与排队打饭[FIFO]
   - 非公平锁: 指多个线程获取锁的顺序不一定时申请锁的顺序, 通过竞争获取锁: 可能优先级反转,或者饥饿

2. `ReentrantLock[默认]/synchronized 都是非公平锁`

   ```java
   public ReentrantLock() {
       sync = new NonfairSync();
   }
   ```

3. 区别

   - 公平锁在并发的环境中每个线程在获取锁时会先查看此锁维护的等待队列, 如果为空, 则当前线程时等待队列的第一个, 就占有锁, 否则会加入等待队列 FIFO
   - 非公平锁会直接竞争尝试占有锁, 如果失败则会采用类似公平锁那种方式
   - 非公平锁优点是吞吐量大, 公平锁是有序的 FIFO

### 可重入锁[递归锁]

1. 概念

   - 指同一线程外层函数获取锁之后, 内层递归函数荣可以获取被该锁锁住的代码[即使内部还有锁]
   - 同一线程在外层获取所有的之后, 在进入到内层方法会自动获取锁
   - `线程可以进入任何一个它已经拥有的锁所同步着的代码块`: **`同步方法可以进入内部调用的同步方法`**
   - 好处: 避免死锁

2. `ReentrantLock/synchronized 都是可重入锁`

   ```java
   private static synchronized void get() {
       log.info("thread: {} synchronized -- get", Thread.currentThread().getName());
       set();
   }
   private static synchronized void set() {
       log.info("thread: {} synchronized -- set", Thread.currentThread().getName());
   }

   private static void m0() {
       try {
           lock.lock();
           lock.lock();
           log.info("thread: {} reentrant-lock -- m0", Thread.currentThread().getName());
           m1();
       } finally {
           lock.unlock();
           log.info("thread: {} reentrant-unlock -- m0", Thread.currentThread().getName());
           lock.unlock();
       }
   }
   private static void m1() {
       try {
           lock.lock();
           log.info("thread: {} reentrant-lock -- m1", Thread.currentThread().getName());
       } finally {
           lock.unlock();
           log.info("thread: {} reentrant-unlock -- m1", Thread.currentThread().getName());
       }
   }
   ```

### 独占锁/共享锁: 读写锁

1. 概念

   - 独占锁: 该锁一次只能被一个线程持有, 保证数据一致安全`[读写, 写写, 写读都是互斥的]`
   - 共享锁: 该锁一次只能被多个线程持有, 保证并发读的高效

2. `ReentrantLock/synchronized 都是独占锁`; `ReentrantReadWriteLock的读锁时共享锁, 写锁则是独占锁`

   - 写 + 读: 等待写锁释放
   - 写 + 写: 阻塞写
   - 读 + 写: 等待写锁释放
   - 读 + 读: 无锁, 只会记录所有的读锁, 都能加锁成功

3. 物理: 红蜘蛛, 机场显示屏
4. 为什么要有读写锁: `读写如果都加锁的话[只有一个线程操作], 数据一致性可以保证但是并发性下降`

   ```java
   // 最终的打印结果与 for 的顺序十分相关
   @Slf4j
   public class RWLock {

       public static void main(String[] args) throws InterruptedException {

           MyCache cache = new MyCache();

           for (int i = 0; i < 9; i++) {
              int finalI = i;
              new Thread(() -> cache.setSafeV2("a" + finalI, finalI), "AAA" + i).start();
              // new Thread(() -> cache.getSafeV2("a" + finalI), "BBB" + i).start();
           }

           for (int i = 0; i < 9; i++) {
              int finalI = i;
              new Thread(() -> cache.getSafeV2("a" + finalI), "BBB" + i).start();
           }
       }
   }

   @Slf4j
   @Getter
   class MyCache {
       private volatile Map<String, Object> cache = new HashMap<>();
       private Lock lock = new ReentrantLock();
       private ReadWriteLock rwLock = new ReentrantReadWriteLock();

       /**
           * 这里的写操作会出现被打断的情况: 线程不安全
           *
           * @param key
           * @param value
           * @see cn.edu.ntu.javase.interview.list.UnsafeHashMap
           */
       @SneakyThrows
       public void set(String key, Object value) {
           log.info("thread: {} is writing key {}", Thread.currentThread().getName(), key);
           TimeUnit.MICROSECONDS.sleep(300);
           cache.put(key, value);
           log.info("thread: {} is write done", Thread.currentThread().getName());
       }

       @SneakyThrows
       public Object get(String key) {
           log.info("thread: {} is read key {}", Thread.currentThread().getName(), key);
           TimeUnit.MICROSECONDS.sleep(300);
           Object o = cache.get(key);
           log.info("thread: {} is read done {}", Thread.currentThread().getName(), o);
           return o;
       }

       /**
           * 写操作是线程安全的, 且只保证写操作的线程安全.<br>
           * 如果调用 set 非线程安全操作, 则会在写操作之间读操作的log<br>
           *
           * @param key
           * @param value
           */
       @SneakyThrows
       public void setSafe(String key, Object value) {
           lock.lock();
           try {
              log.info("thread: {} is writing key {}", Thread.currentThread().getName(), key);
              TimeUnit.MICROSECONDS.sleep(300);
              cache.put(key, value);
              log.info("thread: {} is write done", Thread.currentThread().getName());
           } finally {
             lock.unlock();
           }
       }

       @SneakyThrows
       public Object getSafe(String key) {
           lock.lock();
           try {
              log.info("thread: {} is read key {}", Thread.currentThread().getName(), key);
              TimeUnit.MICROSECONDS.sleep(300);
              Object o = cache.get(key);
              log.info("thread: {} is read done {}", Thread.currentThread().getName(), o);
              return o;
           } finally {
              lock.unlock();
           }
       }

       /**
           * 写锁是独占锁: 原子 + 独占
           *
           * @param key
           * @param value
           */
       @SneakyThrows
       public void setSafeV2(String key, Object value) {
           rwLock.writeLock().lock();
           try {
              log.info("thread: {} is writing key {}", Thread.currentThread().getName(), key);
              TimeUnit.MICROSECONDS.sleep(300);
              cache.put(key, value);
              log.info("thread: {} is write done", Thread.currentThread().getName());
           } finally {
              rwLock.writeLock().unlock();
           }
       }

       /**
           * 读锁是共享锁
           *
           * @param key
           * @return
           */
       @SneakyThrows
       public Object getSafeV2(String key) {
           rwLock.readLock().lock();
           try {
              log.info("thread: {} is read key {}", Thread.currentThread().getName(), key);
              TimeUnit.MICROSECONDS.sleep(300);
              Object o = cache.get(key);
              log.info("thread: {} is read done {}", Thread.currentThread().getName(), o);
              return o;
           } finally {
              rwLock.readLock().unlock();
           }
       }
   }
   ```

### 自旋锁

1. 概念

   - 指尝试获取锁的线程不会立即阻塞, 而是采用循环的方式去尝试获取锁
   - 好处: 较少的上下文切换的开销, 缺点是长时间自选会消耗 CPU
   - AtomicInteger 这些类底层都是 cas+自旋锁

2. AtomicInteger

   ```java
   public final int getAndAddInt(Object var1, long var2, int var4) {
       int var5;
       do {
           // 获取 var1[AtomicInteger对象] 对象中 var2[valueOffset] 地址的值: 从主内存中获取值
           var5 = this.getIntVolatile(var1, var2);
       } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4)); // compareAndSwapInt 再次获取如果还是 var5, 就修改: 这一步是 os 的并发原语, 具有原子性

       return var5;
   }
   ```

3. 手写自旋锁: `cas+while-loop`

   ```java
   public class SpinLock {

       /**
       * 此时没有被调用所以之内存中还是 null<br>
       * 基本数据类型, new 出来时是默认值,<br>
       * 引用类型则是 null
       */
       // 也可以使用 int 定义0 为锁空闲, 1 表示锁被占有: 比如源码中的 state
       private AtomicReference<Thread> reference = new AtomicReference<>();

       private void Lock() {
           log.info("thread: {} try get lock", Thread.currentThread().getName());

           // 成功比较并设置则停止循环
           while (!reference.compareAndSet(null, Thread.currentThread())) {
               // logic
               log.info("thread: {} do-while", Thread.currentThread().getName());
           }
       }

       private void UnLock() {
           reference.compareAndSet(Thread.currentThread(), null);
           log.info("thread: {} unlock", Thread.currentThread().getName());
       }

       @SneakyThrows
       public static void main(String[] args) {
           SpinLock lock = new SpinLock();

           new Thread(() -> {
                       try {
                           lock.Lock();
                           TimeUnit.SECONDS.sleep(5);
                       } finally {
                           lock.UnLock();
                       }}, "AAA").start();

           // 保证 AAA 先获取到锁
           TimeUnit.SECONDS.sleep(1);

           new Thread(() -> {
                   try {
                       lock.Lock();
                       TimeUnit.SECONDS.sleep(1);
                   } finally {
                       lock.UnLock();
                   }}, "BBB").start();
       }
   }
   ```
