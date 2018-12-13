package com.bwee.springboot.gae.model.service;

import com.bwee.springboot.gae.model.dao.ConfigDao;
import com.bwee.springboot.gae.model.pojo.Config;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class ConfigServiceTest {

  private ConfigDao configDao;
  private ConfigService service;

  private Config phone;
  private Config email;
  private Config address;
  private Config blank;

  @Before
  public void before() {
    phone = new Config().setId("phone").setValue("99174834");
    email = new Config().setId("email").setValue("test@email.com");
    address = new Config().setId("address").setValue("123 Baker St.");
    blank = new Config().setId("blank").setValue("");


    configDao = mock(ConfigDao.class);

    service = new ConfigService(configDao, new Gson());

    // Setup configs
    when(configDao.findOne(anyString())).thenReturn(Optional.empty());
    when(configDao.findOne("phone")).thenReturn(Optional.of(phone));
    when(configDao.findOne("email")).thenReturn(Optional.of(email));
    when(configDao.findOne("address")).thenReturn(Optional.of(address));
    when(configDao.findOne("blank")).thenReturn(Optional.of(blank));
  }

  @Test
  public void testGetPropertyAsString_shouldReturnStringValue() {
    // Test with value
    assertThat(service.getProperty("phone").asString()).hasValue("99174834");
    assertThat(service.getProperty("email").asString()).hasValue("test@email.com");

    // Test empty
    assertThat(service.getProperty("unknown").asString()).isEmpty();
  }

  @Test
  public void testGetPropertyAsBool_shouldReturnBoolValue() {
    final Config isSetupDone = new Config().setId("isSetupDone").setValue("true");
    final Config isComplete = new Config().setId("isComplete").setValue("false");

    when(configDao.findOne("isSetupDone")).thenReturn(Optional.of(isSetupDone));
    when(configDao.findOne("isComplete")).thenReturn(Optional.of(isComplete));

    assertThat(service.getProperty("isSetupDone").asBool()).hasValue(TRUE);
    assertThat(service.getProperty("isComplete").asBool()).hasValue(FALSE);
    assertThat(service.getProperty("unknown").asBool()).isEmpty();
  }

  @Test
  public void testGetPropertyAsDouble_shouldReturnDoubleValue() {
    // Test with whole numbers
    final Config wholeNum = new Config().setId("wholeNum").setValue("12345");
    when(configDao.findOne("wholeNum")).thenReturn(Optional.of(wholeNum));
    assertThat(service.getProperty("wholeNum").asDouble()).hasValue(12345d);

    // Test with decimals
    final Config decimal = new Config().setId("decimal").setValue("983.23315");
    when(configDao.findOne("decimal")).thenReturn(Optional.of(decimal));
    assertThat(service.getProperty("decimal").asDouble()).hasValue(983.23315);

    // Test with empty
    assertThat(service.getProperty("unknown").asDouble()).isEmpty();

    // Test with blank
    Assertions.assertThatThrownBy(() -> service.getProperty("blank").asDouble())
        .isInstanceOf(NumberFormatException.class);

    // Test with invalid characters
    Assertions.assertThatThrownBy(() -> service.getProperty("email").asDouble())
        .isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void testSetPropertyString_shouldSetPropertyWithStringValue() {
    service.setProperty("Name", "Yalex");

    final ArgumentCaptor<Config> captor = ArgumentCaptor.forClass(Config.class);
    Mockito.verify(configDao).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo("Yalex");
  }

  @Test
  public void testSetPropertyInt_shouldSetPropertyWithIntValue() {
    service.setProperty("Quantity", 100);

    final ArgumentCaptor<Config> captor = ArgumentCaptor.forClass(Config.class);
    Mockito.verify(configDao).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo("100");
  }

  @Test
  public void testSetPropertyBool_shouldSetPropertyWithBoolValue() {
    service.setProperty("IsDone", false);

    final ArgumentCaptor<Config> captor = ArgumentCaptor.forClass(Config.class);
    Mockito.verify(configDao).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo("false");
  }

  @Test
  public void testSetPropertyNull_shouldSetPropertyWithNullValue() {
    service.setProperty("Null", null);

    final ArgumentCaptor<Config> captor = ArgumentCaptor.forClass(Config.class);
    Mockito.verify(configDao).save(captor.capture());
    assertThat(captor.getValue().getValue()).isEqualTo("null");
  }
}