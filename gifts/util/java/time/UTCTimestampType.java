package com.augmentum.jpa.util;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;

import java.util.Comparator;
import java.util.Date;

public class UTCTimestampType extends AbstractSingleColumnStandardBasicType<Date>
    implements VersionType<Date>, LiteralType<Date> {
  private static final long serialVersionUID = 5848852471383340492L;

  public static final UtcTimestampType INSTANCE = new UtcTimestampType();

  public UtcTimestampType() {
    super(UtcTimestampTypeDescriptor.INSTANCE, JdbcTimestampTypeDescriptor.INSTANCE);
  }

  public String getName() {
    return TimestampType.INSTANCE.getName();
  }

  @Override
  public String[] getRegistrationKeys() {
    return UtcTimestampType.INSTANCE.getRegistrationKeys();
  }

  @Override
  public Date seed(SharedSessionContractImplementor sharedSessionContractImplementor) {
    return UtcTimestampType.INSTANCE.seed(sharedSessionContractImplementor);
  }

  @Override
  public Date next(Date date, SharedSessionContractImplementor sharedSessionContractImplementor) {
    return UtcTimestampType.INSTANCE.next(date, sharedSessionContractImplementor);
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
