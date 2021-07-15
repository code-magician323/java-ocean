## [Thread](./Thread.md)

## core concept

1. wait and sleep

   - wait: will surrend control, then will preempt cpu with other processes
   - sleep: will donot hand over control, this process will still work imedately when sleep time is over

2. parallel and concurrency
   - concurrency: many process compare for one kind resource
   - parallel: one process do many things at same time

## quick start

1. thread -- operation -- resources
   - resources = instance var + instance method
2. high cohesion[resource property] -- low coupling
   - `Put the operation of the resource class on the resource itself to prevent contention`
3. judge -- work -- notify
4. synchronized method likes table lock; synchronized block code likes line lock
5. muti thread should use `while` as judeg condition other than `if`

   - `if` case suit for two status, if one wait, another will get access to execute
   - `while` will always to judge each time

6. demo code

   - 3 seller sale 30 tickets with juc

     ```java
     public class SaleTickets {
         private static final int NUMBER = 500;

         public static void main(String[] args) {
             Ticket tickets = new Ticket();
             tickets.setNumber(NUMBER);
             new Thread(() -> IntStream.rangeClosed(1, NUMBER * 2).forEach(i -> tickets.sale()), "seller01").start();
             new Thread(() -> IntStream.rangeClosed(1, NUMBER).forEach(i -> tickets.sale()), "seller02").start();
         }
     }

     @Slf4j
     @Data
     class Ticket {
         private volatile int number = 3000;
         private Lock lock = new ReentrantLock();

         public void sale() {
             lock.lock();
             try {
                     while (number > 0) {
                         log.info("ale number {} ticket and {} left.", number--, number);
                     }
             } finally {
                 lock.unlock();
             }
         }
     }
     ```

   - Two or many threads alternately modify a variable: ABAB[线程安全的生产者消费者模式]

     ```java
     /**
     * Two threads alternately modify a variable: A-B-A-B
     *
     * <pre>
     *     1. synchronized 和 notify 是一组
     *     2. lock 和 condition 是一组
     *     3. synchronized 和 lock 不能混合使用, 否则会有线程安全问题
     * </pre>
     *
     * @author zack
     * @create 2019-12-04 21:46
     */
     public class NotifyWait {
         public static void main(String[] args) {
             ABABWithSynchronized withSynchronized = new ABABWithSynchronized();
             IntStream.rangeClosed(1, 5).forEach(i -> new Thread(() -> withSynchronized.increase()).start());
             IntStream.rangeClosed(1, 5).forEach(i -> new Thread(() -> withSynchronized.decrease()).start());

             ABABWithLock withLock = new ABABWithLock();
             IntStream.rangeClosed(1, 5).forEach(i -> new Thread(() -> withLock.increase()).start());
             IntStream.rangeClosed(1, 5).forEach(i -> new Thread(() -> withLock.decrease()).start());
         }
     }

     @Slf4j
     class ABABWithSynchronized {
         private int number = 0;

         /** use if to do judge will lead to VirtualWake */
         @SneakyThrows
         public synchronized void increase() {
             while (number != 0) {
                 this.wait();
             }
             ++number;
             log.info("increase number: {}", number);
             this.notifyAll();
         }

         @SneakyThrows
         public synchronized void decrease() {
             while (number != 1) {
                 this.wait();
             }
             --number;
             log.info("decrease number: {}", number);
             this.notifyAll();
         }
     }

     @Slf4j
     class ABABWithLock {
         private int number = 0;
         private Lock lock = new ReentrantLock();
         private Condition condition = lock.newCondition();

         @SneakyThrows
         public void increase() {
             lock.lock();
             try {
                 while (number != 0) {
                     condition.await();
                 }
                 ++number;
                 log.info("increase number: {}", number);
                 condition.signalAll();
             } finally {
                 lock.unlock();
             }
         }

         @SneakyThrows
         public void decrease() {
             lock.lock();
             try {
                 while (number != 1) {
                     condition.await();
                 }
                 --number;
                 log.info("decrease number: {}", number);
                 condition.signalAll();
             } finally {
                 lock.unlock();
             }
         }
     }
     ```

   - many thread sequence execute: ABCABC

     ```java
         public class Abcabc {
             public static void main(String[] args) {
                 ShareResource data = new ShareResource();
                 new Thread(() -> IntStream.rangeClosed(0, 9).forEach(i -> data.executeA(i)), "AAA").start();
                 new Thread(() -> IntStream.rangeClosed(0, 9).forEach(i -> data.executeB(i)), "BBB").start();
                 new Thread(() -> IntStream.rangeClosed(0, 9).forEach(i -> data.executeC(i)), "CCC").start();
             }
         }

         @Slf4j
         class ShareResource {
             /** flag: A-1; B-2; C-3; */
             private int flag = 1;
             private Lock lock = new ReentrantLock();
             /** like key of lock */
             private Condition conditionA = lock.newCondition();
             private Condition conditionB = lock.newCondition();
             private Condition conditionC = lock.newCondition();

             @SneakyThrows
             public void executeA(int loopTimes) {
                 lock.lock();
                 try {
                     // judge
                     while (flag != 1) {
                         log.info("  executeA ");
                         conditionA.await();
                     }
                     // work
                     log.info("executeA " + loopTimes + " times");
                     // notice
                     flag = 2;
                     conditionB.signal();
                 } finally {
                     lock.unlock();
                 }
             }

             @SneakyThrows
             public void executeB(int loopTimes) {
                 lock.lock();
                 try {
                     // judge
                     while (flag != 2) {
                         log.info("  executeB ");
                         conditionB.await();
                     }
                     // work
                     log.info(" executeB " + loopTimes + " times");

                     // notice
                     flag = 3;
                     conditionC.signal();
                 } finally {
                     lock.unlock();
                 }
             }

             @SneakyThrows
             public void executeC(int loopTimes) {
                 lock.lock();
                 try {
                     // judge
                     while (flag != 3) {
                         log.info("  executeC ");
                         conditionC.await();
                     }
                     // work
                     log.info("executeC " + loopTimes + " times");
                     // notice
                     flag = 1;
                     conditionA.signal();
                 } finally {
                     lock.unlock();
                 }
             }
         }

         /**
         * 这里如果值使用一个 Condition, 也可以实现
         *
         * <pre>
         *     1. 如果使用 signal 会导致死锁问题:
         *        - 一共有三个线程, 若此时 flag = 1,
         *        - B 抢到执行权则 B await; C 抢到 C await
         *        - 之后 A 抢到 A 执行, flag = 2 可能唤醒的是 C[都是 一个 condition 的await], C wait,
         *        - A 又抢到, 此时所有人都 await, 所以死锁了
         *    2. 所以要是有 signalAll, 则三个线程都唤醒, 让他们去抢
         *        - flag = 1, C 抢到 await, B 抢到 await, 最后 A 抢到执行, 执行后唤醒所有的线程ABC
         *        - 继续步骤1
         * </pre>
         */
         @Deprecated
         @Slf4j
         class ShareResourceWith1Condition {
             /** flag: A-1; B-2; C-3; */
             private int flag = 1;

             private Lock lock = new ReentrantLock();
             private Condition conditionA = lock.newCondition();

             @SneakyThrows
             public void executeA(int loopTimes) {
                 lock.lock();
                 try {
                     while (flag != 1) {
                         log.info("  executeA ");
                         conditionA.await();
                     }
                     log.info("executeA " + loopTimes + " times");
                     flag = 2;
                     conditionA.signalAll();
                 } finally {
                     lock.unlock();
                 }
             }

             @SneakyThrows
             public void executeB(int loopTimes) {
                 lock.lock();
                 try {
                     while (flag != 2) {
                         log.info("  executeB ");
                         conditionA.await();
                     }
                     log.info(" executeB " + loopTimes + " times");
                     flag = 3;
                     conditionA.signalAll();
                 } finally {
                     lock.unlock();
                 }
             }

             @SneakyThrows
             public void executeC(int loopTimes) {
                 lock.lock();
                 try {
                     while (flag != 3) {
                         log.info("  executeC ");
                         conditionA.await();
                     }
                     log.info("executeC " + loopTimes + " times");
                     flag = 1;
                     conditionA.signalAll();
                 } finally {
                     lock.unlock();
                 }
             }
         }
     ```

   - CountDownLatch
   - CyclicBarrier

