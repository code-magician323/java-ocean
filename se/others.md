## Object 对象

1. 对于==,

   > 如果作用于基本数据类型的变量, 则直接比较其存储的 "值" 是否相等；
   > 如果作用于引用类型的变量, 则比较的是所指向的对象的地址

2. equals() 方法是 Object 类的方法, 由于所有类都继承 Object 类, 也就继承了 equals() 方法。

   - equals 方法不能作用于基本数据类型的变量。
   - 如果没有对 equals 方法进行重写, 则比较的是引用类型的变量所指向的对象的地址；
   - 诸如 String、Date 等类对 equals 方法进行了重写的话, 比较的是所指向的对象的内容。

3. 重写 equals() 就要重写 hashcode()

   - 当两个对象 equals 比较为 true, 那么 hashcode 值应当相等, 反之亦然, 因为当两个对象 hashcode 值相等, 但是 equals 比较为 false
   - 成对重写, 即重写 equals 就应当重写 hashcode.

4. toString() 方法在 Object 类中定义, 其返回值是 String 类型, 返回类名和它的引用地址。
   `这里需要注意的是 ArrayList<Person>:Person 的 toString 方法会被迭代`

## File 类

- 常用方法
  - new File(PATH);
  - getName()
  - getAbsolutePath()
  - getAbsoluteFile()
  - getParent()
  - renameTo()
  -
  - exists()
  - canWrite()
  - canRead()
  - exists()
  - isFile()
  - isDirectory()
  - lastModify()
  - Length()
  - createNewFile()
  - delete()
  - mkDir()
  - list()
  - listFiles()

```java
import java.io.File;
import java.io.IOException;
import org.junit.Test;

/**
 *    功能：测试File类。
 *        File 类代表与平台无关的文件和目录。
 *        File能新建、删除、重命名文件和目录, 但 File 不能访问文件内容本身。
 *        如果需要访问文件内容本身, 则需要使用输入/输出流。
 */
public class _File {
    @Test
    public void testFile() throws IOException {
        //1.创建File对象: 这里真是URL, 而且必须是工程根目录下的；如果根目录下没有这个文件的话, 则一些操作无效2.1 2.2有效
        File file = new File("hello.txt");    //这里是工程根目录

        //2.测试File对象的方法
        //2.1文件名相关方法
        String fileName = file.getName();
        System.out.println(fileName);
        //2.2访问文件的绝对路径
        String path = file.getAbsolutePath();
        System.out.println(path);
        //2.3重命名
        // file.renameTo(new File("1.txt"));
        //这里应该是两个操作:改名, 重新放到String参数代表的目录

        //3.文件检测：
        //3.1文件是否存在：
        System.out.println(file.exists());
        File dir = new File("Java_Project") ;
        //3.2是否为文件、目录
        System.out.println(dir.isDirectory());
        File dir2 = new File("hello.txt") ;
        System.out.println(dir2.exists());
        System.out.println(dir2.isFile());

        //4.获取文件的常规信息：
        System.out.println(file.length());

        System.out.println("--------------------------------");
        //5.文件相关：
        File file2 = new File("hello2.txt") ;
        System.out.println(file2.exists());        //false
        file2.createNewFile();    //创建文件
        System.out.println(file2.exists());        //true
        file2.delete();
        System.out.println(file2.exists());        //false
    }
}
```

## Array

### 创建数组

```java
int[] a1 = new int[] { 1, 2, 3 };
int[] a2 = { 1, 2, 3 };
int[][] a = new int[5][];
// 循环
int[] a3 = new int[3];
for (int a : a3) {
    a = 0;
}
for (int i = 0; i < a1.length; ji++) {
}
```

### 数组排序

- 基本方法

  ```java
  Collections.sort(List<>);
  Collections.sort(List<>, Comparator);
  Arrays.sort(int[])
  Arrays.asList()
  ```

- 自定义的排序 List：comparator 方法
  ```java
  List<Integer> list02 = new ArrayList<>(...);
  // 自定义Comparator对象, 自定义排序 功能：降序;最好使用compareTo方法
  Comparator<Integer> c = new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
          return o2.compareTo(o1);
      }
  };
  Collections.sort(list02, c);
  ```

