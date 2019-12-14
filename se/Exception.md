- [Throwable](#throwable)
  - [Error](#error)
  - [Exception](#exception)
  - [diff between Error and Exceprtion](#diff-between-error-and-exceprtion)
- [demo](#demo)

## Throwable

### Error

### Exception

1.  常见异常:

    - 编译时异常: 在编写代码时及要求处理异常。如 I/O
    - 运行时异常:
      > java.lang.ArrayIndexOutOfBoundsException
      > java.lang.NullPointerException
      > java.lang.ArithmeticException
      > java.lang.ClassCastException

2.  异常处理:

    - throws:

      1.  在方法参数列表后面使用 `throws` 关键字声明抛出异常
      2.  异常在当前的方法内不处理，而是抛给调用这个方法的方法
      3.  可以声明抛出多个异常，使用 , 分割
      4.  `运行时异常不需要使用 throws 关键字进行显示的抛出`
      5.  `重写的方法不能抛出比被重写方法范围更大的异常`

    - try{}catch(){}finally{}

    ```java
    try {
        int a=5/0;
    } catch (java.lang.Exception e) {
        e.getMessage();
        return 10;
    }finally{
        //一定会执行，在return 10之前
        System.out.println("finally...");
    }
    ```

3.  自定义的异常类:

    - 自己定义的异常类，通常继承 RunTimeException 类
    - 自定义异常: 两个构造函数: 一个无参；一个带参数(调用 super()方法)
    - 自定义异常的作用: 看到异常的名字就知道出现了什么错误
    - 自定义异常通常都要使用 throw 关键字来抛出 throw new Own_Exception("没有该用户!");

4.  异常的层次:
    ![avatar](/static/image/java/throwable.png)

### diff between Error and Exceprtion

## demo

```java
import java.util.Arrays;
import java.util.List;

public class OwnDefineException {

    public static void main(String[] args) {
        getUser("AA"); //无反应，运行正常: OK,没有出现异常！
        getUser("ZZ"); //抛出异常: Exception in thread "main" basical.Exception_Basical: 没有该用户！
    }

    public static void getUser(String user) {
        //在集合中放置几个用户名: AA、BB、CC
        List<String>users=Arrays.asList("AA","BB","CC");//创建list快速
        if(users.contains(user)){
            System.out.println("OK,没有出现异常！");;
        } else {
            //若user不在list中则抛出异常
            throw new Own_Exception("没有该用户！");
        }
    }
}

/**
  * 自定义异常:
  *  1.自己定义的异常类，通常继承RunTimeException类
  *  2.自定义异常: 两个构造函数: 一个无参；一个带参数(调用super()方法)
  *  3.自定义异常的作用: 看到异常的名字就知道出现了什么错误
  *  4.自定义异常通常都要使用throw关键字来抛出  throw new Own_Exception("没有该用户!");
  */
class Own_Exception extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public Own_Exception() {}
    public Own_Exception(String msg) {
        super(msg);  //这句话是 Throwable 的反应
        //System.out.println(msg);
    }
}
```
