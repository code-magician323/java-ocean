## volatile

```java
public class VolatileData {
  volatile int number = 0;
  AtomicInteger atomicInteger = new AtomicInteger();

  public void addTo60() {
    this.number = 60;
  }

  /**
   * number 前有 volatile 关键字
   *
   * <pre>
   *     1. [证明原子性]: 50 个线程执行 plus 方法 1k 次, 理论上 number 需要是 5w
   *          - 在只有 volatile 修饰时, 线程1 写入主内存后通知线程2[此时被挂起], 所以没收到通知, 线程2[挂起态结束] 就也写入主内存
   *     2. volatile 是不保证原子性即线程不安全:
   *     3. 解决原子性问题:
   *        - 可以使用 synchronized
   *        - 使用 {@link java.util.concurrent.atomic.AtomicInteger}
   * </pre>
   *
   * <pre>
   *     1. number 数据是放入栈中的: 实例的8种基本类型都是在栈中的
   *     2. volatile 中一个线程正在向主内存中写数据, 其他线程会被挂起
   *     3. class
   *     public void plusOneForAtomicTest();
   *       Code:
   *        0: aload_0
   *        1: dup
   *        2: getfield      #2                  // Field number:I  // 从主内存中取
   *        5: iconst_1
   *        6: iadd                                                 // +1
   *        7: putfield      #2                  // Field number:I  // 放回主内存
   *       10: return
   * </pre>
   */
  public void plusOneForAtomicTest() {
    number++;
  }

  /** atomicInteger 前不需要 volatile, 可以保证原子性 */
  public void plusAtomicInteger() {
    atomicInteger.getAndIncrement(); // i++
  }
}
```

1. 是 java 虚拟机提的`轻量级`的`同步机制`: 乞丐版的 `synchronized`

   - 保证可见性

     1. vilatile 可以保证可见性: jmm 的可见性

     ```java
     public static void main(String[] args) {
         // 1. 资源类
         VolatileData data = new VolatileData();
         // 2. 第一个线程: 3秒之后修改为 60
         new Thread(
                 () -> {
                 log.info("thread: {} come in", Thread.currentThread().getName());

                 try {
                     TimeUnit.SECONDS.sleep(3);
                 } catch (InterruptedException e) {
                 }
                 log.info(
                     "thread: {} update number value to: {}",
                     Thread.currentThread().getName(),
                     data.number);

                 data.addTo60();
                 },
                 "AAA")
             .start();

         /**
         * volatile 的可见性
         *
         * <pre>
         *     1. number 前没有 volatile 关键字: 彼此线程不可见
         *          - 会一直 block: main 线程在最初的时候加载到自己的工作空间的是 0, AAA 线程修改对 main 线程是不可见的[即使AAA把数据写回了主内存]
         *     2. number 前有 volatile 关键字: 彼此线程不可见[主内存实现]
         *          - 则不会 block 在这里, main 可以拿到修改后的值
         * </pre>
         */
         while (data.number == 0) {
         // 只要不等于 0 则向下执行
         }

         log.info("thread: {} get number value is {}", Thread.currentThread().getName(), data.number);
     }
     ```

   - 不保证原子性: 非线程安全

     1. vilatile 不保证原子性: 会出现写丢失
     2. 解决方案: synchronized/AtomicInteger

     ```java
     public static void main(String[] args) {
         // 1. 资源类
         VolatileData data = new VolatileData();

         // 2. 50个线程执行 plus 方法 1k 次: 理论上 number 需要是 5w[证明原子性]
         for (int i = 0; i < 50; i++) {
         new Thread(
                 () -> {
                     for (int j = 0; j < 1000; j++) {
                     data.plusOneForAtomicTest();
                     data.plusAtomicInteger();
                     }
                 },
                 "A" + i)
             .start();
         }

         // 需要等待上面 50 个线程都执行完成后 在 main 线程中去 number
         while (Thread.activeCount() > 2) { // main + gc 两个线程
         Thread.yield(); // 放弃执行, 把当前线程重新置入抢 CPU 时间的队列
         }

         log.info(
             "thread: {} get number value is {}, atomic value: {}",
             Thread.currentThread().getName(),
             data.number,
             data.atomicInteger);
     }
     ```

   - 禁止指令重排: volatile 实现禁止指令重拍优化, 避免多线程下程序出现乱序执行的现象

     ```java
     public void sort() {
         int x = 10;  // 1
         int y = 20;  // 2
         x = x + 5;   // 3
         y = x * x;   // 4
     }
     // 指令重排之后可能是 1234, 2134, 1324
     ```

     ```java
     public class CommandResort {
         int a = 0;
         boolean flag = false;

         /**
         * 这里可能存在指令重排, 则在超多线程都在执行 m0, m2 时会出问题, 导致结果不唯一
         *
         * <pre>
         *      1. flag 在第一句: 就会导致线程执行 m2 时, 且在 a=1 执行之前执行, a=5
         *      2. flag 在第二句: 就会导致线程执行 m2 时,  a=6
         *      3. solution: 在 a 和 flag 之前都加上 volatile 指定涉及到这两个的都不循序指令重排, 则 a=6
         * </pre>
         */
         public void m0() {
             a = 1;
             flag = true;
         }

         public void m2() {
             if (flag) {
             a = a + 5;
             log.info("a: {}", a);
             }
         }
     }
     ```