### demo

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.junit.Test;

/**
 * 功能：
 *  1.数组的初始化的三种方法
 *  2.数组的排序问题：Arrays.sort(int[])、Collections.sort(List<>);
 *  3.二维数组的
 *  4.打印杨辉三角
 */
public class Array {

    @Test
    /**
     * 4.打印杨辉三角
     */
    public void printTriangle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要输出的行数：");
        int n = scanner.nextInt();
        scanner.close();
        //声明一个二维数组
        int [][]a=new int [n][];
        a[0]=new  int[]{1};
        a[1]=new  int[]{1,1};

        for(int i=2;i<n;i++){
            a[i]=new int[i+1];  //a[0]有一个元素
            for(int j=1;j<a[i].length-1;j++){
                //初始化二维数组为杨辉三角
                a[i][0]=1;
                a[i][a[i].length-1]=1;
                a[i][j]=a[i-1][j-1]+a[i-1][j];
            }
        }
        //遍历
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length;j++){
                System.out.print(a[i][j]+"\t");
            }
            System.out.println();
        }
    }

    @Test
    /**
     * 2.数组的排序问题：Collections.sort(List<>);
     */
    public void sort_Array_01() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入学生人数：");
        int n = scanner.nextInt();

        // 初始化学生的分数
        List<Integer> score = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.print("请输入第" + (i + 1) + "名同学的成绩：");
            score.add(scanner.nextInt());
        }
        scanner.close();

        // 对sort进行排序
        Collections.sort(score);
        Arrays.sort(score); // ok
    }
}
```

## final

1. 在 java 中声明类、属性、方法时, 可以使用关键字 final 来修饰.
2. final 标记的<label style="color:red">类不能被继承, 提高安全性和程序的可读性</label>;
   final <label style="color:red">标记的方法不能 override</label>, 增加安全性
3. final <label style="color:red">标记的成员变量必须在声明时或在每个 constructor 中显示赋值, 否则出错</label>
4. final 标记的变量(成员变量和局部变量)即为常量, 只能赋值一次

## Integer-String

### java

#### int 转 String:

```java
String s = String.valueOf(int i);
String s = Integer.toString(int i);
String s = "" + i;
```

#### String 转 int:

```java
int i = Integer.parseInt(String s);
int i = Integer.ValueOf(String s).intValue();
int i = s - 0;
```

### js

#### int 转 String:

```js
String s = Integer.toString(int i);
String s = ''+i;
```

#### String 转 int:

```js
int i=Integer.parseInt(String s);
int i=s-0;
```

---

### String

1.  String 是 `final` 类型的不可变字符序列
2.  关于字符串缓冲池, 直接通过 `=` 为字符串赋值, 会先在缓冲池中查找有无一样的字符串, 若有就把字符串的引用付给字符串变量; 否则会创建一个新的字符串, 并将其放入字符串缓冲池中, 在赋值
3.  字符串的常用方法
    ```java
    trim() // 去除前后的空格
    subString(fromIndex) // 求子字符串 fromIndex--end
    subString(fromIndex ,toIndex)
    indexOf(String str,int fromIndex) // 求字符串的索引
    split() // split(" {1,}")
    charAt(int index)
    byte[] getBytes()
    ```
4.  字符串与数组的转换:

    ```java
    new String(charArray) str.toCharArray()
    ```

5.  字符串与 int 的转换:

    ```java
    // int convert to int
    string s = string.valueOf(int i);
    string s = Integer.toString(int i);
    string s = ""+i;

    // int convert to String
    int i = Integer.ValueOf(string s).intValue();
    int i = Integer.parseInt(string s);
    int i = s-0;
    ```

6.  `StringBuffer(线程安全)/StringBuilder(常用):可修改的字符序列`

```java
String str = "sjihdggvycbhxjkm";
byte[] str2 = str.getBytes();
System.err.println(String.valueOf(str2));  // className
System.err.println(new String(str2)); // StringValue
```

### memory

```java
/**
 * 功能：测试一些内存的分配：
 *  1.Integer
 *  2.String
 *  3.int基本数据类型
 * @author zack
 *
 */
