## JMH

### introduce

1. 性能测试
2. JMH 只适合细粒度的方法测试, 并不适用于系统之间的链路测试
3. oracle

### quick start

1. dependency

   ```xml
   <dependency>
       <groupId>org.openjdk.jmh</groupId>
       <artifactId>jmh-core</artifactId>
       <version>1.19</version>
   </dependency>
   <dependency>
       <groupId>org.openjdk.jmh</groupId>
       <artifactId>jmh-generator-annprocess</artifactId>
       <version>1.19</version>
   </dependency>
   ```

2. HelloWorld

   ```java
   /**
    * 每个方法执行前都进行5次预热执行: 每隔1秒进行一次预热操作
    *
    * <p>预热执行结束之后进行5次实际测量执行: 每隔1秒进行一次实际执行, 基准测试测量的是平均响应时长[单位是us]
    **/
   @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
   @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
   public class JMHHelloWorld {

       static class Demo {
           int id;
           String name;
           public Demo(int id, String name) {
               this.id = id;
               this.name = name;
           }
       }

       static List<Demo> demoList;
       static {
           demoList = new ArrayList();
           for (int i = 0; i < 10000; i ++) {
               demoList.add(new Demo(i, "test"));
           }
       }

       @Benchmark
       @BenchmarkMode(Mode.AverageTime)
       @OutputTimeUnit(TimeUnit.MICROSECONDS)
       public void testHashMapWithoutSize() {
           Map map = new HashMap();
           for (Demo demo : demoList) {
               map.put(demo.id, demo.name);
           }
       }

       @Benchmark
       @BenchmarkMode(Mode.AverageTime)
       @OutputTimeUnit(TimeUnit.MICROSECONDS)
       public void testHashMap() {
           Map map = new HashMap((int)(demoList.size() / 0.75f) + 1);
           for (Demo demo : demoList) {
               map.put(demo.id, demo.name);
           }
       }

       public static void main(String[] args) throws RunnerException {
           Options opt = new OptionsBuilder()
                   .include(JMHSample_01_HelloWorld.class.getSimpleName())
                   .forks(1)
                   .build();

           new Runner(opt).run();
       }
   }

   ======================================result======================================
   Benchmark                                       Mode  Cnt    Score     Error  Units
   JMHSample_01_HelloWorld.testHashMap             avgt    5  147.865 ±  81.128  us/op
   JMHSample_01_HelloWorld.testHashMapWithoutSize  avgt    5  224.897 ± 102.342  us/op
   ======================================result======================================
   ```

### 相关注解

1. @Warmup

   - 预热: 因为 JVM 的 JIT 机制的存在[如果某个函数被调用多次之后, JVM 会尝试将其编译成为机器码从而提高执行速度]
   - @Target({ElementType.METHOD, ElementType.TYPE})
   - 参数:
     1. iterations: 预热的次数
     2. time: 每次预热的时间
     3. timeUnit: 时间单位[默认是 second]
     4. batchSize: 批处理大小[每次操作调用几次方法]
   - Iteration 和 Invocation 区别: 配置 Warmup 时间为 1s[1s 的执行作为一个 Iteration], 假设每次方法的执行是 100ms, 则 `1*Iteration = 10 * Invocation`

2. @Benchmark: 标注基准测试方法

   - 被@Benchmark 标记的方法必须是 public 的
   - 被@Benchmark 标注的方法可以有参数, 但是参数必须是被@State 注解的, 就是为了要控制参数的隔离

3. @Measurement: 用来控制实际执行的内容, 配置的选项本 warmup 一样
4. @BenchmarkMode: 基准测试的维度, 可以指定多个维度

   - Mode.Throughput: 吞吐量纬度
   - Mode.AverageTime: 平均时间
   - Mode.SampleTime: 抽样检测
   - Mode.SingleShotTime: 检测一次调用
   - Mode.All: 运用所有的检测模式

