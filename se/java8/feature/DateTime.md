## JDK 1.7 之前所有的日期时间类操作都是线程不安全的

- 证明 demo

  ```java
  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  Callable<Date> task = new Callable<Date>() {
      @Override
      public Date call() throws Exception {
          return sdf.parse("20161121");
      }
  };
  ExecutorService pool = Executors.newFixedThreadPool(10);
  List<Future<Date>> results = new ArrayList<>();
  for (int i = 0; i < 10; i++) {
      results.add(pool.submit(task));
  }
  for (Future<Date> future : results) {
      System.out.println(future.get());
  }
  pool.shutdown();
  ```

- 改为线程安全 2: 使用 ThreadLocal
  ```java
  // 1. 自定义 DateFormatThreadLocal
  public class DateFormatThreadLocal {
    private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>(){
        protected DateFormat initialValue(){
            return new SimpleDateFormat("yyyyMMdd");
        }
    };
    public static final Date convert(String source) throws ParseException{
        return df.get().parse(source);
    }
  }
  // 2. 使用 DateFormatThreadLocal 的 convert 方法
  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  Callable<Date> task = new Callable<Date>() {
      @Override
      public Date call() throws Exception {
          return DateFormatThreadLocal.convert("20161121");
      }
  };
  ExecutorService pool = Executors.newFixedThreadPool(10);
  List<Future<Date>> results = new ArrayList<>();
  for (int i = 0; i < 10; i++) {
      results.add(pool.submit(task));
  }
  for (Future<Date> future : results) {
      System.out.println(future.get());
  }
  pool.shutdown()
  ```

## JDK 1.8

- 改为线程安全 1: java8

  ```java
  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
  Callable<LocalDate> task = new Callable<LocalDate>() {
      @Override
      public LocalDate call() throws Exception {
          LocalDate ld = LocalDate.parse("20161121", dtf);
          return ld;
      }
  };
  ExecutorService pool = Executors.newFixedThreadPool(10);
  List<Future<LocalDate>> results = new ArrayList<>();
  for (int i = 0; i < 10; i++) {
      results.add(pool.submit(task));
  }
  for (Future<LocalDate> future : results) {
      System.out.println(future.get());
  }
  pool.shutdown();
  ```

- LocalDate LocalTime LocalDateTime 类的实例方法都是 `不可变的对象`

### 1.本地化日期时间 API

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

### 2.使用时区的日期时间 API

- ZonedDate
- ZonedTime
- ZonedDateTime
