package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubPublisher;
import com.bwee.springboot.gae.pubsub.TopicPublisher;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.bwee.springboot.gae.event.PublishEvent.WrapType.collection;
import static com.bwee.springboot.gae.event.PublishEvent.WrapType.itemized;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    PublishEventHandlerTest.Context.class,
    PublishEventHandlerTest.DummyService.class,
    PublishEventHandler.class})
public class PublishEventHandlerTest {
  private static final Logger LOG = LoggerFactory.getLogger(PublishEventHandlerTest.class);

  @Autowired
  private PubSubPublisher pubSubPublisher;

  @Autowired
  private DummyService service;

  private TopicPublisher topicPublisher;

  @Before
  public void before() {
    reset(pubSubPublisher);

    topicPublisher = mock(TopicPublisher.class, RETURNS_SELF);

    when(pubSubPublisher.forTopic("entity-saved")).thenReturn(topicPublisher);
  }

  @Test
  public void testReturnList_shouldPublishEventForList() {
    service.saveAndReturnList();
    verify(pubSubPublisher).forTopic("entity-saved");
    verify(topicPublisher).publish(Lists.newArrayList("A", "B", "C"));
  }

  @Test
  public void testReturnString_shouldPublishEvent() {
    service.saveAndReturnString();
    verify(pubSubPublisher).forTopic("entity-saved");
    verify(topicPublisher).publish("Success");
  }

  @Test
  public void testReturnVoid_shouldPublishEvent() {
    service.saveAndReturnVoid();
    verify(topicPublisher).publish(null);
  }

  @Test
  public void testWithNoPublishEvent_shouldNotPublishEvent() {
    service.saveWithNoPublishEvent();
    verify(pubSubPublisher, never()).forTopic(any());
    verify(topicPublisher, never()).publish(any());
  }

  @Test
  public void testWithAttributes_shouldPublishWithAttributes() {
    service.saveWithAttributes("John", 18, "test@email.com", "Austin");

    final ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
    verify(topicPublisher).attributes(captor.capture());

    assertThat(captor.getValue()).containsOnlyKeys("name", "age", "email");
    assertThat(captor.getValue().get("name")).isEqualTo("John");
    assertThat(captor.getValue().get("age")).isEqualTo("18");
    assertThat(captor.getValue().get("email")).isEqualTo("test@email.com");
  }

  @Test
  public void testReturnStringWrapAsCollection_shouldWrapPayloadInCollection() {
    service.saveAndReturnStringWrapAsCollection();

    verify(pubSubPublisher).forTopic("entity-saved");
    verify(topicPublisher).publish(Collections.singleton("Success"));
  }

  @Test
  public void testReturnListWrapAsCollection_shouldPublishAsIs() {
    service.saveAndReturnListWrapAsCollection();

    verify(pubSubPublisher).forTopic("entity-saved");
    verify(topicPublisher).publish(Lists.newArrayList("A", "B", "C"));
  }

  @Test
  public void testReturnStringWrapAsItemized_shouldPublishEventForString() {
    service.saveAndReturnStringWrapAsItemized();
    verify(pubSubPublisher).forTopic("entity-saved");
    verify(topicPublisher).publish("Success");
  }

  @Test
  public void testWithPayloadConverter_shouldPublishWithConvertedPayload() {
    service.saveWithPayloadConverters();
    verify(pubSubPublisher).forTopic("entity-saved");
    verify(topicPublisher).publish("S");
  }

  /**
   * Test context
   */
  @Configuration
  @EnableAspectJAutoProxy
  public static class Context {

    @Bean
    public PubSubPublisher pubSubPublisher() {
      return mock(PubSubPublisher.class);
    }

    @Bean
    public Function<String, String> firstChar() {
      return (input) -> "" + input.charAt(0);
    }
  }

  /**
   * Dummy service class.
   */
  @Component
  public static class DummyService {

    @PublishEvent(value = "entity-saved")
    public List<String> saveAndReturnList() {
      return Lists.newArrayList("A", "B", "C");
    }

    @PublishEvent("entity-saved")
    public String saveAndReturnString() {
      return "Success";
    }

    @PublishEvent("entity-saved")
    public void saveAndReturnVoid() {}

    public String saveWithNoPublishEvent() {
      return "Success";
    }

    @PublishEvent("entity-saved")
    public String saveWithAttributes(@PublishAttr String name,
                                     @PublishAttr int age,
                                     @PublishAttr("email") String emailAdd,
                                     String city) {
      return "Success";
    }

    @PublishEvent(value = "entity-saved", payloadConverterBean = "firstChar")
    public String saveWithPayloadConverters() {
      return "Success";
    }

    @PublishEvent(value = "entity-saved", wrapType = collection)
    public String saveAndReturnStringWrapAsCollection() {
      return "Success";
    }

    @PublishEvent(value = "entity-saved", wrapType = collection)
    public List<String> saveAndReturnListWrapAsCollection() {
      return Lists.newArrayList("A", "B", "C");
    }

    @PublishEvent(value = "entity-saved", wrapType = itemized)
    public List<String> saveAndReturnListWrapAsItemized() {
      return Lists.newArrayList("A", "B", "C");
    }

    @PublishEvent(value = "entity-saved", wrapType = itemized)
    public String saveAndReturnStringWrapAsItemized() {
      return "Success";
    }
  }
}