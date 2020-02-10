## Java Type System

1. RTTI[Run-Time Type Identification]: it assumes that we already know all the type information at compile time.

   - 编译时就知道使用的类型信息;
   - 在 Java 中所有的类型转换都是在运行时进行有效性检查;
   - RTTI 的含义: `在运行时识别一个对象的类型`

2. Reflect: it allows us to get and use type information in runtime.
   - 运行时获取并使用类型信息;
   - 它主要用于在编译阶段无法获得所有的类型信息的场景, 如各类框架.

## Class Object

1. introduce

   - Java 使用 Class 对象来执行 RTTI
   - 它包含了与类相关的所有信息
   - JVM 使用 类加载器 的子系统产生一个 Class 对象

2. [Class Loader](../ClassLoader.md)
   - 类加载器子系统, 主要完成将 class 二进制文件加载到 JVM 中, 并将其转换为 Class 对象的过程.

## 动态代理

## 基于 SPI 的 Plugin

## 总结

## Reference

1. https://www.cnblogs.com/canacezhang/p/9237953.html
