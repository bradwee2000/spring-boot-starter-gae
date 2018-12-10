package com.bwee.springboot.gae.model.translator;

import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.ValueType;
import com.googlecode.objectify.impl.translate.SimpleTranslatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bradwee2000@gmail.com
 */
public class LocalDateTimeTranslatorFactory extends SimpleTranslatorFactory<LocalDateTime, String> {
  private static final Logger LOG = LoggerFactory.getLogger(LocalDateTimeTranslatorFactory.class);

  private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

  public LocalDateTimeTranslatorFactory() {
    super(LocalDateTime.class, ValueType.STRING, ValueType.NULL);
  }

  @Override
  protected LocalDateTime toPojo(Value<String> value) {
    return LocalDateTime.parse(value.get(), DATE_TIME_FORMAT);
  }

  @Override
  protected Value<String> toDatastore(LocalDateTime localDateTime) {
    return StringValue.of(localDateTime.format(DATE_TIME_FORMAT));
  }
}
