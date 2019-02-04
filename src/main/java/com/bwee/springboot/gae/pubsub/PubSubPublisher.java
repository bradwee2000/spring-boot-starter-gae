package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Publish payload to topic.
 *
 * @author bradwee2000@gmail.com
 */
public class PubSubPublisher {
  private static final Logger LOG = LoggerFactory.getLogger(PubSubPublisher.class);

  private final ObjectMapper om;
  private final PublisherFactory publisherFactory;

  public PubSubPublisher(final ObjectMapper om, final PublisherFactory publisherFactory) {
    this.om = om;
    this.publisherFactory = publisherFactory;
  }

  public void publish(final String topic, final Object payload) {
    publish(topic, payload, Collections.emptyMap());
  }

  public void publish(final String topic, final Object payload, final Map<String, String> attributes) {
    // Create json payload
    final String jsonPayload = toJson(payload);

    // Create message
    final PubsubMessage message = PubsubMessage.newBuilder()
        .putAllAttributes(attributes)
        .setData(ByteString.copyFromUtf8(jsonPayload))
        .build();

    // Publish
    final Publisher publisher = publisherFactory.get(topic);
    checkNotNull(publisher, "No publisher found for topic: " + topic);
    publisher.publish(message);
  }

  private String toJson(final Object payload) {
    try {
      return om.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
