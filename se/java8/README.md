**This repository is intended to describe the new features of java8.**

- [java8-features](#java8-features)
  - [content](#content)
    - [Lambda 表达式](#lambda-%E8%A1%A8%E8%BE%BE%E5%BC%8F)
      - [1.函数式编程](#1%E5%87%BD%E6%95%B0%E5%BC%8F%E7%BC%96%E7%A8%8B)
      - [2.Lambda 表达式](#2lambda-%E8%A1%A8%E8%BE%BE%E5%BC%8F)
    - [Stream 数据流](#stream-%E6%95%B0%E6%8D%AE%E6%B5%81)
      - [1.Stream 流介绍](#1stream-%E6%B5%81%E4%BB%8B%E7%BB%8D)
      - [2.使用流](#2%E4%BD%BF%E7%94%A8%E6%B5%81)
        - [a.筛选切片](#a%E7%AD%9B%E9%80%89%E5%88%87%E7%89%87)
        - [b.映射](#b%E6%98%A0%E5%B0%84)
        - [c.匹配](#c%E5%8C%B9%E9%85%8D)
        - [d.查找](#d%E6%9F%A5%E6%89%BE)
        - [e.归约](#e%E5%BD%92%E7%BA%A6)
        - [f.汇总统计](#f%E6%B1%87%E6%80%BB%E7%BB%9F%E8%AE%A1)
        - [g.遍历](#g%E9%81%8D%E5%8E%86)
      - [3.并行流与串行流](#3%E5%B9%B6%E8%A1%8C%E6%B5%81%E4%B8%8E%E4%B8%B2%E8%A1%8C%E6%B5%81)
    - [方法引用](#%E6%96%B9%E6%B3%95%E5%BC%95%E7%94%A8)
      - [1.方法引用](#1%E6%96%B9%E6%B3%95%E5%BC%95%E7%94%A8)
      - [2.方法引用实例](#2%E6%96%B9%E6%B3%95%E5%BC%95%E7%94%A8%E5%AE%9E%E4%BE%8B)
    - [默认方法](#%E9%BB%98%E8%AE%A4%E6%96%B9%E6%B3%95)
      - [1.语法](#1%E8%AF%AD%E6%B3%95)
      - [2.多个默认方法](#2%E5%A4%9A%E4%B8%AA%E9%BB%98%E8%AE%A4%E6%96%B9%E6%B3%95)
      - [3.静态默认方法](#3%E9%9D%99%E6%80%81%E9%BB%98%E8%AE%A4%E6%96%B9%E6%B3%95)
      - [4.默认方法实例](#4%E9%BB%98%E8%AE%A4%E6%96%B9%E6%B3%95%E5%AE%9E%E4%BE%8B)
    - [JAVA8 全新的时间包](#java8-%E5%85%A8%E6%96%B0%E7%9A%84%E6%97%B6%E9%97%B4%E5%8C%85)
      - [1.本地化日期时间 API](#1%E6%9C%AC%E5%9C%B0%E5%8C%96%E6%97%A5%E6%9C%9F%E6%97%B6%E9%97%B4-api)
      - [2.使用时区的日期时间 API](#2%E4%BD%BF%E7%94%A8%E6%97%B6%E5%8C%BA%E7%9A%84%E6%97%A5%E6%9C%9F%E6%97%B6%E9%97%B4-api)
    - [Optional 类](#optional-%E7%B1%BB)
    - [Base64](#base64)

# java8-features

## content

### [优化底层 Hash 和 内存空间](./feature/Hash底层优化.md)

### [Lambda 表达式](./feature/Lambda.md)

#### 1.函数式编程

- 函数式接口实例
- Java 内置四大核心函数式接口
  > Consumer<T> 消费型接口
  > Supplier<T> 供给型接口
  > Function<T, R> 函数型接口
  > Predicate<T> 断定型接口

#### 2.Lambda 表达式

- 函数接口: `@FunctionalInterface`
- 类型检查、类型推断: Java 编译器根据 Lambda 表达式上下文信息就能推断出参数的正确类型。
- 局部变量限制
  > Lambda 表达式也允许使用自由变量 `外层作用域中定义的变量`, 就像匿名类一样. 它们被称作`捕获 Lambda`.
  > Lambda 可以有限制地捕获 `也就是在其主体中引用` 实例变量和静态变量. 限制为: 局部变量 `必须` 显式声明为 `final` , 或`事实上是 final[java8 默认为 final]` [demo](./feature/Lambda.md#语法).

### [方法引用与构造器引用](./feature/Reference.md)

#### 1.方法引用

#### 2.构造器引用

#### 3.数组引用

### [Stream 数据流](./feature/Stream.md)

#### 1.Stream 流介绍

#### 2.使用流

##### a.筛选切片

- filter
- distinct
- limit
- skip

##### b.映射

- map
- flatMap

##### c.匹配

- anyMatch
- allMatch
- noneMatch

##### d.查找

- noneMatch
- findFirst

##### e.归约

- reduce
- max
- min

##### f.汇总统计

- collect
- count

##### g.遍历

- foreach

### [并行流与串行流](./feature/parallel.md)

- Fork/Join 框架: 这里面的中间值的确定很有问题: 好的值会比 `并行` 快: `工作窃取`算法: 算法是指某个线程从其他队列里窃取任务来执行
- 并行串行切换: `parallel()/sequential()`

### [接口](./feature/Interface.md)

#### 1. 总结

- 类优先于接口。 `如果一个子类继承的父类和接口有相同的方法实现。 那么子类继承父类的方法`
- `子类型中的方法优先于父类型中的方法[就近原则]`
- 如果以上条件都不满足, 则必须显示覆盖/实现其方法, 或者声明成 abstract。

#### 2. 默认方法

#### 3. 静态方法

#### 4. 私有方法

### [JAVA8 全新的时间包](./feature/DateTime.md)

#### 1.本地化日期时间 API

- LocalDate: `日期`
- LocalTime: `时间`
- LocalDateTime: `时间+日期`
- DateTimeFormatter: `日期格式化`
  > ofPattern(str)
- Duration: `时间计算`
  > toMillis()
  > between(,)
- Period: `日期计算`
  > between(,)
  > getHours()
- Instant: `日期`
  > toEpochMilli() `时间戳13位`
  > OffsetDateTime
- temporalAdjustor `时间校正器`
  > firstDayOfNextYear()

#### 2.使用时区的日期时间 API

- ZonedDate
- ZonedTime
- ZonedDateTime

### 其他特性

#### [Optional 类](./feature/Optional.md)

- 没看出有什么优点, 以后再看看

#### 重复注解与类型注解

#### [Base64](./feature/Base64.md)
