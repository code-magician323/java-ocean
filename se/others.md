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
 *    功能：测试File类.
 *        File 类代表与平台无关的文件和目录.
 *        File能新建、删除、重命名文件和目录, 但 File 不能访问文件内容本身.
 *        如果需要访问文件内容本身, 则需要使用输入/输出流.
 */
public class _File {
    @Test
    public void testFile() throws IOException {
        // 1.创建File对象: 这里真是URL, 而且必须是工程根目录下的；如果根目录下没有这个文件的话, 则一些操作无效2.1 2.2有效
        File file = new File("hello.txt");    //这里是工程根目录

        // 2.测试File对象的方法
        // 2.1文件名相关方法
        String fileName = file.getName();
        System.out.println(fileName);
        // 2.2访问文件的绝对路径
        String path = file.getAbsolutePath();
        System.out.println(path);
        // 2.3重命名
        // file.renameTo(new File("1.txt"));
        // 这里应该是两个操作:改名, 重新放到String参数代表的目录

        // 3.文件检测：
        // 3.1文件是否存在：
        System.out.println(file.exists());
        File dir = new File("Java_Project") ;
        // 3.2是否为文件、目录
        System.out.println(dir.isDirectory());
        File dir2 = new File("hello.txt") ;
        System.out.println(dir2.exists());
        System.out.println(dir2.isFile());

        // 4.获取文件的常规信息：
        System.out.println(file.length());

        System.out.println("--------------------------------");
        // 5.文件相关：
        File file2 = new File("hello2.txt") ;
        System.out.println(file2.exists());        //false
        file2.createNewFile();    //创建文件
        System.out.println(file2.exists());        //true
        file2.delete();
        System.out.println(file2.exists());        //false
    }
}
```

## Scanner

- 在外部声明 Scanner 使用后, 进入代码块之后不要用, 重新声明一个新的 Scanner 对象
- Scanner 报错 java.util.NoSuchElementException: 是由于关闭流导致的.

## JavaBean: 形式上与 MVC 的 model 一致.但是作用完全不一样

### 数据 Bean: 减少对数据库的访问, 在各个页面之间进行数据共享

### 业务逻辑 Bean: 处理数据业务逻辑, 主要与数据库打交道

### java-bean 是在整个 web 应用的生命周期内, 保存在 application 对象中

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
