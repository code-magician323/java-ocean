## [JUC](./JUC.md)

## Thread

1. 在 Java 中, `Thread 类代表一个线程`; 每个 Java 程序启动后, 虚拟机将自动创建一个主线程
2. 实现线程的两种方法:
   - 创建 `java.lang.Thread` 类的子类, 重写该类的 run 方 法[放置实质的线程体]; 调用 thread 对象的 `start()` 方法启动线程
   - 创建 `java.lang.Runnable` 接 口的实现类, 实现接口中的 run 方法; 调用 thread 对象的 `start()` 方法启动线程
   - implement `callable` interface: implement Runnable interface and need Callable interface as arg, FutureTask, work as adaptor
3. Runnable 接口优点:

   - 可以继承父类: 继承 Thread 的就不能继承父类了(Java 是单继承的)
   - 共享变量

4. Runnable 接口与 Thread 类之间的区别

   - Runnable 接口必须实现 run 方法,而 `Thread 类中的 run 方法是一个空方法,可以不重写`
   - **Runnable 接口的实现类并不是真正的线程类, 只是线程运行的目标类**.
     - 要想以线程的方式执行 run 方法, 必须依靠 Thread 类
   - Runnable 接口`适合`于资源的共享, Thread 类也可以实现

5. 实现 `Runnable` 接口的方式:

   - **`在实现 Runnable 接口时, 将i写在方法外部会成为共享变量`**
   - Thread thread=new Thread(myRunnable); //这里传进来的 myRunnable 是一个对象, 为引用类型:由于引用所以达到了共享变量的目的
   - 创建实现 `Runnable` 接口的实现类: 必须实现 `run()` 方法
   - 创建 5.1 对应的 `Runnable` 接口的实现类对象
   - 创建 `Thread` 对象, 利用 `Thread(Runnable mr)`
   - 对应 `Thread` 类 `start()` 方法启动线程

6. 方法

   - run(): 实质的线程体
   - yield(): "执行状态"下的线程可以调用 yield 方法, 该方法用于主动出让 CPU 控制权
   - sleep(): 使线程休眠一段时间
   - join(): `当前线程调用其他线程`
   - 执行 I/O 操作:
   - interrupt(): 调用阻塞线程的 ; 直接结束 `sleep`, 并抛出一个 `InterruptedException` 异常
   - isAlive(): 判断线程是否存活
   - `线程优先级`

7. 关于线程通信

   - 方法: `wait(), notify(), notifyAll();`
   - **这些方法要在同步方法中调用**
   - 当一个线程正在使用同步方法时, 其他线程就不能使用这个同步方法, 而有时涉及一些特殊情况:

     ```
     当一个人在一个售票窗口排队买电影票时, 如果她给售票员的不是零钱, 而售票员有没有售票员找她, 那么她必须等待, 并允许后面的人买票, 以便售票员获取零钱找她, 如果第 2 个人也没有零钱, 那么她俩必须同时等待.
     ```

   - 当一个线程使用的同步方法中用到某个变量, 而此变量又需要其他线程修改后才 能符合本线程的需要, 那么`可以在同步方法中使用 wait() 方法`

8. **_Notice:_**
   - **_<font color='red'>testThread(在 main 函数中创建的线程的 run 方法可以使用 sleep; 其他非 main 函数中, 调用带有 sleep 的 run 创建的线程不会执行 run 方法)_**</font>
   - **_<font color='red'>当 run 方法中使用 sleep 时, new 出来的对象就不会执行 run 方法了, 可以使用 sleep 加回来; 或者在 main 方法中创建线程_**</font>
9. 线程安全问题:
   - 理解并写出写出不安全的代码: 多线程共享同一变量资源
   - 使用 `synchronized` 关键字修饰方法: synchronized 参照共同的一个对象
10. 设置优先级, 但是不一定准, 所以不用

## demo

