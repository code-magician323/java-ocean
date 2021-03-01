## question

1. volatile

   - 是什么, 特性[非原子性的替代方案]
   - jmm: 是什么 + 证明
   - 指令重排: 原则 + 举例
   - 内存栅栏
   - DCL + volatile 的多线程单例

2. cas

   - 是什么: 本质+作用
   - 举例 AtomicInteger[cas+spin]
   - unsafe + spin
   - vs synchronized
   - 缺点: 一个值 + cpu
   - aba

3. arraylist 线程不安全

   - 证明 + 原因
   - 解决: Vecter/Collections#synchronizedList/CopyOnWriteArrayList
   - CopyOnWriteArrayList 原理

4. java 中锁的理解

   - 公平锁/非公平锁: 定义 + 影响 + 典型实现 + 过程
   - 可重入锁[递归锁]: 定义 3 + 影响 + 典型实现 + 过程证明
   - 独占锁[写锁]/共享锁[写锁]: 定义 + 影响 + 典型实现
   - 自旋锁: 定义 + 影响 + 典型实现 + 自定义自旋锁
   - 死锁: 定义 + 原因 + 实现 + 证明

5. CountDownLatch/CyclicBarrier/Semaphore
6. 阻塞队列知道

   - 定义
   - 原因
   - api
   - 种类分析: `7-3`
   - 生产者消费者模式: ABAB 的三种实现
   - 多个线程无序操作资源: 3 seller sale 30 tickets
   - 多线程间的顺序调用 ABCABC

7. Synchronized 和 Lock 的区别: 5

   - 构成来源
   - 中断
   - 使用
   - 锁绑定条件
   - 公平

8. sleep 与 wait 的区别: 4

   - 来源
   - 锁
   - 唤醒

9. 创建线程的方法与区别
10. 线程池的理解

    - 使用线程池的好处
    - Executors 中的常见方法
    - ThreadPoolExecutor

      1. flow,
      2. 7 参数
      3. 线程池的拒绝策略
      4. CompletetableFuture

11. 合理配置线程池

    - IO 密集: 2
    - cpu 密集: 1