## 8 lock

1. lock.lock() 一定要写在 try 外面, unlock() 写在 finally 里
2. lock type
   - 实例对象锁： 所得是当前的实例 this [产品个体]
   - 类锁： 锁定整个 class, 即所有实例 [产品工厂]
     > 产品个体 和 产品工厂 是两个独立的东西， 互不干扰
3. lock explain
   - 一个对象里面如果有多个 synchronized 方法， 在某一时刻， 只要一个线程去调用其中的一个 synchronized 方法， 其他线程都只能等待。
   - `[在某一时刻内， 只能有一个线程去访问这些 synchronized 的方法， **锁的是当前的对象**， 被锁定后， 其他线程都不能进入到当前对象的其他 synchronized 方法]`
   - 资源类内添加普通方法[非同步方法]， 普通方法与锁无关， 会直接执行
   - 两个资源类， 两个同步方法，则两个线程分别使用不同的资源类导致不是同一把锁，所以彼此互不相关
   - 两个静态同步方法， 无论资源个数， 都会按调用的顺序执行 `[static 所得是类加载器中的模板，即所有这个类的实例]`

## read write lock

1. 读锁可共享， 写锁必排他
2. code

   ```java
   public class ReadWriteLock {
       public static void main(String[] args) throws InterruptedException {
           CustomQueue customQueue = new CustomQueue();
           new Thread(() -> customQueue.writeObject("zack"), "WriteThread").start();
           // sleep to make write first, then read thread will not read null value
           TimeUnit.SECONDS.sleep(1);
           for (int i = 0; i < 100; i++)
               new Thread(() -> customQueue.readObject(), "ReadThread" + i).start();
       }
   }

   class CustomQueue {
       private static final Logger LOG = LoggerFactory.getLogger(CustomQueue.class);
       private Object object;
       private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

       public void writeObject(Object object) {
           readWriteLock.writeLock().lock();
           this.object = object;
           LOG.info(Thread.currentThread().getName() + " write "+ object.toString());
           readWriteLock.writeLock().unlock();
       }

       public void readObject() {
           readWriteLock.readLock().lock();
           LOG.info(Thread.currentThread().getName() + " read "+ this.object.toString());
           readWriteLock.readLock().unlock();
       }
   }
   ```