- `[TESTTHREADJION_SLEEP]` 线程的实现: 继承 Thread

  ```java
  public class TESTTHREADJION_SLEEP extends Thread{
      @Override
      public void run() {
          for(int i=0;i<=100;i++){
              if(i==10){
                  try {
                      Thread.sleep(1000000000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
              String threadName=Thread.currentThread().getName();
              System.out.println(threadName+":"+i);
          }
      }
  }

  // 使用
  Thread thread=new TESTTHREADJION_SLEEP();
  thread.start();
  ```

- `[MYRUNNABLE]` 线程的实现: 实现 Runnable 接口

  ```java
  public class MYRUNNABLE implements Runnable {
      int i; // 写到外面的I是共享变量
      @Override
      public void run() {
          for(;i<100;i++){
            if(i==10)
            Thread.yield(); // "执行状态" 下的线程可以调用yield方法, 该方法用于主动出让CPU控制权.
            String threadName=Thread.currentThread().getName();
            System.out.println(threadName+":"+i);
          }
      }
  }

  // 使用
  MYRUNNABLE myRunnable=new MYRUNNABLE();
  Thread thread=new Thread(myRunnable); //这里传进来的myRunnable是一个对象, 为引用类型:由于引用所以达到了共享变量的目的
  thread.start();
  ```

- `[AppleRunnable]` 实现 Runable 中的变量共享

  ```java
  public class AppleRunnable implements Runnable {
      private int appleCount=5;	//这个变量是共享的
      //一次拿一个
      synchronized boolean getApple() {
          if(appleCount>0){
              appleCount--;
              try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
              System.out.println(Thread.currentThread().getName()+"拿走了一个苹果, 还剩下"+appleCount+"个苹果！");
              return true;
          }else{
              System.out.println(Thread.currentThread().getName()+"已经死了！");
              return false;
          }
      }

      @Override
      //不停的拿, 拿到没有结束
      public void run() {
          boolean flag=getApple();
          while(flag){
              flag=getApple();
          }
      }
  }
  ```

- `[FIRSTTHREAD]`实现 Thread 中的变量共享

  ```java
  class FIRSTTHREAD extends Thread{
      private static int i;
      @Override
      public  void  run() {
          for(;i<=100;i++){
              String threadName=Thread.currentThread().getName();
              System.out.println(threadName+":"+i);
          }
      }
      public FIRSTTHREAD() {
      }
      public FIRSTTHREAD(String name) {
          super(name);
      }
  }

  public void testThread2() {
    FirstThread thread = new FirstThread("sd" );
    FirstThread thread2 = new FirstThread( "ssjiud");
    thread.start();
    thread2.start();
  }

  ```

- 测试 Interrupt()方法

  ```java
    public static void main(String[] args) throws InterruptedException {
        Thread thread=new TESTTHREADJION_SLEEP(); //次线程；主线程是main(函数本身)
        //判断线程是否活着
        System.out.println(thread.isAlive()); //false
        thread.start();
        // interrupt 方法: 调用阻塞线程的 ; 直接结束 sleep, 并抛出一个 InterruptedException 异常
        thread.interrupt();
        System.out.println(thread.isAlive()); //true
        thread.join();
        System.out.println(thread.isAlive()); //false
        //  thread.start();  //已经执行过的线程, 再次调用会出现异常
    }
  ```

- 测试 join() 方法

  ```java
  // 这里先执行main, 在执行thread;
  public static void main(String[] args) {
      Thread thread=new TESTTHREADJION_SLEEP();
      thread.start();

      for(int i=0;i<100;i++){
          if(i==10)
              try {
                  // thread.join():意思是把thread加进来
                  thread.join();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          String threadName=Thread.currentThread().getName();
          System.out.println(threadName+":"+i);
      }
  }

  // 这里没有 main, 执行thread;
  public void test() {
    Thread thread=new TESTTHREADJION_SLEEP();
    thread.start();

    for(int i=0;i<100;i++){
        if(i==10)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        String threadName=Thread.currentThread().getName();
        System.out.println(threadName+":"+i);
    }
  }
  ```

