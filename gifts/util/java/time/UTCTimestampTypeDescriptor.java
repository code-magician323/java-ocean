package com.augmentum.jpa.util;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

import java.sql.*;
import java.util.Calendar;

public class UTCTimestampTypeDescriptor extends TimestampTypeDescriptor {

  private static final long serialVersionUID = -6384224470621790140L;

  public static final UtcTimestampTypeDescriptor INSTANCE = new UtcTimestampTypeDescriptor();

  public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
    return new BasicBinder<X>(javaTypeDescriptor, this) {
      @Override
      protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
          throws SQLException {
        Timestamp timestamp = javaTypeDescriptor.unwrap(value, Timestamp.class, options);

        if (timestamp != null) {
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(UTCTimeUtil.localToUtc(timestamp));
          timestamp = new Timestamp(calendar.getTimeInMillis());
        }
        st.setTimestamp(index, timestamp);
      }

      @Override
      protected void doBind(
          CallableStatement callableStatement, X x, String s, WrapperOptions wrapperOptions)
          throws SQLException {}
    };
  }

  public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
    return new BasicExtractor<X>(javaTypeDescriptor, this) {
      @Override
      protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {

        Timestamp timestamp = rs.getTimestamp(name);

        if (timestamp != null) {
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(UTCTimeUtil.utcToLocal(timestamp));
          timestamp = new Timestamp(calendar.getTimeInMillis());
        }
        return javaTypeDescriptor.wrap(timestamp, options);
      }

      @Override
      protected X doExtract(
          CallableStatement callableStatement, int i, WrapperOptions wrapperOptions)
          throws SQLException {
        return null;
      }

      @Override
      protected X doExtract(
          CallableStatement callableStatement, String s, WrapperOptions wrapperOptions)
          throws SQLException {
        return null;
      }
    };
  }
}