public class TestBascialSpaceAllocate {
    public static void main(String[] args) {
        Integer integer = new Integer(2);
        Integer integer2 = new Integer(2);
        System.out.println(integer == integer2);  //false
        System.out.println(integer.equals(integer2)); //true
        Integer integer3 = 2; //这里自动装箱
        Integer integer4 = 2;
        System.out.println(integer3 == integer4);  //这里自动拆箱 //true
        System.out.println(integer3.equals(integer4)); //true
        System.out.println(integer == integer3); // False

        String String = "123";
        String String2 = "123";
        System.out.println(String == String2);   //true
        System.out.println(String.equals(String2)); //true
    }
}
```

## Scanner

- 在外部声明 Scanner 使用后, 进入代码块之后不要用, 重新声明一个新的 Scanner 对象
- Scanner 报错 java.util.NoSuchElementException: 是由于关闭流导致的。

## JavaBean: 形式上与 MVC 的 model 一致.但是作用完全不一样

### 数据 Bean: 减少对数据库的访问, 在各个页面之间进行数据共享

### 业务逻辑 Bean: 处理数据业务逻辑, 主要与数据库打交道

### java-bean 是在整个 web 应用的生命周期内, 保存在 application 对象中

## BigDecimal

1.  java.math.BigDecimal 类. BigDecimal 类支持任何精度的定点数.
2.  创建:
    ```java
    public BigDecimal(double val)
    public BigDecimal(String val)
    ```
3.  方法:
    ```java
    public BigDecimal add(BigDecimal augend)
    public BigDecimal subtract(BigDecimal subtrahend)
    public BigDecimal multiply(BigDecimal multiplicand)
    public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode)
    ```

## Prime 自定义对象

```java
import java.util.Scanner;

/**
 * 功能：判断一个数是否为素数
 * @author Zhang
 */
public class TestPrime {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入要判斷的數字：");
        int n = scanner.nextInt();
        scanner.close();
        Prime(n);

        /**
         * 功能：判断一定范围内的所有素数
         */
        long begin=System.currentTimeMillis();
        for (int i = 2; i < 20000; i++) {
            System.out.println(i + "是素数：" + Prime(i));
        }
        long end=System.currentTimeMillis();
        System.out.println("代码所用时间："+(end-begin));
    }

    public static boolean Prime(int n) {
        int i = 2;
        //int sn=(int) Math.sqrt(n);    //负优化
        for (; i <=Math.sqrt(n); i++) {
            if (n % i == 0)
                break;
            else
                continue;
        }
        if (i >= Math.sqrt(n))
            return true;
        else
            return false;
    }
}
```

## CharSet

### demo

```java
package basical;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

/**
 * 功能：测试Charset字符集
 *
 */
public class _Charset {
    @Test
    /**
     * 功能：测试编码解码的问题
     * 	1.notice:这里的flip()的使用：只有在使用put()z之后采取去掉用，耳边吗解码这里是不需要哦调用flip()的
     * 	2.
     */
    public void testCharset2() {
        try {
            //1.创建码的方式
            Charset cs=Charset.forName("GBK");
            //2.创建Buffer,并放入数据
            CharBuffer charBuffer=CharBuffer.allocate(1024);
            charBuffer.put("张壮壮啧啧啧！".toCharArray());

            //3.创建编码器(编码或是字节流),并CharBuffer中的数据进行编码
             charBuffer.flip();
             CharsetEncoder cer= cs.newEncoder();
             ByteBuffer byteBuffer=cer.encode(charBuffer);
             //4.创建解码器，并解码
             // System.out.println(byteBuffer);
             CharsetDecoder cdr=cs.newDecoder();
             CharBuffer charBuffer2=cdr.decode(byteBuffer);

             //5.输出解码后的
             System.out.println("dfgwhsjkal;,"+charBuffer2.toString());
        } catch (CharacterCodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    /**
     * 打印Charset中有多少种字符集
     */
    public void testCharset() {

        SortedMap<String, Charset> map=Charset.availableCharsets();
        System.out.println("Charset中有"+map.size()+"种字符集");	//168
        for (Map.Entry<String, Charset> iterable_element : map.entrySet()) {
            System.out.println(iterable_element.getKey()+":"+iterable_element.getValue());
        }
    }
}
```