- 测试 sleep() 方法

  ```java
  // 在当 run 方法中使用 sleep 时, new出来的对象就不会执行run方法了, 可以使用sleep加回来
  // 在 main 函数中创建的线程的 run 方法可以使用 sleep;
  // 其他非 main 函数中创建, 调用带有 sleep 的 run 创建的线程不会执行run方法
  public void testThread() {
      Thread thread=new FirstThread();
      // 这里的 FristThraed 如果在 run 方法中使用了 sleep 函数, 则这个start开始后不会打印任何东西
      thread.start();
      for(int i=0;i<100;i++){
          String threadName=Thread.currentThread().getName();
          System.out.println(threadName+":"+i);
      }
  }
  ```

- 测试优先级

  ```java
    public class TestThreadPriority extends Thread{
        @Override
        public void run() {
            for(int i=0;i<10;i++){
                System.out.println(Thread.currentThread().getName()+":"+i);
            }
        }
        public TestThreadPriority() {
        }
        public TestThreadPriority(String name) {
            super(name);
        }

        public static void main(String[] args) {
            TestThreadPriority priority=new TestThreadPriority();
            priority.start();

            //设置优先级, 但是不一定准, 所以不用
            priority.setPriority(MAX_PRIORITY);

            for(int i=0;i<10;i++){
                System.out.println(Thread.currentThread().getName()+":"+i);
            }

            System.out.println(priority.getPriority());
            System.out.println(Thread.currentThread().getPriority());
        }
    }
  ```

- 测试线程安全
  ```java
  public class ThreadSafe_synchronized{
      public static void main(String[] args) {
          Runnable runnable=new AppleRunnable();
          Thread thread=new Thread(runnable);
          Thread thread2=new Thread(runnable);
          thread.setName("小强");
          thread2.setName("小明");
          thread.start();	//这个线程只要开启就要不停的拿, 直到结束；这个就需要run来实现
          thread2.start();
      }
  }
  ```

## 扩展

1. 进程和多线程简介

   - 进程: 程序的一次执行过程, 是系统运行程序的基本单位, 因此进程是动态的. 系统运行一个程序即是一个进程从创建, 运行到消亡的过程.
   - 线程: 线程与进程相似, 但线程是一个比进程更小的执行单位. 一个进程在其执行的过程中可以产生多个线程.
     与进程不同的是同类的多个线程共享同一块内存空间和一组系统资源, 所以系统在产生一个线程, 或是在各个线程之间作切换工作时,
     负担要比进程小得多, 也正因为如此, 线程也被称为轻量级进程.
   - 多线程: 多线程就是多个线程同时运行或交替运行. 单核 CPU 的话是顺序执行, 也就是交替运行. 多核 CPU 的话, 因为每个 CPU 有自己的运算器, 所以在多个 CPU 中可以同时运行. 开发高并发系统的基础, 利用好多线程机制可以大大提高系统整体的并发能力以及性能.
   - **线程就是轻量级进程, 是程序执行的最小单位. 使用多线程而不是用多进程去进行并发程序的设计, 是因为线程间的切换和调度的成本远远小于进程.**

2. 几个重要的概念
   - 同步和异步: 同步和异步通常用来形容一次方法调用.
     同步方法调用一旦开始, 调用者必须等到方法调用返回后, 才能继续后续的行为.
     异步方法调用更像一个消息传递, 一旦开始, 方法调用就会立即返回, 调用者可以继续后续的操作.
   - 关于异步目前比较经典以及常用的实现方式就是消息队列:
   ```java
   在不使用消息队列服务器的时候, 用户的请求数据直接写入数据库, 在高并发的情况下数据库压力剧增, 使得响应速度变慢.
   但是在使用消息队列之后, 用户的请求数据发送给消息队列之后立即 返回, 再由消息队列的消费者进程从消息队列中获取数据, 异步写入数据库.
   由于消息队列服务器处理速度快于数据库（消息队列也比数据库有更好的伸缩性）, 因此响应速度得到大幅改善.
   ```
   - 并发(Concurrency)和并行(Parallelism)
