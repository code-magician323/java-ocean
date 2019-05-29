## 方法引用

- 条件: 若 Lambda 中内容在其他地方已经实现, 就可以使用 `方法引用`. 方法引用是 Lambda 的另一种表现形式.
- 约束:
  1. Lambda 表达式中调用方法的参数列表与返回值类型, 要与函数式接口的抽象方法的参数列表和返回值一致
  2. `对象::实例方法` 使用要满足: 第一个参数是实例方法的调用者, 第二个参数是实例方法的参数. 使用 `ClassName::MethodName`
- 表现形式

  > 对象::实例方法
  > 类::静态方法
  > 类::实例方法

- 对象::实例方法

  ```java
  Consumer<Integer> consumer = x -> System.out.println(x);
  consumer.accept(50);
  // 方法引用: 返回值和形参一致
  Consumer<Integer> consumer1 = System.out::println;
  consumer1.accept(50);

  Employee employee = new Employee();
  Supplier<Integer> supplier = () -> employee.getAge();
  System.out.println(supplier.get());
  // 方法引用: 返回值和形参一致
  Supplier<Integer> supplier1 = employee::getAge;
  System.out.println(supplier1.get());
  ```

- 类::静态方法

  ```java
   // 类::静态方法
  Comparator<Integer> comparator = (x, y) -> Integer.compare(x, y);
  Comparator<Integer> comparator1 = Integer::compareTo;
  ```

- 类::实例方法

  ```java
  // 类::实例方法
  BiPredicate<Integer, Integer> biPredicate = (x, y) -> x.equals(y);
  BiPredicate<Integer, Integer> biPredicate1 = Integer::equals;
  ```

## 构造器引用

- 表现形式

  > ClassName::new

- 约束:
  > 会被调用的构造器总是与函数式接口抽象方法参数列表保持一致的构造器.
- ClassName::new

  ```java
  // 自定义接口
  public interface CreateEmployee<T, U, W, R> {
      R sup (T t, U u, W w);
  }

  // 有参数
  CreateEmployee<String, Integer, Double, Employee> createEmployee = (name, age, salary) -> new Employee(name, age, salary);
  System.out.println(createEmployee.sup("zack", 25, 125.03));

  CreateEmployee<String, Integer, Double, Employee> createEmployee2 = Employee::new;
  System.out.println(createEmployee2.sup("zack", 25, 125.03));
  ```

## 数组引用

- 表现形式
  > Type::new
- 约束
- Type::new

  ```java
  // Type::new
  Function<Integer, String[]> func = (x) -> new String[x];
  String[] strs = func.apply(10);
  System.out.println(strs.length);

  Function<Integer, String[]> func1 = String[]::new;
  String[] strs1 = func.apply(20);
  System.out.println(strs1.length);
  ```
