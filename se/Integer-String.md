## java

### int 转 String:

```java
String s = String.valueOf(int i);
String s = Integer.toString(int i);
String s = "" + i;
```

### String 转 int:

```java
int i = Integer.parseInt(String s);
int i = Integer.ValueOf(String s).intValue();
int i = s - 0;
```

## js

### int 转 String:

```js
String s = Integer.toString(int i);
String s = ''+i;
```

### String 转 int:

```js
int i=Integer.parseInt(String s);
int i=s-0;
```

---

## memory

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
