package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class TopicPublisherTest {

  private TopicPublisher topicPublisher;
  private ObjectMapper om = new ObjectMapper();
  private Publisher publisher;
  private ApiFuture future;

  @Before
  public void before() throws ExecutionException, InterruptedException, TimeoutException {
    publisher = mock(Publisher.class);
    future = mock(ApiFuture.class);
    topicPublisher = new TopicPublisher(publisher, om);

    when(publisher.publish(any())).thenReturn(future);
    when(publisher.getTopicNameString()).thenReturn("test-topic");
    when(future.get()).thenReturn("success");
    when(future.get(anyLong(), any())).thenReturn("success");
    when(future.isDone()).thenReturn(true);
    when(future.isCancelled()).thenReturn(false);
    when(future.cancel(anyBoolean())).thenReturn(true);
  }

  @Test
  public void testPublishWithStringPayload_shouldPublishStringPayload() {
    final ArgumentCaptor<PubsubMessage> messageCaptor = ArgumentCaptor.forClass(PubsubMessage.class);

    topicPublisher.publish("Sample Payload");

    // Verify message was published
    verify(publisher).publish(messageCaptor.capture());

    // Verify message payload
    assertThat(messageCaptor.getValue().getData().toStringUtf8()).isEqualTo("\"Sample Payload\"");
  }

  @Test
  public void testPublishWithNullPayload_shouldPublishNullPayload() {
    final ArgumentCaptor<PubsubMessage> messageCaptor = ArgumentCaptor.forClass(PubsubMessage.class);

    topicPublisher.publish(null);

    // Verify message was published
    verify(publisher).publish(messageCaptor.capture());

    // Verify message payload
    assertThat(messageCaptor.getValue().getData().toStringUtf8()).isEqualTo("null");
  }

  @Test
  public void testPublishWithAttributes_shouldPublishPayloadWithAttributes() {
    final ArgumentCaptor<PubsubMessage> messageCaptor = ArgumentCaptor.forClass(PubsubMessage.class);

    topicPublisher.attributes("name", "John", "email", "john@email.com").publish("Payload");

    // Verify message was published
    verify(publisher).publish(messageCaptor.capture());

    // Verify message payload and attributes
    assertThat(messageCaptor.getValue().getData().toStringUtf8()).isEqualTo("\"Payload\"");
    assertThat(messageCaptor.getValue().getAttributesMap().get("name")).isEqualTo("John");
    assertThat(messageCaptor.getValue().getAttributesMap().get("email")).isEqualTo("john@email.com");
  }

  @Test
  public void testShutdown_shouldShutdownPublisher() throws Exception {
    topicPublisher.shutdown();

    verify(publisher).shutdown();
  }
}