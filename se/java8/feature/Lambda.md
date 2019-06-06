**Table of Lambda Contents**

- [Lambda 表达式](#lambda-%E8%A1%A8%E8%BE%BE%E5%BC%8F)
  - [语法](#%E8%AF%AD%E6%B3%95)
  - [函数式接口](#%E5%87%BD%E6%95%B0%E5%BC%8F%E6%8E%A5%E5%8F%A3)
  - [demo](#demo)

## Lambda 表达式

- 原因: `主要是精简代码: 如匿名内部类相关的代码`
- 条件: 需要函数式接口的支持

### 语法

- 无参数无返回值[Runnable]: () -> System.out.println("hello lambda2");

  ```java
  final int num = 0; // JDK 1.7 之前必须是 final; JDK 1.8 默认加了 final: 不能让改变 num.
  // 不使用 Lambda 表达式
  Runnable runnable =new Runnable() {
      @Override
      public void run() {
          System.out.println("hello lambda" + num);
      }
  };

  // 使用 Lambda 表达式
  Runnable runnable1 = () -> System.out.println("hello lambda2");
  ```

- 有一个参数无返回值[Consumer]: (x) -> System.out.println(x) // ()可以不写

  ```java
  Consumer<String> consumer = (x) -> System.out.println(x);
  Consumer<String> consumer = x -> System.out.println(x);
  consumer.accept("练顺大傻逼！");
  ```

- 有两个参数有返回值[Comparator]: (x, y) -> x-y;

  ```java
  Comparator<Integer> comparator = (x, y) -> {
      System.out.println("sa");
      return Integer.compare(x, y);
  };
  ```

- 参数类型可以不写: JVM 可以进行 `类型推断`

### 函数式接口

- 定义: **若接口中只有一个未实现的方法, 就称之为 `函数式接口`, 打上 `@FunctionalInterface` 注解**
- Consumer<T>: void accept(T t)

  ```java
  public void consume(double money, Consumer<Double> consumer) {
      // 这里可以进行相应的逻辑处理
      consumer.accept(money);
  }

  @Test
  public void testConsumer() {
      consume(1000, (money) -> System.out.println("练顺去洗脚, 消费 " + money + " 元."));
  }

  // 说明
  Consumer<Double> con = (money) -> System.out.println("练顺去洗脚, 消费 " + money + " 元.");
  consume(1000, con); // con.accept(1000)

  // 说明2
  Consumer<Integer> consumer = (money) -> System.out.println("练顺去洗脚, 消费 " + money + " 元.");
  consumer.accept(200);
  ```

- Function<T, R>: R apply(T t)

  ```java
  // 处理字符串: 获取字符串长度
  public Integer strHandler(String str, Function<String, Integer> func) {
      Integer size = func.apply(str);
      return size;
  }

  @Test
  public void testFunctionNoLambda() {
      Integer size = strHandler("And this file just interprets the directory information at that level.", new Function<String, Integer>() {
          @Override
          public Integer apply(String s) {
              return s.length();
          }
      });
      System.out.println(size);
  }

  @Test
  public void testFunction() {
      Integer size = strHandler("And this file just interprets the directory information at that level.",
              (str) -> str.length());
      System.out.println(size);
  }

  // 说明
  @Test
  public void testFunction() {
    Function<String, Integer> func = (str) -> str.length();
    int size = func.apply("And this file just interprets the directory information at that level.");
    System.out.println(size);
  }

   // list
  Function<Integer, List<Integer>> func2 = (x) -> {
      List<Integer> integers = new ArrayList<>();

      for (int i = 0; i < x; i++) {
          int n = (int)(Math.random() * 100);
          integers.add(n);
      }
      return integers;
  };
  System.out.println(func2.apply(50).size());
  ```

- Supplier<T>: T get()

  ```java
  // 在 num 范围内产生一些数, 并放入集合中
  public Set<Integer> generateData(Integer num, Supplier<Integer> supplier) {
      Set set = new HashSet();
      for (int i = 0; i < num; i++) {
          Integer n = supplier.get(); // 接口中的方法实现: () -> (int)(Math.random() * 100)
          set.add(n);
      }
      return set;
  }
  @Test
  public void testSupplier() {
      Set set = generateData(50, () -> (int)(Math.random() * 100));
      System.out.println(set);
  }

  // 说明
  @Test
  public void testSupplier2() {
      Set set = new HashSet();
      int size = 50;
      Supplier<Integer> supplier = () -> (int)(Math.random() * 100);
      for (int i = 0; i < size; i++) {
          Integer n = supplier.get();
          set.add(n);
      }
      System.out.println(set.size());
  }
  ```

- Predicate<T>: bool test(T t)
  ```java
  // 将满足条件的字符串, 过滤满足条件的字符串.
   public List<String> filterStrings(List<String> strs, Predicate<String> predicate) {
      List<String> sts = new ArrayList<>();
      for (String str : strs) {
          if (predicate.test(str)) sts.add(str);
      }
      return sts;
  }
  @Test
  public void testAddListNoLambda() {
      List<String> strings = Arrays.asList("hello", "zack", "logo", "fans");
      List<String> strs = filterStrings(strings, new Predicate<String>() {
          @Override
          public boolean test(String s) {
              return s.contains("a");
          }
      });
      strs.forEach(System.out::println);
  }
  @Test
  public void testAddList() {
      List<String> strings = Arrays.asList("hello", "zack", "logo", "fans");
      List<String> strs = filterStrings(strings, (str) -> str.contains("a"));
      strs.forEach(System.out::println);
  }
  ```
- 其他函数式接口
  > BiFunction<T, U, R>: R apply(T t, U u)
  > BinaryOperator<T>: T apply(T t1, T t2)
  > UnaryOperator<T>: T apply(T t)
  > BiConsumer<T, U>: void accept(T t, U u)
  > ToIntFunction<T> ToLongFunction<T> ToDoubleFunction<T>: int applyAsInt(T value);
  > IntFunction<R> LongFunction<R> DoubleFunction<R>: R apply(int value);

### demo

- 定制排序 Employee: 年龄-姓名
  ```java
  Collections.sort(employees, (employee1, employee2) -> {
      if (employee1.getAge() == employee2.getAge()) return Double.compare(employee1.getSalary(), employee2.getSalary());
      else return Integer.compare(employee1.getAge(), employee2.getAge());
  });
  employees.forEach(System.out::println);
  ```
