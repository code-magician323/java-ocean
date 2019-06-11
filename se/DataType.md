**Contents of DataType**

- [java 中基本的数据类型](#java-%E4%B8%AD%E5%9F%BA%E6%9C%AC%E7%9A%84%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B)
- [定义流程控制的关键字](#%E5%AE%9A%E4%B9%89%E6%B5%81%E7%A8%8B%E6%8E%A7%E5%88%B6%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [访问权限修饰符的关键字](#%E8%AE%BF%E9%97%AE%E6%9D%83%E9%99%90%E4%BF%AE%E9%A5%B0%E7%AC%A6%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [定义类, 函数, 变量修饰符的关键字](#%E5%AE%9A%E4%B9%89%E7%B1%BB-%E5%87%BD%E6%95%B0-%E5%8F%98%E9%87%8F%E4%BF%AE%E9%A5%B0%E7%AC%A6%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [定义类与类之间关系的关键字](#%E5%AE%9A%E4%B9%89%E7%B1%BB%E4%B8%8E%E7%B1%BB%E4%B9%8B%E9%97%B4%E5%85%B3%E7%B3%BB%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [定义建立实例及引用实例, 判断实例的关键字](#%E5%AE%9A%E4%B9%89%E5%BB%BA%E7%AB%8B%E5%AE%9E%E4%BE%8B%E5%8F%8A%E5%BC%95%E7%94%A8%E5%AE%9E%E4%BE%8B-%E5%88%A4%E6%96%AD%E5%AE%9E%E4%BE%8B%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [异常处理的关键字](#%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [用于包的关键字](#%E7%94%A8%E4%BA%8E%E5%8C%85%E7%9A%84%E5%85%B3%E9%94%AE%E5%AD%97)
- [其他修饰符关键字](#%E5%85%B6%E4%BB%96%E4%BF%AE%E9%A5%B0%E7%AC%A6%E5%85%B3%E9%94%AE%E5%AD%97)
- [标识符](#%E6%A0%87%E8%AF%86%E7%AC%A6)
- [dataType](#datatype)
- [switch(表达式)中表达式的返回值必须是下述几种类型之一：](#switch%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%B8%AD%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%9A%84%E8%BF%94%E5%9B%9E%E5%80%BC%E5%BF%85%E9%A1%BB%E6%98%AF%E4%B8%8B%E8%BF%B0%E5%87%A0%E7%A7%8D%E7%B1%BB%E5%9E%8B%E4%B9%8B%E4%B8%80)
- [break/continue:](#breakcontinue)
- [数组：](#%E6%95%B0%E7%BB%84)

## java 中基本的数据类型

```java
class      interface     enum        byte        short
int        long          float       double      char
boolean    void
```

## 定义流程控制的关键字

```java
if          else    switch     case       default
while       do        for      break      continue
return
```

## 访问权限修饰符的关键字

```java
private     protected       public
```

## 定义类, 函数, 变量修饰符的关键字

```java
abstract        final       static      synchronized
```

## 定义类与类之间关系的关键字

```java
extends     implements
```

## 定义建立实例及引用实例, 判断实例的关键字

```java
new     this        super       instanceof
```

## 异常处理的关键字

```java
try     catch       finally     throw       throws
```

## 用于包的关键字

```java
package     import
```

## 其他修饰符关键字

```java
native      strictfp        transient       volatile    assert
```

## 标识符

- 定义合法标识符规则：子母、下划线、\$开头,且不能有空格区分大小写, 数字不能开头

## dataType

![avatar](https://img-blog.csdnimg.cn/20190509230500367.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

1. 引用数据类型：在栈中放的是堆内存中值的首地址
   基本数据类型：在栈中放的是值.
2. float f=12.34F;
   float f=12.34; // error
3. 有多种类型的数据混合运算时, 系统首先自动将所有数据转换成容量最大的那种数据类型, 然后再进行计算。
4. byte,short,char 之间不会相互转换, 他们三者在计算时首先转换为 int 类型。
5. 取模：
   38%3=2; 38%-2=2; (负号忽略不计) -38%2=2;

## switch(表达式)中表达式的返回值必须是下述几种类型之一：

- switch(表达式) 中表达式的返回值必须是下述几种类型之一:
  byte, char, short, int,枚举, 字符串;
- case 子句中的值必须是常量, 且所有 case 子句中的值应是不同的;
  default 子句是任选的;
- break 语句用来在执行完一个 case 分支后使程序跳出 switch 语句块;
- **如果没有 break, 正确的 case 之后全部执行; 且用 break 之后 case 真能执行一个. 可以在一定的情况下不使用 break**

## break/continue:

- break 退出整个循环; continue 只是退出单词循环
- **`break 只能用于 switch 语句和循环语句中; continue 只能用于循环语句中`**。
- `break 和 continue 后不能有其他的语句, 永远不会执行break 和 continue之后的语句`。
- 标号(label)语句必须紧接在循环的头部。标号语句不能用在非循环语句的前面。

## 数组：

1. 一维数组初始化：

```java
int[] a1 = new int[] { 1, 2, 3 };

int[] a2 = { 1, 2, 3 };

int[] a3 = new int[3];
for (int a : a3) {
    a = 0;
}
```

2. 二维数组：

- 是一个数组, 但每一个元素都是一个一维数组
- for 循环+双下标
  ```java
  public void two_dimensional_array() {
      int[][] a = new int[5][]; // 5行
      for (int i = 0; i < a.length; i++) { // a.length表示行数
          a[i] = new int[i + 1]; // 元素=new数组
          for (int j = 0; j < a[i].length; j++) {
              a[i][j] = i * j + 10;
          }
      }
      // 遍历
      for (int i = 0; i < a.length; i++) { // a.length表示行数
          for (int j = 0; j < a[i].length; j++) {
              System.out.print(a[i][j]+"\t");
          }
          System.out.println();
      }
  }
  ```
- 排序问题：
  > Arrays.sort(int[])
  > Collections.sort(List<>);

3. ArrayList：
   ```java
   private List<Customer>customers = new ArrayList<Customer>();
   customers.get(i); //按下标索引
   return customers.set(i, new Customer("5", "5")); //按下标 set
   ```
