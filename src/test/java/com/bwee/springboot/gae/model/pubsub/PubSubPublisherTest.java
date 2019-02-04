package com.bwee.springboot.gae.model.pubsub;

import com.bwee.springboot.gae.pubsub.PubSubPublisher;
import com.bwee.springboot.gae.pubsub.PublisherFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class PubSubPublisherTest {

  private ObjectMapper om = new ObjectMapper();
  private PublisherFactory publisherFactory;
  private Publisher publisher;

  private PubSubPublisher pubSubPublisher;

  @Before
  public void before() {
    publisherFactory = mock(PublisherFactory.class);
    publisher = mock(Publisher.class);
    pubSubPublisher = new PubSubPublisher(om, publisherFactory);

    when(publisherFactory.get("test_event")).thenReturn(publisher);
  }

  @Test
  public void testPublishWithStringPayload_shouldPublishStringPayload() {
    final ArgumentCaptor<PubsubMessage> messageCaptor = ArgumentCaptor.forClass(PubsubMessage.class);

    pubSubPublisher.publish("test_event", "Sample Payload");

    // Verify message was published
    verify(publisher).publish(messageCaptor.capture());

    // Verify message payload
    assertThat(messageCaptor.getValue().getData().toStringUtf8()).isEqualTo("\"Sample Payload\"");
  }

  @Test
  public void testPublishWithNullPayload_shouldPublishNullPayload() {
    final ArgumentCaptor<PubsubMessage> messageCaptor = ArgumentCaptor.forClass(PubsubMessage.class);

    pubSubPublisher.publish("test_event", null);

    // Verify message was published
    verify(publisher).publish(messageCaptor.capture());

    // Verify message payload
    assertThat(messageCaptor.getValue().getData().toStringUtf8()).isEqualTo("null");
  }
}