## diff between lock and synchronized

1. synchronized cannot guarantee sequence, all thread will have access to gain execution access
2. lock ca make sequence, can **`signal specify thread`**
3. synchronized 是重量级的锁: 线程阻塞 + 上下文切换 + 线程调度

## CountDownLatch

1. let some threads block until they are finished, then can execute specify thread
2. when call await() to block threads
3. call countDown() will reduce 1, util CountDownLatch value is zero and then will unblock `2` threads

4. code

   ```java
   public static void main(String[] args) {
       int count = 20;
       CountDownLatch cdl = new CountDownLatch(count);
       for (int i = 0; i < count; i++) {
       new Thread(
           () -> {
               LOG.info(Thread.currentThread().getName() + " leave room.");
               cdl.countDown();
           },
           String.valueOf(i)).start();
       }
       cdl.await();
       LOG.info("no person in room, can close door!");
   }
   ```

## CyclicBarrier

1. 可循环(Cyclic)的使用屏障(Barrier)
2. 让一组线程达到一个屏障(同步点)时被阻塞, 直到最后一个达到屏障时, 屏障才会打开，所有的被阻塞的线程才会继续
3. await() 可以使线程进入屏障
4. code

   ```java
   public class CyclicBarrierDemo {

       private static final int NUMBER = 10;

       public static void main(String[] args) {
           CyclicBarrier cb = new CyclicBarrier(NUMBER, () -> LOG.info("all things is ok, open barrier!"));

           for (int i = 0; i < NUMBER; i++) {
           final int times = i;
           new Thread(
               () -> {
                    LOG.info(Thread.currentThread().getName() + " on condition, and will await for open barrier, and now have " + times + " in wait.");
                    cb.await();
               },
               String.valueOf(i)).start();
           }
       }
   }
   ```

## diff between CountDownLatch and CyclicBarrier

1. CountDownLatch is reduce, `Everyone's gone to close door`
2. CyclicBarrier is plus, `Only when people get together will they meet`

## Semaphore

1. first in first get
2. leaved then others in
3. like `get access to park; Eat hot pot in order`
4. code

   ```java
   public class SemaphoreDemo {
       private static final int PARK_NUMBER = 5;
       private static final Logger LOG = LoggerFactory.getLogger(SemaphoreDemo.class);

       public static void main(String[] args) {
           // mockup park number
           Semaphore semaphore = new Semaphore(PARK_NUMBER);

           for (int i = 0; i < PARK_NUMBER * 2; i++) {
           new Thread(
               () -> {
                   semaphore.acquire();
                   LOG.info(Thread.currentThread().getName() + " get access to park");

                   TimeUnit.SECONDS.sleep(new Random().nextInt(10)); // park random second, then leave
                   LOG.info(Thread.currentThread().getName() + " leave park");
               },
               String.valueOf(i)).start();
           }
       }
   }
   ```

## not thread safe

1. List: just if read and write in same time will occur ConcurrentModificationException

   - CopyOnWrite 容器即写时赋值容器。 add(Element e) 时将当前容器进行 copy， 赋值出一个新的容器， 然后向新的容器内添加元素， 完成后将原来容器的引用指向新的容器
   - feture: 可以对 CopyOnWrite 容器并发的读， 且不需要加锁， 因为当前容器不会添加值； CopyOnWrite 容器也是一种读写分离的思想， 读数据和写数据在不同的容器进行
   - code

   ```java
   // not safe
   public static void testUnSafedList() throws InterruptedException {
       List<String> list = new ArrayList();
       for (int i =0; i< 30; i++) {
           new Thread(() -> {
               list.add(UUID.randomUUID().toString());
               System.out.println(list);  // write and read will occur ConcurrentModificationException
           }).start();
       }
       TimeUnit.SECONDS.sleep(5); // can use CountDownLatch to await add finished
       System.out.println(list);
   }

   // CopyOnWriteArrayList
   public static void testSafedList() {
       List<String> list = new CopyOnWriteArrayList<>();
       for (int i =0; i< 30; i++) {
           new Thread(() -> {
               list.add(UUID.randomUUID().toString());
               System.out.println(list);
           }).start();
       }
       System.out.println(list);
   }
   ```

2. Set: `CopyOnWriteArraySet`
3. Map: `ConcurrentHashMap`
