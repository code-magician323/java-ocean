## Integer

1. recommend use `Integer.valueOf(int value)`
2. int is 4 byte, default value is 0
3. Integer is ? byte, default value is null
4. the nature of char[2*8 bit] is alphas[a-z + Chinese character + number]

   - char + char, char + int will upgrate to int type

5. the nature of byte[8 bit] is signed int8[-128~127]

### AtomicInteger

### BigDecimal

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

## String

1.  String 是 `final` 类型的不可变字符序列
2.  关于字符串缓冲池

    - 直接通过 `=` 为字符串赋值, 会先在缓冲池中查找有无一样的字符串, 若有就把字符串的引用付给字符串变量;
    - 否则会创建一个新的字符串, 并将其 `放入字符串缓冲池` 中, 再赋值

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
    new String(charArray)
    str.toCharArray()
    ```

5.  convert between []byte and String

    -

    ```java

    ```

6.  `StringBuffer(线程安全)/StringBuilder(常用):可修改的字符序列`

```java
String str = "sjihdggvycbhxjkm";
byte[] bytes = str.getBytes();
System.err.println(String.valueOf(bytes));  // className
System.err.println(new String(bytes)); // StringValue
```

---

## convert

### java

1. convert between int and String:

   - int to String

   ```java
   String s = String.valueOf(int i);
   String s = Integer.toString(int i);
   String s = "" + i;
   ```

   - String to int

   ```java
   int i = Integer.parseInt(String s);
   int i = Integer.ValueOf(String s).intValue();
   int i = s - 0;
   ```

2. convert between char[] and String

   - char[] to String

   - String to char[]

   ```java
    char[] chars = str.toCharArray();
    log.info(String.valueOf(chars[0]));
   ```

3. convert between byte[] and String

   - byte[] to String

   ```java
   // StrUtil.bytes(str);
   byte[] bytes = str.getBytes();

   // byte nature is int8
   log.info(String.valueOf(bytes[0]));

   // this will log ClassName
   log.info(String.valueOf(bytes));

   // this will log String value
   log.info(new String(bytes));
   ```

   - String to byte[]

   ```java
   // StrUtil.str(new byte[]{1}, Charset.defaultCharset());
   String str = new String([]bytes)
   ```

### js

1. int 转 String:

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

## interview

```java
Integer a = new Integer(2);
Integer b = new Integer(2);
Assert.isFalse(a == b);

Integer integer = Integer.valueOf(2);
Integer integer2 = Integer.valueOf(2);
Assert.isTrue(integer == integer2);
Assert.isTrue(integer.equals(integer2));

// automatic inbox
Integer integer3 = 2;
Integer integer4 = 2;
// unboxing here
Assert.isTrue(integer3 == integer4);
Assert.isTrue(integer3.equals(integer4));
Assert.isTrue(integer == integer3);
Assert.isFalse(a == integer3);

String String = "123";
String String2 = "123";
Assert.isTrue(String == String2);
Assert.isTrue(String.equals(String2));
```
