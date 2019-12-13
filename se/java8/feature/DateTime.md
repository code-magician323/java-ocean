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

- UTC

```java

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author zack
 * @create 2019-09-21 20:22
 * @function
 */
public final class UTCTimeUtil {

  /** util class should not be initial */
  private UTCTimeUtil() {}

  /**
   * Get calendar that time zone is UTC
   *
   * @param date
   * @return
   */
  public static Calendar toUTCCalendar(Date date) {
    if (date == null) {
      return null;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    return calendar;
  }

  /**
   * @function Convert local time to utc time (time subtract local timezone offset)
   * @param localDate
   * @param localTimeZone
   * @return utc date
   */
  public static Date localToUtc(Date localDate, TimeZone localTimeZone) {

    if (localDate == null || localTimeZone == null) {
      return null;
    }

    Date utc;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(localDate);
    calendar.add(
        Calendar.MILLISECOND, 0 - localTimeZone.getRawOffset() - localTimeZone.getDSTSavings());
    utc = calendar.getTime();

    return utc;
  }

  /**
   * @function: Convert local time to utc time (time subtract local timezone offset)
   * @param localDate
   * @return utc date
   */
  public static Date localToUtc(Date localDate) {

    return localToUtc(localDate, TimeZone.getDefault());
  }

  /**
   * @function Convert utc time to local time (time add local timezone offset)
   * @param UTCDate
   * @param localTimeZone
   * @return local date
   */
  public static Date utcToLocal(Date UTCDate, TimeZone localTimeZone) {

    Date localDate = null;
    if (UTCDate != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(UTCDate);
      calendar.add(
          Calendar.MILLISECOND, localTimeZone.getRawOffset() + localTimeZone.getDSTSavings());
      localDate = calendar.getTime();
    }

    return localDate;
  }

  /**
   * @function Convert utc time to local time (time add local timezone offset)
   * @param utcDate
   * @return local date
   */
  public static Date utcToLocal(Date utcDate) {

    return utcToLocal(utcDate, TimeZone.getDefault());
  }
}
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

## hibernate

- 在转换为 entity 时减小 8 小时后再插入

```java
import java.util.Comparator;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;

public class UtcTimestampType extends
        AbstractSingleColumnStandardBasicType<Date> implements
        VersionType<Date>, LiteralType<Date> {
    private static final long serialVersionUID = 5848852471383340492L;

    public static final UtcTimestampType INSTANCE = new UtcTimestampType();

    public UtcTimestampType() {
        super(UtcTimestampTypeDescriptor.INSTANCE,
                JdbcTimestampTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return TimestampType.INSTANCE.getName();
    }

    @Override
    public String[] getRegistrationKeys() {
        return UtcTimestampType.INSTANCE.getRegistrationKeys();
    }

    public Date next(Date current, SessionImplementor session) {
        return UtcTimestampType.INSTANCE.next(current, session);
    }

    public Date seed(SessionImplementor session) {
        return UtcTimestampType.INSTANCE.seed(session);
    }

    public Comparator<Date> getComparator() {
        return UtcTimestampType.INSTANCE.getComparator();
    }

    public String objectToSQLString(Date value, Dialect dialect) {
        return UtcTimestampType.INSTANCE.objectToSQLString(value, dialect);
    }

    public Date fromStringValue(String xml) throws HibernateException {
        return UtcTimestampType.INSTANCE.fromStringValue(xml);
    }
}


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

import com.augmentum.velocms.camputils.utils.UTCTimeUtil;


public class UtcTimestampTypeDescriptor extends TimestampTypeDescriptor  {

    private static final long serialVersionUID = -6384224470621790140L;

    public static final UtcTimestampTypeDescriptor INSTANCE = new UtcTimestampTypeDescriptor();

    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>( javaTypeDescriptor, this ) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                Timestamp timestamp = javaTypeDescriptor.unwrap( value, Timestamp.class, options );

                if (timestamp != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(UTCTimeUtil.localToUtc(timestamp));
                    timestamp = new Timestamp(calendar.getTimeInMillis());
                }
                st.setTimestamp( index, timestamp);
            }
        };
    }

    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>( javaTypeDescriptor, this ) {
            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {

                Timestamp timestamp = rs.getTimestamp( name);

                if (timestamp != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(UTCTimeUtil.utcToLocal(timestamp));
                    timestamp = new Timestamp(calendar.getTimeInMillis());
                }
                return javaTypeDescriptor.wrap( timestamp, options );
            }
        };
    }
}

// use not work in new version should be tie to getXXX()
@Column(name = "GamingDay")
@Type(type = "**.UtcTimestampType")
@DateTimeFormat(style = "MM")
private Date gamingDay;

// then if get data from database use JDBC, will execute getExtractor utcToLocal;
// insert data into database use JDBC, will execute doBind localToUtc.
```
