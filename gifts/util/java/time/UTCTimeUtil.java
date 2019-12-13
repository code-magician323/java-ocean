import javax.rmi.CORBA.Util;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author zack
 * @create 2019-09-21 20:22
 * @function convert date zone
 */
public final class UTCTimeUtil {

  /** util class should not be initial */
  private UTCTimeUtil() {}

  /**
   * @function convert localDatetime fromZone to toZone
   * @param localDateTime
   * @param fromZone
   * @param toZone
   * @return
   */
  public static LocalDateTime toZone(
      final LocalDateTime localDateTime, final ZoneId fromZone, final ZoneId toZone) {
    if (localDateTime == null || fromZone == null || toZone == null) {
      return null;
    }
    final ZonedDateTime zonedTime = localDateTime.atZone(fromZone);
    final ZonedDateTime converted = zonedTime.withZoneSameInstant(toZone);
    return converted.toLocalDateTime();
  }

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
  public static Date zoneToUtc(Date localDate, TimeZone localTimeZone) {

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

    return zoneToUtc(localDate, TimeZone.getDefault());
  }

  /**
   * @function convert specified Zone localDateTime to UTC localDateTime using fromZone.
   * @param localDateTime
   * @param fromZone
   * @return UTC localDateTime
   */
  public static LocalDateTime zoneToUtc(final LocalDateTime localDateTime, final ZoneId fromZone) {
    return UTCTimeUtil.toZone(localDateTime, fromZone, ZoneOffset.UTC);
  }

  /**
   * @function convert localDateTime to UTC localDateTime using localZone.
   * @param time
   * @return UTC localDateTime
   */
  public static LocalDateTime localToUtc(final LocalDateTime time) {
    return UTCTimeUtil.zoneToUtc(time, ZoneId.systemDefault());
  }

  /**
   * @function Convert utc time to local time (time add local timezone offset)
   * @param UTCDate
   * @param localTimeZone
   * @return local date
   */
  public static Date utcToZone(Date UTCDate, TimeZone localTimeZone) {

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

    return utcToZone(utcDate, TimeZone.getDefault());
  }

  /**
   * @function convert utc localDateTime to specified Zone localDateTime.
   * @param localDateTime
   * @param toZone
   * @return specified Zone localDateTime
   */
  public static LocalDateTime utcToZone(final LocalDateTime localDateTime, final ZoneId toZone) {
    return UTCTimeUtil.toZone(localDateTime, ZoneOffset.UTC, toZone);
  }

  /**
   * @function convert utc localDateTime to local localDateTime.
   * @param localDateTime
   * @return
   */
  public static LocalDateTime utcToLocal(final LocalDateTime localDateTime) {
    return UTCTimeUtil.utcToZone(localDateTime, ZoneId.systemDefault());
  }

  public static void main(String[] args) {
    LocalDateTime localDateTime = localToUtc(LocalDateTime.now());
    System.out.println(localDateTime);
  }
}
