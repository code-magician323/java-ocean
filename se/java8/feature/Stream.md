## Stream 简介

- Stream 对数据源`[集合等, 核心: 数据]`做一系列的流水线式的中间操作`[核心:计算]`, 产生一个新的 Stream; 且影响之前的数据源, 自己不存储流数据, `懒加载`
- 示例图
  ![avatar](https://img-blog.csdnimg.cn/20190530144939845.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)
- 流程
  > 1. 创建 Stream
  > 2. 中间操作
  > 3. 终止操作

## 创建 Stream

1. 通过 Collection 系列集合的 stream() 或 parallelStream()
2. 通过 Arrays 中的静态方法 stream() 获取数组流
3. 通过 Stream 类中的静态方法 of() 获取
4. 创建无限流

   > 4.1 迭代: seed ---> 开始位置; Function Interface
   > 4.1 生成: Suppiler Interface

   ```java
   // 1. 通过 Collection 系列集合的 stream() 或 parallelStream()
   List<String> stringList = new ArrayList<>();
   Stream<String> stringStream = stringList.stream();

   // 2. 通过 Arrays 中的静态方法 stream() 获取数组流
   Employee []emps = new Employee[10];
   Stream<Employee> employeeStream = Arrays.stream(emps);

   // 3. 通过 Stream 类中的静态方法 of() 获取
   Stream<String> stringStream1 = Stream.of("aa", "bb", "cc");

   // 4. 创建无限流
   // 4.1 迭代: seed ---> 开始位置;
   Stream <Integer> integerStream = Stream.iterate(0, (x) -> x+2);
   // 4.2 生成
   Stream <Integer> integerStream2 = Stream.generate(() -> (int)(Math.random() *100));
   ```

## 创建 中间操作: 不会对数据源产生任何操作

### a.筛选切片

- filter(Predicate<T>)
- distinct() `必须重写 生成元素的 hashCode() 和 equals() 去除重复元素`
- limit(long maxSize)
- skip(long n)
  ```java
  // 跳过满足条件的2个
  Stream<Employee> stream2 = employees.stream();
  stream2.filter(e -> {
      System.out.println("短路");
      return e.getAge() > 30;
  }).skip(2).forEach(System.out::println);
  ```

### b.映射

- map 与 flatMap 的区别:
  > List 中 add(list2) 与 addAll(list2) 一样
- map(Function f) `函数参数会被应用到每个元素上, 并将其映射成一个新的元素; 可迭代`

  ```java
  Stream<Employee> stream3 = employees.stream();
  stream3.map(Employee::getName).forEach(System.out::println); // stream3.map(e -> e.getName()).forEach(System.out::println);
  ```

- flatMap(Function f) `接收一个函数作为参数, 将流中的每个值都换成另一个流, 然后把所有流连接成一个流`

  ```java
  public static Stream<Character> filterCharactor(String string) {
      List<Character> list = new ArrayList<>();
      for (char c : string.toCharArray()) {
          list.add(c);
      }
      return list.stream();
  }
  @Test
  public void testMiddleOperation() {
      System.out.println("--------map iterator-------------");
      List<String> list = Arrays.asList("aaa", "bbb", "ccc", "ddd");
      Stream<Stream<Character>> streamStream = list.stream().map((str) -> {
          List<Character> list2 = new ArrayList<>();
          for (char c : str.toCharArray()) {
              list2.add(c);
          }
          return list2.stream();
      });
      Stream<Stream<Character>> streamStream2 = list.stream().map(StreamAPI::filterCharactor); // {{'a', 'a', 'a'}, {'b', 'b', 'b',} ...}
      streamStream2.forEach(sm -> sm.forEach(System.out::println));

      System.out.println("--------flatMap-------------");
      Stream<Character> characterStream = list.stream().flatMap(StreamAPI::filterCharactor); // {'a', 'a', 'a', 'b', 'b', 'b', ...}
      characterStream.forEach(System.out::println);
  }
  ```

- mapToDouble(ToDoubleFunction f) `产生一个新的 DoubleStream`
- mapToInt(ToIntFunction f) `产生一个新的 IntStream`
- mapToLong(ToLongFunction f) `产生一个新的 LongStream`

### c.排序

- sorted() `产生一个新流, 其中按自然顺序排序:` Comparable
- sorted(Comparator comp) `产生一个新流, 其中按比较器顺序排序:` Comparator
  ```java
  List<String> list = Arrays.asList("bbb", "aaa", "ccc", "ddd");
  list.stream().sorted().forEach(System.out::println);
  employees.stream().sorted(Comparator.comparingInt(x -> (int)(-x.getAge()))).forEach(System.out::println);
  ```

## 创建 终止操作: 对数据源进行 `中间操作处理`

### a. 匹配

- allMatch(Predicate p) `检查是否匹配所有元素`
- anyMatch(Predicate p) `检查是否至少匹配一个元素`
- noneMatch(Predicate p) `检查是否没有匹配所有元素`

### b. 查找

- findFirst() `返回第一个元素`
- findAny() `返回当前流中的任意元素`
- max(Comparator c) `返回流中最大值`
- min(Comparator c) `返回流中最小值`

### c. 遍历

- forEach(Consumer c) `内部迭代`
  ```java
  // 找到两个满足添加之后的就停止运算
  Stream<Employee> stream = employees.stream();
  stream.filter(e -> {
      System.out.println("短路");
      return e.getAge() > 30;
  }).limit(2).forEach(System.out::println);
  ```

### d. 归约 `map 和 reduce 的连接通常称为 map-reduce 模式, 因 Google 用它来进行网络搜索而出名`

- reduce(T iden, BinaryOperator b) `可以将流中元素反复结合起来, 得到一个值. 返回 T` **iden: 初始值**
- reduce(BinaryOperator b) `可以将流中元素反复结合起来, 得到一个值. 返回 Optional<T>`
  ```java
  Double reduce1 = employees.stream().map(Employee::getSalary).reduce(0.0, (x, y) -> x + y);
  System.out.println(reduce1);
  Optional<Double> reduce = employees.stream().map(Employee::getSalary).reduce(Double::sum);
  System.out.println(reduce.get());
  ```

### e. 汇总统计

- count() `返回流中元素总数`
- collect(Collector c) `将流转换为其他形式. 接收一个 Collector接口的实现, 用于给Stream中元素做汇总的方法`

  ```java
  List<String> collect = employees.stream().map(Employee::getName).collect(Collectors.toList());
  collect.forEach(System.out::println);
  HashSet<String> collect1 = employees.stream().map(Employee::getName).collect(Collectors.toCollection(() -> new HashSet<>()));
  HashSet<String> collect2 = employees.stream().map(Employee::getName).collect(Collectors.toCollection(HashSet::new));
  collect2.forEach(System.out::println);

  // groupBy
  Map<String, List<Employee>> collect3 = employees.stream().collect(Collectors.groupingBy((Employee e) -> {
      if (e.getSalary() > 4500) return "HIGH";
      else return "LOWER";
  }));
  // groupBy more
  Map<String, Map<String, List<Employee>>> collect4 = employees.stream().collect(Collectors.groupingBy((Employee e) -> {
      if (e.getSalary() > 4500) return "HIGH";
      else return "LOWER";
  }, Collectors.groupingBy((Employee e) -> {
      if (e.getAge() > 30) return "OLDER";
      else return "YOUNGER";
  })));
  ```

- collect(Collector c) 中 Collector 补充
  ```java
  // 1. toList List<T> 把流中元素收集到List
  List<Employee> emps= list.stream().collect(Collectors.toList());
  // 2. toSet Set<T> 把流中元素收集到Set
  Set<Employee> emps= list.stream().collect(Collectors.toSet());
  // 3. toCollection Collection<T> 把流中元素收集到创建的集合
  Collection<Employee> emps=list.stream().collect(Collectors.toCollection(ArrayList::new));
  // 4. counting Long 计算流中元素的个数
  long count = list.stream().collect(Collectors.counting());
  // 5. summingInt Integer 对流中元素的整数属性求和
  int total=list.stream().collect(Collectors.summingInt(Employee::getSalary));
  // 6. averagingInt Double 计算流中元素Integer属性的平均值
  double avg= list.stream().collect(Collectors.averagingInt(Employee::getSalary));
  // 7. summarizingInt IntSummaryStatistics 收集流中Integer属性的统计值 如: 平均值
  IntSummaryStatistics iss= list.stream().collect(Collectors.summarizingInt(Employee::getSalary));
  // 8. joining String 连接流中每个字符串
  String str= list.stream().map(Employee::getName).collect(Collectors.joining());
  // 9. maxBy Optional<T> 根据比较器选择最大值
  Optional<Emp> max= list.stream().collect(Collectors.maxBy(comparingInt(Employee::getSalary)));
  // 10. minBy Optional<T> 根据比较器选择最小值
  Optional<Emp> min = list.stream().collect(Collectors.minBy(comparingInt(Employee::getSalary)));
  // 11. reducing 归约产生的类型 从一个作为累加器的初始值开始, 利用BinaryOperator与流中元素逐个结合, 从而归约成单个值
  int total=list.stream().collect(Collectors.reducing(0, Employee::getSalar, Integer::sum));
  // 12. collectingAndThen 转换函数返回的类型 包裹另一个收集器, 对其结果转换函数
  int how= list.stream().collect(Collectors.collectingAndThen(Collectors.toList(), List::size));
  // 13. groupingBy Map<K, List<T>> 根据某属性值对流分组, 属性为K, 结果为V
  Map<Emp.Status, List<Emp>> map= list.stream().collect(Collectors.groupingBy(Employee::getStatus));
  // 14. partitioningBy Map<Boolean, List<T>> 根据true或false进行分区
  Map<Boolean,List<Emp>> vd= list.stream().collect(Collectors.partitioningBy(Employee::getManage));
  ```
