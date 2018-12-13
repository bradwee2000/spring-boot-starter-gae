package com.bwee.springboot.gae.model.translator;

import com.google.cloud.datastore.StringValue;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author bradwee2000@gmail.com
 */
public class LocalDateTimeTranslatorFactoryTest {

  private final LocalDateTime date1 = LocalDateTime.of(2018, 12, 31, 11, 44, 55);
  private final LocalDateTime date2 = LocalDateTime.of(2018, 2, 3, 1, 5, 6);
  private final LocalDateTime date3 = LocalDateTime.of(2018, 10, 1, 13, 50, 6);

  private LocalDateTimeTranslatorFactory factory;

  @Before
  public void before() {
    factory = new LocalDateTimeTranslatorFactory();
  }

  @Test
  public void testToDatastore_shouldConvertToString() {
    assertThat(factory.toDatastore(date1).get()).isEqualTo("20181231 11:44:55");
    assertThat(factory.toDatastore(date2).get()).isEqualTo("20180203 01:05:06");
    assertThat(factory.toDatastore(date3).get()).isEqualTo("20181001 13:50:06");
  }

  @Test
  public void testToPojo_shouldConvertToPojo() {
    assertThat(factory.toPojo(StringValue.of("20181231 11:44:55"))).isEqualTo(date1);
    assertThat(factory.toPojo(StringValue.of("20180203 01:05:06"))).isEqualTo(date2);
    assertThat(factory.toPojo(StringValue.of("20181001 13:50:06"))).isEqualTo(date3);
  }
}