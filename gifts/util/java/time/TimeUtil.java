import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author zack
 * @create 2019-09-21 21:30
 * @function time util: convert date type
 */
public final class TimeUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtils.class);

  /**
   * @function convert timestamp to localDateTime
   * @param timestamp
   * @return localDateTime
   */
  public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {

    Instant instant = Instant.ofEpochMilli(timestamp);
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }

  /**
   * @function convert date to localDateTime
   * @param date
   * @return localDateTime
   */
  public static LocalDateTime date2LocalDateTime(Date date) {

    Instant instant = date.toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

    return localDateTime;
  }

  /**
   * @function convert localDate to date
   * @param localDateTime
   * @return date
   */
  public static Date localDateTime2Date(LocalDate localDateTime) {

    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atStartOfDay().atZone(zone).toInstant();
    return Date.from(instant);
  }
}
