## CAS

1. 本质: `比较成功后交换[CompareAndSet]`, 否则不改变值

### 底层原理

1. CAS: `自旋锁` + `UnSafe`

   - Compare-And-Swap, 是`一条CPU并发原语[操作系统的]`
   - 判断内存某个位置的值是否时预期值, 如果是则更改, 原子的操作

   ```java
   public class AtomicInteger extends Number implements java.io.Serializable {
       private static final Unsafe unsafe = Unsafe.getUnsafe();
       private static final long valueOffset;

       static {
           try {
               valueOffset = unsafe.objectFieldOffset
                   (AtomicInteger.class.getDeclaredField("value"));
           } catch (Exception ex) { throw new Error(ex); }
       }

       private volatile int value;

       public final int getAndIncrement() {
           // this 表示当前对象, valueOffset 表示内存偏移量[引用地址]
           return unsafe.getAndAddInt(this, valueOffset, 1);
       }
   }

   public final class Unsafe {
       public native int getIntVolatile(Object var1, long var2);
       public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);

       public final int getAndAddInt(Object var1, long var2, int var4) {
           int var5;
           do {
               // 获取 var1[AtomicInteger对象] 对象中 var2[valueOffset] 地址的值: 从主内存中获取值
               var5 = this.getIntVolatile(var1, var2);
           } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4)); // compareAndSwapInt 再次获取如果还是 var5, 就修改: 这一步是 os 的并发原语, 具有原子性

           return var5;
       }
   }
   ```

2. 自旋锁
3. UnSafe

   - 是 CAS 的核心类[并发原语的体现], 由于 Java 方法无法直接访问底层系统, 需要通过 native 方法
   - UnSafe 相当于一个后门, 基于该类可以直接操作指定内存的数据
   - UnSafe 类存在于 `sun.misc`[rt.jar]
   - 其内部方法可以像 C 的指针一样直接操作内存
   - Unsafe 类中所有的方法都是 native 的, 本质都是直接调用操作系统底层资源执行任务的
   - 调用 Unsafe 类中的 CAS 方法, JVM 会帮我们实现出 CAS 汇编指令: 完全依赖硬件, 实现原子操作

4. 并发原语

   - 并发原语 时操作系统的语言
   - 有若干条指令组成, 用于实现指定功能
   - 原语的执行必须是连续的, 在执行过程中不能中断, 具有原子性

5. valueOffset: 表示变量值在内存中的偏移地址, UnSafe 可以根据内存偏移地址操作变量数据
6. value 使用 volatile 修饰保证了线程间的可见性
7. **`cas vs synchronized`**

   - synchronized 加锁同一时间内只有一个线程访问, 一致性得到保障, 但是并发性下降
   - cas 没有加锁, 反复比较直到更新完后, 保证一致性和并发性

8. cas 的缺点

   - 循环时间长开销大: 如果 CAS 失败就会一直尝试, 如果 CAS`长时间一直不成功`会给 CPU 带来很大的开销
   - 只能保证`一个`共享变量的原子操作: 一个共享变量时可以使用循环 CAS 保证原子性, 多个共享变量时循环 CAS 无法保证原子性[需要使用 Lock]
   - ABA 问题

9. ABA 问题

   - CAS 算法实现的一个重要前提需要取出内存中取出某个时刻的数据并与当下时刻进行比较替换, 那么在这个时间差内会导致数据变化
   - eg. t1 从主内存中 V 位置取出 A[后挂起], t2 也取出 A, t2 将值变成 B, t2 又将值变成 A; 此时 t1 进行 CAS 操作时发现内存中 V 位置是 A, 然后`t1操作成功`
   - soulution: 加修改版本号: `AtomicStampedReference`

### flow

- 假设线程 A 和线程 B 同时执行 getAndAddInt 操作[跑在不同的 CPU 上]

1. AtomicInteger 中的 value 的原始值时 3, 即主内存中的 AtomicInteger 中的 value 是 3, 根据 JMM 模型, 线程 A 和线程 B 各自持有一份值为 3 的副本在各自的各自空间
2. 线程 A 通过 getAndAddInt(var1, var2) 拿到 value 值为 3, 此时线程 A 挂起
3. 线程 B 通过 getAndAddInt(var1, var2) 拿到 value 值为 3, 线程 B 没有挂起, 执行了 compareAndSwapInt 方法, 比较主内存中的值也是 3, 则成功修改主内存中的值为 4, 线程 B 结束
4. 此时线程 A 恢复, 接着执行 compareAndSwapInt 方法进行比较发现获取的主内存的值为 4 不同于 var5[3], 说明该值被其他线程抢先异步修改了, 那么线程 A 本次修改失败, 只能重新读取重新来一遍*5*
5. 线程 A 重新获取 A 的值, 因为 value 是被 volatile 修饰的, 所以其他线程可见修改, 线程 A 继续执行 compareAndSwapInt 进行比较替换, 直到成功

### 原子引用: from ABA

1. 原子引用

   ```java
   public class AtomicReferenceTest {

   public static void main(String[] args) {
       User z3 = new User("z3", 15);
       User l4 = new User("l4", 25);

       AtomicReference<User> atomicReference = new AtomicReference<>();
       atomicReference.set(z3);

       boolean success = atomicReference.compareAndSet(z3, l4);

       log.info("update z3 to l4 is success: {}, now user: {}", success, atomicReference.get());
   }
   }

   @Data
   @ToString
   @NoArgsConstructor
   @AllArgsConstructor
   class User {
   String name;
   int age;
   }
   ```

2. ABA 问题

   ```java
   public class ABATest {
       static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
       public static void main(String[] args) {
           new Thread(
                   () -> {
                       atomicReference.compareAndSet(100, 101);
                       atomicReference.compareAndSet(101, 100);
                   },
                   "AAA").start();

           new Thread(
                   () -> {
                       // 保证AAA线程完成一次ABA操作
                       TimeUnit.SECONDS.sleep(1);
                       atomicReference.compareAndSet(100, 102);
                   },
                   "BBB").start();

           // 等待 AAA, BBB 线程执行结束
           while (Thread.activeCount() > 2) {
               Thread.yield();
           }
           log.info("main thread atomicReference value: {}", atomicReference.get());
       }
   }
   ```
