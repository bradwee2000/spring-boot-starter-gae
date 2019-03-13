package com.bwee.springboot.gae.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Publish payload to a PubSub topic.
 *
 * @author bradwee2000@gmail.com
 */
public class PubSubPublisher {
  private static final Logger LOG = LoggerFactory.getLogger(PubSubPublisher.class);

  private final ObjectMapper om;
  private final Map<String, Publisher> publishers = Maps.newHashMap();

  public PubSubPublisher(final ObjectMapper om) {
    this.om = om;
  }

  /**
   * Create Google PubSub Publisher for topic name.
   */
  public TopicPublisher forTopic(final String topic) {
    if (!publishers.containsKey(topic)) {
      publishers.put(topic, createPublisherForTopic(topic));
    }

    return new TopicPublisher(publishers.get(topic), om);
  }

  private Publisher createPublisherForTopic(final String topic) {
    try {
      return Publisher.newBuilder(topic).build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
