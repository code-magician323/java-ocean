## Fork/Jion 框架

- 简介: 就是在必要的情况下, 将一个大任务, 进行拆分(fork)成若干个小任务(拆到不可再拆时), 再将一个个的小任务运算的结果进行 join 汇总.
- 原理:
  ![avatar](https://img-blog.csdnimg.cn/20190531142543864.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)
- 说明:
  > Fork/Join 框架可以很大效率的提高计算速度
  > parallel() 并不一定比 Fork/Join 框架速度快, 这里的 Fork/Join 框架 的临界值选择很重要[速度取决于临界值的选取]
  > 并行串行切换: `parallel()/sequential()`
- java 8 `工作窃取算法`: 算法是指某个线程从其他队列里窃取任务来执行
  > 对于常见的一个大型任务, 我们可以把这个大的任务切割成很多个小任务, 然后这些小任务会放在不同的队列中,
  > 每一个队列都有一个相应的的工作执行线程来执行, 当一个线程所需要执行的队列中, 任务执行完之后, 这个线程就会被闲置,
  > 为了提高线程的利用率, 这些空闲的线程可以从其他的任务队列中窃取一些任务, 来避免使自身资源浪费, 这种在自己线程闲置,
  > 同时窃取其他任务队列中任务执行的算法, 就是工作窃取算法.
- demo

  ```java
  public class ForkJoin extends RecursiveTask<Long> {

      private long start;
      private long end;
      private static final long THRESHOLD = 10000000L;

      public ForkJoin() {
      }

      public ForkJoin(long start, long end) {
          this.start = start;
          this.end = end;
      }

      @Override
      protected Long compute() {
          long length = end - start;
          long sum = 0;
          if (length <= THRESHOLD) {
              for (long i = start; i <= end; i++) {
                  sum += i;
              }
              return sum;
          } else {
              // 拆分: 递归
              long middle = (start + end) / 2;
              ForkJoin forkJoinLeft = new ForkJoin(start, middle);
              forkJoinLeft.fork(); // 进行拆分并压如栈
              ForkJoin forkJoinRight = new ForkJoin(middle + 1, end);
              forkJoinRight.fork(); // 进行拆分并压如栈
              return forkJoinLeft.join() + forkJoinRight.join();
          }
      }

      public static void main(String[] args) {
          Instant start = Instant.now();
          ForkJoinPool forkJoinPool = new ForkJoinPool();
          ForkJoinTask<Long> task = new ForkJoin(0, 1000000000L);
          forkJoinPool.invoke(task);
          Instant end = Instant.now();
          System.out.println(Duration.between(start, end).toMillis());  // 228

          Instant start1 = Instant.now();
          long sum = 0;
          for (int i = 0; i <= 1000000000L; i++) {
              sum += i;
          }
          Instant end1 = Instant.now();
          System.out.println(Duration.between(start1, end1).toMillis());  // 542

          Instant start2 = Instant.now();
          LongStream.rangeClosed(0, 1000000000L).reduce(Long::sum);  // 638
          Instant end2 = Instant.now();
          System.out.println(Duration.between(start2, end2).toMillis());

          Instant start3 = Instant.now();
          LongStream.rangeClosed(0, 1000000000L).parallel().reduce(Long::sum);
          Instant end3 = Instant.now();
          System.out.println(Duration.between(start3, end3).toMillis()); //  393
      }
  }
  ```
