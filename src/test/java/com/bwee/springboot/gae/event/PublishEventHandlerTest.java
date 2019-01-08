package com.bwee.springboot.gae.event;

import com.bwee.springboot.gae.pubsub.PubSubPublisher;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static java.util.Collections.emptyMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
  private PubSubPublisher publisher;

  @Autowired
  private DummyService service;

  @Before
  public void before() {
    Mockito.reset(publisher);
  }

  @Test
  public void testReturnList_shouldPublishEventForList() {
    service.saveAndReturnList();
    verify(publisher).publish("entity-saved", Lists.newArrayList("A", "B", "C"), emptyMap());
  }

  @Test
  public void testReturnListWithItemized_shouldPublishEventForEachItemInList() {
    service.saveAndReturnItemizedList();
    verify(publisher).publish("entity-saved", "A", emptyMap());
    verify(publisher).publish("entity-saved", "B", emptyMap());
    verify(publisher).publish("entity-saved", "C", emptyMap());
  }

  @Test
  public void testReturnString_shouldPublishEvent() {
    service.saveAndReturnString();
    verify(publisher).publish("entity-saved", "Success", emptyMap());
  }

  @Test
  public void testReturnVoid_shouldPublishEvent() {
    service.saveAndReturnVoid();
    verify(publisher).publish("entity-saved", null, emptyMap());
  }

  @Test
  public void testWithNoPublishEvent_shouldNotPublishEvent() {
    service.saveWithNoPublishEvent();
    verify(publisher, times(0)).publish(any(), any());
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

    @PublishEvent(value = "entity-saved", itemized = true)
    public List<String> saveAndReturnItemizedList() {
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
  }
}