5. @OutputTimeUnit: 测量的单位

   - @Target({ElementType.METHOD, ElementType.TYPE})
   - 大多使用 `微妙和毫秒级别`

6. @State: 主要是方便框架来控制变量的过程逻辑

   - 在很多时候我们需要维护一些状态内容, 比如在多线程的时候我们会维护一个共享的状态, 这个状态值可能会在每隔线程中都一样, 也有可能是每个线程都有自己的状态.- JMH 为我们提供了状态的支持, 该注解只能用来标注在类上, 因为类作为一个属性的载体
   - @Target({ElementType.TYPE})
   - values

     1. Scope.Benchmark: 所有的 Benchmark 的工作线程中共享变量内容
     2. Scope.Group: 同一个 Group 的线程可以享有同样的变量
     3. Scope.Thread: 每个线程都享有一份变量的副本, 线程之间对于变量的修改不会相互影响

   - usages
     1. 直接在内部类中使用 @State 作为 "PropertyHolder"
     2. 在 Main 类中直接使用 @State 作为注解, 是 Main 类直接成为 "PropertyHolder"

7. @Setup/@TearDown 必须标示在 @State 注解的类内部, 表示`初始化/销毁`操作

   - Level.Trial: 只会在个基础测试的前后执行, 包括 Warmup 和 Measurement 阶段, 一共只会执行一次
   - Level.Iteration: 每次执行基准测试方法的时候都会执行, 如果 Warmup 和 Measurement 都配置了 2 次执行的话, 那么 @Setup 和 @TearDown 配置的方法的执行次数就 4 次
   - ~~Level.Invocation~~: 每个方法执行的前后执行[一般不推荐这么用]

8. @Param: 测试不同的参数的不同结果且测试的了逻辑一样, 被 @Param 注解标识的参数组会依次被 benchmark 消费到

9. @Threads: 测试线程的数量, 可以配置在方法或者类上,代表执行测试的线程数量

### JMH 高级

1. 不要编写无用代码:

   - 测量的方法中避免使用 void 方法: 在代码使用了没有用处的变量的话, 就容易被编译器优化掉
   - `编译器不要改变这段代码执行`
   - 不要在 Beanchmark 中使用循环, 结合 @BenchmarkMode(Mode.SingleShotTime) 和 @Measurement(batchSize = N) 来做循环

2. Blackhole: 会消费传进来的值, 不提供任何信息来确定这些值是否在之后被实际使用

   - 死代码消除: 入参应该在每次都被用到, 因此编译器就不会把这些参数优化为常量或者在计算的过程中对他们进行其他优化
   - 处理内存壁: 我们需要尽可能减少写的量, 因为它会干扰缓存, 污染写缓冲区等, 这很可能导致过早地撞到内存壁

   - sample code

   ```java
   // 1. 返回测试结果, 防止编译器优化
   @Benchmark
   public double measureRight_1() {
       return Math.log(x1) + Math.log(x2);
   }

   // 2. 通过Blackhole消费中间结果, 防止编译器优化
   @Benchmark
   public void measureRight_2(Blackhole bh) {
       bh.consume(Math.log(x1));
       bh.consume(Math.log(x2));
   }
   ```

3. 方法内联

   - 如果 JVM 监测到一些小方法被频繁的执行, 它会把方法的调用替换成方法体本身

     ```java
     private int add4(int x1, int x2, int x3, int x4) {
         return add2(x1, x2) + add2(x3, x4);
     }
     private int add2(int x1, int x2) {
         return x1 + x2;
     }

     // 运行一段时间后JVM会把add2方法去掉, 并把你的代码翻译成:
     private int add4(int x1, int x2, int x3, int x4) {
         return x1 + x2 + x3 + x4;
     }
     ```

   - @CompilerControl 控制方法内联

     1. CompilerControl.Mode.DONT_INLINE: 强制限制不能使用内联
     2. CompilerControl.Mode.INLINE: 强制使用内联
