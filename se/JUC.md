- [Thread](#thread)
- [thead and process](#thead-and-process)
- [quick start](#quick-start)
- [8 lock](#8-lock)
- [read write lock](#read-write-lock)
- [diff between lock and synchronized](#diff-between-lock-and-synchronized)
- [diff between Callable and Runnable](#diff-between-callable-and-runnable)
  - [FutureTask/Callable](#futuretaskcallable)
- [CountDownLatch](#countdownlatch)
- [CyclicBarrier](#cyclicbarrier)
- [diff between CountDownLatch and CyclicBarrier](#diff-between-countdownlatch-and-cyclicbarrier)
- [Semaphore](#semaphore)
- [Executors](#executors)
- [not thread safe](#not-thread-safe)

## [Thread](./Thread.md)

## thead and process

1. definition

2. muti thread state: thread.start() will not start immedately

   - NEW
   - RUNNABLE
   - WAITING: alwys
   - TIMED_WAITING: Outdated
   - TERMINATED

3. wait and sleep

   - wait: will surrend control, then will preempt cpu with other processes
   - sleep: will donot hand over control, this process will still work imedately when sleep time is over

4. parallel and concurrency
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
       public static void main(String[] args) {
           Ticket tickets = new Ticket();

           new Thread(() -> {for (int i = 1; i < 400; i++) tickets.sale();}, "seller01").start();
           new Thread(() -> {for (int i = 1; i < 400; i++) tickets.sale();}, "seller02").start();
           new Thread(() -> {for (int i = 1; i < 400; i++) tickets.sale();}, "seller03").start();
           new Thread(() -> {for (int i = 1; i < 400; i++) tickets.sale();}, "seller04").start();
       }
   }
   // resources = instance var + instance method
   class Ticket {
       private static final Logger LOG = LoggerFactory.getLogger(Ticket.class);
       private int number = 300;
       private Lock lock = new ReentrantLock();

       public synchronized void sale() {
           lock.lock();
           LOG.info( Thread.currentThread().getName() + " sale ticket number: " + number-- + " ," + number + " tickets left.");
           lock.unlock();
       }
   }
   ```

   - Two or many threads alternately modify a variable

   ```java
   public class NotifyWait {
       public static void main(String[] args) {
           ShareDataVersion data = new ShareDataVersion();
           new Thread(() -> { for (int i = 0; i < 500; i++) data.increase(); }, "A").start();
           new Thread(() -> { for (int i = 0; i < 500; i++)  data.decrease(); }, "B").start();
       }
   }

   class ShareDataVersion {
       private int number = 0;
       private Lock lock = new ReentrantLock();
       private Condition condition = lock.newCondition();
       private static final Logger LOG = LoggerFactory.getLogger(ShareDataVersion.class);

       public synchronized void increase() {
               // 2.1 judge
               while (number != 0) // if (number != 0) {
                   this.wait();
               // 2.2 work
               ++number;
               LOG.info(Thread.currentThread().getName() + " increase shareData finished, number: " + number);
               // 2.3 notify
               this.notifyAll();
       }

       public void decrease() {
           lock.lock();
           while (number != 1)
               this.wait();
           --number;
           LOG.info(Thread.currentThread().getName() + " decrease shareData finished, number: " + number);
           condition.signalAll();
           lock.unlock();
       }
   }
   ```

   - many thread sequence execute

   ```java
   public class ThreadOrderAccess {
       public static void main(String[] args) {
           ShareResource data = new ShareResource();
           new Thread(() -> {for (int i = 1; i <= 10; i++) data.executeA(i); }, "A").start();
           new Thread(() -> {for (int i = 1; i <= 10; i++) data.executeB(i); }, "B").start();
           new Thread(() -> {for (int i = 1; i <= 10; i++) data.executeC(i); }, "C").start();
       }
   }

   class ShareResource {
       private static final Logger LOG = LoggerFactory.getLogger(ShareResource.class);
       private int flag = 1; // flag: A-1; B-2; C-3;
       private Lock lock = new ReentrantLock();
       // like key of lock
       private Condition conditionA = lock.newCondition();
       private Condition conditionB = lock.newCondition();
       private Condition conditionC = lock.newCondition();

       public void executeA(int loopTimes) {
           lock.lock();
           while ( flag != 1)
               conditionA.await();

           LOG.info(Thread.currentThread().getName() + " execute " + loopTimes +" times");

           flag = 2;
           conditionB.signal();
           lock.unlock();
       }

       public void executeB(int loopTimes) {
           lock.lock();
           while ( flag != 2)
               conditionB.await();

           LOG.info(Thread.currentThread().getName() + " execute " + loopTimes +" times");
           flag = 3;
           conditionC.signal();
           lock.unlock();
       }

       public void executeC(int loopTimes) {
           lock.lock();
           while ( flag != 3)
               conditionC.await();

           LOG.info(Thread.currentThread().getName() + " execute " + loopTimes +" times");
           flag = 1;
           conditionA.signal();
           lock.unlock();
       }
   }
   ```

   - thread condition: reduce, threads call sequence[others thead finished work, then execute specify thread]

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

   - thread condition: plus, when all thread reached barrier, can execute specify thread

   ```java
   public class CyclicBarrierDemo {

       private static final int NUMBER = 10;
       private static final Logger LOG = LoggerFactory.getLogger(CyclicBarrierDemo.class);

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

## 8 lock

1. lock type
   - 实例对象锁： 所得是当前的实例 this [产品个体]
   - 类锁： 锁定整个 class, 即所有实例 [产品工厂]
     > 产品个体 和 产品工厂 是两个独立的东西， 互不干扰
2. lock explain
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

## diff between Callable and Runnable

- code

```java
public class CallableDemo {
  private static final Logger LOG = LoggerFactory.getLogger(CallableDemo.class);

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    FutureTask<Integer> future = new FutureTask<>(() -> {
              TimeUnit.SECONDS.sleep(4);
              return 200;
            });

    // call() will just execute one time, unless use many new FutureTask<>(new Callable() {});
    new Thread(future, "Callable Thread1 implement").start();
    new Thread(future, "Callable Thread2 implement").start();

    LOG.info(Thread.currentThread().getName() + ": Main Thread execute");

    Integer integer = future.get();
    LOG.info("Callable Thread response: " + integer);
  }
}

class RThread implements Runnable {
  @Override
  public void run() {}
}

class CThread implements Callable<Integer> {
  @Override
  public Integer call() throws Exception {
    TimeUnit.SECONDS.sleep(4);
    return 200;
  }
}
```

1. 泛型
2. 返回值
3. 方法名不同
4. 异常抛出
5. 异步回调[FutureTask]

### FutureTask/Callable

1. one `new FutureTask<>(new CThread())`, no matter how many thread, Callable call() method will just execute one time.
2. FutureTask often used to calculate time consuming task
3. thread method should be in last, when execute thread method will block main thread.
4. use get() to get FutureTask result, if fisish calcutate, it will nerver recalculate and cannot be canceled
5. if call get() method, calculate donot finish, the thread will be blocked until finish calculation

## CountDownLatch

1. let some threads block until they are finished, then can execute specify thread
2. when call await() will block these threads
3. call countDown() will reduce 1, util CountDownLatch value is zero and then will unblock threads

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
2. 让一组线程达到一个屏障(同步点)时被阻塞， 直到最后一个达到屏障时， 屏障才会打开，所有的被阻塞的线程才会继续
3. await() 可以使线程进入屏障
4. code

   ```java
   public class CyclicBarrierDemo {

       private static final int NUMBER = 10;
       private static final Logger LOG = LoggerFactory.getLogger(CyclicBarrierDemo.class);

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

## Executors

1. the object got by new can use pool to get
2. code

   ```java
   private static void scheduledExecutorPool() {
       Future<Integer> result;
       int poolSize = 5;
       ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize); // specify number thread in pool
       ScheduledExecutorService singleExecutor = Executors.newSingleThreadScheduledExecutor(); // 1 thread in pool

       try {
           for (int i = 0; i < poolSize * 3; i++) {
               result = executor.schedule( () ->  return new Random().nextInt(10), 5, TimeUnit.SECONDS);
               LOG.info(Thread.currentThread().getName() + " result: " + result.get());
           }
       } finally {
           executor.shutdown();
       }
   }

   private static void executorPool() {
       Future<Integer> result;
       int poolSize = 5;
       ExecutorService executor = Executors.newFixedThreadPool(poolSize); // specify number thread in pool
       ExecutorService singleExecutor = Executors.newSingleThreadExecutor(); // 1 thread in pool
       ExecutorService nExecutor = Executors.newCachedThreadPool(); // N thread in pool

       try {
           for (int i = 0; i < poolSize * 3; i++) {
               result = executor.submit(() -> return new Random().nextInt(10));
               LOG.info(Thread.currentThread().getName() + " result: " + result.get());
           }
       } finally {
           executor.shutdown();
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
