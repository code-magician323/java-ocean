## JVM

### JVM 体系结构概述

1. JVM 体系结构图

```java
 Class Files  ==>  类装载器子系统 Class loader
                    ||       ||
  ---------------------------------------------------------------
  |            运行时数据区[Runtime Data Area]                   |
  | 方法区[share]          Java 栈 NoGC             本地方法栈    |
  |  Method Area          Java Stack     Native Method Stack   |
  | 永久区是对其的实现                                            |
  |                                                            |
  |   堆[share]                      程序计数器                  |
  |     Heap              Program Counter Register             |
  |------------------------------------------------------------|
       || ||                          ||  ||
      执行引擎           ==>            本地方法接口     <== 本地方法库
  Execution Engine      <==         Native Interface
```

2. [ClassLoader](./ClassLoader.md)

- diagram
  ![avatar](/static/image/java/class-loader.png)

3. Native Method Stack/ Native Interface / Native Method lib

- native mehod is out of control java, it need call other lib or system resouce
  ```java
  // this hint thread based on system operation, has noting with java
  private native void start0();
  ```

4. Program Counter Register

- a point indicate next execte method, like `Duty watch`
- using little memory
- native method, PC Register is null

5. Method Area: PermGen space / Meta space

- `Non-Heap Memory, PermanentGeneration is implement of Method Area`
- store class `struct description`, means class template
  > Runtime constants pool
  > filed
  > constructor
  > method data
  > method content
- shared by all thread
- **`insatnce var store in heap`**
- JDK1.7 PermGen: in JVM
- JDK1.8 MetaData: donot exist in JVM, use physical memory

6. stack

- 栈管运行， 堆管存储
- no GC, and thread private, dependency on Thread lifeCycle
- store
  > 8 kinds basic data + Reference Object + instance method is allocated all in function stack memory
  > Local Variables: input or output args and variables in method
  > Operand Stack: record in stack and out stack action
  > Frame Data: includes Class file and mathods etc: `including 局部变量表， 操作数帧， 运行时常量池引用， 方法返回地址， 动态链接`

7. relation in stack heap and method area

- diagram
  ![avatar](/static/image/java/stack-heap-MA.png)

8. PermGen load rt.jar etc, which cotains JDK Class, Interface metadata. Only close JVM can release these

### 堆体系结构概述

1. diagram
   ![avatar](/static/image/java/heap.png)
2. struct

   - [10]新生区: new/young
     > [8]伊甸园区
     > [1]幸存者 0 区 [from 区][1]幸存者 1 区 [to 区]
   - [20]养老区: old/tenure
   - [logic]永久区: implement of MethodArea

3. new Object() new 出来的对象放在 `伊甸园区`; 如果不停地 new, 新生区满了, 会触发 YGC

4. processor

   > 1. new 出来的对象放在 `伊甸园区`
   > 2. `伊甸园区` 满了出发 YGC
   > 3. `伊甸园区` 中多次存活的数据到 `from` 区 $\color{red}{from 和 to 区进行交换}$
   > 4. 15 次 YGC 后还存活的数据放到 `养老区`
   > 5. `养老区` 满了出发 Full GC
   > 6. 多次 FGC 后还是满的， 抛出 OOM Error

5. explain 4.3 交换

- from 区 和 to 区并不是固定的， 每次 YGC 后会交换， 谁空谁是 to 区
- YGC 时伊甸园区并须全清空， 幸存数据复制存入 From 区， 下次 YGC 将 from 区和伊甸园区都算作伊甸园区进行收割， 剩下的 to 区则成为存放存货数据的 from 区;

### 堆参数调优入门

- diagram
  ![avatar](/static/image/java/jvm.png)

1. -Xms: heap start, default 1/64 of physical memory
2. -Xmx: heap max, default 1/4 of physical memory
3. -Xmn: new
4. -XX:+PrintGCDetail:
5. JDK1.7:
   > -XX:PermSize
   > -XX:MaxPermSize
6. JDK1.8:

- notice
  > pro env -Xms avlue is equals to -Xmx to avoid GC compare Memory for Application, which will lead to some odd question

### GCDetails

- diagram
  ![avatar](/static/image/java/FullGC.png)
  ![avatar](/static/image/java/GC.png)
- config

```xml
-Xms10m -Xmx10m -XX:+PrintGCDetails
```

- detail

```java
[GC (Allocation Failure)[PSYoungGen: 2048K{YGC前内存占用}->488K{YGC后内存占用}(2560K{新生区总内存})] 2048K{YGC前堆内存占用}->754K{YGC后堆内存占用}(9728K{JVM堆总大小}), 0.0010957 secs{YGC耗时}] [Times: user=0.00{YGC用户耗时} sys=0.00{YGC系统耗时}, real=0.00 secs{YGC实际耗时}]

[GC (Allocation Failure) [PSYoungGen: 2536K->488K(2560K)] 2802K->1039K(9728K), 0.0015812 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (Ergonomics) [PSYoungGen: 1211K->0K(1536K)] [ParOldGen: 6525K->3418K(7168K)] 7736K->3418K(8704K), [Metaspace: 4761K->4761K(1056768K)], 0.0058887 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 40K->32K(2048K)] 6273K->6264K(9216K), 0.0003920 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (Ergonomics) [PSYoungGen: 32K->0K(2048K)] [ParOldGen: 6232K->4603K(7168K)] 6264K->4603K(9216K), [Metaspace: 4762K->4762K(1056768K)], 0.0139514 secs] [Times: user=0.05 sys=0.00, real=0.01 secs]
[GC (Allocation Failure) [PSYoungGen: 20K->32K(2048K)] 6030K->6042K(9216K), 0.0008025 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
[Full GC (Allocation Failure) Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
[PSYoungGen: 0K->0K(2048K)] [ParOldGen: 5284K->5219K(7168K)] 5284K->5219K(9216K), [Metaspace: 4763K->4763K(1056768K)], 0.0103089 secs] [Times: user=0.02 sys=0.03, real=0.01 secs]
    at java.util.Arrays.copyOf(Arrays.java:3332)
Heap
 PSYoungGen      total 2048K, used 61K [0x00000000ffd00000, 0x0000000100000000, 0x0000000100000000)
    at java.lang.AbstractStringBuilder.ensureCapacityInternal(AbstractStringBuilder.java:124)
  eden space 1024K, 5% used [0x00000000ffd00000,0x00000000ffd0f548,0x00000000ffe00000)
    at java.lang.AbstractStringBuilder.append(AbstractStringBuilder.java:674)
  from space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)
    at java.lang.StringBuilder.append(StringBuilder.java:208)
  to   space 1024K, 0% used [0x00000000ffe00000,0x00000000ffe00000,0x00000000fff00000)
    at OOM.main(OOM.java:18)
 ParOldGen       total 7168K, used 5219K [0x00000000ff600000, 0x00000000ffd00000, 0x00000000ffd00000)
  object space 7168K, 72% used [0x00000000ff600000,0x00000000ffb18c68,0x00000000ffd00000)
 Metaspace       used 4797K, capacity 4992K, committed 5248K, reserved 1056768K
  class space    used 529K, capacity 560K, committed 640K, reserved 1048576K

Process finished with exit code 1
```

### 小总结

1. Native is Deprecated.
