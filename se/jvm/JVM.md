## JVM

### Introduce

1. JVM 体系结构图
   ![avatar](/static/image/java/GC.bmp)

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

![avatar](/static/image/java/class-loader.png)

3. Program Counter Register

   - 一个指向下一个执行方法的指针
   - 内存使用少
   - native 方法的 PC Register 是 null

4. Method Area: PermGen space / Meta space

   - 非堆内存和永久代通过使用 方法区 实现
   - 存储存类信息[constructor], 类静态变量, 常量, 方法的执行, 和一些变量在堆中的引用 4byte[线程共享信息]
   - **`insatnce var store in heap`**
   - JDK1.7 PermGen: 在 jvm 中
   - JDK1.8 MetaData: 直接使用物理内存

5. stack

   - 栈管运行, 堆管存储
   - 不发生 GC, 线程私有
   - 存储内容: `8 基数, 对象引用`; `including 局部变量表, 操作数帧, 运行时常量池引用, 方法参数及返回地址, 动态链接`

6. relation in stack heap and method area

![avatar](/static/image/java/stack-heap-MA.png)

7. PermGen 负责加载 `rt.jar: JDK Class, Interface etc.`; 只有 jvm 销毁时才会释放这些资源

### 堆体系结构概述

1. diagram

   ![avatar](/static/image/java/heap.png)

2. struct

   - [10]新生区: new/young[区域小， 存活率低]
     > [8]伊甸园区
     > [1]幸存者 0 区 [from 区][1]幸存者 1 区 [to 区]
   - [20]养老区: old/tenure[区域大， 存活率大]
   - [logic]永久区: implement of MethodArea

3. new Object() new 出来的对象放在 `伊甸园区`; 如果不停地 new, 新生区满了, 会触发 YGC

4. processor

   - new 出来的对象放在 `伊甸园区`
   - `伊甸园区` 满了出发 YGC
   - `伊甸园区` 中多次存活的数据到 `from` 区 $\color{red}{from 和 to 区进行交换}$
   - 15 次 YGC 后还存活的数据放到 `养老区`
   - `养老区` 满了出发 Full GC
   - 多次 FGC 后还是满的， 抛出 OOM Error

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
5. -XX:MaxTenuringThreshold: 设置存过次数后移到 `老年区`
6. JDK1.7:

   - -XX:PermSize
   - -XX:MaxPermSize

7. JDK1.8:

   - pro env -Xms avlue is equals to -Xmx to avoid GC compare Memory for Application, which will lead to some odd question

8. code

   ```java
   long xms = Runtime.getRuntime().totalMemory();
   long xmx = Runtime.getRuntime().maxMemory();
   ```

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
[Full GC 区名: GC前大小 -> GC后大小(该区总空间), 耗时]

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

### GC 4 算法: 没有最好的算法， 只能根据每代采用最合适的算法`[分代收集]`

1. ~~引用计数法~~: `无法解决循环依赖问题`, 维护计数器本身有消耗

   - 原理: 被引用的次数为 0, 就可以使用 System.gc() 进行回收
   - code

     ```java
     public class RefCountGC
     {
       private byte[] bigSize = new byte[2 * 1024 * 1024];//这个成员属性唯一的作用就是占用一点内存
       Object instance = null;

       public static void main(String[] args)
       {
         RefCountGC objectA = new RefCountGC();
         RefCountGC objectB = new RefCountGC();
         objectA.instance = objectB;
         objectB.instance = objectA;
         objectA = null;
         objectB = null;

         System.gc();
       }
     }
     ```

2. [GCRoot]复制算法(Copying): `新生代`

   - 原理: 从根集合 GCRoot 开始, 通过 Tracing 从统计是否存活
   - 过程:

     1. eden + From 复制到 To 区, 年龄 +1
     2. 清空 Eden + From
     3. 互换 From 和 To 区

   - feature:

     1. 没有碎片[YGC 时 eden+from 全空]
     2. **耗内存**[需要将幸存区数据复制到 To 区]

3. [GCRoot]标记清除(Mark-Sweep): `养老区`

   - 原理:

     1. 标记要回收的对象
     2. 统一回收

   - feature:

     1. `节约空间`
     2. 扫描两次, 耗时严重
     3. 产生内存碎片
     4. GC 时程序不动

4. [GCRoot]标记[清除]压缩(Mark-Compact): `养老区`

   - 原理: 标记 + 清除 + 将存活对象整理到一端
   - feature:

     1. **无碎片**
     2. 需要移动对象的成本
     3. **耗时最长**
     4. GC 时程序不动

5. 种算法比较

   - 内存效率: 复制算法 > 标记清除算法 > 标记整理算法[此处的效率只是简单的对比时间复杂度, 实际情况不一定如此]
   - 内存整齐度: 复制算法 = 标记整理算法 > 标记清除算法
   - 内存利用率: 标记整理算法 = 标记清除算法 > 复制算法

### JMM

1. [link](./jmm.md)

### 小总结

1. Native is Deprecated.

### Reference

1. [JVM 调优总结](https://www.cnblogs.com/andy-zhou/p/5327288.html)