2. 指令重排: 计算机在执行程序时为了提高性, 编译器和处理器常常会对指令做重排

   `源代码 == 编译器优化重排 == 指令并行的重排 == 内存系统重排 == 最终执行的指令`

   1. 单线程里确保最终执行结果和顺序执行结果一致
   2. 处理器在进行重排时必须考虑指令间的数据依赖性
   3. 多线程环境中线程交换执行, 由于编译器优化重排的存在, `两个线程中使用的同一变量`**能否保持一致性**是无法确定的, `结果无法预测`

3. 禁止指令重排

   - 内存屏障: 内存栅栏, 是一个 CPU 的指令, 具有以下 2 个特性和作用: `顺序执行`, `某些变量的内存可见性`, **强制刷出各种 CPU 缓存数据, 保证 CPU 上的线程都能读取到最新的数据**
   - 如果在指令间插入一条 Memory Barrier 则会告诉编译器和 CPU, 无论什么指令都不能和这条 MB 指令重排: 通过插入内存屏障禁止在内存屏障前后的指令重排优化

4. 是对 `JMM` 规定的一种实现, 但是不保证原子性

5. 单例模式

   - 非线程安全

     ```java
     public class Singleton {
       private static Singleton instance = null;

       // 可以使用 synchronized 可以解决: 性能不好
       public static synchronized Singleton getInstance() {
         if (instance == null) {
           instance = new Singleton();
         }

         return instance;
       }

       public static void main(String[] args) {
         IntStream.rangeClosed(0, 100)
             .forEach(x -> new Thread(() -> Singleton.getInstance(), "AAA" + x).start());
       }
     }
     ```

   - 线程安全: DCL[Double Check Lock]

     ```java
     private static volatile Singleton instance = null;
     /**
     * DCL: 双端检锁机制, 在加锁前后都检查<br>
     * 还是线程不安全的[未初始化的对象]: 指令重排导致的
     *
     * <pre>
     *     1. new Singleton()
     *         - 分配内存空间
     *         - 初始化对象
     *         - 设置 instance 指向刚分配的内存地址
     *     2. 由于指令重排的话可能是1-3-2, 此时返回的只是内存空间还没有初始化
     *     3. 在变量前加 volatile 禁止指令重排
     * </pre>
     *
     * @return
     */
     public static Singleton getInstanceV2() {
       if (instance == null) {
         synchronized (Singleton.class) {
           if (instance == null) {
             instance = new Singleton();
           }
         }
       }

       return instance;
     }
     ```
