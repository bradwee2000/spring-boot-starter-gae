package com.bwee.springboot.gae.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Publish payload to topic.
 *
 * @author bradwee2000@gmail.com
 */
public class PubSubPublisher {
  private static final Logger LOG = LoggerFactory.getLogger(PubSubPublisher.class);

  private final Gson gson;
  private final PublisherFactory publisherFactory;

  public PubSubPublisher(final Gson gson, final PublisherFactory publisherFactory) {
    this.gson = gson;
    this.publisherFactory = publisherFactory;
  }

  public void publish(final String topic, final Object payload) {
    // Create json payload
    final String jsonPayload = gson.toJson(payload);

    // Create message
    final PubsubMessage message = PubsubMessage.newBuilder()
        .setData(ByteString.copyFromUtf8(jsonPayload))
        .build();

    // Publish
    final Publisher publisher = publisherFactory.get(topic);
    checkNotNull(publisher, "No publisher found for topic: " + topic);
    publisher.publish(message);
  }
}
